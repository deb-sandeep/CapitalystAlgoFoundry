package com.sandy.capitalyst.algofoundry.app.core.util;

import com.sandy.capitalyst.algofoundry.app.core.net.HTTPResourceDownloader;

import static com.sandy.capitalyst.algofoundry.app.AlgoFoundry.getConfig ;

public class CapitalystServerUtil {
    
    public static String getResource( String url ) throws Exception {
        return HTTPResourceDownloader.instance()
                                     .getResource( formatUrl( url ) ) ;
    }
    
    private static String formatUrl( String url ) {
        return url.replace( "{server}", getConfig().getServerName() ) ;
    }
}
