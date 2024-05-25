package com.sandy.capitalyst.algofoundry.tradebook;

public interface TradeBookListener {
    
    default void tradeBookUpdated( TradeBook tradeBook ) {} ;
    
    default void buyTradeExecuted( BuyTrade buyTrade ) {}
    
    default void sellTradeExecuted( SellTrade sellTrade ) {}
}
