package com.follow_coin.follow_coin_coordinator.repos;

import com.follow_coin.follow_coin_coordinator.dtos.CoinPriceDifferenceEvent;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface CoinPriceDifferenceEventRepo extends ReactiveMongoRepository<CoinPriceDifferenceEvent, String> {

    @Aggregation(pipeline = {
            "{ $addFields: { newField: 'lastEvents'} }",
            "{ $sort: { '_id.endDateTime': -1 } }",
            "{ '$limit' : 1 }"
    })
    Flux<CoinPriceDifferenceEvent> getLatestEvent();

    @Aggregation(pipeline = {
            "{ '$match': {  '_id.symbol': {$eq: ?0} } }",
            "{ '$sort': { '_id.endDateTime': -1 } }",
            "{ '$limit' : ?1 }"
    })
    Flux<CoinPriceDifferenceEvent> getLatestNEventForCoin(String coin, int n);

    @Aggregation(pipeline = {
            "{ $addFields: { newField: 'lastEvents'} }",
            "{ $sort: { '_id.endDateTime': -1 } }",
            "{ '$limit' : ?0 }"
    })
    Flux<CoinPriceDifferenceEvent> getLatestNEvent(int n);

    @Aggregation(pipeline = {
            "{ '$match': {  '_id.endDateTime' : { $eq : ?0 }   } }",
    })
    Flux<CoinPriceDifferenceEvent> getCoinPriceEventByDateTime(String localDateTimeString);

}
