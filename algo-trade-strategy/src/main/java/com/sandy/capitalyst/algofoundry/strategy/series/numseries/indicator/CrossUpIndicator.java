package com.sandy.capitalyst.algofoundry.strategy.series.numseries.indicator;

import com.sandy.capitalyst.algofoundry.strategy.series.numseries.SeriesIndicator;
import com.sandy.capitalyst.algofoundry.strategy.series.numseries.NumberSeries;

public class CrossUpIndicator implements SeriesIndicator {
    
    private final NumberSeries series1 ;
    private final NumberSeries series2 ;
    
    public CrossUpIndicator( NumberSeries series1, NumberSeries series2 ) {
        this.series1 = series1 ;
        this.series2 = series2 ;
    }
    
    @Override
    public boolean isSatisfied( int index ) {
        if( index < 1 ) {
            return false ;
        }
        
        double diffLast = series1.getValue( index-1 ) - series2.getValue( index-1 ) ;
        double diffCurr = series1.getValue( index ) - series2.getValue( index ) ;
        
        return diffLast <= 0 && diffCurr > 0 ;
    }
}
