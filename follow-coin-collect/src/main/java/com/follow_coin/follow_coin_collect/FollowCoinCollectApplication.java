package com.follow_coin.follow_coin_collect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FollowCoinCollectApplication {

	public static void main(String[] args) {
		SpringApplication.run(FollowCoinCollectApplication.class, args);
	}



}
