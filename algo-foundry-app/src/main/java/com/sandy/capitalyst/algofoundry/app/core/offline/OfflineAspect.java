package com.sandy.capitalyst.algofoundry.app.core.offline;

import com.sandy.capitalyst.algofoundry.app.core.AlgoFoundryConfig;
import com.sandy.capitalyst.algofoundry.app.core.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class OfflineAspect {
    
    @Autowired private AlgoFoundryConfig config ;
    
    @Around( "@annotation(com.sandy.capitalyst.algofoundry.app.core.offline.Offline)" )
    public Object processOffline( ProceedingJoinPoint joinPoint )
        throws Throwable {
        
        boolean isOffline = config.isWorkOffline() ;
        Object returnValue ;
        
        String joinPointSignature = joinPoint.getSignature().toLongString() + "::" +
                                    Arrays.toString( joinPoint.getArgs() ) ;
        
        String joinPointMarker = joinPoint.getSignature().toShortString() + "::" +
                                 Arrays.toString( joinPoint.getArgs() ) ;
        
        File cacheFile = getCacheFile( joinPointSignature ) ;

        if( isOffline ) {
            if( !cacheFile.exists() ) {
                throw new IllegalStateException( "Offline cache for " +
                                           joinPointSignature + " not found" ) ;
            }
            log.debug( "Loading cache for " + joinPointMarker ) ;
            returnValue = loadCacheContents( cacheFile ) ;
        }
        else {
            returnValue = joinPoint.proceed() ;
            log.debug( "Saving cache for " + joinPointMarker ) ;
            saveCacheContents( cacheFile, returnValue ) ;
        }
        
        return returnValue ;
    }
    
    private File getCacheFile( String joinPointSignature ) {
        
        String fileName = StringUtil.getHash( joinPointSignature ) + ".cache" ;
        File cacheDir = new File( config.getWorkspacePath(), "server-cache" ) ;
        if( !cacheDir.exists() ) {
            cacheDir.mkdirs() ;
        }
        return new File( cacheDir, fileName ) ;
    }
    
    private Object loadCacheContents( File file ) throws Exception {
        
        Object retVal = null ;
        try( ObjectInputStream oin = new ObjectInputStream( new FileInputStream( file ) ) ) {
            retVal = oin.readObject() ;
        }
        return retVal ;
    }
    
    private void saveCacheContents( File file, Object obj ) throws Exception {
        try( ObjectOutputStream oout = new ObjectOutputStream( new FileOutputStream( file ) ) ) {
            oout.writeObject( obj ) ;
        }
    }
}
