package com.sandy.capitalyst.algofoundry.trigger;

import com.sandy.capitalyst.algofoundry.equityhistory.AbstractDayValue;
import com.sandy.capitalyst.algofoundry.equityhistory.DayValueListener;
import org.ta4j.core.Trade;

import java.util.ArrayList;
import java.util.List;

public class TradeTriggerEvaluator implements DayValueListener {
    
    private final List<TradeTriggerListener> listeners = new ArrayList<>() ;
    private final TradeRule buyRule ;
    private final TradeRule sellRule ;
    
    private int lastIndexEvalauted = -1 ;
    
    public TradeTriggerEvaluator( TradeRule buyRule, TradeRule sellRule ) {
        this.buyRule = buyRule ;
        this.sellRule = sellRule ;
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
        if( seriesIndex > lastIndexEvalauted ) {
            
            TradeTrigger trigger ;
            
            if( buyRule != null && buyRule.isTriggered( seriesIndex ) ) {
                trigger = new TradeTrigger( Trade.TradeType.BUY, dayValue.getDate() ) ;
            }
            else if( sellRule != null && sellRule.isTriggered( seriesIndex ) ) {
                trigger = new TradeTrigger( Trade.TradeType.SELL, dayValue.getDate() ) ;
            }
            else {
                trigger = null;
            }
            
            this.lastIndexEvalauted = dayValue.getSeriesIndex() ;
            
            if( trigger != null ) {
                listeners.forEach( l -> l.handleTradeTrigger( trigger ) ) ;
            }
        }
    }
}
