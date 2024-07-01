package com.sandy.capitalyst.algofoundry.app.tuner;

import com.sandy.capitalyst.algofoundry.strategy.impl.MyStrategyConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Slf4j
public class HyperParameterGroup {
    
    @Getter private List<HyperParameter> parameters ;
    
    private int activeParamIndex = 0 ;
    private boolean traversalComplete = false ;
    
    public HyperParameterGroup( List<HyperParameter> parameters ) {
        this.parameters = parameters ;
    }
    
    public boolean isGridExplorationComplete() {
        for( HyperParameter param : parameters ) {
            if( param.hasNextVal() ) {
                return false ;
            }
        }
        return true ;
    }
    
    public void populateNextGridValue( MyStrategyConfig config )
        throws Exception {
        for( HyperParameter p : parameters ) {
            BeanUtils.setProperty( config, p.getFieldName(), p.currentVal() ) ;
        }
        traversalComplete = incrementGridPoint( 0 ) ;
    }
    
    private static String ident( int spaces ) {
        return StringUtils.repeat( ' ', spaces ) ;
    }
    
    private boolean incrementGridPoint( int paramIndex ) {
        
        HyperParameter p = parameters.get( paramIndex ) ;
        log.debug( "{}Param {} [{}] - Incrementing grid point.",
                   ident( paramIndex ), paramIndex, p.getFieldName() ) ;

        // If we are at the last parameter
        if( paramIndex == parameters.size()-1 ) {
            // If there is a scope of incrementing the last parameter,
            // do so and return true.
            if( p.hasNextVal() ) {
                p.incrementStep() ;
                log.debug( "{} Incremented last param step. Current val = {}",
                           ident( paramIndex ), p.currentVal() ) ;
                return true ;
            }
            else {
                // If there is no scope of incrementing the last parameter
                // return a false, so that the upper cogs can turn
                log.debug( "{} Last param exploration complete.", ident( paramIndex ) ) ;
                return false ;
            }
        }
        else {
            // If we are at a parameter which is not the last, we try to
            // increment the step counter of the next parameter. This will
            // recursively drill down to the last paramter.
            boolean nextParamIncremented = incrementGridPoint( paramIndex+1 ) ;
            
            // If the step count of the parameters below has been incremented
            // we bubble up. No further step increments are needed.
            if( nextParamIncremented ) {
                return true ;
            }
            else {
                // If the next parameter could not be incremented further,
                // we try to see if the current parameter step can be incremented.
                // If so, we increment the current parameter step and reset
                // the step counters of all subsequent parameters to 0 and
                // inform the recursion step above that we have successfully
                // incremented the grid reference.
                //
                // However, if the current parameter is also at the step end,
                // we return a false letting the parameter above us know that
                // it has to increment its step count and reset the ones below it.
                if( p.hasNextVal() ) {
                    p.incrementStep() ;
                    log.info( "{} {} = {}",
                               ident( paramIndex ), p.getFieldName(), p.currentVal() ) ;
                    resetGridPoint( paramIndex+1 ) ;
                    return  true ;
                }
                return false ;
            }
        }
    }
    
    private void resetGridPoint( int paramIndex ) {
        if( paramIndex < parameters.size() ) {
            HyperParameter p = parameters.get( paramIndex ) ;
            p.setCurrentStep( 0 ) ;
            resetGridPoint( paramIndex+1 ) ;
        }
    }
}
