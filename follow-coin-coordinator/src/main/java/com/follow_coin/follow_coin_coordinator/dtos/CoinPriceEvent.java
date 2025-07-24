package com.follow_coin.follow_coin_coordinator.dtos;

import com.follow_coin.follow_coin_coordinator.entities.CoinPrice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CoinPriceEvent extends EventWrapper {

    @Id
    private CoinPrice coinPrice;


}
