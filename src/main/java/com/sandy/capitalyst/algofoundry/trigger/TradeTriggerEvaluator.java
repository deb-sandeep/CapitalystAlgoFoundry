package com.sandy.capitalyst.algofoundry.trigger;

import com.sandy.capitalyst.algofoundry.equityhistory.AbstractDayValue;
import com.sandy.capitalyst.algofoundry.equityhistory.DayValueListener;
import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import org.ta4j.core.Bar;
import org.ta4j.core.Trade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TradeTriggerEvaluator implements DayValueListener {
    
    private final List<TradeTriggerListener> listeners = new ArrayList<>() ;
    
    private final TradeRule buyRule ;
    private final TradeRule sellRule ;
    private final EquityEODHistory history ;
    
    private int lastIndexEvalauted = -1 ;
    
    public TradeTriggerEvaluator( EquityEODHistory history,
                                  TradeStrategy tradeStrategy ) {
        this.history = history ;
        this.buyRule = tradeStrategy.getBuyRule() ;
        this.sellRule = tradeStrategy.getSellRule() ;
    }
    
    public void addTradeTriggerListener( TradeTriggerListener listener ) {
        listeners.add( listener ) ;
    }
    
    public void removeTradeTriggerListener( TradeTriggerListener listener ) {
        listeners.remove( listener ) ;
    }
    
    @Override
    public void handleDayValue( AbstractDayValue dayValue ) {
        
        int seriesIndex = dayValue.getSeriesIndex() ;
        computeTradeTrigger( seriesIndex ) ;
    }
    
    public TradeTrigger computeTradeTrigger( int seriesIndex ) {
        
        TradeTrigger trigger = null ;
        if( seriesIndex > lastIndexEvalauted ) {
            
            Bar bar = history.getBarSeries().getBar( seriesIndex ) ;
            Date date = Date.from( bar.getEndTime().toInstant() ) ;
            
            if( buyRule != null && buyRule.isTriggered( seriesIndex ) ) {
                trigger = new TradeTrigger( Trade.TradeType.BUY, date,
                                            history.getSymbol(),
                                            bar.getClosePrice().doubleValue() ) ;
            }
            else if( sellRule != null && sellRule.isTriggered( seriesIndex ) ) {
                trigger = new TradeTrigger( Trade.TradeType.SELL, date,
                                            history.getSymbol(),
                                            bar.getClosePrice().doubleValue() ) ;
            }
            
            this.lastIndexEvalauted = seriesIndex ;
            
            if( trigger != null ) {
                for( TradeTriggerListener l : listeners ) {
                    l.handleTradeTrigger( trigger ) ;
                }
            }
        }
        return trigger ;
    }
}
