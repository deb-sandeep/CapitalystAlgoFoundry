package com.sandy.capitalyst.algofoundry.strategy.series.numseries;

import lombok.Getter;
import lombok.Setter;

public class ConstantSeries extends NumberSeries {

    @Getter @Setter
    private double constantValue;
    
    public static ConstantSeries of( double val ) {
        return new ConstantSeries( val ) ;
    }
    
    public ConstantSeries( double val ) {
        this.constantValue = val ;
    }
    
    public double getValue( int index ) {
        return this.constantValue;
    }
}
