package com.sandy.capitalyst.algofoundry.strategy.tradebook;

import com.sandy.capitalyst.algofoundry.strategy.util.StringUtil;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.* ;

public class BuyTrade extends Trade {
    
    private final List<SellTrade> sellTrades = new ArrayList<>() ;
    
    @Getter private int unsoldQty ;
    
    public BuyTrade( Date date, double price, int quantity ) {
        super( date, price, quantity ) ;
        this.unsoldQty = quantity ;
    }
    
    public void addSellTrade( SellTrade trade, int quantity ) {
        if( quantity > unsoldQty ) {
            throw new IllegalArgumentException( "Sell quantity > remaining quantity." ) ;
        }
        sellTrades.add( trade ) ;
        this.unsoldQty -= quantity ;
    }
    
    public String toString() {
        return " | " + rightPad( "BUY", 5 ) +
               " | " + rightPad( StringUtil.fmtDate( getDate() ), 10 ) +
               " | " + leftPad ( StringUtil.fmtDbl( getPrice() ), 8 ) +
               " | " + leftPad ( String.valueOf( getQuantity() ), 5 ) +
               " | " + rightPad( "", 7 ) +
               " | " ;
    }
}
