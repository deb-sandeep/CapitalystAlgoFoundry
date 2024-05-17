package com.sandy.capitalyst.algofoundry.strategy;

import lombok.Getter;

import java.util.Date;

public class TradeSignal {
    
    public enum Type { ENTRY, EXIT }
    
    @Getter private final Type tradeType ;
    
    @Getter private final Date    date ;
    @Getter private final String  symbol ;
    @Getter private final double  price ;
    
    public TradeSignal( Type type, Date date, String symbol, double price ) {
        this.tradeType = type ;
        this.date = date ;
        this.symbol = symbol ;
        this.price = price ;
    }
    
    public boolean isEntrySignal() {
        return this.tradeType == Type.ENTRY ;
    }

    public boolean isExitSignal() {
        return this.tradeType == Type.EXIT ;
    }
}
