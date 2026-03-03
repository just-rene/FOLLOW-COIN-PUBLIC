package com.follow_coin.follow_coin_compute.computation;

import com.follow_coin.follow_coin_compute.computation.dtos.ComputationResult;
import com.follow_coin.follow_coin_compute.dtos.CoinPriceData;

public interface CoinPriceAlgorithm {

    ComputationResult compute(CoinPriceData coinPriceData);

}
