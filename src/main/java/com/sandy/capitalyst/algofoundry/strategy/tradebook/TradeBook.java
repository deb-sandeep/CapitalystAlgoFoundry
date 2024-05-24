package com.sandy.capitalyst.algofoundry.strategy.tradebook;

import com.sandy.capitalyst.algofoundry.equityhistory.AbstractDayValue;
import com.sandy.capitalyst.algofoundry.equityhistory.DayValueListener;
import com.sandy.capitalyst.algofoundry.equityhistory.dayvalue.OHLCVDayValue;
import com.sandy.capitalyst.algofoundry.strategy.StrategyEvent;
import com.sandy.capitalyst.algofoundry.strategy.StrategyEventListener;
import com.sandy.capitalyst.algofoundry.strategy.event.TradeEvent;
import com.sandy.capitalyst.algofoundry.strategy.tradebook.trade.BuyTrade;
import com.sandy.capitalyst.algofoundry.strategy.tradebook.trade.SellTrade;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class TradeBook
        implements StrategyEventListener, DayValueListener {
    
    protected final List<AbstractTrade> trades     = new ArrayList<>() ;
    protected final List<BuyTrade>      buyTrades  = new ArrayList<>() ;
    protected final List<SellTrade>     sellTrades = new ArrayList<>() ;
    
    private final List<TradeBookListener> listeners = new ArrayList<>() ;
    
    @Getter private double totalProfit    = 0 ;
    @Getter private double totalProfitPct = 0 ;
    @Getter private double totalBuyPrice  = 0 ;
    @Getter private int    holdingQty     = 0 ;
    @Getter private double avgCostPrice   = 0 ;
    
    private double latestClosingPrice = 0 ;
    
    public void clear() {
        this.trades.clear() ;
        this.buyTrades.clear() ;
        this.sellTrades.clear() ;
        this.listeners.clear() ;
        
        this.totalProfit    = 0 ;
        this.totalProfitPct = 0 ;
        this.totalBuyPrice  = 0 ;
        this.holdingQty     = 0 ;
        this.avgCostPrice   = 0 ;
        
        notifyListeners() ;
    }
    
    public void addListener( TradeBookListener listener ) {
        this.listeners.add( listener ) ;
    }
    
    @Override
    public final void handleStrategyEvent( StrategyEvent event ) {
        if( event instanceof TradeEvent te ) {
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
            updateAttributes( true ) ;
        }
    }
    
    private void updateAttributes( boolean notify ) {
        this.holdingQty = computeUnsoldQty() ;
        this.totalProfit = computeTotalProfit( latestClosingPrice ) ;
        this.totalProfitPct = computeProfitPct( latestClosingPrice ) ;
        this.avgCostPrice = computeAvgCostPrice() ;
        
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
    
    private int computeUnsoldQty() {
        holdingQty = 0 ;
        for( BuyTrade trade : buyTrades ) {
            holdingQty += trade.getQuantity() ;
        }
        return this.holdingQty ;
    }
    
    private double computeAvgCostPrice() {
        avgCostPrice = 0 ;
        if( !buyTrades.isEmpty() ) {
            for( BuyTrade trade : buyTrades ) {
                avgCostPrice += trade.getQuantity()*trade.getPrice() ;
            }
            avgCostPrice /= buyTrades.size() ;
        }
        return this.avgCostPrice ;
    }
    
    private double computeTotalProfit( double currentPrice ) {
        totalProfit = computeSellProfit() + computeNotionalProfit( currentPrice ) ;
        return totalProfit ;
    }
    
    private double computeProfitPct( double currentPrice ) {
        if( totalBuyPrice == 0 ) {
            totalProfitPct = 0 ;
        }
        else {
            totalProfitPct = ( this.totalProfit / totalBuyPrice )*100 ;
            log.debug( "Total buy price - {}", totalBuyPrice ) ;
        }
        return totalProfitPct;
    }

    private double computeSellProfit() {
        double profit = 0 ;
        for( SellTrade sell : sellTrades ) {
            profit += sell.getProfit() ;
        }
        log.debug( "Sell profit = {}", profit );
        return profit ;
    }
    
    private double computeNotionalProfit( double currentPrice ) {
        double notionalProfit = 0 ;
        if( !buyTrades.isEmpty() ) {
            for( BuyTrade buyTrade : buyTrades ) {
                notionalProfit += (currentPrice-buyTrade.getPrice())*buyTrade.getQuantity() ;
            }
        }
        log.debug( "Notional profit = {}", notionalProfit );
        return notionalProfit ;
    }
    
    private void notifyListeners() {
        listeners.forEach( l -> l.tradeBookUpdated( this ) ) ;
    }
    
    protected abstract BuyTrade handleBuySignal( TradeEvent te ) ;
    
    protected abstract SellTrade handleSellSignal( TradeEvent te ) ;
    
}
