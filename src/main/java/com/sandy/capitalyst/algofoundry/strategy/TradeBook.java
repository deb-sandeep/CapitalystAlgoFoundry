package com.sandy.capitalyst.algofoundry.strategy;

import lombok.Getter;

import java.util.*;

public class TradeBook {
    
    @Getter private double profitEarned   = 0 ;
    @Getter private double notionalProfit = 0 ;
    @Getter private int    quantity       = 0 ;
    @Getter private double avgPrice       = 0 ;
    
    public TradeBook(){}
    
    private void clearBookState() {
        notionalProfit = 0 ;
        profitEarned   = 0 ;
        quantity       = 0 ;
        avgPrice       = 0 ;
    }
}
