package com.follow_coin.follow_coin_compute.dtos;


import com.follow_coin.follow_coin_compute.computation.dtos.CoinPricePair;
import com.follow_coin.follow_coin_compute.entities.CoinPrice;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * wrapper class for different input types (list or singe datapoint)
 */
@Getter
public class CoinPriceData {

    public CoinPriceData(CoinPrice coinPrice){
        this.coinPrice = coinPrice;
    }

    public CoinPriceData(List<CoinPrice> coinPrices){
        this.coinPrices = coinPrices;
    }

    public CoinPriceData(CoinPricePair coinPricePair){
        this.coinPricePair = coinPricePair;
    }

    CoinPrice coinPrice;
    CoinPricePair coinPricePair;
    List<CoinPrice> coinPrices;

}
