package com.sandy.capitalyst.algofoundry.strategy.signal;

import com.sandy.capitalyst.algofoundry.eodhistory.AbstractDayValue;
import com.sandy.capitalyst.algofoundry.eodhistory.DayValueListener;
import com.sandy.capitalyst.algofoundry.eodhistory.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.strategy.signal.event.TradeSignalEvent;
import org.ta4j.core.Bar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.sandy.capitalyst.algofoundry.strategy.signal.event.SignalStrategyLogEvent.* ;
import static com.sandy.capitalyst.algofoundry.strategy.signal.event.SignalStrategyLogEvent.Level.* ;
import static com.sandy.capitalyst.algofoundry.strategy.signal.event.SignalStrategyLogEvent.Type.* ;

public abstract class AbstractSignalStrategy
        implements DayValueListener {
    
    private final List<SignalStrategyEventListener> eventListeners = new ArrayList<>() ;
    
    protected final EquityEODHistory history ;
    
    protected int lastIndexEvaluated = -1 ;
    protected Date date = null ;
    protected Bar bar = null ;
    
    protected AbstractSignalStrategy( EquityEODHistory history ) {
        this.history = history ;
        //this.eventListeners.add( new SignalStrategyConsoleLogger() ) ;
    }
    
    public final void addStrategyEventListener( SignalStrategyEventListener listener ) {
        this.eventListeners.add( listener ) ;
    }
    
    protected final void publishEvent( SignalStrategyEvent event ) {
        eventListeners.forEach( l -> l.handleStrategyEvent( event ) ) ;
    }
    
    protected void pubTradeSignal( TradeSignalEvent.Type type ) {
        publishEvent( new TradeSignalEvent( date, bar, type ) ) ;
    }
    
    protected final void debug0( String msg ) {
        publishEvent( logEvent( date, bar, DEBUG, L0, msg ) ) ;
    }
    
    protected final void debug1( String msg ) {
        publishEvent( logEvent( date, bar, DEBUG, L1, msg ) ) ;
    }
    
    protected final void debug2( String msg ) {
        publishEvent( logEvent( date, bar, DEBUG, L2, msg ) ) ;
    }
    
    protected final void debug3( String msg ) {
        publishEvent( logEvent( date, bar, DEBUG, L3, msg ) ) ;
    }
    
    protected final void info0( String msg ) {
        publishEvent( logEvent( date, bar, INFO, L0, msg ) ) ;
    }
    
    protected final void info1( String msg ) {
        publishEvent( logEvent( date, bar, INFO, L1, msg ) ) ;
    }
    
    protected final void info2( String msg ) {
        publishEvent( logEvent( date, bar, INFO, L2, msg ) ) ;
    }
    
    protected final void info3( String msg ) {
        publishEvent( logEvent( date, bar, INFO, L3, msg ) ) ;
    }
    
    protected final void error( String msg ) {
        publishEvent( logEvent( date, bar, ERROR, L0, msg ) ) ;
    }
    
    public void clear() {
        lastIndexEvaluated = -1 ;
        eventListeners.clear() ;
    }
    
    @Override
    public final void handleDayValue( AbstractDayValue dayValue ) {
        int seriesIndex = dayValue.getSeriesIndex() ;
        if( seriesIndex > lastIndexEvaluated ) {
            
            this.bar  = history.getBarSeries().getBar( seriesIndex ) ;
            this.date = Date.from( bar.getEndTime().toInstant() ) ;
            
            executeStrategy( seriesIndex ) ;
            
            this.lastIndexEvaluated = seriesIndex ;
        }
    }
    
    public abstract void executeStrategy( int seriesIndex ) ;
}
