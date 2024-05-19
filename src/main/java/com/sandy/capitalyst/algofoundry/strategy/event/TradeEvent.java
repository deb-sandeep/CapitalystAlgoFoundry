package com.sandy.capitalyst.algofoundry.strategy.event;

import com.sandy.capitalyst.algofoundry.strategy.StrategyEvent;
import lombok.Getter;

import java.util.Date;

public class TradeEvent extends StrategyEvent {
    public enum Type { BUY, SELL } ;
    
    @Getter private final Type type ;
    
    public TradeEvent( Date date, Type type ) {
        super( date ) ;
        this.type = type ;
    }
}
