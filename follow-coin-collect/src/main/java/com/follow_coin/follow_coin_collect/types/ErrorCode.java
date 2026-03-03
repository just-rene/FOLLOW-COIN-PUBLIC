package com.follow_coin.follow_coin_collect.types;


import lombok.Getter;

@Getter
public enum ErrorCode {

    INVALID_INPUT(1000, "INVALID_INPUT"),
    DUPLICATE(1001, "DUPLICATE");

    private final int code;
    private final String description;

    ErrorCode(int code, String description) {
        this.code = code;
        this.description = description;
    }
}
