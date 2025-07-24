package com.follow_coin.follow_coin_collect.services;

import com.follow_coin.follow_coin_collect.entities.Coin;
import com.follow_coin.follow_coin_collect.repos.CoinRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


/**
 *  coins register with this service will be
 *  part of the data collection process in @{@link CollectService}
 */

@Service
public class RegisterService {

    @Autowired
    private CoinRegister coinRegister;

    @Value("${coin-symbol-length:6}")
    private int coinSymbolLength;

    public Mono<Coin> registerCoin(String coinSymbol)  {

        if (isValidCoin(coinSymbol)) {
            return coinRegister.save(new Coin(coinSymbol.toUpperCase()));
        } else {
            return Mono.just(new Coin());
        }
    }

    private boolean isValidCoin(String coinSymbol) {
        //System.err.println(coinSymbolLength);
        if((coinSymbol.length() < 2) || (coinSymbol.length() > coinSymbolLength) ){
            return false;
        }
        if(!coinSymbol.matches("[a-zA-Z]+")){
            return false;
        }
        return true;
    }

    public Mono<Long> count(){
        return coinRegister.count();
    }

}
