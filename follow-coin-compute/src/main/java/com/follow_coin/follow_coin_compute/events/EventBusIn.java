package com.follow_coin.follow_coin_compute.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.follow_coin.follow_coin_compute.dtos.CoinPriceDifferenceEvent;
import com.follow_coin.follow_coin_compute.dtos.CoinPriceDifferenceEventKey;
import com.follow_coin.follow_coin_compute.dtos.CoinPriceEvent;
import com.follow_coin.follow_coin_compute.entities.Coin;
import com.follow_coin.follow_coin_compute.entities.CoinPrice;
import com.follow_coin.follow_coin_compute.entities.CoinPriceKey;
import com.follow_coin.follow_coin_compute.repos.CoinPriceRepo;
import com.mongodb.reactivestreams.client.MongoClient;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Function;

import static reactor.core.publisher.Mono.defer;

@Component
public class EventBusIn {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private CoinPriceRepo coinPriceRepo;

    @Autowired
    private SqsTemplate sqsTemplate;


    private static final Logger logger = LoggerFactory.getLogger(EventBusIn.class);

    @SqsListener(value = "coin-price-event")
    public void listen(String message) throws JsonProcessingException {

        CoinPriceEvent currentCoinPriceEvent = mapper.readValue(message, CoinPriceEvent.class);

        //if coin price doesn't exist, then save it and compute DifferenceEvent
        checkIfCoinPriceAlreadyExists(currentCoinPriceEvent)
                .filter(exists -> !exists)
                .map(exists -> computeCoinPriceDifference(currentCoinPriceEvent.getCoinPrice()))
                .flatMap(Function.identity())
                .doOnNext(this::sendToCoordinator)
                .subscribe();
    }

    public Mono<Boolean> checkIfCoinPriceAlreadyExists(CoinPriceEvent currentCoinPriceEvent){
        return coinPriceRepo.exists(Example.of(currentCoinPriceEvent.getCoinPrice()));
    }

    public Mono<Void> sendToCoordinator(CoinPriceDifferenceEvent coinPriceDifferenceEvent){
        try {
            String dataJson = mapper.writeValueAsString(coinPriceDifferenceEvent);
            //send to coordinator
            sqsTemplate.sendAsync("coin-price-difference-events", dataJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return Mono.empty();
    }

    public Mono<CoinPriceDifferenceEvent> computeCoinPriceDifference(CoinPrice currentCoinPrice){

        //collects 2 CoinPriceEvents and computes price difference
        return coinPriceRepo.save(currentCoinPrice).map(ccp -> {

            //extract and get event one minute before
            LocalDateTime localDateTimeCurrentEvent = extractDateTime(ccp);

            LocalDateTime localDateTimeLastEvent = localDateTimeCurrentEvent.minusMinutes(1);
            String localDateTimeLastEventString = localDateTimeLastEvent.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
            String symbol = ccp.getCoinPriceKey().getSymbol();

            Mono<CoinPrice> lastCoinPriceEvent = coinPriceRepo
                    .getCoinPriceByDateTimeAndSymbol(localDateTimeLastEventString, symbol)
                    .switchIfEmpty(defer( () -> interpolateWithLatestValue(localDateTimeCurrentEvent, ccp))); //if empty -> interpolate  with older value (if it exists)

            return Mono.zip(lastCoinPriceEvent, Mono.just(ccp));

            //creates the CoinPriceDifferenceEvent
        }).map(coinPrices -> {
            return coinPrices.map(x -> {
                double priceDifference = x.getT1().getPrice() - x.getT2().getPrice();

                String symbol = x.getT1().getCoinPriceKey().getSymbol();

                String startDate = x.getT1().getCoinPriceKey().getDatetime();
                String endDate = x.getT2().getCoinPriceKey().getDatetime();

                CoinPriceDifferenceEventKey coinPriceDifferenceEventKey = new CoinPriceDifferenceEventKey(startDate, endDate,symbol);

                boolean isOneOfTheEntitiesInterpolated = x.getT1().isInterpolated();

                return new CoinPriceDifferenceEvent(coinPriceDifferenceEventKey, priceDifference, isOneOfTheEntitiesInterpolated );
            });
        }).flatMap(Function.identity());
    }


    /**
     * very basic interpolation... expecting x_0 = 0, x_1 = 1,  x = 0.5
     * doesn't account for actual time delta between the data points -> because points shouldn't be very far apart
     * <a href="https://de.wikipedia.org/wiki/Interpolation_(Mathematik)#Lineare_Interpolation">linear interpolation</a>
     *
     * @return
     */
    private Mono<CoinPrice> interpolateWithLatestValue(LocalDateTime localDateTimeCurrentEvent, CoinPrice cpep) {

        Mono<CoinPrice> latestCoinPriceEventBefore = coinPriceRepo
                .getCoinPriceBefore(localDateTimeCurrentEvent.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")), cpep.getCoinPriceKey().getSymbol());


        return latestCoinPriceEventBefore.map(lcpe -> {
            double f0 = lcpe.getPrice();
            double f1 = cpep.getPrice();

            double x_0 = 0;
            double x_1 = 1;
            double x = 0.5;

            double interpolatedCoinPrice = f0 + ((f1 - f0) / (x_1 - x_0)) * (x - x_0);


            String symbol = lcpe.getCoinPriceKey().getSymbol();
            String dateTime = localDateTimeCurrentEvent.minusMinutes(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

            CoinPriceKey coinPriceKey = new CoinPriceKey(symbol,dateTime );
            return new CoinPrice(coinPriceKey, interpolatedCoinPrice, true);

        });
    }

    private LocalDateTime extractDateTime(CoinPrice c) {
        String datetimeString = c.getCoinPriceKey().getDatetime();
        return LocalDateTime.parse(datetimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
    }

}

