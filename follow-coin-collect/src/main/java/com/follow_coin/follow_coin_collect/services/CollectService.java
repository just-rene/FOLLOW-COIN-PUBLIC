package com.follow_coin.follow_coin_collect.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.follow_coin.follow_coin_collect.repos.CoinPriceRepo;
import com.follow_coin.follow_coin_collect.repos.CoinRegister;
import com.follow_coin.follow_coin_collect.services.extractor.CoinDataExtractorVisitor;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


/**
 * fetches data from coinmarketcap and saves them in the db
 * only for coins registered via @{@link RegisterService}
 */
@Service
public class CollectService {

    private final String URI = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";


    @Value("${api-key}")
    private String API_KEY;

    @Autowired
    private WebClient.Builder client;

    @Autowired
    private SqsTemplate sqsTemplate;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private CoinRegister coinRegister;

    @Autowired
    private CoinPriceRepo coinPriceRepo;

    private static final Logger logger = LoggerFactory.getLogger(CollectService.class);

    //will be pinged every 60 seconds by EventBridge
    public Mono<String> collectCoinData() {
        return client.build().get().uri(URI)
                .header(HttpHeaders.ACCEPT, "application/json")
                .header("X-CMC_PRO_API_KEY", API_KEY)
                .retrieve()
                .bodyToMono(String.class);
    }


    public CollectService accept(CoinDataExtractorVisitor visitor) {
        visitor.visit(this);
        return this;
    }


}
