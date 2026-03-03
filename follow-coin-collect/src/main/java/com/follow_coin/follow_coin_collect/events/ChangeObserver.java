package com.follow_coin.follow_coin_collect.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.follow_coin.follow_coin_collect.dtos.CoinPriceEvent;
import com.follow_coin.follow_coin_collect.entities.CoinPrice;
import com.follow_coin.follow_coin_collect.types.EventTypes;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import org.w3c.dom.events.Event;

import java.util.Objects;

@Service
public class ChangeObserver {


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SqsTemplate sqsTemplate;

    private static final Logger logger = LoggerFactory.getLogger(ChangeObserver.class);

    public static final String COLLECTION_NAME = "coinPrice";

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public ChangeObserver(ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        watchForChanges();
    }

    public void watchForChanges() {
        reactiveMongoTemplate.changeStream(CoinPrice.class)
                .watchCollection(COLLECTION_NAME)
                .listen()
                .filter(e -> Objects.requireNonNull(e.getOperationType()).getValue().equals("insert"))
                .doOnNext(this::accept)
                .doOnError(error -> System.err.println("Error in change stream: " + error.getMessage()))
                .subscribe();
    }

    private void accept(ChangeStreamEvent<CoinPrice> event) {
        System.err.println("change event");
        String data = "";
        try {
            CoinPriceEvent coinPriceEvent = new CoinPriceEvent(event.getBody());
            data = objectMapper.writeValueAsString(coinPriceEvent);
        } catch (JsonProcessingException e) {
            logger.error("could not deserialize value!");
        }
        logger.info(data);
        sqsTemplate.sendAsync(EventTypes.COIN_PRICE_EVENT.value, data);
    }

}
