package com.sandy.capitalyst.algofoundry.strategy.trade;

import com.sandy.capitalyst.algofoundry.strategy.signal.event.TradeSignalEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultTradeBook extends TradeBook {
    
    @Override
    protected BuyTrade handleBuySignal( TradeSignalEvent te ) {
        
        double investmentQuantum = Math.min( 5*ema60Price, 10000 ) ;
        int quantity = (int)(investmentQuantum/te.getClosingPrice()) ;
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
