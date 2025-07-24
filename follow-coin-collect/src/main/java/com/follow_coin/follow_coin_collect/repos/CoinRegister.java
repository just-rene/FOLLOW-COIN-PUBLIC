package com.follow_coin.follow_coin_collect.repos;

import com.follow_coin.follow_coin_collect.entities.Coin;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoinRegister extends ReactiveMongoRepository<Coin, String> {

}
