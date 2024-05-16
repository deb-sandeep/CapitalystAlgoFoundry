package com.sandy.capitalyst.algofoundry.trigger.strategy;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.trigger.TradeRule;
import com.sandy.capitalyst.algofoundry.trigger.TradeStrategy;
import com.sandy.capitalyst.algofoundry.trigger.rule.atom.ADXStrengthRule;
import com.sandy.capitalyst.algofoundry.trigger.rule.atom.EMADownCrossoverRule;
import com.sandy.capitalyst.algofoundry.trigger.rule.atom.EMAUpCrossoverRule;

public class ADXEMATradeStrategy extends TradeStrategy {
    
    public static final String NAME = "EMA Crossover + ADX" ;
    
    public ADXEMATradeStrategy( EquityEODHistory history ) {
        super( history ) ;
    }
    
    @Override
    protected TradeRule createBuyRule() {
        return new EMAUpCrossoverRule( history, 5, 20 ).and(
                    new ADXStrengthRule( history, 20 ) ) ;
    }
    
    @Override
    protected TradeRule createSellRule() {
        return new EMADownCrossoverRule( history, 5, 20 ).and(
                new ADXStrengthRule( history, 20 ) ) ;
    }
}
