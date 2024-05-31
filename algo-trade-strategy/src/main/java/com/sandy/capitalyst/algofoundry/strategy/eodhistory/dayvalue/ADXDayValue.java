package com.sandy.capitalyst.algofoundry.strategy.eodhistory.dayvalue;

import com.sandy.capitalyst.algofoundry.strategy.eodhistory.AbstractDayValue;
import com.sandy.capitalyst.algofoundry.strategy.eodhistory.EquityEODHistory;
import lombok.Getter;
import org.ta4j.core.Bar;

import java.util.Date;

public class ADXDayValue extends AbstractDayValue {

    @Getter private final double adx;
    @Getter private final double plusDMI ;
    @Getter private final double minusDMI ;
    
    public ADXDayValue( Date date, Bar bar, String symbol,
                        double adx, double plusDMI, double minusDMI ) {

        super( EquityEODHistory.PayloadType.ADX, date, bar, symbol ) ;
        
        this.adx = adx ;
        this.plusDMI = plusDMI ;
        this.minusDMI = minusDMI ;
    }
}
