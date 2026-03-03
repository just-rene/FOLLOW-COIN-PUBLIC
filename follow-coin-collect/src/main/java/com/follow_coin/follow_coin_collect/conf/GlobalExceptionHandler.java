package com.follow_coin.follow_coin_collect.conf;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

//
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<String> handleGeneralException(Exception ex) {
//        System.err.println(ex.getMessage());
//        return new ResponseEntity<>("something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
//    }
}
