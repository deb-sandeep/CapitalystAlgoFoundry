package com.sandy.capitalyst.algofoundry.strategy.signal;

import com.sandy.capitalyst.algofoundry.strategy.StrategyConfig;
import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.CandleSeries;
import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.DayValue;
import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.DayValueListener;
import com.sandy.capitalyst.algofoundry.strategy.signal.event.TradeSignalEvent;
import lombok.Getter;
import org.ta4j.core.Bar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.sandy.capitalyst.algofoundry.strategy.signal.event.SignalStrategyLogEvent.Level;
import static com.sandy.capitalyst.algofoundry.strategy.signal.event.SignalStrategyLogEvent.Level.*;
import static com.sandy.capitalyst.algofoundry.strategy.signal.event.SignalStrategyLogEvent.Type.*;
import static com.sandy.capitalyst.algofoundry.strategy.signal.event.SignalStrategyLogEvent.logEvent;

public abstract class SignalStrategy
        implements DayValueListener {
    
    private final List<SignalStrategyEventListener> eventListeners = new ArrayList<>() ;
    
    @Getter protected final CandleSeries candleSeries;
    
    protected int lastIndexEvaluated = -1 ;
    protected Date date = null ;
    protected Bar bar = null ;
    
    protected final StrategyConfig config ;
    
    protected SignalStrategy( CandleSeries candleSeries, StrategyConfig config ) {
        this.candleSeries = candleSeries;
        this.config = config ;
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
        publishEvent( logEvent( date, bar, DEBUG, Level.L0, msg ) ) ;
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
    public final void handleDayValue( DayValue dayValue ) {
        int seriesIndex = dayValue.getSeriesIndex() ;
        if( seriesIndex > lastIndexEvaluated ) {
            
            this.bar  = candleSeries.getBarSeries().getBar( seriesIndex ) ;
            this.date = Date.from( bar.getEndTime().toInstant() ) ;
            
            executeStrategy( seriesIndex ) ;
            
            this.lastIndexEvaluated = seriesIndex ;
        }
    }
    
    public abstract void executeStrategy( int seriesIndex ) ;
}
