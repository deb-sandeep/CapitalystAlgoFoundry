package com.sandy.capitalyst.algofoundry.strategy;

import lombok.Getter;
import org.ta4j.core.Bar;

import java.util.Date;

public class StrategyEvent {
    
    @Getter private Date date ;
    @Getter private Bar bar ;
    
    protected StrategyEvent( Date date, Bar bar ) {
        this.date = date ;
        this.bar = bar ;
    }
}
