package com.follow_coin.follow_coin_compute.repos;


import com.follow_coin.follow_coin_compute.entities.CoinPrice;
import com.follow_coin.follow_coin_compute.entities.CoinPriceKey;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CoinPriceRepo extends ReactiveMongoRepository<CoinPrice, CoinPriceKey> {


    @Aggregation(pipeline = {
            "{ '$match': { $and: [ { '_id.datetime' : { $lt : ?0 } } ,  { '_id.symbol': {$eq: ?1} }   ] } }",
            "{ '$sort' : { '_id.datetime' : -1 } }",
            "{ '$limit' : 1 }"
    })
    Mono<CoinPrice> getCoinPriceBefore(String localDateTimeString, String symbol);

    @Aggregation(pipeline = {
            "{ '$match': { $and: [ { '_id.datetime' : { $lt : ?0 } } ,  { '_id.symbol': {$eq: ?1} }   ] } }",
            "{ '$sort' : { '_id.datetime' : -1 } }",
            "{ '$limit' : 1 }"
    })
    Flux<CoinPrice> getCoinPriceEventBeforeFlux(String localDateTimeString, String symbol);

    @Aggregation(pipeline = {
            "{ '$match': { $and: [ { '_id.datetime' : { $eq : ?0 }  } ,  { '_id.symbol': {$eq: ?1}}   ] } }",
            "{ '$limit' : 1 }"
    })
    Mono<CoinPrice> getCoinPriceByDateTimeAndSymbol(String localDateTimeString, String symbol);

    @Aggregation(pipeline = {
            "{ '$match': {  '_id.datetime' : { $eq : ?0 }   } }",
    })
    Flux<CoinPrice> getCoinPriceEventByDateTime(String localDateTimeString);


    @Aggregation(pipeline = {
            "{ '$match': { $and: [ { '_id.datetime' : { $eq : ?0 }  } ,  { '_id.symbol': {$eq: ?1}}   ] } }",
            "{ '$limit' : 1 }"
    })
    Flux<CoinPrice> getCoinPriceEventByDateTimeAndSymbol2(String localDateTimeString, String symbol);


}
