package com.follow_coin.follow_coin_coordinator.dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CoinPriceDifferenceEvent extends EventWrapper {

        @Id
        private CoinPriceDifferenceEventKey _id;
        private BigDecimal differenceAbsolute;
        private boolean interpolated = false;
}
