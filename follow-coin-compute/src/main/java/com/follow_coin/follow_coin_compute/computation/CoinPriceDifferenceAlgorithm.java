package com.follow_coin.follow_coin_compute.computation;

import com.follow_coin.follow_coin_compute.computation.dtos.CoinPriceDifferenceResult;
import com.follow_coin.follow_coin_compute.computation.dtos.CoinPricePair;
import com.follow_coin.follow_coin_compute.computation.dtos.ComputationResult;
import com.follow_coin.follow_coin_compute.dtos.CoinPriceData;
import com.follow_coin.follow_coin_compute.dtos.CoinPriceDifferenceEvent;
import com.follow_coin.follow_coin_compute.dtos.CoinPriceDifferenceEventKey;
import com.follow_coin.follow_coin_compute.entities.CoinPrice;
import com.follow_coin.follow_coin_compute.entities.CoinPriceKey;
import com.follow_coin.follow_coin_compute.repos.CoinPriceRepo;
import com.follow_coin.follow_coin_compute.tools.DateTimeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
//@Scope("prototype")
public class CoinPriceDifferenceAlgorithm implements CoinPriceAlgorithm {


    @Autowired
    private DateTimeTool dateTimeTool;

    @Autowired
    private CoinPriceRepo coinPriceRepo;


    @Override
    public ComputationResult compute(CoinPriceData coinPriceData) {
        System.err.println("CoinPriceDifferenceAlgorithm");
        CoinPricePair coinPricePair = prepare(coinPriceData.getCoinPrice());

        CoinPrice coinPriceCurrent = coinPricePair.getCoinPriceCurrent();
        CoinPrice coinPriceBefore = coinPriceRepo.getCoinPriceByDateTimeAndSymbol(coinPricePair.getCoinPriceBefore().getCoinPriceKey().getDatetime(), coinPricePair.getCoinPriceBefore().getCoinPriceKey().getSymbol()).block();

        if (coinPriceBefore != null && coinPriceCurrent != null) {

            double priceDifference = coinPriceBefore.getPrice() - coinPriceCurrent.getPrice();

            String symbol = coinPriceBefore.getCoinPriceKey().getSymbol();

            String startDate = coinPriceBefore.getCoinPriceKey().getDatetime();
            String endDate = coinPriceCurrent.getCoinPriceKey().getDatetime();

            CoinPriceDifferenceEventKey coinPriceDifferenceEventKey = new CoinPriceDifferenceEventKey(startDate, endDate, symbol);

            boolean isOneOfTheEntitiesInterpolated = coinPriceBefore.isInterpolated();

            return new CoinPriceDifferenceResult(new CoinPriceDifferenceEvent(coinPriceDifferenceEventKey, priceDifference, isOneOfTheEntitiesInterpolated));
        }

        return null;
    }

    private CoinPricePair prepare(CoinPrice coinPrice) {
        //extract and get event one minute before
        LocalDateTime localDateTimeCurrentEvent = dateTimeTool.extractDateTime(coinPrice);
        System.err.println("current coin price event: " + localDateTimeCurrentEvent.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));

        LocalDateTime localDateTimeLastEvent = localDateTimeCurrentEvent.minusMinutes(1);
        String localDateTimeLastEventString = localDateTimeLastEvent.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        String symbol = coinPrice.getCoinPriceKey().getSymbol();

        CoinPriceKey coinPriceKeyBefore = new CoinPriceKey(symbol, localDateTimeLastEventString);
        CoinPrice coinPriceBefore = new CoinPrice(coinPriceKeyBefore, -1, false);

        return new CoinPricePair(coinPrice, coinPriceBefore);
    }

//    private Mono<CoinPrice> interpolateWithLatestValue(LocalDateTime localDateTimeCurrentEvent, CoinPrice cpep) {
//
//        Mono<CoinPrice> latestCoinPriceEventBefore = coinPriceRepo
//                .getCoinPriceBefore(localDateTimeCurrentEvent.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")), cpep.getCoinPriceKey().getSymbol());
//
//
//        return latestCoinPriceEventBefore.map(lcpe -> {
//            double f0 = lcpe.getPrice();
//            double f1 = cpep.getPrice();
//
//            double x_0 = 0;
//            double x_1 = 1;
//            double x = 0.5;
//
//            double interpolatedCoinPrice = f0 + ((f1 - f0) / (x_1 - x_0)) * (x - x_0);
//
//
//            String symbol = lcpe.getCoinPriceKey().getSymbol();
//            String dateTime = localDateTimeCurrentEvent.minusMinutes(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
//
//            CoinPriceKey coinPriceKey = new CoinPriceKey(symbol,dateTime );
//            return new CoinPrice(coinPriceKey, interpolatedCoinPrice, true);
//
//        });
//    }


}
