package com.sandy.capitalyst.algofoundry.strategy;

import lombok.Getter;

import java.util.Date;

public class StrategyEvent {
    
    @Getter private Date date ;
    
    protected StrategyEvent( Date date ) {
        this.date = date ;
    }
}
