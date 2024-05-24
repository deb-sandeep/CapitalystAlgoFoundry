package com.sandy.capitalyst.algofoundry.strategy.trade;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BuyTrade extends AbstractTrade {
    
    private final List<SellTrade> sellTrades = new ArrayList<>() ;
    
    @Getter private int quantityLeft = 0 ;
    
    public BuyTrade( Date date, double price, int quantity ) {
        super( date, price, quantity ) ;
        this.quantityLeft = quantity ;
    }
    
    public void addSellTrade( SellTrade trade, int quantity ) {
        if( quantity > quantityLeft ) {
            throw new IllegalArgumentException( "Quantity > remaining quantity." ) ;
        }
        sellTrades.add( trade ) ;
        this.quantityLeft -= quantity ;
    }
}
