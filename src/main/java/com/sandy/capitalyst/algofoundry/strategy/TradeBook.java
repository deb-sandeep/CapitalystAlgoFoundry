package com.sandy.capitalyst.algofoundry.strategy;

import lombok.Getter;

import java.util.*;

public class TradeBook {
    
    private static final int POST_TRADE_COOLOFF_DAYS = 5 ;
    
    private final List<TradeSignal> trades = new ArrayList<>() ;
    
    @Getter private double profitEarned   = 0 ;
    @Getter private double notionalProfit = 0 ;
    @Getter private int    quantity       = 0 ;
    @Getter private double avgPrice       = 0 ;
    
    private int lastTradeSeriesIndex = 0 ;
    
    public TradeBook(){}
    
    public void clear() {
        trades.clear() ;
        clearBookState() ;
    }
    
    public void handleTradeSignal( int seriesIndex, Date date,
                                   double closePrice, TradeSignal signal ) {
        if( signal != null && !isInCooloffPeriod( seriesIndex ) ) {
            addTrade( signal ) ;
            lastTradeSeriesIndex = seriesIndex ;
        }
        computeNotionalProfit( closePrice ) ;
    }
    
    private boolean isInCooloffPeriod( int currentSeriesIndex ) {
        return ( currentSeriesIndex - lastTradeSeriesIndex ) <= POST_TRADE_COOLOFF_DAYS ;
    }
    
    private void computeNotionalProfit( double closePrice ) {
        notionalProfit = quantity * ( closePrice - avgPrice ) ;
    }

    public void addTrade( TradeSignal trade ) {
        trades.add( trade ) ;
        computeBookState() ;
    }
    
    private void computeBookState() {
        clearBookState() ;
        Queue<TradeSignal> buyTrades = new LinkedList<>() ;
        for( TradeSignal trade : trades ) {
            if( trade.isEntrySignal() ) {
                quantity++ ;
                buyTrades.add( trade ) ;
            }
            else {
                if( !buyTrades.isEmpty() ) {
                    while( !buyTrades.isEmpty() ) {
                        TradeSignal buy = buyTrades.remove() ;
                        profitEarned += trade.getPrice() - buy.getPrice() ;
                    }
                    quantity = 0 ;
                }
            }
        }
        
        avgPrice = 0 ;
        if( !buyTrades.isEmpty() ) {
            double cost = 0 ;
            for( TradeSignal trade : buyTrades ) {
                cost += trade.getPrice() ;
            }
            avgPrice = cost/buyTrades.size() ;
        }
    }
    
    private void clearBookState() {
        profitEarned = 0 ;
        quantity = 0 ;
        avgPrice = 0 ;
    }
}
