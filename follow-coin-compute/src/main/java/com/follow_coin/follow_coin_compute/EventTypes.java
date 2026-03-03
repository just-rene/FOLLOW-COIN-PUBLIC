package com.follow_coin.follow_coin_compute;

public enum EventTypes {
    COIN_PRICE_DIFFERENCE_EVENT("coin-price-difference-events"),
    COIN_PRICE_EVENT("coin-price-event");

    public final String value;

    EventTypes(String value){
        this.value = value;
    }
}
