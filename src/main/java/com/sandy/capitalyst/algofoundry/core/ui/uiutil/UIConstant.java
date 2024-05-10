package com.sandy.capitalyst.algofoundry.core.ui.uiutil;

import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.SimpleDateFormat;

public class UIConstant {

    private static final String FONT_NAME = "Courier" ;
    
    public static final Font BASE_FONT = new Font( FONT_NAME, Font.PLAIN, 20 ) ;

    public static final Font CHART_XAXIS_FONT = new Font( FONT_NAME, Font.PLAIN, 12 ) ;
    public static final Font CHART_YAXIS_FONT = new Font( FONT_NAME, Font.PLAIN, 10 ) ;
    
    public static final SimpleDateFormat DF_TIME_LG = new SimpleDateFormat( "H:mm:ss" ) ;
    public static final SimpleDateFormat DF_TIME_SM = new SimpleDateFormat( "H:mm" ) ;
}
