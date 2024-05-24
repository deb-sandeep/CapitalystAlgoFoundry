package com.sandy.capitalyst.algofoundry.core.util;

import com.sandy.capitalyst.algofoundry.AlgoFoundry;
import com.sandy.capitalyst.algofoundry.core.AlgoFoundryConfig;
import com.sandy.capitalyst.algofoundry.core.net.HTTPResourceDownloader;
import org.apache.commons.io.FileUtils;

import java.io.File;

import static com.sandy.capitalyst.algofoundry.core.util.StringUtil.getHash;
import static com.sandy.capitalyst.algofoundry.AlgoFoundry.getConfig ;

public class CapitalystServerUtil {
    
    public static String getResource( String url ) throws Exception {
        return HTTPResourceDownloader.instance()
                                     .getResource( formatUrl( url ) ) ;
    }
    
    private static String formatUrl( String url ) {
        return url.replace( "{server}", getConfig().getServerName() ) ;
    }
}
