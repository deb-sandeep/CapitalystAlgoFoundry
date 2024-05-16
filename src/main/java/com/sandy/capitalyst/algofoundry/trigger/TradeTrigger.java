package com.sandy.capitalyst.algofoundry.trigger;

import lombok.Getter;
import org.ta4j.core.Trade;

import java.util.Date;

public class TradeTrigger {
    
    @Getter private final Trade.TradeType tradeType ;
    @Getter private final Date date ;
    
    TradeTrigger( Trade.TradeType type, Date date ) {
        this.tradeType = type ;
        this.date = date ;
    }
}
