package com.follow_coin.follow_coin_collect.repos;

import com.follow_coin.follow_coin_collect.entities.CoinPrice;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@Repository
public interface CoinPriceRepo extends ReactiveMongoRepository<CoinPrice,String> {

    @Query(value = "  {   '_id.datetime': {'$gte' : ?0 , '$lt':  ?1   } }")
    Flux<CoinPrice> getCoinPricesBetween(String timestampStart, String timestampEnd);


    @Query(value = "{'_id.datetime':  ?0  }")
    Flux<CoinPrice> getCoinPricesForDate(String timestampStart);
}
