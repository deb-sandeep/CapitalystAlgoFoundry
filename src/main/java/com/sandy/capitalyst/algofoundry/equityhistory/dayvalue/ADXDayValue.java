package com.sandy.capitalyst.algofoundry.equityhistory.dayvalue;

import com.sandy.capitalyst.algofoundry.equityhistory.AbstractDayValue;
import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import lombok.Getter;

import java.util.Date;

public class ADXDayValue extends AbstractDayValue {

    @Getter private final double adx;
    @Getter private final double plusDMI ;
    @Getter private final double minusDMI ;
    
    public ADXDayValue( Date date, String symbol,
                        double adx, double plusDMI, double minusDMI ) {

        super( EquityEODHistory.PayloadType.ADX, date, symbol ) ;
        
        this.adx = adx ;
        this.plusDMI = plusDMI ;
        this.minusDMI = minusDMI ;
    }
}
