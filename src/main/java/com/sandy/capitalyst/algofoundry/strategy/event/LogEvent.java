package com.sandy.capitalyst.algofoundry.strategy.event;

import com.sandy.capitalyst.algofoundry.strategy.StrategyEvent;
import lombok.Getter;

import java.util.Date;

public class LogEvent extends StrategyEvent {
    
    public enum Type { DEBUG, INFO }
    public enum Level { L0, L1, L2, L3 }
    
    @Getter private final Type type ;
    @Getter private final Level level ;
    
    @Getter private final String msg ;
    
    public LogEvent( Date date, Type type, Level level, String msg ) {
        super( date ) ;
        this.type = type ;
        this.level = level ;
        this.msg = msg ;
    }
}
