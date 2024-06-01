package com.sandy.capitalyst.algofoundry.strategy.series.numseries;

import java.util.ArrayList;
import java.util.List;

public class NumberSeries {
    
    private final List<Double> values = new ArrayList<>() ;
    
    public NumberSeries(){}
    
    public void add( double d ) { values.add( d ) ; }
    
    public int getSize() { return values.size() ; }
    
    public double getValue( int index ) {
        return values.get( index ) ;
    }
    
    public void clear() {
        this.values.clear() ;
    }
}
