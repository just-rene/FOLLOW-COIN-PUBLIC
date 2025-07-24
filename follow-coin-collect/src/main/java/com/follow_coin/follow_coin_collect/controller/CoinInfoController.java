package com.follow_coin.follow_coin_collect.controller;


import com.follow_coin.follow_coin_collect.entities.Coin;
import com.follow_coin.follow_coin_collect.entities.CoinPrice;
import com.follow_coin.follow_coin_collect.repos.CoinPriceRepo;
import com.follow_coin.follow_coin_collect.services.CollectService;
import com.follow_coin.follow_coin_collect.services.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class CoinInfoController {

    @Autowired
    private CollectService collectService;

    @Autowired
    private RegisterService registerService;

    @Autowired
    private ReactiveMongoTemplate template;

    //this controller is pinged every n seconds by an event controller (e.g. aws Event Bridge)
    @GetMapping("/collect")
    public ResponseEntity<Mono<Boolean>> collectCoinData() {
        //return ResponseEntity.status(200).body(Mono.just(true));
        return ResponseEntity.status(200).body(collectService.collectCoinData());
    }

    //TODO post mapping
    @GetMapping("/register/{coinSymbol}")
    public Mono<ResponseEntity<Coin>> registerNewCoin(@PathVariable String coinSymbol) throws Exception {
        return registerService.registerCoin(coinSymbol)
                .map(coin -> {
                            if (!(coin.getSymbol() == null)) {
                                return ResponseEntity.status(HttpStatus.OK).body(coin);
                            } else {
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(coin);
                            }
                        }
                );
    }





}
