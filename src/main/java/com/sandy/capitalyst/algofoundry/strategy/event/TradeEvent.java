package com.sandy.capitalyst.algofoundry.strategy.event;

import com.sandy.capitalyst.algofoundry.strategy.StrategyEvent;
import lombok.Getter;
import org.ta4j.core.Bar;

import java.util.Date;

public class TradeEvent extends StrategyEvent {
    public enum Type { BUY, SELL } ;
    
    @Getter private final Type type ;
    
    public TradeEvent( Date date, Bar bar, Type type ) {
        super( date, bar ) ;
        this.type = type ;
    }
}
