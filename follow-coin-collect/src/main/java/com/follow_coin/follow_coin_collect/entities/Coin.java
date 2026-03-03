package com.follow_coin.follow_coin_collect.entities;

import com.follow_coin.follow_coin_collect.dtos.CoinDto;
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
public class Coin {

    @Id
    private String symbol;


    public CoinDto toDto() {
        return new CoinDto(this);
    }

}
