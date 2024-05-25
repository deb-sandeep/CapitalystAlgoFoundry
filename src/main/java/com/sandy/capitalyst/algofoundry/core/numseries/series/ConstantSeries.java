package com.sandy.capitalyst.algofoundry.core.numseries.series;

import com.sandy.capitalyst.algofoundry.core.numseries.NumberSeries;

public class ConstantSeries extends NumberSeries {

    private double constantVal ;
    
    public static ConstantSeries of( double val ) {
        return new ConstantSeries( val ) ;
    }
    
    public void setThreshold( double newVal ) {
        this.constantVal = newVal ;
    }
    
    public ConstantSeries( double val ) {
        this.constantVal = val ;
    }
    
    public double getValue( int index ) {
        return this.constantVal ;
    }
}
