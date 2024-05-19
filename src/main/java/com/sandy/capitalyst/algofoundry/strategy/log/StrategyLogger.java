package com.sandy.capitalyst.algofoundry.strategy.log;

import com.sandy.capitalyst.algofoundry.core.util.StringUtil;
import com.sandy.capitalyst.algofoundry.strategy.StrategyLogListener;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public class StrategyLogger {
    
    private final List<StrategyLogListener> listeners = new ArrayList<>() ;
    
    public StrategyLogger(){}
    
    public void addListener( StrategyLogListener listener ) {
        listeners.add( listener ) ;
    }
    
    public void clearListeners() {
        listeners.clear() ;
    }
    
    public void log( Date date, String str ) {
        log( date, str, false ) ;
    }
    
    public void log( Date date, String str, boolean overline ) {
        if( overline ) {
            logString( "----------------------------------------" ) ;
        }
        logString( StringUtil.fmtDate( date ) + " : " + str ) ;
    }
    
    public void log( String str ) {
        logString( str ) ;
    }
    
    public void log1( String str ) {
        logString( "  " + str ) ;
    }
    
    public void log2( String str ) {
        logString( "    " + str ) ;
    }
    
    private void logString( String str ) {
        log.debug( str ) ;
        listeners.forEach( l -> l.log( str ) );
    }
}
