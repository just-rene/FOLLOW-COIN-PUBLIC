package com.follow_coin.follow_coin_compute.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.follow_coin.follow_coin_compute.dtos.CoinPriceEvent;
import com.follow_coin.follow_coin_compute.entities.CoinPrice;
import com.follow_coin.follow_coin_compute.repos.CoinPriceRepo;
import com.follow_coin.follow_coin_compute.tools.CleanMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static reactor.core.publisher.Mono.defer;


@Component
public class EventBusIn {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private CleanMapper cleanMapper;

    @Autowired
    private CoinPriceRepo coinPriceRepo;

    @Autowired
    private SqsTemplate sqsTemplate;

    @Autowired
    private EventValidator eventValidator;

    private static final Logger logger = LoggerFactory.getLogger(EventBusIn.class);

    @SqsListener(value = "coin-price-event")
    public void listen(String message) {
        logger.info(message);

        CoinPriceEvent coinPriceEvent = cleanMapper.readValue(message);
        store(coinPriceEvent).subscribe();
    }

    public Mono<CoinPrice> store(CoinPriceEvent coinPriceEvent) {
        if (coinPriceEvent == null) {
            return Mono.empty();
        }

        return Mono.just(coinPriceEvent)
                .flatMap(eventValidator::returnIfValid)
                .flatMap(eventValidator::returnIfExists) //must be empty
                .switchIfEmpty(defer(() -> coinPriceRepo.save(coinPriceEvent.getCoinPrice())));
    }
}

