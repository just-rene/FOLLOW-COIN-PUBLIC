package com.follow_coin.follow_coin_collect.conf;


import com.follow_coin.follow_coin_collect.services.RegisterService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Stream;

@Configuration
public class InitBean implements InitializingBean {

    @Autowired
    private RegisterService registerService;

    @Override
    public void afterPropertiesSet() {
        registerStartCoins();
    }

    private void registerStartCoins() {
        Stream
                .of(StarterCoins.values())
                .forEach(starterCoin -> registerService.registerCoin(starterCoin.val).subscribe());
    }


    private enum StarterCoins {

        BTC("BTC"),
        ADA("ADA"),
        DOGE("DOGE"),
        ETH("ETH");

        public final String val;

        StarterCoins(String val) {
            this.val = val;
        }
    }

}
