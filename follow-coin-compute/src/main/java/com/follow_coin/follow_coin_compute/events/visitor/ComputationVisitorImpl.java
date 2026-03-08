package com.follow_coin.follow_coin_compute.events.visitor;

import com.follow_coin.follow_coin_compute.computation.CoinPriceAlgorithm;
import com.follow_coin.follow_coin_compute.computation.CoinPriceAlgorithmType;
import com.follow_coin.follow_coin_compute.computation.ComputationFactory;
import com.follow_coin.follow_coin_compute.computation.dtos.CoinPriceDifferenceResult;
import com.follow_coin.follow_coin_compute.computation.dtos.CoinPricePair;
import com.follow_coin.follow_coin_compute.computation.dtos.ComputationResult;
import com.follow_coin.follow_coin_compute.dtos.CoinPriceData;
import com.follow_coin.follow_coin_compute.entities.CoinPrice;
import com.follow_coin.follow_coin_compute.repos.CoinPriceRepo;
import com.follow_coin.follow_coin_compute.tools.CleanMapper;
import com.follow_coin.follow_coin_compute.tools.DateTimeTool;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
public class ComputationVisitorImpl implements ComputationVisitor {


    @Autowired
    private CleanMapper mapper;

    @Autowired
    private SqsTemplate sqsTemplate;

    @Autowired
    CoinPriceRepo coinPriceRepo;

    @Autowired
    DateTimeTool dateTimeTool;

    @Autowired
    private ComputationFactory computationFactory;

    private static final Logger logger = LoggerFactory.getLogger(ComputationVisitorImpl.class);

    @Override
    public Mono<String> visit(CoinPrice currentCoinPrice) {
        return computeCoinPriceDifference(currentCoinPrice);
    }

    public Mono<String> computeCoinPriceDifference(CoinPrice currentCoinPrice){
        LocalDateTime dateTimeCurrent = dateTimeTool.extractDateTime(currentCoinPrice);
        String localDateTimeLastEventString = dateTimeTool.getDateTimeBefore(dateTimeCurrent);

        return coinPriceRepo
                .getCoinPriceByDateTimeAndSymbol(localDateTimeLastEventString, currentCoinPrice.getCoinPriceKey().getSymbol())
                .map(coinPriceBefore -> new CoinPriceData(new CoinPricePair(coinPriceBefore, currentCoinPrice)))
                .map(this::compute)
                .map(this::castToType);
    }

    public ComputationResult compute(CoinPriceData coinPriceData){
        CoinPriceAlgorithm coinPriceAlgorithm;
        try {
            coinPriceAlgorithm = computationFactory.create(CoinPriceAlgorithmType.COIN_PRICE_DIFFERENCE_ALGORITHM);
        } catch (Exception e) {
            throw new RuntimeException("can't create algorithm with factory!");
        }
        return coinPriceAlgorithm.compute(coinPriceData);
    }

    private String castToType(ComputationResult computationResult) {
        if (computationResult instanceof CoinPriceDifferenceResult coinPriceDifferenceResult) {
            return mapper.writeValueAsString(coinPriceDifferenceResult.getCoinPriceDifferenceEvent());
        } else {
            logger.error("tried to cast to unknown type");
            return "";
        }
    }


}
