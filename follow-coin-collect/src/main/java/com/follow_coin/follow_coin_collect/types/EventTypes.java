package com.follow_coin.follow_coin_collect.types;

public enum EventTypes {

    COIN_PRICE_EVENT("coin-price-event");

    public final String value;
    EventTypes(String value) {
        this.value = value;
    }
}
