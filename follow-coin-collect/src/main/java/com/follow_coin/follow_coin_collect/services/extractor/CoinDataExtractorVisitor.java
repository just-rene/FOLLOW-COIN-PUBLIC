package com.follow_coin.follow_coin_collect.services.extractor;

import com.follow_coin.follow_coin_collect.services.CollectService;

public interface CoinDataExtractorVisitor {

    void visit(CollectService collectService);
}
