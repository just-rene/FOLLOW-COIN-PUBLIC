package com.follow_coin.follow_coin_collect.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CoinPrice {

    @Id
    private CoinPriceKey coinPriceKey;
    private double price;//in USD



}
