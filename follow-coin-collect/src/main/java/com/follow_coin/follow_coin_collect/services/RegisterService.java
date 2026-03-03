package com.follow_coin.follow_coin_collect.services;

import com.follow_coin.follow_coin_collect.dtos.CoinDto;
import com.follow_coin.follow_coin_collect.entities.Coin;
import com.follow_coin.follow_coin_collect.repos.CoinRegister;
import com.follow_coin.follow_coin_collect.tools.CoinValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


/**
 * coins register with this service will be
 * part of the data collection process in @{@link CollectService}
 */

@Service
public class RegisterService {

    @Autowired
    private CoinRegister coinRegister;

    @Autowired
    private CoinValidator coinValidator;

    public Mono<CoinDto> registerCoin(String coinSymbol) {
        if (coinValidator.isValid(coinSymbol)) {
            return coinRegister.save(new Coin(coinSymbol)).map(Coin::toDto);
        }
        return Mono.error(new Exception("invalid coin symbol"));
    }

    public Mono<Long> count() {
        return coinRegister.count();
    }

}
