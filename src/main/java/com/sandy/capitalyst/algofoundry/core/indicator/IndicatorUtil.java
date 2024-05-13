package com.sandy.capitalyst.algofoundry.core.indicator;

import com.sandy.capitalyst.algofoundry.core.ui.UITheme;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static com.sandy.capitalyst.algofoundry.core.ui.UITheme.* ;
import static com.sandy.capitalyst.algofoundry.core.indicator.IndicatorType.* ;

public class IndicatorUtil {
    
    private static final Map<String, Color>       colorMap  = new HashMap<>() ;
    private static final Map<String, BasicStroke> strokeMap = new HashMap<>() ;
    
    static {
        setColor( CLOSING_PRICE, new Color( 178, 255, 102 ).darker() );
        setColor( BOLLINGER_UP,  new Color( 255, 102, 255 ).darker() );
        setColor( BOLLINGER_LOW, new Color( 255, 102, 255 ).darker() );
        setColor( BOLLINGER_MID, new Color( 103, 153, 227 ).darker() );
        setColor( MACD,          new Color( 43, 229, 7 ).darker() );
        setColor( MACD_SIGNAL,   new Color( 250, 7, 34 ).darker() );
        
        setStroke( CLOSING_PRICE, LINE_STROKE   );
        setStroke( BOLLINGER_UP,  DASHED_STROKE );
        setStroke( BOLLINGER_LOW, DASHED_STROKE );
        setStroke( BOLLINGER_MID, DASHED_STROKE );
    }

    private static void setColor( String seriesKey, Color c ) {
        colorMap.put( seriesKey, c ) ;
    }
    
    public static Color getColor( String seriesKey ) {
        Color c = colorMap.get( seriesKey ) ;
        return c != null ? c : Color.WHITE ;
    }
    
    private static void setStroke( String seriesKey, BasicStroke stroke ) {
        strokeMap.put( seriesKey, stroke ) ;
    }
    
    public static BasicStroke getStroke( String seriesKey ) {
        BasicStroke stroke = strokeMap.get( seriesKey ) ;
        return stroke != null ? stroke : UITheme.LINE_STROKE ;
    }
}
