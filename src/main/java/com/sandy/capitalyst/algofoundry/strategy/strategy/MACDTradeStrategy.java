package com.sandy.capitalyst.algofoundry.strategy.strategy;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.strategy.TradeRule;
import com.sandy.capitalyst.algofoundry.strategy.TradeStrategy;
import com.sandy.capitalyst.algofoundry.strategy.rule.atom.adx.ADXStrengthRule;
import com.sandy.capitalyst.algofoundry.strategy.rule.atom.ema.EMADownCrossoverRule;
import com.sandy.capitalyst.algofoundry.strategy.rule.atom.ema.EMAUpCrossoverRule;
import com.sandy.capitalyst.algofoundry.strategy.rule.atom.macd.MACDStartNegativeSignalRule;
import com.sandy.capitalyst.algofoundry.strategy.rule.atom.macd.MACDStartPositiveSignalRule;

public class MACDTradeStrategy extends TradeStrategy {
    
    public static final String NAME = "MACD Strategy" ;
    
    public MACDTradeStrategy( EquityEODHistory history ) {
        super( history ) ;
    }
    
    @Override
    protected TradeRule createBuyRule() {
        return new MACDStartPositiveSignalRule( history ) ;
    }
    
    @Override
    protected TradeRule createSellRule() {
        return new MACDStartNegativeSignalRule( history ) ;
    }
}
