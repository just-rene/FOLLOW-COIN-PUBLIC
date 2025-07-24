package com.follow_coin.follow_coin_compute.events.unit_tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.follow_coin.follow_coin_compute.entities.CoinPrice;
import com.follow_coin.follow_coin_compute.entities.CoinPriceKey;
import com.follow_coin.follow_coin_compute.events.EventBusIn;
import com.follow_coin.follow_coin_compute.repos.CoinPriceRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
class EventBusInTest {


    @Mock
    public CoinPriceRepo coinPriceRepo;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    public EventBusIn eventBusIn;


    @BeforeEach
    void setUp() {}

    @AfterEach
    void tearDown() {}

    @Test
    void test_repo() {}

    @Test
    void test_testcontainers_are_available_and_save_correctly() {}

    @Test
    void test_well_formed_message() throws JsonProcessingException {

        //test if standard behavior works
        // -> must compute coinPriceDifferenceEvent correctly

        //set up
        CoinPriceKey coinPriceKey = new CoinPriceKey("BTC", "2025-07-10T14:29:00.000Z");
        CoinPrice coinPrice = new CoinPrice(coinPriceKey, 100.0,false);

        Mockito.when(coinPriceRepo.save(coinPrice)).thenReturn(Mono.just(coinPrice));

        //generate a event 1 minute before
        String datetimeString = coinPrice.getCoinPriceKey().getDatetime();
        LocalDateTime localDateTimeCurrentEvent = LocalDateTime.parse(datetimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        LocalDateTime localDateTimeLastEvent = localDateTimeCurrentEvent.minusMinutes(1);

        CoinPriceKey coinPriceKey2 = new CoinPriceKey("BTC", "2025-07-10T14:30:00.000Z");
        CoinPrice coinPrice2 = new CoinPrice(coinPriceKey2, 102.0,false);

        Mockito.when(coinPriceRepo.getCoinPriceByDateTimeAndSymbol(localDateTimeLastEvent.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")), "BTC"))
                .thenReturn(Mono.just(coinPrice2));

        //when
        var res = eventBusIn.computeCoinPriceDifference(coinPrice).block();

        assertEquals(2, res.getDifferenceAbsolute());
        assertEquals("BTC", res.get_id().getSymbol());

    }


    @Test
    void test_first_coin_price_arrives_arrives() throws JsonProcessingException {

        //test what happens when the first coin price event arrives

        //set up
        CoinPriceKey coinPriceKey = new CoinPriceKey("BTC", "2025-07-10T14:30:00.000Z");
        CoinPrice coinPrice = new CoinPrice(coinPriceKey, 100.0,false);

        Mockito.when(coinPriceRepo.save(coinPrice)).thenReturn(Mono.just(coinPrice));

        //generate a event 1 minute before
        String datetimeString = coinPrice.getCoinPriceKey().getDatetime();
        LocalDateTime localDateTimeCurrentEvent = LocalDateTime.parse(datetimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        LocalDateTime localDateTimeLastEvent = localDateTimeCurrentEvent.minusMinutes(1);


        Mockito.when(coinPriceRepo.getCoinPriceByDateTimeAndSymbol(localDateTimeLastEvent.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")), "BTC"))
                .thenReturn(Mono.empty());

        Mockito.when(coinPriceRepo.getCoinPriceBefore(localDateTimeCurrentEvent.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")), "BTC"))
                .thenReturn(Mono.empty());

        //when
        var res = eventBusIn.computeCoinPriceDifference(coinPrice).block();

        //then
        //if only one entry exists it should just be ignored, no exceptions should be thrown
        assertNull(res);

    }

    @Disabled
    @Test
    void test_interpolation(){
        //TODO
    }
}