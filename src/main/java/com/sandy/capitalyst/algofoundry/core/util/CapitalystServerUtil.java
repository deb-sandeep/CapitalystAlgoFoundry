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
        
        File serverCacheDir = getServerCacheDir() ;
        String fmtUrl = formatUrl( url ) ;
        File urlCacheFile = new File( serverCacheDir, getHash( fmtUrl ) + ".cache" ) ;
        HTTPResourceDownloader downloader ;
        String response = null ;
        
        if( getConfig().isWorkOffline() ) {
            if( !urlCacheFile.exists() ) {
                throw new Exception( "Working offline and cached " +
                                     "response doesn't exist." ) ;
            }
            else {
                response = FileUtils.readFileToString( urlCacheFile, "UTF-8" ) ;
            }
        }
        else {
            downloader = HTTPResourceDownloader.instance() ;
            response = downloader.getResource( fmtUrl ) ;
            FileUtils.write( urlCacheFile, response, "UTF-8" ) ;
        }
        return response ;
    }
    
    private static String formatUrl( String url ) {
        return url.replace( "{server}", getConfig().getServerName() ) ;
    }
    
    private static File getServerCacheDir() {
        File serverCacheDir = new File( getConfig().getWorkspacePath(), "server-cache" ) ;
        if( !serverCacheDir.exists() ) {
            serverCacheDir.mkdirs() ;
        }
        return serverCacheDir ;
    }
}
