package com.sandy.capitalyst.algofoundry.strategy.log;

import com.sandy.capitalyst.algofoundry.strategy.StrategyEvent;
import com.sandy.capitalyst.algofoundry.strategy.StrategyEventListener;
import com.sandy.capitalyst.algofoundry.strategy.event.LogEvent;
import com.sandy.capitalyst.algofoundry.strategy.event.CurrentZoneEvent;
import com.sandy.capitalyst.algofoundry.strategy.event.TradeEvent;
import lombok.extern.slf4j.Slf4j;

import static com.sandy.capitalyst.algofoundry.core.util.StringUtil.* ;
import static com.sandy.capitalyst.algofoundry.strategy.event.LogEvent.Level.L2;
import static com.sandy.capitalyst.algofoundry.strategy.event.LogEvent.getIndent;

@Slf4j
public class StrategyConsoleLogger implements StrategyEventListener {
    
    @Override
    public void handleStrategyEvent( StrategyEvent event ) {
        
        if( event instanceof CurrentZoneEvent ze ) {
            log.info( fmtDate( ze.getDate() ) + " : " +
                      ze.getMovementType() + " : " +
                      ze.getZoneType() ) ;
        }
        else if( event instanceof TradeEvent te ) {
            log.info( getIndent( L2 ) + ">> " + te.getType() + " signal." ) ;
        }
        else if( event instanceof LogEvent evt ) {
            String indent = getIndent( evt.getLevel() ) ;
            String message = indent + evt.getMsg() ;
            
            switch( evt.getType() ) {
                case DEBUG -> log.debug( message ) ;
                case INFO  -> log.info( message ) ;
                case ERROR -> log.error( message ) ;
            }
        }
    }
}
