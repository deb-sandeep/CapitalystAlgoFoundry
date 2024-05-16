package com.sandy.capitalyst.algofoundry.strategy;

import lombok.Getter;

import java.util.Date;

import static org.ta4j.core.Trade.TradeType.* ;

public class Trade {
    
    @Getter private final org.ta4j.core.Trade.TradeType tradeType ;
    
    @Getter private final Date    date ;
    @Getter private final String  symbol ;
    @Getter private final double  price ;
    
    Trade( org.ta4j.core.Trade.TradeType type, Date date, String symbol, double price ) {
        this.tradeType = type ;
        this.date = date ;
        this.symbol = symbol ;
        this.price = price ;
    }
    
    public boolean isBuy() {
        return this.tradeType == BUY ;
    }

    public boolean isSell() {
        return this.tradeType == SELL ;
    }
}
