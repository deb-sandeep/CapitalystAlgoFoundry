package com.sandy.capitalyst.algofoundry.strategy.util;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class StringUtil {
    
    private static SimpleDateFormat SDF = new SimpleDateFormat( "yyyy-MM-dd" ) ;
    private static DecimalFormat DF = new DecimalFormat( "##.0" ) ;

    public static boolean isEmptyOrNull( final String str ) {
        return ( str == null || "".equals( str.trim() ) ) ;
    }

    public static boolean isNotEmptyOrNull( final String str ) {
        return !isEmptyOrNull( str ) ;
    }
    
    public static String fmtDate( Date date ) {
        return SDF.format( date ) ;
    }
    
    public static String bs( boolean b ) {
        return "[" + ( b ? "✓" : "x" ) + "]" ;
    }
    
    public static String fmtDbl( double d ) { return DF.format( d ) ; }
}
