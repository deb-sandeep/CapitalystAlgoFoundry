package com.sandy.capitalyst.algofoundry.strategy.tradebook;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public abstract class Trade {
    
    @Getter private Date date ;
    @Getter private double price ;
    @Getter private int quantity ;
    
    protected Trade( Date date, double price, int quantity ) {
        this.date = date ;
        this.price = price ;
        this.quantity = quantity ;
    }
}
