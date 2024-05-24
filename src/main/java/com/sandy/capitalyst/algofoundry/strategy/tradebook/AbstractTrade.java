package com.sandy.capitalyst.algofoundry.strategy.tradebook;

import lombok.Getter;

import java.util.Date;

public class AbstractTrade {
    
    @Getter private Date date ;
    @Getter private double price ;
    @Getter private int quantity ;
    
    protected AbstractTrade( Date date, double price, int quantity ) {
        this.date = date ;
        this.price = price ;
        this.quantity = quantity ;
    }
}
