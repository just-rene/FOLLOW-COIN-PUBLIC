package com.follow_coin.follow_coin_compute.events.observer;

import com.follow_coin.follow_coin_compute.EventTypes;
import com.follow_coin.follow_coin_compute.computation.CoinPriceAlgorithm;
import com.follow_coin.follow_coin_compute.computation.CoinPriceAlgorithmType;
import com.follow_coin.follow_coin_compute.computation.ComputationFactory;
import com.follow_coin.follow_coin_compute.computation.dtos.CoinPriceDifferenceResult;
import com.follow_coin.follow_coin_compute.computation.dtos.CoinPricePair;
import com.follow_coin.follow_coin_compute.computation.dtos.ComputationResult;
import com.follow_coin.follow_coin_compute.dtos.CoinPriceData;
import com.follow_coin.follow_coin_compute.dtos.CoinPriceDifferenceEvent;
import com.follow_coin.follow_coin_compute.dtos.CoinPriceEvent;
import com.follow_coin.follow_coin_compute.entities.Coin;
import com.follow_coin.follow_coin_compute.entities.CoinPrice;
import com.follow_coin.follow_coin_compute.events.visitor.ComputationVisitor;
import com.follow_coin.follow_coin_compute.repos.CoinPriceRepo;
import com.follow_coin.follow_coin_compute.tools.CleanMapper;
import com.follow_coin.follow_coin_compute.tools.DateTimeTool;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
public class CoinPriceChangeObserver implements ChangeObserver {


    @Autowired
    private CleanMapper mapper;

    @Autowired
    private SqsTemplate sqsTemplate;

    @Autowired
    private CoinPriceRepo coinPriceRepo;

    @Autowired
    private DateTimeTool dateTimeTool;

    @Autowired
    private ComputationVisitor computationVisitor;

    private static final Logger logger = LoggerFactory.getLogger(CoinPriceChangeObserver.class);

    public static final String COLLECTION_NAME = "coinPrice";

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @Autowired
    private ComputationFactory computationFactory;

    public CoinPriceChangeObserver(ReactiveMongoTemplate reactiveMongoTemplate, List<CoinPriceAlgorithm> coinPriceAlgorithms) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
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
    public void doOnChange(ChangeStreamEvent<CoinPrice> event)  {
        var currentCoinPrice = Objects.requireNonNull(event.getBody());
        accept(computationVisitor, currentCoinPrice);
    }

    @Override
    public void accept(ComputationVisitor computationVisitor, CoinPrice currentCoinPrice) {
        computationVisitor.visit(currentCoinPrice)
                .subscribe(this::send);
    }

    private void send(String data) {
        logger.info("send sqs: {}", data);

        sqsTemplate.sendAsync(EventTypes.COIN_PRICE_DIFFERENCE_EVENT.value, data);
    }
}

