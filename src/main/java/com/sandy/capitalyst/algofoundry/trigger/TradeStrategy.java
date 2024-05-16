package com.sandy.capitalyst.algofoundry.trigger;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;

public abstract class TradeStrategy {
    
    protected final EquityEODHistory history ;
    
    protected final TradeRule buyRule ;
    protected final TradeRule sellRule ;
    
    protected TradeStrategy( EquityEODHistory history ) {
        this.history = history ;
        this.buyRule = createBuyRule() ;
        this.sellRule = createSellRule() ;
    }
    
    protected abstract TradeRule createBuyRule() ;
    
    protected abstract TradeRule createSellRule() ;
    
    public final TradeRule getBuyRule() { return this.buyRule ; }
    
    public final TradeRule getSellRule() { return this.sellRule ; }
}
