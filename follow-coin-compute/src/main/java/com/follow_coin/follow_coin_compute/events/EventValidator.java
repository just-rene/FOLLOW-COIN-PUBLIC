package com.follow_coin.follow_coin_compute.events;

import com.follow_coin.follow_coin_compute.dtos.CoinPriceEvent;
import com.follow_coin.follow_coin_compute.entities.CoinPrice;
import com.follow_coin.follow_coin_compute.entities.CoinPriceKey;
import com.follow_coin.follow_coin_compute.repos.CoinPriceRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class EventValidator {


    @Autowired
    private CoinPriceRepo coinPriceRepo;

    private static final Logger logger = LoggerFactory.getLogger(EventValidator.class);

    public Mono<CoinPriceEvent> returnIfValid(CoinPriceEvent currentCoinPriceEvent) {

        if (currentCoinPriceEvent == null || currentCoinPriceEvent.getUuid() == null || currentCoinPriceEvent.getCoinPrice() == null) {
            logger.info("Rejected event! All fields must be not null. {}", currentCoinPriceEvent);
            return Mono.empty();
        }

        return Mono.just(currentCoinPriceEvent);
    }



    public Mono<CoinPrice> returnIfExists(CoinPriceEvent currentCoinPriceEvent){
        CoinPriceKey coinPriceKey = currentCoinPriceEvent.getCoinPrice().getCoinPriceKey();
        return coinPriceRepo.getCoinPriceByDateTimeAndSymbol(coinPriceKey.getDatetime(),coinPriceKey.getSymbol());
    }

    public Mono<Boolean> checkIfCoinPriceAlreadyExists(CoinPriceEvent currentCoinPriceEvent){
        return coinPriceRepo.exists(Example.of(currentCoinPriceEvent.getCoinPrice()));
    }



}
