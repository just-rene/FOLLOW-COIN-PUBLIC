package com.follow_coin.follow_coin_compute.conf;

import com.follow_coin.follow_coin_compute.entities.CoinPrice;
import com.mongodb.reactivestreams.client.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ConfBean {

    @Bean
    public WebClient.Builder lbWebClient() {
        return WebClient.builder();
    }


}
