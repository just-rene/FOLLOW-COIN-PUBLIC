package com.follow_coin.follow_coin_coordinator.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.follow_coin.follow_coin_coordinator.dtos.CoinPriceDifferenceEvent;
import com.follow_coin.follow_coin_coordinator.repos.CoinPriceDifferenceEventRepo;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventBusIn {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private SqsTemplate sqsTemplate;

    @Autowired
    private CoinPriceDifferenceEventRepo coinPriceDifferenceEventRepo;


    private static final Logger logger = LoggerFactory.getLogger(EventBusIn.class);

    @SqsListener(value = "coin-price-difference-events")
    public void listen(String message) throws JsonProcessingException {

        System.err.println(message);
        CoinPriceDifferenceEvent currentCoinPriceEvent = mapper.readValue(message, CoinPriceDifferenceEvent.class);


        coinPriceDifferenceEventRepo.save(currentCoinPriceEvent).subscribe();

    }
}

