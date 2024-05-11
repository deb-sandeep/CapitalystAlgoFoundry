package com.sandy.capitalyst.algofoundry.core.ui;

import java.awt.*;

public class UITheme {
    
    private static final String FONT_NAME      = "Helvetica" ;
    private static final String MONO_FONT_NAME = "Courier New" ;
    
    public static final Color BACKGROUND_COLOR = Color.LIGHT_GRAY ;
    
    public static final Color TABLE_GRID_COLOR     = new Color( 220, 220, 220 ) ;
    public static final Color TABLE_EVEN_ROW_COLOR = new Color( 240, 240, 240 ) ;
    public static final Color TABLE_ODD_ROW_COLOR  = Color.white ;
    public static final int   TABLE_ROW_HEIGHT     = 20 ;
    
    public static final Font META_TABLE_FONT         = new Font( MONO_FONT_NAME, Font.PLAIN, 12 ) ;
    public static final Font META_TABLE_HDR_FONT     = new Font( MONO_FONT_NAME, Font.BOLD,  12 ) ;
    public static final Font TABLE_DECIMAL_FONT      = new Font( MONO_FONT_NAME, Font.PLAIN, 12 ) ;
    
    public static final Font CHART_AXIS_FONT         = new Font( FONT_NAME, Font.PLAIN, 8 ) ;
    public static final Font CHART_LEGEND_FONT       = new Font( FONT_NAME, Font.PLAIN, 8 ) ;
    
    public static final BasicStroke DASHED_STROKE =
            new BasicStroke( 0.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                             1.0f, new float[] {1.0f, 2.0f}, 0.0f ) ;
    
    public static final BasicStroke LINE_STROKE = new BasicStroke( 0.3f ) ;
}
