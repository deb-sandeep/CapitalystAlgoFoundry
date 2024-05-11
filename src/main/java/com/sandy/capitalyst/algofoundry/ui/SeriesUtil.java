package com.sandy.capitalyst.algofoundry.ui;

import com.sandy.capitalyst.algofoundry.core.ui.SeriesRenderingAttributes;

import java.awt.*;

import static com.sandy.capitalyst.algofoundry.core.ui.SeriesRenderingAttributes.* ;
import static com.sandy.capitalyst.algofoundry.core.ui.UITheme.* ;

public class SeriesUtil {
    
    public static final String SN_CLOSING_PRICE  = "Closing Price" ;
    public static final String SN_BOLLINGER_MID  = "Bollinger (mid)" ;
    public static final String SN_BOLLINGER_UP   = "Bollinger (up)" ;
    public static final String SN_BOLLINGER_LOW  = "Bollinger (down)" ;
    
    public static void initializeSeriesColorMap() {
        setColor( SN_CLOSING_PRICE, new Color( 178, 255, 102 ).darker() );
        setColor( SN_BOLLINGER_UP,  new Color( 255, 102, 255 ).darker() );
        setColor( SN_BOLLINGER_LOW, new Color( 255, 102, 255 ).darker() );
        setColor( SN_BOLLINGER_MID, new Color( 103, 153, 227 ).darker() );

        setStroke( SN_CLOSING_PRICE, LINE_STROKE   );
        setStroke( SN_BOLLINGER_UP,  DASHED_STROKE );
        setStroke( SN_BOLLINGER_LOW, DASHED_STROKE );
        setStroke( SN_BOLLINGER_MID, DASHED_STROKE );
    }
}
