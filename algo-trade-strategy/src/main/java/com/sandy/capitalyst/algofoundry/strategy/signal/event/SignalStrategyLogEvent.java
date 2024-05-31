package com.sandy.capitalyst.algofoundry.strategy.signal.event;

import com.sandy.capitalyst.algofoundry.strategy.signal.SignalStrategyEvent;
import lombok.Getter;
import org.ta4j.core.Bar;

import java.util.Date;

public class SignalStrategyLogEvent extends SignalStrategyEvent {
    
    public enum Type { DEBUG, INFO, ERROR }
    public enum Level { L0, L1, L2, L3 }
    
    @Getter private final Type type ;
    @Getter private final Level level ;
    
    @Getter private final String msg ;
    
    public static SignalStrategyLogEvent logEvent( Date date, Bar bar, Type type, Level level, String msg ) {
        return new SignalStrategyLogEvent( date, bar, type, level, msg ) ;
    }
    
    public SignalStrategyLogEvent( Date date, Bar bar, Type type, Level level, String msg ) {
        super( date, bar ) ;
        this.type = type ;
        this.level = level ;
        this.msg = msg ;
    }
    
    public static String getIndent( SignalStrategyLogEvent.Level level ) {
        switch( level ) {
            case L0 -> { return "" ; }
            case L1 -> { return "  " ; }
            case L2 -> { return "    " ; }
            case L3 -> { return "      " ; }
        }
        return "" ;
    }
}
