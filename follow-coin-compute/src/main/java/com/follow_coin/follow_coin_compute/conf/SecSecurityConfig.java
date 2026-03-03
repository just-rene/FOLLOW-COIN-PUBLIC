package com.follow_coin.follow_coin_compute.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecSecurityConfig {


//    @Value("${development}")
//    boolean isDevelopment;

    @Value("${cred.user}")
    public String user;

    @Value("${cred.pass}")
    public String pass;

//    @Bean
//    public InMemoryUserDetailsManager userDetailsService() {
//        UserDetails admin = User.withUsername(user)
//                .password(passwordEncoder().encode(pass))
//                .roles("ADMIN")
//                .build();
//        return new InMemoryUserDetailsManager(admin);
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


//    @Bean
//    @Profile({"prod"})
//    public SecurityFilterChain filterChainProd(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(authorize -> authorize
//                        .anyRequest().authenticated()
//                )
//                .formLogin(withDefaults())
//                .httpBasic(withDefaults());
//        http.csrf(AbstractHttpConfigurer::disable);
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain filterChainProd(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

//    @Bean
//    @Profile("dev")
//    public SecurityFilterChain filterChainDev(HttpSecurity http) throws Exception {
//        http.csrf(AbstractHttpConfigurer::disable);
//        return http.build();
//    }


}
