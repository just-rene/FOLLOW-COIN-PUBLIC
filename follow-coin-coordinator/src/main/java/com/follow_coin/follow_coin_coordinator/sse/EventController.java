package com.follow_coin.follow_coin_coordinator.sse;

import ch.qos.logback.core.util.AggregationType;
import com.follow_coin.follow_coin_coordinator.dtos.CoinPriceDifferenceEvent;
import com.follow_coin.follow_coin_coordinator.entities.CoinPrice;
import com.follow_coin.follow_coin_coordinator.repos.CoinPriceDifferenceEventRepo;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.reactivestreams.client.MongoClient;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.OperationType;
@RestController
public class EventController {


    @Autowired
    private CoinPriceDifferenceEventRepo coinPriceDifferenceEventRepo;
//
//    @Autowired
//    private MongoClient mongoClient;

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;


    @GetMapping(path = "/ticker-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Object> streamFlux()  {
        System.err.println("new client subscription to ticker-stream");
       return reactiveMongoTemplate.changeStream("coinPriceDifferenceEvent", ChangeStreamOptions.empty(), CoinPriceDifferenceEvent.class)
                .map(x-> {
                        if(x.getOperationType().getValue().equals("insert")){
                            return x.getBody();
                        }else {
                            return Flux.empty();
                        }
                });

    }



    @GetMapping(path = "/history/{coin}")
    public Flux<CoinPriceDifferenceEvent> history(@PathVariable String coin)  {


        if(isValidCoin(coin)){
//            System.err.println(coin);
            return coinPriceDifferenceEventRepo.getLatestNEventForCoin(coin.toUpperCase(),20);
        }else {
            return Flux.empty();
        }
    }

    private boolean isValidCoin(String coinSymbol) {
        if((coinSymbol.length() < 2) || (coinSymbol.length() > 6) ){
            return false;
        }
        if(!coinSymbol.matches("[a-zA-Z]+")){
            return false;
        }
        return true;
    }
}
