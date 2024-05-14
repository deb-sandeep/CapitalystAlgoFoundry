package com.sandy.capitalyst.algofoundry.ui.indchart;

import com.sandy.capitalyst.algofoundry.core.indicator.IndicatorType;
import com.sandy.capitalyst.algofoundry.core.ui.IndicatorChart;

public class PriceChart extends IndicatorChart {
    
    public PriceChart( String symbol ) {
        super( symbol, "Price" ) ;
        super.addIndicatorTimeSeries( IndicatorType.CLOSING_PRICE ) ;
    }
}
