package com.follow_coin.follow_coin_compute.events.integragtions_tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.follow_coin.follow_coin_compute.dtos.CoinPriceDifferenceEvent;
import com.follow_coin.follow_coin_compute.dtos.CoinPriceEvent;
import com.follow_coin.follow_coin_compute.entities.CoinPrice;
import com.follow_coin.follow_coin_compute.entities.CoinPriceKey;
import com.follow_coin.follow_coin_compute.events.EventBusIn;
import com.follow_coin.follow_coin_compute.repos.CoinPriceRepo;
import io.awspring.cloud.autoconfigure.core.AwsAutoConfiguration;
import io.awspring.cloud.autoconfigure.sqs.SqsAutoConfiguration;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Integration Testing against full implementation with repositories
 */


@ActiveProfiles("integration")
@SpringBootTest
@TestPropertySource(locations = "classpath:application-integration.properties")
@EnableAutoConfiguration(exclude = {
        AwsAutoConfiguration.class,
        SqsAutoConfiguration.class
})
class EventBusInIntegrationsTest {

    @MockitoBean
    private SqsTemplate sqsTemplate;

    @Autowired
    public EventBusIn eventBusIn;

    @Autowired
    private CoinPriceRepo coinPriceRepo;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0").withExposedPorts(27017);

    @DynamicPropertySource
    static void containersProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.host", mongoDBContainer::getHost);
        registry.add("spring.data.mongodb.port", mongoDBContainer::getFirstMappedPort);
        mongoDBContainer.start();

    }

    @BeforeEach
    void setUp() {
        coinPriceRepo.deleteAll().block();
    }

    @AfterEach
    void tearDown() {
        coinPriceRepo.deleteAll().block();
    }


    @Test
    void integration_does_save_work() throws JsonProcessingException {

        //set up
        CoinPriceKey coinPriceKey = new CoinPriceKey("COIN", "2025-07-11T11:11:00.000Z");
        CoinPrice coinPrice = new CoinPrice(coinPriceKey, 1001.0,false);

        //when
        eventBusIn.computeCoinPriceDifference(coinPrice).block();

        //then
        var result = coinPriceRepo.findOne(Example.of(coinPrice)).block();

        assertNotNull(result);

        assertEquals(result.getPrice(), coinPrice.getPrice(), "CoinPriceEvents should be equal!");
        assertEquals(result.getCoinPriceKey().getDatetime(), coinPrice.getCoinPriceKey().getDatetime(), "CoinPriceEvents should be equal!");
        assertEquals(result.getCoinPriceKey().getSymbol(), coinPrice.getCoinPriceKey().getSymbol(), "CoinPriceEvents should be equal!");
    }
