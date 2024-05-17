package com.sandy.capitalyst.algofoundry.strategy.strategy;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.strategy.TradeRule;
import com.sandy.capitalyst.algofoundry.strategy.rule.atom.adx.ADXDownTrendRule;
import com.sandy.capitalyst.algofoundry.strategy.rule.atom.adx.ADXStrengthRule;
import com.sandy.capitalyst.algofoundry.strategy.rule.atom.adx.ADXUpTrendRule;
import com.sandy.capitalyst.algofoundry.strategy.rule.atom.ema.EMADownCrossoverRule;
import com.sandy.capitalyst.algofoundry.strategy.rule.atom.ema.EMAUpCrossoverRule;
import com.sandy.capitalyst.algofoundry.strategy.rule.atom.macd.*;
import com.sandy.capitalyst.algofoundry.strategy.rule.logic.AndRule;
import com.sandy.capitalyst.algofoundry.strategy.rule.logic.OrRule;

public class MACDTradeStrategy extends TradeStrategy {
    
    public static final String NAME = "MACD Strategy" ;
    
    public MACDTradeStrategy( EquityEODHistory history ) {
        super( history ) ;
    }
    
    @Override
    protected TradeRule createEntryRule() {
        
        return new AndRule(
            new OrRule(
                new MACDHistPositiveStartRule( history ),
                new EMAUpCrossoverRule( history, 5, 20 )
            ),
            new AndRule(
                new ADXUpTrendRule( history ),
                new ADXStrengthRule( history, 30 )
            )
        ) ;
    }
    
    @Override
    protected TradeRule createExitRule() {
        
        return new AndRule(
            new OrRule(
                new MACDHistNegativeStartRule( history ),
                new EMADownCrossoverRule( history, 5, 20 )
            ),
            new AndRule(
                new ADXDownTrendRule( history ),
                new ADXStrengthRule( history, 30 )
            )
        ) ;
    }
}
