package com.sandy.capitalyst.algofoundry.strategy.numseries.series;

import com.sandy.capitalyst.algofoundry.strategy.numseries.NumberSeries;

public class ConstantSeries extends NumberSeries {

    private double constantVal ;
    
    public static ConstantSeries of( double val ) {
        return new ConstantSeries( val ) ;
    }
    
    public void setThreshold( double newVal ) {
        this.constantVal = newVal ;
    }
    
    public double getThreshold() { return this.constantVal ; }
    
    public ConstantSeries( double val ) {
        this.constantVal = val ;
    }
    
    public double getValue( int index ) {
        return this.constantVal ;
    }
}
