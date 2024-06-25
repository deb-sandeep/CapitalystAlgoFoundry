package com.sandy.capitalyst.algofoundry.strategy.impl;

import com.sandy.capitalyst.algofoundry.strategy.StrategyConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode( callSuper = true )
@Data
public class MyStrategyConfig extends StrategyConfig {
    
    /** Max amount of one investment. */
    private float investmentQuantum  = 25000 ;
    
    /**
     * The percentage by which successive investments need to be reduced. For
     * example, the first buy transaction starts with an upper cap of the
     * amount specified with investmentQuantum amount. If there is another
     * buy opportunity immediate following (without having a sell in between),
     * the investment quantum will be reduced by this percentage, i.e.
     * 0.9 * investmentQuantum, the successive one after that will be further
     * reduced as 0.8 * investmentQuantum and so forth.
     * <p>
     * Why? For few reasons:
     * <ul>
     *     <li>If we are on an upswing, the higher we go, the more the
     *     probability of the trend breaking</li>
     *     <li>We need to limit our exposure in a bull cycle.</li>
     *     <li>The earlier we buy, the better we can leverage the upswing</li>
     * </ul>
     * Note that the value of this configuration is from 0-100.
     */
    private float successiveInvestmentTaperPct = 10 ;
    
    /**
     * Number of days to cool off after a buy. In the cooloff period any buy
     * signals are ignored.
     */
    private int buyCooloffDuration = 6 ;
    
    /** If true, strategy decisions will be logged. */
    private boolean logStrategyDecisions = true ;
    
    /**
     * The gap in number of days across which indicators (EMA/SMA) is
     * compared to check against a threshold value.
     */
    private int zoneActivationTestGap = 3 ;
    
    /**
     * The buy activation EMA 20 jump which will trigger an entry into
     * the buy zone. This value is used in conjunction with the activation
     * test gap. So, with the default setting if the EMA 20 value jumps
     * more than 1% in 3 days, it triggers a buy zone activation.
     */
    private float ema20JumpForBuyZoneActivation = 1 ;
    
    /**
     * The sell activation EMA 20 dip which will trigger an entry into
     * the sell zone. This value is used in conjunction with the activation
     * test gap. So, with the default setting if the EMA 20 value dips
     * more than -1% in 3 days, it triggers a buy zone activation.
     * <p>
     * NOTE: This value should be specified as negative.
     */
    private float ema20DipForSellZoneActivation = -1 ;
    
    /**
     * Once we are within the buy zone, one of the conditions of generating
     * a buy signal is that the EMA5 should have jumped more than the
     * specified percentage from the start of the zone.
     * <p>
     * This parameter specifies the minimum EMA5 jump within the buy zone.
     * This value is enhanced with the ema5JumpIncrementPerDayForBuySignal
     * parameter.
     */
    private float ema5JumpForBuySignal = 1 ;
    
    /**
     * The percentage by which the EMA5 should increase per day within the
     * buy zone to positively contribute towards generating a buy signal.
     */
    private float ema5JumpIncrementPerDayForBuySignal = 0.125f ;
    
    /**
     * Once we are within the sell zone, one of the conditions of generating
     * a sell signal is that the EMA5 should have dipped more than the
     * specified percentage from the start of the zone.
     * <p>
     * This parameter specifies the minimum EMA5 dip within the buy zone.
     * This value is enhanced with the ema5DipIncrementPerDayForSellSignal
     * parameter.
     */
    private float ema5DipForSellSignal = -1 ;
    
    /**
     * The percentage by which the EMA5 should dip per day within the
     * sell zone to positively contribute towards generating a sell signal.
     */
    private float ema5DipDecrementPerDayForSellSignal = -0.125f ;
    
    /**
     * If a SLTP strategy is employed, this configuration needs to provide
     * a value between 0-100 representing the percentage of current price
     * below which the SLTP is to be set.
     * <p>
     * Note that the trading algorithm keeps adjusting the trigger price
     * as long as the new trigger price is higher than the old one. This is
     * done to lock is as much profit as possible. However if the price is
     * going down, the trigger price is left as is.
     * <p>
     * A value of 0 or negative number indicates that stop loss strategy
     * is not to be employed.
     */
    private float sltpCurrentPricePct = 10 ;
}
