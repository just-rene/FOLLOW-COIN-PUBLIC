package com.follow_coin.follow_coin_coordinator.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.UUID;


@Document
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CoinPrice {

    private CoinPriceKey coinPriceKey;
    private BigDecimal price;//in USD
    private UUID uuid; //for message transmission via sqs
}
