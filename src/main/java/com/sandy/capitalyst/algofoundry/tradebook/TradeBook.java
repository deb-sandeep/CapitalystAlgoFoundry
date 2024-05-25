package com.sandy.capitalyst.algofoundry.tradebook;

import com.sandy.capitalyst.algofoundry.core.numseries.NumberSeries;
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

import static org.apache.commons.lang3.StringUtils.repeat;
import static org.apache.commons.lang3.StringUtils.rightPad;

@Slf4j
public abstract class TradeBook
        implements SignalStrategyEventListener, DayValueListener {
    
    protected final List<Trade>     allTrades  = new ArrayList<>() ;
    protected final List<BuyTrade>  buyTrades  = new ArrayList<>() ;
    protected final List<SellTrade> sellTrades = new ArrayList<>() ;
    
    private final List<TradeBookListener> listeners = new ArrayList<>() ;
    
    @Getter private double totalProfit       = 0 ;
    @Getter private double totalProfitPct    = 0 ;
    @Getter private double totalSellProfit   = 0 ;
    @Getter private double notionalProfit    = 0 ;
    @Getter private double notionalProfitPct = 0 ;
    @Getter private int    holdingQty        = 0 ;
    @Getter private double avgCostPrice      = 0 ;
    @Getter private double totalBuyPrice     = 0 ;
    
    private double latestClosingPrice = 0 ;
    
    protected final NumberSeries notionalProfitPctSeries = new NumberSeries() ;
    
    public void clearState() {
        this.allTrades.clear() ;
        this.buyTrades.clear() ;
        this.sellTrades.clear() ;
        
        this.totalProfit       = 0 ;
        this.totalProfitPct    = 0 ;
        this.totalSellProfit   = 0 ;
        this.notionalProfit    = 0 ;
        this.notionalProfitPct = 0 ;
        this.totalBuyPrice     = 0 ;
        this.holdingQty        = 0 ;
        this.avgCostPrice      = 0 ;
        
        this.notionalProfitPctSeries.clear() ;
        
        notifyListeners() ;
    }
    
    public void addListener( TradeBookListener listener ) {
        this.listeners.add( listener ) ;
    }
    
    public void removeListeners() {
        this.listeners.clear() ;
    }
    
    @Override
    public final void handleStrategyEvent( SignalStrategyEvent event ) {
        
        if( event instanceof TradeSignalEvent tse ) {
            Trade trade ;
            trade = tse.isBuy() ? handleBuySignal( tse ) :
                                  handleSellSignal( tse ) ;
            
            if( trade != null ) {
                
                allTrades.add( trade ) ;
                
                if( trade instanceof BuyTrade buyTrade ) {
                    processBuyTrade( buyTrade ) ;
                    listeners.forEach( l -> l.buyTradeExecuted( buyTrade ) ) ;
                }
                else {
                    SellTrade sellTrade = (SellTrade)trade ;
                    processSellTrade( sellTrade ) ;
                    listeners.forEach( l -> l.sellTradeExecuted( sellTrade ) ) ;
                }
                print() ;
            }
        }
    }
    
    @Override
    public void handleDayValue( AbstractDayValue dayValue ) {
        if( dayValue instanceof OHLCVDayValue ohlc ) {
            this.latestClosingPrice = ohlc.getClose() ;
            computeProfit() ;
            notifyListeners() ;
        }
    }
    
    protected void processBuyTrade( BuyTrade buyTrade ) {
        
        buyTrades.add( buyTrade ) ;
        
        totalBuyPrice += buyTrade.getPrice()*buyTrade.getQuantity() ;
        holdingQty += buyTrade.getQuantity() ;
        avgCostPrice = computeAvgCostPrice() ;
        
        computeProfit() ;
        notifyListeners() ;
    }
    
    protected void processSellTrade( SellTrade sellTrade ) {
        
        int    sellQty          = sellTrade.getQuantity() ;
        int    remainingSellQty = sellQty ;
        
        if( sellQty > holdingQty ) {
            throw new IllegalArgumentException(
                        "Sell quantity can't be great than " +
                        "remaining quantity in trade book." ) ;
        }
        
        sellTrades.add( sellTrade ) ;
        
        while( remainingSellQty > 0 ) {
     
            BuyTrade buyTrade = this.buyTrades.get( 0 ) ;

            int buyQtyLeft = buyTrade.getUnsoldQty() ;
            int qtyToSell  = Math.min( remainingSellQty, buyQtyLeft ) ;
            
            buyTrade.addSellTrade( sellTrade, qtyToSell ) ;
            sellTrade.addBuyTrade( buyTrade, qtyToSell ) ;
            
            remainingSellQty -= qtyToSell ;
            holdingQty -= qtyToSell ;
            
            if( buyTrade.getUnsoldQty() == 0 ) {
                this.buyTrades.remove( 0 ) ;
            }
        }
        
        totalSellProfit += sellTrade.getProfit() ;
        avgCostPrice     = computeAvgCostPrice() ;
        
        computeProfit() ;
        notifyListeners() ;
    }
    
    private double computeAvgCostPrice() {
        double avgCP = 0 ;
        if( !buyTrades.isEmpty() ) {
            for( BuyTrade trade : buyTrades ) {
                avgCP += trade.getUnsoldQty()*trade.getPrice() ;
            }
            avgCP /= holdingQty ;
        }
        return avgCP ;
    }
    
    private void computeProfit() {
        
        totalProfit = 0 ;
        notionalProfit = 0 ;
        totalProfitPct = 0 ;
        notionalProfitPct = 0 ;
        
        if( !buyTrades.isEmpty() ) {
            for( BuyTrade buyTrade : buyTrades ) {
                notionalProfit += (latestClosingPrice-buyTrade.getPrice())*buyTrade.getUnsoldQty() ;
            }
            notionalProfitPct = (notionalProfit/(avgCostPrice*holdingQty))*100 ;
        }

        if( totalBuyPrice > 0 ) {
            totalProfit = totalSellProfit + notionalProfit ;
            totalProfitPct = ( totalProfit / totalBuyPrice )*100 ;
        }
    }
    
    private void notifyListeners() {
        listeners.forEach( l -> l.tradeBookUpdated( this ) ) ;
    }
    
    protected abstract BuyTrade handleBuySignal( TradeSignalEvent te ) ;
    
    protected abstract SellTrade handleSellSignal( TradeSignalEvent te ) ;
    
    public void print() {
        
        String hdr = " | " + rightPad( "SELL", 5 ) +
                     " | " + rightPad( "Date", 10 ) +
                     " | " + rightPad( "Price", 8 ) +
                     " | " + rightPad( "Qty",   5 ) +
                     " | " + rightPad( "Profit%", 7 ) +
                     " | " ;

        String line  = " + " + repeat( "-",  5 ) +
                       " + " + repeat( "-", 10 ) +
                       " + " + repeat( "-",  8 ) +
                       " + " + repeat( "-",  5 ) +
                       " + " + repeat( "-",  7 ) +
                       " + " ;
        
        log.debug( hdr ) ;
        log.debug( line ) ;
        allTrades.forEach( t -> log.debug( t.toString() ) ) ;
        log.debug( line ) ;
    }
}
