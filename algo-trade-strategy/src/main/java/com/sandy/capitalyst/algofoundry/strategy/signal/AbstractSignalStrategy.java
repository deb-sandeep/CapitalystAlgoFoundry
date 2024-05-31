package com.sandy.capitalyst.algofoundry.strategy.signal;

import com.sandy.capitalyst.algofoundry.strategy.candleseries.DayValue;
import com.sandy.capitalyst.algofoundry.strategy.candleseries.DayValueListener;
import com.sandy.capitalyst.algofoundry.strategy.candleseries.CandleSeries;
import com.sandy.capitalyst.algofoundry.strategy.signal.event.SignalStrategyLogEvent;
import com.sandy.capitalyst.algofoundry.strategy.signal.event.TradeSignalEvent;
import org.ta4j.core.Bar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class AbstractSignalStrategy
        implements DayValueListener {
    
    private final List<SignalStrategyEventListener> eventListeners = new ArrayList<>() ;
    
    protected final CandleSeries history ;
    
    protected int lastIndexEvaluated = -1 ;
    protected Date date = null ;
    protected Bar bar = null ;
    
    protected AbstractSignalStrategy( CandleSeries history ) {
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
        publishEvent( SignalStrategyLogEvent.logEvent( date, bar, SignalStrategyLogEvent.Type.DEBUG, SignalStrategyLogEvent.Level.L0, msg ) ) ;
    }
    
    protected final void debug1( String msg ) {
        publishEvent( SignalStrategyLogEvent.logEvent( date, bar, SignalStrategyLogEvent.Type.DEBUG, SignalStrategyLogEvent.Level.L1, msg ) ) ;
    }
    
    protected final void debug2( String msg ) {
        publishEvent( SignalStrategyLogEvent.logEvent( date, bar, SignalStrategyLogEvent.Type.DEBUG, SignalStrategyLogEvent.Level.L2, msg ) ) ;
    }
    
    protected final void debug3( String msg ) {
        publishEvent( SignalStrategyLogEvent.logEvent( date, bar, SignalStrategyLogEvent.Type.DEBUG, SignalStrategyLogEvent.Level.L3, msg ) ) ;
    }
    
    protected final void info0( String msg ) {
        publishEvent( SignalStrategyLogEvent.logEvent( date, bar, SignalStrategyLogEvent.Type.INFO, SignalStrategyLogEvent.Level.L0, msg ) ) ;
    }
    
    protected final void info1( String msg ) {
        publishEvent( SignalStrategyLogEvent.logEvent( date, bar, SignalStrategyLogEvent.Type.INFO, SignalStrategyLogEvent.Level.L1, msg ) ) ;
    }
    
    protected final void info2( String msg ) {
        publishEvent( SignalStrategyLogEvent.logEvent( date, bar, SignalStrategyLogEvent.Type.INFO, SignalStrategyLogEvent.Level.L2, msg ) ) ;
    }
    
    protected final void info3( String msg ) {
        publishEvent( SignalStrategyLogEvent.logEvent( date, bar, SignalStrategyLogEvent.Type.INFO, SignalStrategyLogEvent.Level.L3, msg ) ) ;
    }
    
    protected final void error( String msg ) {
        publishEvent( SignalStrategyLogEvent.logEvent( date, bar, SignalStrategyLogEvent.Type.ERROR, SignalStrategyLogEvent.Level.L0, msg ) ) ;
    }
    
    public void clear() {
        lastIndexEvaluated = -1 ;
        eventListeners.clear() ;
    }
    
    @Override
    public final void handleDayValue( DayValue dayValue ) {
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
