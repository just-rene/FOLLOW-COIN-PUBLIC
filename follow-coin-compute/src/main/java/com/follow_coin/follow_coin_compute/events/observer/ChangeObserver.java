package com.follow_coin.follow_coin_compute.events.observer;

import com.follow_coin.follow_coin_compute.entities.CoinPrice;
import com.follow_coin.follow_coin_compute.events.visitor.ComputationVisitor;
import org.springframework.data.mongodb.core.ChangeStreamEvent;

public interface ChangeObserver {

    void watchForChanges();

    void doOnChange(ChangeStreamEvent<CoinPrice> event) throws Exception;

    void accept(ComputationVisitor computationVisitor, CoinPrice coinPrice);

}
