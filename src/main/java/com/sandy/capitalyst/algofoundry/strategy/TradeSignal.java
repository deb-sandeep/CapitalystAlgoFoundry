package com.sandy.capitalyst.algofoundry.strategy;

import lombok.Getter;

import java.util.Date;

public class TradeSignal {
    
    public enum Type { ENTRY, EXIT }
    
    @Getter private final Type type;
    
    @Getter private final Date    date ;
    @Getter private final String  symbol ;
    @Getter private final double  price ;
    
    public TradeSignal( Type type, Date date, String symbol, double price ) {
        this.type = type ;
        this.date = date ;
        this.symbol = symbol ;
        this.price = price ;
    }
    
    public boolean isEntrySignal() {
        return this.type == Type.ENTRY ;
    }

    public boolean isExitSignal() {
        return this.type == Type.EXIT ;
    }
}
