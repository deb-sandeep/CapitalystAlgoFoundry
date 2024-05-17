package com.sandy.capitalyst.algofoundry.strategy;

import com.sandy.capitalyst.algofoundry.equityhistory.AbstractDayValue;
import com.sandy.capitalyst.algofoundry.equityhistory.DayValueListener;
import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import org.ta4j.core.Bar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.ta4j.core.Trade.TradeType.BUY;
import static org.ta4j.core.Trade.TradeType.SELL;

public abstract class AbstractTradeStrategy
        implements DayValueListener {
    
    private final List<TradeSignalListener> listeners = new ArrayList<>() ;
    
    protected final EquityEODHistory history ;
    protected final TradeBook tradeBook = new TradeBook() ;
    protected int   lastIndexEvalauted = -1 ;
    
    private TradeRule entryRule;
    private TradeRule exitRule;
    
    protected AbstractTradeStrategy( EquityEODHistory history ) {
        this.history = history ;
    }
    
    protected abstract TradeRule createEntryRule() ;
    
    protected abstract TradeRule createExitRule() ;
    
    public final TradeRule getEntryRule() {
        if( entryRule == null ) {
            entryRule = createEntryRule() ;
        }
        return entryRule;
    }
    
    public final TradeRule getExitRule() {
        if( exitRule == null ) {
            exitRule = createExitRule() ;
        }
        return exitRule;
    }
    
    public final TradeBook getTradeBook() {
        return this.tradeBook ;
    }
    
    public final void addTradeSignalListener( TradeSignalListener listener ) {
        listeners.add( listener ) ;
    }
    
    public final void clear() {
        listeners.clear() ;
        tradeBook.clear() ;
    }
    
    @Override
    public final void handleDayValue( AbstractDayValue dayValue ) {
        int seriesIndex = dayValue.getSeriesIndex() ;
        executeStrategy( seriesIndex ) ;
    }
    
    public final TradeSignal executeStrategy( int seriesIndex ) {
        
        TradeSignal signal = null ;
        if( seriesIndex > lastIndexEvalauted ) {
            
            Bar  bar  = history.getBarSeries().getBar( seriesIndex ) ;
            Date date = Date.from( bar.getEndTime().toInstant() ) ;
            
            signal = executeStrategy( seriesIndex, date, bar ) ;
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
            
            tradeBook.computeNotionalProfit( bar.getClosePrice().doubleValue() ) ;
            this.lastIndexEvalauted = seriesIndex ;
        }
        return signal ;
    }
    
    public abstract TradeSignal executeStrategy( int seriesIndex, Date date, Bar bar ) ;
}
