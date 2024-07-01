package com.sandy.capitalyst.algofoundry.app.tuner;

import com.sandy.capitalyst.algofoundry.strategy.impl.MyStrategyConfig;

public class Test {
    public static void main( String[] args ) throws Exception {
        
        MyStrategyConfig config = new MyStrategyConfig() ;
        GridSearch gs = new GridSearch( null, config ) ;
        gs.tuneHyperParameters() ;
    }
}
