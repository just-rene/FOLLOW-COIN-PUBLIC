package com.follow_coin.follow_coin_compute.tools;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.follow_coin.follow_coin_compute.dtos.CoinPriceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CleanMapper {

    private static final Logger logger = LoggerFactory.getLogger(CleanMapper.class);

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * @param inputData (String)
     * @return JsonNode or null
     */
    public JsonNode readTree(String inputData){
        JsonNode data = null;
        try {
            data = objectMapper.readTree(inputData);
        } catch (JsonProcessingException e) {
            logger.error("couldn't process json data!{}", e.getMessage());
        }
        return data;
    }

    /**
     * @param value (String)
     * @return CoinPriceEvent or null
     */
    public CoinPriceEvent readValue(String value){
        CoinPriceEvent data = null;
        try {
            data =  objectMapper.readValue(value, CoinPriceEvent.class);
        } catch (JsonProcessingException e) {
            logger.error("couldn't process json data!{}", e.getMessage());
        }
        return data;
    }

    /**
     * @param o (Object)
     * @return String or null
     */
    public String writeValueAsString(Object o){
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            logger.error("couldn't process json data!{}", e.getMessage());
        }
        return null;
    }

}
