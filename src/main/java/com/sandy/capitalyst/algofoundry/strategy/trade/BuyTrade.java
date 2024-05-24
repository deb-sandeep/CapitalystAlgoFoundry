package com.sandy.capitalyst.algofoundry.strategy.trade;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class BuyTrade extends AbstractTrade {
    
    @Getter @Setter
    private SellTrade sellTrade ;
    
    public BuyTrade( Date date, double price, int quantity ) {
        super( date, price, quantity );
    }
}
