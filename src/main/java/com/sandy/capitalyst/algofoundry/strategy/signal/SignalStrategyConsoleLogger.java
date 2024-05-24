package com.sandy.capitalyst.algofoundry.strategy.signal;

import com.sandy.capitalyst.algofoundry.strategy.signal.event.SignalStrategyLogEvent;
import com.sandy.capitalyst.algofoundry.strategy.signal.event.CurrentSignalZoneEvent;
import com.sandy.capitalyst.algofoundry.strategy.signal.event.TradeSignalEvent;
import lombok.extern.slf4j.Slf4j;

import static com.sandy.capitalyst.algofoundry.core.util.StringUtil.* ;
import static com.sandy.capitalyst.algofoundry.strategy.signal.event.SignalStrategyLogEvent.Level.L2;
import static com.sandy.capitalyst.algofoundry.strategy.signal.event.SignalStrategyLogEvent.getIndent;

@Slf4j
public class SignalStrategyConsoleLogger implements SignalStrategyEventListener {
    
    @Override
    public void handleStrategyEvent( SignalStrategyEvent event ) {
        
        if( event instanceof CurrentSignalZoneEvent ze ) {
            log.info( fmtDate( ze.getDate() ) + " : " +
                      ze.getMovementType() + " : " +
                      ze.getZoneType() ) ;
        }
        else if( event instanceof TradeSignalEvent te ) {
            log.info( getIndent( L2 ) + ">> " + te.getType() + " signal." ) ;
        }
        else if( event instanceof SignalStrategyLogEvent evt ) {
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
