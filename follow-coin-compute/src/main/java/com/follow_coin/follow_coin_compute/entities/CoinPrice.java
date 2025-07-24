package com.follow_coin.follow_coin_compute.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.UUID;


@Document
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CoinPrice {

    @Id
    private CoinPriceKey coinPriceKey;
    private double price;//in USD
    private boolean interpolated = false;
}
