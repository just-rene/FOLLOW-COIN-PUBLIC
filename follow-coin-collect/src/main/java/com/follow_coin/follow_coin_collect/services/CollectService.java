package com.follow_coin.follow_coin_collect.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.follow_coin.follow_coin_collect.dtos.CoinPriceEvent;
import com.follow_coin.follow_coin_collect.entities.Coin;
import com.follow_coin.follow_coin_collect.entities.CoinPrice;
import com.follow_coin.follow_coin_collect.entities.CoinPriceKey;
import com.follow_coin.follow_coin_collect.repos.CoinPriceRepo;
import com.follow_coin.follow_coin_collect.repos.CoinRegister;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.w3c.dom.events.Event;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


/**
 * fetches data from coinmarketcap and saves them in the db
 * only for coins registered via @{@link RegisterService}
 */
@Service
public class CollectService {

    @Value("${api-key}")
    private String apiKey;

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
    public Mono<Boolean> collectCoinData() {

        List<Coin> requiredCoinSymbols = coinRegister.findAll().collectList().block();

        String uri = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";

        var response = client.build().get().uri(uri)
                .header(HttpHeaders.ACCEPT, "application/json")
                .header("X-CMC_PRO_API_KEY", apiKey).retrieve().bodyToMono(String.class);

        return response.map(y -> {
            try {
                var data = mapper.readTree(y);
                List<CoinPrice> prices = this.extractRelevantCoinData(data,requiredCoinSymbols);

                var res = coinPriceRepo.saveAll(prices);
                this.publishLatestData(res);
                return true;
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private List<CoinPrice> extractRelevantCoinData(JsonNode data, List<Coin> requiredCoinSymbols) {

        var coinPrices = new ArrayList<CoinPrice>();

        for (Coin coinSymbol : requiredCoinSymbols) {
            for (var d : data.findPath("data")) {
                //System.err.println(d);
                if (d.get("symbol").asText().equals(coinSymbol.getSymbol())) {

                    String date = d.get("last_updated").asText();
                    double price = d.get("quote").get("USD").get("price").asDouble();

                    CoinPrice coinPrice = new CoinPrice(new CoinPriceKey(d.get("symbol").asText(), date), price);
                    coinPrices.add(coinPrice);
                    break;
                }
            }
        }
        return coinPrices;
    }

    public void publishLatestData(Flux<CoinPrice> coinPrice) {

        coinPrice.doOnNext(x -> {
            String data = "";
            try {
                CoinPriceEvent coinPriceEvent =  new CoinPriceEvent(x);

                data = mapper.writeValueAsString(coinPriceEvent);
            } catch (JsonProcessingException e) {
                logger.error("could not deserialize value!");
            }
            logger.info(data);
            sqsTemplate.sendAsync("coin-price-event", data);
        }).subscribe();
    }
}
