package com.follow_coin.follow_coin_collect.conf;


import com.follow_coin.follow_coin_collect.services.RegisterService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitBean implements InitializingBean {

    @Autowired
    private RegisterService registerService;

    @Override
    public void afterPropertiesSet() {
        registerStartCoins();
    }

    private void registerStartCoins()  {
        registerService.count().subscribe( x -> {
            if (x == 0 ){
                registerService.registerCoin("BTC").subscribe();
                registerService.registerCoin("ADA").subscribe();
                registerService.registerCoin("DOGE").subscribe();
                registerService.registerCoin("ETH").subscribe();
            }
        });

    }

}
