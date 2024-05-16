package com.sandy.capitalyst.algofoundry.trigger;

import lombok.Getter;
import org.ta4j.core.Trade;

import java.util.Date;

public class TradeTrigger {
    
    @Getter private final Trade.TradeType tradeType ;
    @Getter private final Date date ;
    @Getter private final String symbol ;
    @Getter private final double price ;
    
    TradeTrigger( Trade.TradeType type, Date date, String symbol, double price ) {
        this.tradeType = type ;
        this.date = date ;
        this.symbol = symbol ;
        this.price = price ;
    }
}
