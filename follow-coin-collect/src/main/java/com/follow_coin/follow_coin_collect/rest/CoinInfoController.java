package com.follow_coin.follow_coin_collect.rest;


import com.follow_coin.follow_coin_collect.dtos.CoinDto;
import com.follow_coin.follow_coin_collect.entities.Coin;
import com.follow_coin.follow_coin_collect.entities.CoinPrice;
import com.follow_coin.follow_coin_collect.entities.CoinPriceKey;
import com.follow_coin.follow_coin_collect.repos.CoinPriceRepo;
import com.follow_coin.follow_coin_collect.services.CollectService;
import com.follow_coin.follow_coin_collect.services.RegisterService;
import com.follow_coin.follow_coin_collect.services.extractor.CoinDataExtractorVisitor;
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
    private CoinPriceRepo coinPriceRepo;

    @Autowired
    private CollectService collectService;

    @Autowired
    private RegisterService registerService;

    @Autowired
    private ReactiveMongoTemplate template;

    @Autowired
    private CoinDataExtractorVisitor coinDataExtractorVisitor;

    //this controller is pinged every n seconds by an event controller (e.g. aws Event Bridge)
    @GetMapping("/collect")
    public ResponseEntity<Mono<Boolean>> collectCoinData() {
        collectService.accept(coinDataExtractorVisitor);
        return ResponseEntity.status(200).body(Mono.just(true));
    }

    //TODO post mapping
    @GetMapping("/register/{coinSymbol}")
    public Mono<ResponseEntity<CoinDto>> registerNewCoin(@PathVariable String coinSymbol) throws Exception {
        return registerService.registerCoin(coinSymbol)
                .map( coinDto -> ResponseEntity.status(HttpStatus.OK).body(coinDto))
                .onErrorResume( x ->
                        Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body((CoinDto)new CoinDto(new Coin()).setInfo(x.getMessage()))));
    }

    @GetMapping("/coin-prices/{startDate}/{endDate}")
    public Flux<ResponseEntity<CoinPrice>> getCoinPrices(@PathVariable String startDate, @PathVariable String endDate) {
        return coinPriceRepo.getCoinPricesBetween(startDate, endDate)
                .map( coinPrice -> ResponseEntity.status(HttpStatus.OK).body(coinPrice))
                .onErrorResume( x-> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CoinPrice())));
    }

    @GetMapping("/coin-prices/{date}")
    public Flux<ResponseEntity<CoinPrice>> getCoinPrices(@PathVariable String date) {
        return coinPriceRepo.getCoinPricesForDate(date)
                .map( coinPrice -> ResponseEntity.status(HttpStatus.OK).body(coinPrice))
                .onErrorResume( x-> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CoinPrice())));
    }
}
