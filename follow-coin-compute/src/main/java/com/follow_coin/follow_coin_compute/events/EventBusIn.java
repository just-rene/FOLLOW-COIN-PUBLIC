package com.follow_coin.follow_coin_compute.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.follow_coin.follow_coin_compute.EventTypes;
import com.follow_coin.follow_coin_compute.dtos.CoinPriceDifferenceEvent;
import com.follow_coin.follow_coin_compute.dtos.CoinPriceDifferenceEventKey;
import com.follow_coin.follow_coin_compute.dtos.CoinPriceEvent;
import com.follow_coin.follow_coin_compute.entities.CoinPrice;
import com.follow_coin.follow_coin_compute.entities.CoinPriceKey;
import com.follow_coin.follow_coin_compute.repos.CoinPriceRepo;
import com.follow_coin.follow_coin_compute.tools.CleanMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

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
    public void listen(String message) throws JsonProcessingException {

        CoinPriceEvent coinPriceEvent = cleanMapper.readValue(message);

        Mono.just(coinPriceEvent)
                .flatMap(eventValidator::returnIfValid)
                .flatMap(eventValidator::returnIfExists) //must be empty
                .switchIfEmpty(defer(() -> coinPriceRepo.save(coinPriceEvent.getCoinPrice())))
                .subscribe();
    }


    private LocalDateTime extractDateTime(CoinPrice c) {
        String datetimeString = c.getCoinPriceKey().getDatetime();
        return LocalDateTime.parse(datetimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
    }

}

