package com.follow_coin.follow_coin_compute.conf;


import com.follow_coin.follow_coin_compute.entities.CoinPrice;
import com.mongodb.reactivestreams.client.MongoClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

@Configuration
public class InitBean implements InitializingBean {

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    //TODO create time series collection
    @Override
    public void afterPropertiesSet() throws Exception {
//        reactiveMongoTemplate.changeStream("coinPrice", ChangeStreamOptions.empty(), CoinPrice.class)
//                .subscribe(x-> {
//                    System.err.println(x.getBody().getPrice());
//                });
    }
}
