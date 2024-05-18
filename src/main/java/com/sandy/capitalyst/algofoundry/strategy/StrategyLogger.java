package com.sandy.capitalyst.algofoundry.strategy;

import com.sandy.capitalyst.algofoundry.core.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public class StrategyLogger {
    
    private List<StrategyLogListener> listeners = new ArrayList<>() ;
    
    public StrategyLogger(){}
    
    public void addListener( StrategyLogListener listener ) {
        listeners.add( listener ) ;
    }
    
    public void clearListeners() {
        listeners.clear() ;
    }
    
    public void log( Date date, String str ) {
        logString( StringUtil.fmtDate( date ) + " : " + str ) ;
    }
    
    public void log( String str ) {
        logString( "  " + str ) ;
    }
    
    public void log1( String str ) {
        logString( "    " + str ) ;
    }
    
    public void log2( String str ) {
        logString( "      " + str ) ;
    }
    
    private void logString( String str ) {
        listeners.forEach( l -> l.log( str ) );
    }
}
