package com.follow_coin.follow_coin_compute.rest;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventController {

//
//    @Autowired
//    private CoinPriceEventRepo coinPriceEventRepo;
//
//    @GetMapping
//    public Flux<CoinPriceEvent> getCoinPriceEvent() {
//        return coinPriceEventRepo.findAll(Sort.by(Sort.Direction.DESC,"_id.coinPriceKey.datetime"));
//    }
//
//
//    @GetMapping(path = "/ticker-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<CoinPriceEvent> streamFlux() throws Exception {
//        //upcoming feature
//
//
//        return Flux.interval(Duration.ofSeconds(10)).flatMap(x -> {
////            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
////            String currentPrincipalName = authentication.getName();
////            System.err.println(currentPrincipalName);
//
//            String time = LocalDateTime.now(ZoneOffset.UTC ).withSecond(0).withNano(0).minusMinutes(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
//            System.err.println(time);
//
//            return coinPriceEventRepo.getCoinPriceEventByDateTime(time);
//        });
//    }

//    @Autowired
//    ReactiveMongoTemplate template;
//
//    @GetMapping(path = "/ticker-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<ChangeStreamEvent<CoinPriceEvent>> streamFlux() throws Exception {
//        //upcoming feature
//
//
//    }


}
