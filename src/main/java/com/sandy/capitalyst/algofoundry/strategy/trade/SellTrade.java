package com.sandy.capitalyst.algofoundry.strategy.trade;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SellTrade extends AbstractTrade {
    
    private final List<BuyTrade> buyTrades = new ArrayList<>() ;
    
    @Getter private double profit = 0 ;
    
    public SellTrade( Date date, double price, int quantity ) {
        super( date, price, quantity );
    }
    
    public void addBuyTrade( BuyTrade buyTrade, int sellQty ) {
        this.buyTrades.add( buyTrade ) ;
        profit += (super.getPrice() - buyTrade.getPrice())*sellQty ;
    }
}
