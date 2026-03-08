package com.follow_coin.follow_coin_compute.events.visitor;

import com.follow_coin.follow_coin_compute.entities.CoinPrice;
import reactor.core.publisher.Mono;

public interface ComputationVisitor {
    //modified visitor pattern -> just for separation of concerns
    Mono<String> visit(CoinPrice coinPriceCurrent);
}
