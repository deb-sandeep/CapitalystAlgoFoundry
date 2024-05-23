package com.sandy.capitalyst.algofoundry.strategy.event;

import com.sandy.capitalyst.algofoundry.strategy.StrategyEvent;
import lombok.Getter;
import org.ta4j.core.Bar;

import java.util.Date;

public class LogEvent extends StrategyEvent {
    
    public enum Type { DEBUG, INFO, ERROR }
    public enum Level { L0, L1, L2, L3 }
    
    @Getter private final Type type ;
    @Getter private final Level level ;
    
    @Getter private final String msg ;
    
    public static LogEvent logEvent( Date date, Bar bar, Type type, Level level, String msg ) {
        return new LogEvent( date, bar, type, level, msg ) ;
    }
    
    public LogEvent( Date date, Bar bar, Type type, Level level, String msg ) {
        super( date, bar ) ;
        this.type = type ;
        this.level = level ;
        this.msg = msg ;
    }
}
