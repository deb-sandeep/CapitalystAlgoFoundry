package com.sandy.capitalyst.algofoundry.strategy.tradebook;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.* ;
import static com.sandy.capitalyst.algofoundry.strategy.util.StringUtil.* ;

public class SellTrade extends Trade {
    
    private final List<BuyTrade> buyTrades = new ArrayList<>() ;
    
    @Getter private double profit = 0 ;
    @Getter private double profitPct = 0 ;
    
    private double totalBuyPrice = 0 ;
    
    public SellTrade( Date date, double price, int quantity ) {
        super( date, price, quantity );
    }
    
    public void addBuyTrade( BuyTrade buyTrade, int sellQty ) {
        buyTrades.add( buyTrade ) ;
        
        totalBuyPrice += buyTrade.getPrice() * sellQty ;
        profit += (super.getPrice() - buyTrade.getPrice())*sellQty ;
        profitPct = ( profit / totalBuyPrice )*100 ;
    }
    
    public String toString() {
        return " | " + rightPad( "SELL", 5 ) +
               " | " + rightPad( fmtDate( getDate() ), 10 ) +
               " | " + leftPad( fmtDbl( getPrice() ), 8 ) +
               " | " + leftPad( String.valueOf( getQuantity() ), 5 ) +
               " | " + leftPad( fmtDbl( profitPct ) + "%", 7 ) +
               " | " ;
    }
}
