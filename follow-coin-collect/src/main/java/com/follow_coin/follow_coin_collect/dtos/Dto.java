package com.follow_coin.follow_coin_collect.dtos;

import com.follow_coin.follow_coin_collect.types.ErrorCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Dto {


    private ErrorCode internalErrorCode = null;
    private String info = null;


    public Dto setInternalErrorCode(ErrorCode internalErrorCode) {
        this.internalErrorCode = internalErrorCode;
        return this;
    }

    public Dto setInfo(String info) {
        this.info = info;
        return this;

    }




}
