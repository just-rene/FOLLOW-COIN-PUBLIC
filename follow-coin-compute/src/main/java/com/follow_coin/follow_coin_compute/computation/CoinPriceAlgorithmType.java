package com.follow_coin.follow_coin_compute.computation;

public enum CoinPriceAlgorithmType {

    COIN_PRICE_DIFFERENCE_ALGORITHM("coinPriceDifferenceEvent") ;

    final String value;

    CoinPriceAlgorithmType(String value) {
        this.value = value;
    }
}
