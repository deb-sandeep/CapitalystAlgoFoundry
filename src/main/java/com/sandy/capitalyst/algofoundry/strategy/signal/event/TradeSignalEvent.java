package com.sandy.capitalyst.algofoundry.strategy.signal.event;

import com.sandy.capitalyst.algofoundry.strategy.signal.SignalStrategyEvent;
import lombok.Getter;
import org.ta4j.core.Bar;

import java.util.Date;

public class TradeSignalEvent extends SignalStrategyEvent {
    public enum Type { BUY, SELL } ;
    
    @Getter private final Type type ;
    
    public TradeSignalEvent( Date date, Bar bar, Type type ) {
        super( date, bar ) ;
        this.type = type ;
    }
    
    public boolean isBuy() {
        return this.type == Type.BUY ;
    }
}