//
    @Test
    void integration_does_difference_event_get_computed_correctly() throws JsonProcessingException {

        //set up

        //first event
        CoinPriceKey coinPriceKey = new CoinPriceKey("COIN", "2025-07-11T12:00:00.000Z");
        CoinPrice coinPrice = new CoinPrice(coinPriceKey, 10.0,false);

        //second event
        CoinPriceKey coinPriceKey2 = new CoinPriceKey("COIN", "2025-07-11T12:01:00.000Z");
        CoinPrice coinPrice2 = new CoinPrice(coinPriceKey2, 15.0,false);

        //when
        eventBusIn.computeCoinPriceDifference(coinPrice).block();
        CoinPriceDifferenceEvent coinPriceDifferenceEvent = eventBusIn.computeCoinPriceDifference(coinPrice2).block();

        //then
        assertNotNull(coinPriceDifferenceEvent, "no difference event has been computed");
        assertEquals(10.0 - 15.0, coinPriceDifferenceEvent.getDifferenceAbsolute(), "difference was not computed correctly!");
    }

    @Test
    void integration_does_not_compute_difference_event_between_different_coins() throws JsonProcessingException {

        //set up

        //first event
        CoinPriceKey coinPriceKey = new CoinPriceKey("COIN_A", "2025-07-11T12:00:00.000Z");
        CoinPrice coinPrice = new CoinPrice(coinPriceKey, 10.0,false);

        //second event
        CoinPriceKey coinPriceKey2 = new CoinPriceKey("COIN_B", "2025-07-11T12:01:00.000Z");
        CoinPrice coinPrice2 = new CoinPrice(coinPriceKey2, 15.0,false);

        //when
        eventBusIn.computeCoinPriceDifference(coinPrice).block();
        CoinPriceDifferenceEvent coinPriceDifferenceEvent = eventBusIn.computeCoinPriceDifference(coinPrice2).block();

        //then
        assertNull(coinPriceDifferenceEvent, "must be null, it might be that the system " +
                "is computing CoinPriceDifferenceEvent between different coins, which is wrong!");
    }


    @Test
    void integration_repos_must_not_save_duplicates() throws JsonProcessingException {

        //set up
        //first event
        CoinPriceKey coinPriceKey = new CoinPriceKey("COIN_A", "2025-07-11T11:59:00.000Z");
        CoinPrice coinPrice = new CoinPrice(coinPriceKey, 30.0, false);

        //second event
        UUID uu = UUID.randomUUID();
        CoinPriceKey coinPriceKey2 = new CoinPriceKey("COIN_A", "2025-07-11T12:00:00.000Z");
        CoinPrice coinPrice2 = new CoinPrice(coinPriceKey2, 10.0, false);

        //third event (duplicate of second event)
        CoinPriceKey coinPriceKey3 = new CoinPriceKey("COIN_A", "2025-07-11T12:00:00.000Z");
        CoinPrice coinPrice3 = new CoinPrice(coinPriceKey3, 10.0, false);

        //fourth event (duplicate of second event with little change)
        CoinPriceKey coinPriceKey4 = new CoinPriceKey("COIN_A", "2025-07-11T12:00:00.000Z");
        CoinPrice coinPrice4 = new CoinPrice(coinPriceKey4, 10.0, true);


        //when
        coinPriceRepo.save(coinPrice).block();
        coinPriceRepo.save(coinPrice2).block();
        coinPriceRepo.save(coinPrice3).block();
        coinPriceRepo.save(coinPrice4).block();

        var savedObjects = coinPriceRepo.findAll().collectList().block();

        //then
        assertNotNull(savedObjects);
        assertEquals(2, savedObjects.size(), "to much objects, -> probably saves duplicates!");

        assertEquals("2025-07-11T11:59:00.000Z", savedObjects.getFirst().getCoinPriceKey().getDatetime());
        assertEquals("2025-07-11T12:00:00.000Z", savedObjects.getLast().getCoinPriceKey().getDatetime());
    }

    @Test
    void integration_already_saved_coinPrices_get_blocked_by_checkIfCoinPriceAlreadyExists(){
        //1 event
        UUID uu = UUID.randomUUID();
        CoinPriceKey coinPriceKey = new CoinPriceKey("COIN_A", "2025-07-11T12:00:00.000Z");
        CoinPrice coinPrice = new CoinPrice(coinPriceKey, 10.0, false);

        coinPriceRepo.save(coinPrice).block();

        //2 event (duplicate of 1 event)
        CoinPriceKey coinPriceKey2 = new CoinPriceKey("COIN_A", "2025-07-11T12:00:00.000Z");
        CoinPrice coinPrice2 = new CoinPrice(coinPriceKey2, 10.0, false);
        CoinPriceEvent coinPriceEvent2 = new CoinPriceEvent(coinPrice2);


        //3 event (no duplicate -> must pass)
        CoinPriceKey coinPriceKey3 = new CoinPriceKey("COIN_A", "2025-07-11T13:00:00.000Z");
        CoinPrice coinPrice3 = new CoinPrice(coinPriceKey3, 10.0, false);
        CoinPriceEvent coinPriceEvent3 = new CoinPriceEvent(coinPrice3);


        // must be true and therefore be blocked
        assertTrue(eventBusIn.checkIfCoinPriceAlreadyExists(coinPriceEvent2).block().booleanValue());

        // must be false and let through
        assertFalse(eventBusIn.checkIfCoinPriceAlreadyExists(coinPriceEvent3).block().booleanValue());

    }







}