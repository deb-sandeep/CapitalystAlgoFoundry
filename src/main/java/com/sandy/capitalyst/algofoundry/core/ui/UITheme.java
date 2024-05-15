package com.sandy.capitalyst.algofoundry.core.ui;

import java.awt.*;

public class UITheme {
    
    private static final String FONT_NAME      = "Helvetica" ;
    private static final String MONO_FONT_NAME = "Courier New" ;
    
    public static final Color BACKGROUND_COLOR = Color.DARK_GRAY ;
    
    public static final Color TABLE_GRID_COLOR     = new Color( 220, 220, 220 ) ;
    public static final Color TABLE_EVEN_ROW_COLOR = new Color( 240, 240, 240 ) ;
    public static final Color TABLE_ODD_ROW_COLOR  = Color.white ;
    public static final int   TABLE_ROW_HEIGHT     = 20 ;
    
    public static final Font META_TABLE_FONT         = new Font( MONO_FONT_NAME, Font.PLAIN, 12 ) ;
    public static final Font META_TABLE_HDR_FONT     = new Font( MONO_FONT_NAME, Font.BOLD,  12 ) ;
    public static final Font TABLE_DECIMAL_FONT      = new Font( MONO_FONT_NAME, Font.PLAIN, 12 ) ;
    
    public static final Font  CHART_AXIS_LABEL_FONT = new Font( FONT_NAME, Font.PLAIN, 12 ) ;
    public static final Font  CHART_AXIS_TICK_FONT  = new Font( FONT_NAME, Font.PLAIN, 11 ) ;
    public static final Font  CHART_TITLE_FONT      = new Font( FONT_NAME, Font.PLAIN, 15 ) ;
    public static final Color CHART_TITLE_COLOR     = Color.GRAY ;
    public static final Color CHART_LABEL_COLOR     = Color.GRAY ;
    public static final Color CHART_AXIS_TICK_COLOR = Color.DARK_GRAY.brighter().brighter() ;
    
    public static final BasicStroke DASHED_STROKE =
            new BasicStroke( 0.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                             1.0f, new float[] {2.0f, 3.0f}, 0.0f ) ;
    
    public static final BasicStroke LINE_STROKE =
            new BasicStroke( 0.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) ;
}
