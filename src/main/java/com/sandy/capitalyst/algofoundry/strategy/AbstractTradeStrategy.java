package com.sandy.capitalyst.algofoundry.strategy;

import com.sandy.capitalyst.algofoundry.equityhistory.AbstractDayValue;
import com.sandy.capitalyst.algofoundry.equityhistory.DayValueListener;
import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.strategy.event.LogEvent;
import com.sandy.capitalyst.algofoundry.strategy.log.StrategyConsoleLogger;
import com.sandy.capitalyst.algofoundry.strategy.log.StrategyLogger;
import org.ta4j.core.Bar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.sandy.capitalyst.algofoundry.strategy.event.LogEvent.* ;
import static com.sandy.capitalyst.algofoundry.strategy.event.LogEvent.Level.* ;
import static com.sandy.capitalyst.algofoundry.strategy.event.LogEvent.Type.* ;

public abstract class AbstractTradeStrategy
        implements DayValueListener {
    
    private final List<StrategyEventListener> eventListeners = new ArrayList<>() ;
    private final List<TradeSignalListener> listeners = new ArrayList<>() ;
    
    protected final EquityEODHistory history ;
    protected final TradeBook        tradeBook = new TradeBook() ;
    
    protected int lastIndexEvaluated = -1 ;
    protected Date date = null ;
    protected Bar bar = null ;
    
    protected StrategyLogger logger = new StrategyLogger() ;
    
    protected AbstractTradeStrategy( EquityEODHistory history ) {
        this.history = history ;
        this.eventListeners.add( new StrategyConsoleLogger() ) ;
    }
    
    public final TradeBook getTradeBook() {
        return this.tradeBook ;
    }
    
    public final void addStrategyEventListener( StrategyEventListener listener ) {
        this.eventListeners.add( listener ) ;
    }
    
    public final void addTradeSignalListener( TradeSignalListener listener ) {
        listeners.add( listener ) ;
    }
    
    public final void addLogListener( StrategyLogListener listener ) {
        logger.addListener( listener ) ;
    }
    
    protected final void publishEvent( StrategyEvent event ) {
        eventListeners.forEach( l -> l.handleStrategyEvent( event ) ) ;
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
        
        listeners.clear() ;
        tradeBook.clear() ;
        logger.clearListeners() ;
    }
    
    @Override
    public final void handleDayValue( AbstractDayValue dayValue ) {
        int seriesIndex = dayValue.getSeriesIndex() ;
        executeStrategy( seriesIndex ) ;
    }
    
    public final TradeSignal executeStrategy( int seriesIndex ) {
        
        TradeSignal signal = null ;
        if( seriesIndex > lastIndexEvaluated ) {
            
            this.bar  = history.getBarSeries().getBar( seriesIndex ) ;
            this.date = Date.from( bar.getEndTime().toInstant() ) ;
            
            signal = executeSignalStrategy( seriesIndex ) ;
            if( signal != null ) {
                if( signal.isEntrySignal() ) {
                    if( tradeBook.getQuantity() > 0 ) {
                        tradeBook.addTrade( signal ) ;
                    }
                }
                for( TradeSignalListener l : listeners ) {
                    l.handleTradeSignal( signal ) ;
                }
            }
            
            double closePrice = bar.getClosePrice().doubleValue() ;
            tradeBook.handleTradeSignal( seriesIndex, date, closePrice, signal ) ;
            this.lastIndexEvaluated = seriesIndex ;
        }
        return signal ;
    }
    
    public abstract TradeSignal executeSignalStrategy( int seriesIndex ) ;
}
