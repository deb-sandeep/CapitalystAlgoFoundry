package com.sandy.capitalyst.algofoundry.strategy.strategy;

import com.sandy.capitalyst.algofoundry.equityhistory.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.strategy.AbstractTradeStrategy;
import com.sandy.capitalyst.algofoundry.strategy.TradeRule;
import com.sandy.capitalyst.algofoundry.strategy.TradeSignal;
import org.ta4j.core.Bar;

import java.util.Date;

public abstract class AbstractZonedTradeStrategy extends AbstractTradeStrategy {
    
    private static final int ACTIVATION_WINDOW = 5 ;
    private static final int POST_TRADE_COOLOFF_PERIOD = 5 ;
    
    private int blackoutDaysLeft    = 20 ;
    private int activeEntryDaysLeft = 0 ;
    private int activeExitDaysLeft  = 0 ;
    
    private TradeRule entryActivationRule ;
    private TradeRule exitActivationRule;
    
    protected AbstractZonedTradeStrategy( EquityEODHistory history ) {
        super( history ) ;
    }
    
    public void clear() {
        super.clear() ;
        this.blackoutDaysLeft = 20 ;
        this.activeEntryDaysLeft = 0 ;
        this.activeExitDaysLeft = 0 ;
    }
    
    protected final void setNumBlackoutDays( int numDays ) {
        this.blackoutDaysLeft = numDays ;
    }
    
    protected final TradeRule getEntryActivationRule() {
        if( entryActivationRule == null ) {
            entryActivationRule = createEntryActivationRule() ;
        }
        return entryActivationRule ;
    }
    
    protected final TradeRule getExitActivationRule() {
        if( exitActivationRule == null ) {
            exitActivationRule = createExitActivationRule() ;
        }
        return exitActivationRule;
    }
    
    protected boolean isInBlackoutPeriod() {
        return this.blackoutDaysLeft > 0 ;
    }
    
    protected boolean isInEntryActivePeriod() {
        return this.activeEntryDaysLeft > 0 ;
    }
    
    protected boolean isInExitActivePeriod() {
        return this.activeExitDaysLeft > 0 ;
    }
    
    protected int getNumDaysIntoEntryActivationPeriod() {
        if( isInEntryActivePeriod() ) {
            return ACTIVATION_WINDOW - activeEntryDaysLeft ;
        }
        return 0 ;
    }
    
    protected int getNumDaysIntoExitActivationPeriod() {
        if( isInExitActivePeriod() ) {
            return ACTIVATION_WINDOW - activeExitDaysLeft ;
        }
        return 0 ;
    }
    
    public TradeSignal executeStrategy( int seriesIndex, Date date, Bar bar ) {
        
        TradeSignal signal = null ;
        double closingPrice = bar.getClosePrice().doubleValue() ;
        
        if( isInBlackoutPeriod() ) {
            // We do not signal in a blackout period
            //logger.log( "In blackout period" ) ;
            blackoutDaysLeft-- ;
        }
        else if( !( isInEntryActivePeriod() || isInEntryActivePeriod() ) ) {
            //logger.log( "In lookout period" ) ;
            if( isEntryActivationTriggered( seriesIndex ) ) {
                logger.log( "Entry activation triggered" ) ;
                activeEntryDaysLeft = ACTIVATION_WINDOW ;
                activeExitDaysLeft = 0 ;
            }
            else if( isExitActivationTriggered( seriesIndex ) ) {
                logger.log( "Exit activation triggered" ) ;
                activeEntryDaysLeft = 0 ;
                activeExitDaysLeft = ACTIVATION_WINDOW ;
            }
        }
        
        if( isInEntryActivePeriod() ) {
            if( isEntryPreconditionMet( seriesIndex ) ) {
                logger.log1( "Entry precondition met" ) ;
                if( getEntryRule().isTriggered( seriesIndex ) ) {
                    logger.log1( "Entry rule triggered." ) ;
                    signal = new TradeSignal( TradeSignal.Type.ENTRY, date,
                             history.getSymbol(), closingPrice ) ;
                }
            }
            activeEntryDaysLeft-- ;
        }
        else if( isInExitActivePeriod() ) {
            if( isExitPreconditionMet( seriesIndex ) ) {
                logger.log1( "Exit precondition met" ) ;
                if( getExitRule().isTriggered( seriesIndex ) ) {
                    logger.log1( "Exit rule triggered." ) ;
                    signal = new TradeSignal( TradeSignal.Type.EXIT, date,
                            history.getSymbol(), closingPrice ) ;
                }
            }
            activeExitDaysLeft-- ;
        }
        
        if( signal != null ) {
            activeExitDaysLeft = 0 ;
            activeEntryDaysLeft = 0 ;
            blackoutDaysLeft = POST_TRADE_COOLOFF_PERIOD ;
        }
        
        return signal ;
    }
    
    private boolean isEntryActivationTriggered( int seriesIndex ) {
        return getEntryActivationRule().isTriggered( seriesIndex ) ;
    }
    
    private boolean isExitActivationTriggered( int seriesIndex ) {
        return getExitActivationRule().isTriggered( seriesIndex ) ;
    }
    
    protected abstract boolean isEntryPreconditionMet( int seriesIndex ) ;
    
    protected abstract boolean isExitPreconditionMet( int seriesIndex ) ;
    
    protected abstract TradeRule createEntryActivationRule() ;
    
    protected abstract TradeRule createExitActivationRule() ;
}
