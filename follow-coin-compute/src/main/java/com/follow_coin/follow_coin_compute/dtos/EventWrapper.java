package com.follow_coin.follow_coin_compute.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
abstract class EventWrapper {
        private UUID uuid = UUID.randomUUID(); //for message transmission via sqs
}
