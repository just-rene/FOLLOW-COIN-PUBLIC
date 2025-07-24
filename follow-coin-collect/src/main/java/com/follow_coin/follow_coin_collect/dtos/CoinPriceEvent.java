package com.follow_coin.follow_coin_collect.dtos;

import com.follow_coin.follow_coin_collect.entities.CoinPrice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CoinPriceEvent extends EventWrapper {

    private CoinPrice coinPrice;
}
