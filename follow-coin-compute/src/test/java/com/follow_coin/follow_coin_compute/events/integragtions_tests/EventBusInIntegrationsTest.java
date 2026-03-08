package com.follow_coin.follow_coin_compute.events.integragtions_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;


/**
 * integration tests
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

    @Autowired
    private ObjectMapper objectMapper;

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
    void integration_does_save_events() {

        //set up
        CoinPriceKey coinPriceKey = new CoinPriceKey("COIN", "2025-07-11T11:11:00.000Z");
        CoinPrice coinPrice = new CoinPrice(coinPriceKey, 1001.0, false);
        CoinPriceEvent coinPriceEvent = new CoinPriceEvent(coinPrice);

        //when
        //then
        StepVerifier.create(eventBusIn.store(coinPriceEvent))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void integration_blocks_malformed_events() {

        //set up
        CoinPriceKey coinPriceKey = new CoinPriceKey("COIN", "2025-07-11T11:11:00.000Z");
        CoinPrice coinPrice = new CoinPrice(coinPriceKey, 1001.0, false);
        CoinPriceEvent coinPriceEvent = new CoinPriceEvent(coinPrice);

        //when
        //then+
        StepVerifier.create(eventBusIn.store(null))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void integration_repos_must_not_save_duplicates() {

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
        StepVerifier.create(coinPriceRepo.saveAll(List.of(coinPrice, coinPrice2, coinPrice3, coinPrice4)))
                .expectNextCount(4)
                .expectComplete()
                .verify();

        StepVerifier.create(coinPriceRepo.findAll())
                .expectNextCount(2)
                .verifyComplete();
    }

}