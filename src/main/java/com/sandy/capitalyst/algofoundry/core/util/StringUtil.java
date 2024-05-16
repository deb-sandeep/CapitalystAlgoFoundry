package com.sandy.capitalyst.algofoundry.core.util;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class StringUtil {
    
    private static SimpleDateFormat SDF = new SimpleDateFormat( "yyyy-MM-dd" ) ;

    public static boolean isEmptyOrNull( final String str ) {
        return ( str == null || "".equals( str.trim() ) ) ;
    }

    public static boolean isNotEmptyOrNull( final String str ) {
        return !isEmptyOrNull( str ) ;
    }
    
    public static String getHash( String input ) {
        return new String( Hex.encodeHex( DigestUtils.md5( input ) ) ) ;
    }
    
    public static String fmtDate( Date date ) {
        return SDF.format( date ) ;
    }
}
