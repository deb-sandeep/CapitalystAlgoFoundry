package com.sandy.capitalyst.algofoundry.strategy.trade;

import com.sandy.capitalyst.algofoundry.eodhistory.AbstractDayValue;
import com.sandy.capitalyst.algofoundry.eodhistory.DayValueListener;
import com.sandy.capitalyst.algofoundry.eodhistory.dayvalue.OHLCVDayValue;
import com.sandy.capitalyst.algofoundry.strategy.signal.SignalStrategyEvent;
import com.sandy.capitalyst.algofoundry.strategy.signal.SignalStrategyEventListener;
import com.sandy.capitalyst.algofoundry.strategy.signal.event.TradeSignalEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class TradeBook
        implements SignalStrategyEventListener, DayValueListener {
    
    protected final List<AbstractTrade> trades     = new ArrayList<>() ;
    protected final List<BuyTrade>      buyTrades  = new ArrayList<>() ;
    protected final List<SellTrade>     sellTrades = new ArrayList<>() ;
    
    private final List<TradeBookListener> listeners = new ArrayList<>() ;
    
    @Getter private double totalProfit       = 0 ;
    @Getter private double totalProfitPct    = 0 ;
    @Getter private double notionalProfit    = 0 ;
    @Getter private double notionalProfitPct = 0 ;
    @Getter private double totalBuyPrice     = 0 ;
    @Getter private int    holdingQty        = 0 ;
    @Getter private double avgCostPrice      = 0 ;
    
    protected double ema60Price = 0 ;
    
    private double latestClosingPrice = 0 ;
    
    public void clear() {
        this.trades.clear() ;
        this.buyTrades.clear() ;
        this.sellTrades.clear() ;
        
        this.totalProfit       = 0 ;
        this.totalProfitPct    = 0 ;
        this.notionalProfit    = 0 ;
        this.notionalProfitPct = 0 ;
        this.totalBuyPrice     = 0 ;
        this.holdingQty        = 0 ;
        this.avgCostPrice      = 0 ;
        
        notifyListeners() ;
    }
    
    public void addListener( TradeBookListener listener ) {
        this.listeners.add( listener ) ;
    }
    
    @Override
    public final void handleStrategyEvent( SignalStrategyEvent event ) {
        if( event instanceof TradeSignalEvent te ) {
            AbstractTrade trade ;
            trade = te.isBuy() ? handleBuySignal( te ) :
                                 handleSellSignal( te ) ;
            if( trade != null ) {
                trades.add( trade ) ;
                if( trade instanceof BuyTrade buyTrade ) {
                    buyTrades.add( buyTrade ) ;
                    totalBuyPrice += buyTrade.getPrice()*buyTrade.getQuantity() ;
                }
                else {
                    processSellTrade( ( SellTrade )trade );
                }
                updateAttributes( false ) ;
            }
        }
    }
    
    @Override
    public void handleDayValue( AbstractDayValue dayValue ) {
        if( dayValue instanceof OHLCVDayValue ohlc ) {
            this.latestClosingPrice = ohlc.getClose() ;
            this.ema60Price = ohlc.getEma60Price() ;
            updateAttributes( true ) ;
        }
    }
    
    private void updateAttributes( boolean notify ) {
        this.holdingQty = computeUnsoldQty() ;
        this.avgCostPrice = computeAvgCostPrice() ;
        this.totalProfit = computeTotalProfit( latestClosingPrice ) ;
        this.totalProfitPct = computeTotalProfitPct() ;
        this.notionalProfitPct = computeNotionalProfitPct() ;
        
        if( notify ) {
            notifyListeners() ;
        }
    }
    
    private void processSellTrade( SellTrade sellTrade ) {
        
        int sellQty = sellTrade.getQuantity() ;
        int remainingSellQty = sellQty ;
        int unsoldQty = computeUnsoldQty() ;
        
        if( sellQty > unsoldQty ) {
            throw new IllegalArgumentException( "Sell quantity can't be " +
                    "greater than remaining quantity in trade book." ) ;
        }
        
        sellTrades.add( sellTrade ) ;
        while( remainingSellQty > 0 ) {
     
            BuyTrade buyTrade = this.buyTrades.remove( 0 ) ;
     
            buyTrade.setSellTrade( sellTrade ) ;
            sellTrade.addBuyTrade( buyTrade ) ;
            
            remainingSellQty -= buyTrade.getQuantity() ;
        }
    }
    
    // Compute - 1
    private int computeUnsoldQty() {
        holdingQty = 0 ;
        for( BuyTrade trade : buyTrades ) {
            holdingQty += trade.getQuantity() ;
        }
        return this.holdingQty ;
    }
    
    // Compute - 2
    private double computeAvgCostPrice() {
        avgCostPrice = 0 ;
        if( !buyTrades.isEmpty() ) {
            for( BuyTrade trade : buyTrades ) {
                avgCostPrice += trade.getQuantity()*trade.getPrice() ;
            }
            avgCostPrice /= this.holdingQty ;
        }
        return this.avgCostPrice ;
    }
    
    // Compute - 3
    private double computeTotalProfit( double currentPrice ) {
        totalProfit = computeSellProfit() + computeNotionalProfit( currentPrice ) ;
        return totalProfit ;
    }

    // Compute - 4
    private double computeSellProfit() {
        double profit = 0 ;
        for( SellTrade sell : sellTrades ) {
            profit += sell.getProfit() ;
        }
        log.debug( "Sell profit = {}", profit );
        return profit ;
    }
    
    // Compute - 5
    private double computeNotionalProfit( double currentPrice ) {
        notionalProfit = 0 ;
        if( !buyTrades.isEmpty() ) {
            for( BuyTrade buyTrade : buyTrades ) {
                notionalProfit += (currentPrice-buyTrade.getPrice())*buyTrade.getQuantity() ;
            }
        }
        log.debug( "Notional profit = {}", notionalProfit );
        return notionalProfit ;
    }
    
    // Compute - 6
    private double computeTotalProfitPct() {
        if( totalBuyPrice == 0 ) {
            totalProfitPct = 0 ;
        }
        else {
            totalProfitPct = ( this.totalProfit / totalBuyPrice )*100 ;
            log.debug( "Total buy price - {}", totalBuyPrice ) ;
        }
        return totalProfitPct;
    }
    
    // Compute - 7
    private double computeNotionalProfitPct() {
        notionalProfitPct = 0 ;
        if( holdingQty > 0 ) {
            notionalProfitPct = (notionalProfit/(avgCostPrice*holdingQty))*100 ;
        }
        return notionalProfitPct ;
    }

    private void notifyListeners() {
        listeners.forEach( l -> l.tradeBookUpdated( this ) ) ;
    }
    
    protected abstract BuyTrade handleBuySignal( TradeSignalEvent te ) ;
    
    protected abstract SellTrade handleSellSignal( TradeSignalEvent te ) ;
    
}
