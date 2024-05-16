package com.sandy.capitalyst.algofoundry.strategy.strategy;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.strategy.TradeRule;
import com.sandy.capitalyst.algofoundry.strategy.TradeStrategy;
import com.sandy.capitalyst.algofoundry.strategy.rule.atom.adx.ADXStrengthRule;
import com.sandy.capitalyst.algofoundry.strategy.rule.atom.ema.EMADownCrossoverRule;
import com.sandy.capitalyst.algofoundry.strategy.rule.atom.ema.EMAUpCrossoverRule;

public class ADXEMATradeStrategy extends TradeStrategy {
    
    public static final String NAME = "EMA Crossover + ADX" ;
    
    private final int minEMA ;
    private final int maxEMA ;
    private final int adxStrength ;
    
    public ADXEMATradeStrategy( EquityEODHistory history,
                                int minEMA, int maxEMA, int adxStrength ) {
        super( history ) ;
        this.minEMA = minEMA ;
        this.maxEMA = maxEMA ;
        this.adxStrength = adxStrength ;
    }
    
    @Override
    protected TradeRule createBuyRule() {
        return new EMAUpCrossoverRule( history, minEMA, maxEMA ).and(
                    new ADXStrengthRule( history, adxStrength ) ) ;
    }
    
    @Override
    protected TradeRule createSellRule() {
        return new EMADownCrossoverRule( history, minEMA, maxEMA ).and(
                new ADXStrengthRule( history, maxEMA ) ) ;
    }
}
