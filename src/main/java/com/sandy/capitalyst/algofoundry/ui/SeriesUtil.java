package com.sandy.capitalyst.algofoundry.ui;

import java.awt.*;

import static com.sandy.capitalyst.algofoundry.core.ui.SeriesRenderingAttributes.* ;
import static com.sandy.capitalyst.algofoundry.core.ui.UITheme.* ;
import static com.sandy.capitalyst.algofoundry.core.util.IndicatorType.* ;

public class SeriesUtil {
    
    public static void initializeSeriesColorMap() {
        setColor( CLOSING_PRICE, new Color( 178, 255, 102 ).darker() );
        setColor( BOLLINGER_UP,  new Color( 255, 102, 255 ).darker() );
        setColor( BOLLINGER_LOW, new Color( 255, 102, 255 ).darker() );
        setColor( BOLLINGER_MID, new Color( 103, 153, 227 ).darker() );

        setStroke( CLOSING_PRICE, LINE_STROKE   );
        setStroke( BOLLINGER_UP,  DASHED_STROKE );
        setStroke( BOLLINGER_LOW, DASHED_STROKE );
        setStroke( BOLLINGER_MID, DASHED_STROKE );
    }
}
