package com.follow_coin.follow_coin_compute.computation.dtos;

import com.follow_coin.follow_coin_compute.entities.CoinPrice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CoinPricePair {

    CoinPrice coinPriceCurrent;
    CoinPrice coinPriceBefore;
}
