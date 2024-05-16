package com.sandy.capitalyst.algofoundry.strategy;

import lombok.Getter;
import org.ta4j.core.num.Num;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TradeBook {
    
    private List<Trade> trades = new ArrayList<>() ;
    
    @Getter private double profitEarned   = 0 ;
    @Getter private double notionalProfit = 0 ;
    @Getter private int    quantity       = 0 ;
    @Getter private double avgPrice       = 0 ;
    
    TradeBook(){}
    
    public void addTrade( Trade trade ) {
        trades.add( trade ) ;
        computeBookState() ;
    }
    
    private void computeBookState() {
        clearBookState() ;
        Queue<Trade> buyTrades = new LinkedList<>() ;
        for( Trade trade : trades ) {
            if( trade.isBuy() ) {
                quantity++ ;
                buyTrades.add( trade ) ;
            }
            else {
                quantity-- ;
                Trade buy = buyTrades.remove() ;
                profitEarned += trade.getPrice() - buy.getPrice() ;
            }
        }
        
        double cost = 0 ;
        for( Trade trade : buyTrades ) {
            cost += trade.getPrice() ;
        }
        avgPrice = (double)(cost/buyTrades.size()) ;
    }
    
    public void computeNotionalProfit( double closePrice ) {
        notionalProfit = quantity * ( closePrice - avgPrice ) ;
    }

    public int getNumTrades() {
        return trades.size() ;
    }
    
    public boolean isEmpty() {
        return getNumTrades() == 0 ;
    }
    
    public void clear() {
        trades.clear() ;
        clearBookState() ;
    }
    
    private void clearBookState() {
        profitEarned = 0 ;
        quantity = 0 ;
        avgPrice = 0 ;
    }
    
}
