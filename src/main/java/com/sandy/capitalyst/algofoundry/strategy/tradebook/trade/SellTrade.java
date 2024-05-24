package com.sandy.capitalyst.algofoundry.strategy.tradebook.trade;

import com.sandy.capitalyst.algofoundry.strategy.tradebook.AbstractTrade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SellTrade extends AbstractTrade {
    
    private final List<BuyTrade> buyTrades = new ArrayList<>() ;
    
    public SellTrade( Date date, double price, int quantity ) {
        super( date, price, quantity );
    }
    
    public void addBuyTrade( BuyTrade buyTrade ) {
        this.buyTrades.add( buyTrade ) ;
    }
    
    public double getProfit() {
        double profit = 0 ;
        for( BuyTrade buy : buyTrades ) {
            profit += (super.getPrice() - buy.getPrice())*buy.getQuantity() ;
        }
        return profit ;
    }
}
