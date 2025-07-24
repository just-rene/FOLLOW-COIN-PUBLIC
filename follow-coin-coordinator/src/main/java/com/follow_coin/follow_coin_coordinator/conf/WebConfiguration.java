package com.follow_coin.follow_coin_coordinator.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;



@Configuration
public class WebConfiguration implements WebMvcConfigurer {


    @Value("${cors.pattern}")
    String cors;


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        System.err.println(cors);
        registry.addMapping("/**").allowedOriginPatterns(cors);
    }
}
