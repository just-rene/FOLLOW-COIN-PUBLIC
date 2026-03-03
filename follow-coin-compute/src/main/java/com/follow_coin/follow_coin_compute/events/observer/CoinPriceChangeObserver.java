package com.follow_coin.follow_coin_compute.events.observer;

import com.follow_coin.follow_coin_compute.EventTypes;
import com.follow_coin.follow_coin_compute.computation.CoinPriceAlgorithm;
import com.follow_coin.follow_coin_compute.computation.dtos.CoinPriceDifferenceResult;
import com.follow_coin.follow_coin_compute.computation.dtos.ComputationResult;
import com.follow_coin.follow_coin_compute.dtos.CoinPriceData;
import com.follow_coin.follow_coin_compute.entities.CoinPrice;
import com.follow_coin.follow_coin_compute.tools.CleanMapper;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CoinPriceChangeObserver implements ChangeObserver {


    @Autowired
    private CleanMapper mapper;

    @Autowired
    private SqsTemplate sqsTemplate;

    private final List<CoinPriceAlgorithm> coinPriceAlgorithms;

    private static final Logger logger = LoggerFactory.getLogger(CoinPriceChangeObserver.class);

    public static final String COLLECTION_NAME = "coinPrice";

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public CoinPriceChangeObserver(ReactiveMongoTemplate reactiveMongoTemplate, List<CoinPriceAlgorithm> coinPriceAlgorithms) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.coinPriceAlgorithms = coinPriceAlgorithms;
        watchForChanges();
    }

    @Override
    public void watchForChanges() {
        reactiveMongoTemplate.changeStream(CoinPrice.class)
                .watchCollection(COLLECTION_NAME)
                .listen()
                .filter(e -> Objects.requireNonNull(e.getOperationType()).getValue().equals("insert"))
                .doOnNext(this::doOnChange)
                .doOnError(error -> System.err.println("Error in change stream: " + error.getMessage()))
                .subscribe();
    }

    @Override
    public void doOnChange(ChangeStreamEvent<CoinPrice> event) {
        coinPriceAlgorithms.stream()
                .map(x -> x.compute(new CoinPriceData(event.getBody())))
                .map(this::castToType)
                .forEach(this::send);
    }

    private String castToType(ComputationResult computationResult) {
        if (computationResult instanceof CoinPriceDifferenceResult coinPriceDifferenceResult) {
            return mapper.writeValueAsString(coinPriceDifferenceResult.getCoinPriceDifferenceEvent());
        } else {
            logger.error("tried to cast to unknown type");
            return "";
        }
    }

    private void send(String data) {
        sqsTemplate.sendAsync(EventTypes.COIN_PRICE_DIFFERENCE_EVENT.value, data);
    }


}

