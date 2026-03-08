package com.follow_coin.follow_coin_compute.events.unit_tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.follow_coin.follow_coin_compute.computation.CoinPriceDifferenceAlgorithm;
import com.follow_coin.follow_coin_compute.computation.dtos.CoinPriceDifferenceResult;
import com.follow_coin.follow_coin_compute.computation.dtos.CoinPricePair;
import com.follow_coin.follow_coin_compute.computation.dtos.ComputationResult;
import com.follow_coin.follow_coin_compute.dtos.CoinPriceData;
import com.follow_coin.follow_coin_compute.entities.CoinPrice;
import com.follow_coin.follow_coin_compute.entities.CoinPriceKey;
import com.follow_coin.follow_coin_compute.events.visitor.ComputationVisitorImpl;
import com.follow_coin.follow_coin_compute.repos.CoinPriceRepo;
import com.follow_coin.follow_coin_compute.tools.DateTimeTool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@ExtendWith({MockitoExtension.class})
@TestPropertySource(locations = "classpath:application-test.properties")
class ComputationVisitorTest {


    @Mock
    public CoinPriceRepo coinPriceRepo;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private DateTimeTool dateTimeTool;

    @InjectMocks
    public ComputationVisitorImpl computationVisitor;


    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void test_repo() {
    }

    @Test
    void test_testcontainers_are_available_and_save_correctly() {
    }

    @Test
    void test_well_formed_message() throws JsonProcessingException {

        //test if standard behavior works
        // -> must compute coinPriceDifferenceEvent correctly

        //set up
        CoinPriceKey coinPriceKey = new CoinPriceKey("BTC", "2025-07-10T14:29:00.000Z");
        CoinPrice coinPrice = new CoinPrice(coinPriceKey, 100.0, false);


        //generate a event 1 minute before
        String datetimeString = coinPrice.getCoinPriceKey().getDatetime();
        LocalDateTime localDateTimeCurrentEvent = LocalDateTime.parse(datetimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        LocalDateTime localDateTimeLastEvent = localDateTimeCurrentEvent.minusMinutes(1);

        CoinPriceKey coinPriceKey2 = new CoinPriceKey("BTC", "2025-07-10T14:30:00.000Z");
        CoinPrice coinPrice2 = new CoinPrice(coinPriceKey2, 102.0, false);

        CoinPriceData coinPriceData = new CoinPriceData(new CoinPricePair(coinPrice, coinPrice2));

        //when
        ComputationResult res = new CoinPriceDifferenceAlgorithm().compute(coinPriceData);
        var x = (CoinPriceDifferenceResult) res;

        assertEquals(2, x.getCoinPriceDifferenceEvent().getDifferenceAbsolute());
        assertEquals("BTC", x.getCoinPriceDifferenceEvent().get_id().getSymbol());

    }

    @Test
    void test_first_coin_price_arrives() throws JsonProcessingException {

        //test what happens when the first coin price event arrives
        //must be ignored, no exceptions must be thrown

        //set up
        CoinPriceKey coinPriceKey = new CoinPriceKey("BTC", "2025-07-10T14:30:00.000Z");
        CoinPrice currentCoinPrice = new CoinPrice(coinPriceKey, 100.0, false);

        //generate a event 1 minute before
        String datetimeString = currentCoinPrice.getCoinPriceKey().getDatetime();
        LocalDateTime localDateTimeCurrentEvent = LocalDateTime.parse(datetimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        LocalDateTime localDateTimeLastEvent = localDateTimeCurrentEvent.minusMinutes(1);

        Mockito.when(coinPriceRepo.getCoinPriceByDateTimeAndSymbol(localDateTimeLastEvent.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")), "BTC"))
                .thenReturn(Mono.empty());

        Mockito.when(dateTimeTool.extractDateTime(currentCoinPrice))
                .thenReturn(localDateTimeCurrentEvent);

        Mockito.when(dateTimeTool.getDateTimeBefore(localDateTimeCurrentEvent)).thenReturn(localDateTimeLastEvent.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));

        //then
        //if only one entry exists it should just be ignored, no exceptions should be thrown
        assertDoesNotThrow(() -> {
            //when
            computationVisitor.computeCoinPriceDifference(currentCoinPrice);
        });

    }

}