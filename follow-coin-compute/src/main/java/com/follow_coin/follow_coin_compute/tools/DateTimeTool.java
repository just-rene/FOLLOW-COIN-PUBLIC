package com.follow_coin.follow_coin_compute.tools;

import com.follow_coin.follow_coin_compute.entities.CoinPrice;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateTimeTool {


    public LocalDateTime extractDateTime(CoinPrice c) {
        String datetimeString = c.getCoinPriceKey().getDatetime();
        return LocalDateTime.parse(datetimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
    }
}
