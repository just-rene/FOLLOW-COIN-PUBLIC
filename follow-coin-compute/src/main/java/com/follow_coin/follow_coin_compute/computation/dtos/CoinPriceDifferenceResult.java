package com.follow_coin.follow_coin_compute.computation.dtos;

import com.follow_coin.follow_coin_compute.dtos.CoinPriceDifferenceEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class CoinPriceDifferenceResult implements ComputationResult{

    CoinPriceDifferenceEvent coinPriceDifferenceEvent;

}
