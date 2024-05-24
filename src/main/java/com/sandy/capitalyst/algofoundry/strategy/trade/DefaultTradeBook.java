package com.sandy.capitalyst.algofoundry.strategy.trade;

import com.sandy.capitalyst.algofoundry.strategy.signal.event.TradeSignalEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultTradeBook extends TradeBook {
    
    private static final int BUY_QUANTUM_AMT = 10000 ;
    
    @Override
    protected BuyTrade handleBuySignal( TradeSignalEvent te ) {
        
        int quantity = (int)(BUY_QUANTUM_AMT/te.getClosingPrice()) ;
        if( quantity > 0 ) {
            log.debug( "Buying {} stocks", quantity ) ;
            return new BuyTrade( te.getDate(), te.getClosingPrice(), quantity ) ;
        }
        return null ;
    }
    
    @Override
    protected SellTrade handleSellSignal( TradeSignalEvent te ) {
        return new SellTrade( te.getDate(), te.getClosingPrice(), super.getHoldingQty() ) ;
    }
}
