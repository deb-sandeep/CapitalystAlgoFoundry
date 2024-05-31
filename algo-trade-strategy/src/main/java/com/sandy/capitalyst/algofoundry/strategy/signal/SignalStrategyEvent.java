package com.sandy.capitalyst.algofoundry.strategy.signal;

import lombok.Getter;
import org.ta4j.core.Bar;

import java.util.Date;

public class SignalStrategyEvent {
    
    @Getter private Date date ;
    @Getter private Bar bar ;
    
    protected SignalStrategyEvent( Date date, Bar bar ) {
        this.date = date ;
        this.bar = bar ;
    }
    
    public double getClosingPrice() {
        return bar.getClosePrice().doubleValue() ;
    }
}
