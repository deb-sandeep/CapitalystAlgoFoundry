package com.sandy.capitalyst.algofoundry.app.tuner;

import lombok.Getter;

public class HyperParameter {
    
    @Getter private final String fieldName ;
    @Getter private final float min ;
    @Getter private final float max ;
    @Getter private final float step ;
    @Getter private final int numSteps ;
    
    @Getter private float currentStep = 0 ;
    
    public HyperParameter( String fName, float min, float max, float step ) {
        assert min <= max ;
        assert step >= 0 ;

        this.fieldName = fName ;
        this.min = min ;
        this.max = max ;
        this.step = step ;
        this.numSteps = (int)((max-min)/step) + 1 ;
    }
    
    public boolean hasNextVal() {
        return currentStep < numSteps-1 ;
    }
    
    public float currentVal() {
        return min + currentStep*step ;
    }
    
    public void setCurrentStep( int step ) {
        assert step >=0 && step < numSteps ;
        currentStep = step ;
    }
    
    public void incrementStep() {
        currentStep++ ;
        if( currentStep >= numSteps ) {
            currentStep = numSteps-1 ;
            throw new IllegalStateException( "No next value for hyper parameter - " + fieldName ) ;
        }
    }
    
    public float nextVal() {
        incrementStep() ;
        return currentVal() ;
    }
    
    public String toString() {
        return "HyperParameter : name = " + this.fieldName +
                ", min = " + this.min +
                ", max = " + this.max +
                ", step = " + this.step +
                ", #iterations = " + getNumSteps() ;
    }
}
