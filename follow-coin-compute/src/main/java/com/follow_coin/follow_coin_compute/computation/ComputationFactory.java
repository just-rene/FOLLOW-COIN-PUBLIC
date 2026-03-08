package com.follow_coin.follow_coin_compute.computation;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;


//TODO: implement
@Component
public class ComputationFactory {


    private final List<CoinPriceAlgorithm> coinPriceAlgorithms;

    public ComputationFactory(List<CoinPriceAlgorithm> coinPriceAlgorithms) {
        this.coinPriceAlgorithms = coinPriceAlgorithms;
    }


    public CoinPriceAlgorithm create(CoinPriceAlgorithmType type) throws Exception {
        if(Objects.equals(CoinPriceAlgorithmType.COIN_PRICE_DIFFERENCE_ALGORITHM, type)){
            return find(CoinPriceDifferenceAlgorithm.class);
        }
        throw new Exception("unknown algorithm type.");
    }

    private CoinPriceAlgorithm find(Class algo){
       return coinPriceAlgorithms
               .stream()
               .filter(algo::isInstance)
               .findFirst()
               .orElse(null);

    }

}
