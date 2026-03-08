package com.follow_coin.follow_coin_collect.services.extractor;

import com.fasterxml.jackson.databind.JsonNode;
import com.follow_coin.follow_coin_collect.entities.Coin;
import com.follow_coin.follow_coin_collect.entities.CoinPrice;
import com.follow_coin.follow_coin_collect.entities.CoinPriceKey;
import com.follow_coin.follow_coin_collect.repos.CoinPriceRepo;
import com.follow_coin.follow_coin_collect.repos.CoinRegister;
import com.follow_coin.follow_coin_collect.services.CollectService;
import com.follow_coin.follow_coin_collect.tools.CleanMapper;
import com.follow_coin.follow_coin_collect.types.ExtractFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.StreamSupport;

@Component
public class CoinDataExtractorVisitorImpl implements CoinDataExtractorVisitor {


    @Autowired
    private CoinRegister coinRegister;

    @Autowired
    private CoinPriceRepo coinPriceRepo;

    @Autowired
    private CleanMapper mapper;

    @Override
    public void visit(CollectService collectService) {

        List<String> requiredCoinSymbols = coinRegister.findAll().map(Coin::getSymbol).collectList().block();

        collectService
                .collectCoinData()
                .map(mapper::readTree)
                .mapNotNull(data -> extractRelevantCoinData(data, requiredCoinSymbols))
                .subscribe(coins -> coinPriceRepo.saveAll(coins).subscribe());
    }

    private List<CoinPrice> extractRelevantCoinData(JsonNode data, List<String> requiredCoinSymbols) {
        return StreamSupport
                .stream(data.findPath(ExtractFields.DATA.val).spliterator(), false)
                .filter(jsonNode -> requiredCoinSymbols.contains(jsonNode.get(ExtractFields.SYMBOL.val).asText()))
                .map(jsonNode -> {
                    String date = jsonNode.get(ExtractFields.LAST_UPDATE.val).asText();
                    double price = jsonNode.get(ExtractFields.QUOTE.val).get(ExtractFields.USD.val).get(ExtractFields.PRICE.val).asDouble();
                    return new CoinPrice(new CoinPriceKey(jsonNode.get(ExtractFields.SYMBOL.val).asText(), date), price);
                }).toList();
    }
}
