package com.follow_coin.follow_coin_collect.tools;


import com.follow_coin.follow_coin_collect.entities.Coin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.security.InvalidParameterException;

@Component
public class CoinValidator {

    @Value("${coin-symbol-length:6}")
    private int coinSymbolLength;

    public boolean isValid(String coinSymbol) {
        if((coinSymbol.length() < 2) || (coinSymbol.length() > coinSymbolLength) ){
            return false;
        }
        return coinSymbol.matches("[a-zA-Z]+");
    }

    public Coin toEntity(String coinSymbol) throws Exception {
        if (this.isValid(coinSymbol)) {
            return new Coin(coinSymbol.toUpperCase());
        }
        throw new Exception("invalid coinSymbol");
    }

}
