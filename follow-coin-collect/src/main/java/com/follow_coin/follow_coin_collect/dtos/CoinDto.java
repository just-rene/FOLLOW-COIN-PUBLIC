package com.follow_coin.follow_coin_collect.dtos;

import com.follow_coin.follow_coin_collect.entities.Coin;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CoinDto extends Dto {

    private String coinSymbol;

    public CoinDto(Coin coin) {
        this.coinSymbol = coin.getSymbol();
    }

    public Coin toEntity() {
        return new Coin(coinSymbol);
    }

}
