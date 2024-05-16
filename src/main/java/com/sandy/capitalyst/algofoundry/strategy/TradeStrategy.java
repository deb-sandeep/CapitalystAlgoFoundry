package com.sandy.capitalyst.algofoundry.strategy;

import com.sandy.capitalyst.algofoundry.equityhistory.AbstractDayValue;
import com.sandy.capitalyst.algofoundry.equityhistory.DayValueListener;
import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import org.ta4j.core.Bar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.ta4j.core.Trade.TradeType.* ;

public abstract class TradeStrategy
        implements DayValueListener {
    
    private final List<TradeListener> listeners = new ArrayList<>() ;
    
    protected final EquityEODHistory history ;
    
    private TradeRule buyRule ;
    private TradeRule sellRule ;
    
    private final TradeBook tradeBook = new TradeBook() ;
    
    private int lastIndexEvalauted = -1 ;
    
    protected TradeStrategy( EquityEODHistory history ) {
        this.history = history ;
    }
    
    protected abstract TradeRule createBuyRule() ;
    
    protected abstract TradeRule createSellRule() ;
    
    public final TradeRule getBuyRule() {
        if( buyRule == null ) {
            buyRule = createBuyRule() ;
        }
        return buyRule ;
    }
    
    public final TradeRule getSellRule() {
        if( sellRule == null ) {
            sellRule = createSellRule() ;
        }
        return sellRule ;
    }
    
    public final void addTradeListener( TradeListener listener ) {
        listeners.add( listener ) ;
    }
    
    public final void clear() {
        listeners.clear() ;
        tradeBook.clear() ;
    }
    
    @Override
    public void handleDayValue( AbstractDayValue dayValue ) {
        
        int seriesIndex = dayValue.getSeriesIndex() ;
        computeTrade( seriesIndex ) ;
    }
    
    public Trade computeTrade( int seriesIndex ) {
        
        Trade trade = null ;
        if( seriesIndex > lastIndexEvalauted ) {
            
            Bar  bar  = history.getBarSeries().getBar( seriesIndex ) ;
            Date date = Date.from( bar.getEndTime().toInstant() ) ;
            
            if( getBuyRule() != null && getBuyRule().isTriggered( seriesIndex ) ) {
                trade = new Trade( BUY, date,
                        history.getSymbol(),
                        bar.getClosePrice().doubleValue() ) ;
            }
            else if( getSellRule() != null && getSellRule().isTriggered( seriesIndex ) ) {
                trade = new Trade( SELL, date,
                        history.getSymbol(),
                        bar.getClosePrice().doubleValue() ) ;
            }
            
            if( trade != null ) {
                if( trade.isBuy() || (tradeBook.getQuantity() > 0) ) {
                    tradeBook.addTrade( trade ) ;
                    for( TradeListener l : listeners ) {
                        l.handleTrade( trade ) ;
                    }
                }
            }
            
            tradeBook.computeNotionalProfit( bar.getClosePrice().doubleValue() ) ;
            this.lastIndexEvalauted = seriesIndex ;
        }
        return trade ;
    }
}
