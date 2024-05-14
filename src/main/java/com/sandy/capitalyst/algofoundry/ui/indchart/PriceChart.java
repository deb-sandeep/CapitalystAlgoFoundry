package com.sandy.capitalyst.algofoundry.ui.indchart;

import com.sandy.capitalyst.algofoundry.core.indicator.IndicatorType;
import com.sandy.capitalyst.algofoundry.core.ui.IndicatorChart;

import static com.sandy.capitalyst.algofoundry.core.indicator.IndicatorType.* ;

public class PriceChart extends IndicatorChart {
    
    public PriceChart( String symbol ) {
        super( symbol, "Price" ) ;
        super.addIndicatorSeries( CLOSING_PRICE, BOLLINGER_MID,
                                  BOLLINGER_UP, BOLLINGER_LOW ) ;
    }
}
