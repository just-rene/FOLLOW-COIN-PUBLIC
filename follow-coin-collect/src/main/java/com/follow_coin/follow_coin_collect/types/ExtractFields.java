package com.follow_coin.follow_coin_collect.types;

public enum ExtractFields {


    DATA("data"),
    LAST_UPDATE("last_updated"),
    QUOTE("quote"),
    USD("USD"),
    PRICE("price"),
    SYMBOL("symbol");

    public final String val;

    ExtractFields(String val) {
        this.val = val;
    }

}
