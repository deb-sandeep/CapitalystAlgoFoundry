package com.sandy.capitalyst.algofoundry.strategy.tradebook;

import com.sandy.capitalyst.algofoundry.strategy.event.TradeEvent;
import com.sandy.capitalyst.algofoundry.strategy.tradebook.trade.BuyTrade;
import com.sandy.capitalyst.algofoundry.strategy.tradebook.trade.SellTrade;

public class DefaultTradeBook extends TradeBook {
    
    @Override
    protected BuyTrade handleBuySignal( TradeEvent te ) {
        return new BuyTrade( te.getDate(), te.getClosingPrice(), 1 ) ;
    }
    
    @Override
    protected SellTrade handleSellSignal( TradeEvent te ) {
        return new SellTrade( te.getDate(), te.getClosingPrice(), super.getHoldingQty() ) ;
    }
}
