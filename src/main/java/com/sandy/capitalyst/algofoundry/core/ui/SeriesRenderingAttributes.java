package com.sandy.capitalyst.algofoundry.core.ui;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class SeriesRenderingAttributes {
    
    private static Map<String, Color>       colorMap  = new HashMap<>() ;
    private static Map<String, BasicStroke> strokeMap = new HashMap<>() ;
    
    public static void setColor( String seriesKey, Color c ) {
        colorMap.put( seriesKey, c ) ;
    }
    
    public static Color getColor( String seriesKey ) {
        Color c = colorMap.get( seriesKey ) ;
        return c != null ? c : Color.WHITE ;
    }
    
    public static void setStroke( String seriesKey, BasicStroke stroke ) {
        strokeMap.put( seriesKey, stroke ) ;
    }
    
    public static BasicStroke getStroke( String seriesKey ) {
        BasicStroke stroke = strokeMap.get( seriesKey ) ;
        return stroke != null ? stroke : UITheme.LINE_STROKE ;
    }
}
