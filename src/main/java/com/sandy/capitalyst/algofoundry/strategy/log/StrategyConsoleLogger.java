package com.sandy.capitalyst.algofoundry.strategy.log;

import com.sandy.capitalyst.algofoundry.strategy.StrategyEvent;
import com.sandy.capitalyst.algofoundry.strategy.StrategyEventListener;
import com.sandy.capitalyst.algofoundry.strategy.event.LogEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StrategyConsoleLogger implements StrategyEventListener {
    
    @Override
    public void handleStrategyEvent( StrategyEvent event ) {
        if( event instanceof LogEvent evt ) {
            String indent = getIndent( evt ) ;
            String message = indent + evt.getMsg() ;
            
            switch( evt.getType() ) {
                case DEBUG -> log.debug( message ) ;
                case INFO  -> log.info( message ) ;
                case ERROR -> log.error( message ) ;
            }
        }
    }
    
    private String getIndent( LogEvent evt ) {
        switch( evt.getLevel() ) {
            case L0 -> { return "" ; }
            case L1 -> { return "  " ; }
            case L2 -> { return "    " ; }
            case L3 -> { return "      " ; }
        }
        return "" ;
    }
}
