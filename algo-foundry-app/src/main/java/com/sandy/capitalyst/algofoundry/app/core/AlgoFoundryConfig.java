package com.sandy.capitalyst.algofoundry.app.core;

import com.sandy.capitalyst.algofoundry.strategy.impl.MyStrategyConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.File;

@Configuration( "config" )
@PropertySource( "classpath:algo-foundry.properties" )
@ConfigurationProperties( "algofoundry" )
@Data
public class AlgoFoundryConfig extends MyStrategyConfig {

    private File workspacePath = null ;
    private String serverName = null ;
    private boolean workOffline = false ;
    
    private int dateWindowSize = 260 ;
    private boolean refreshOfflineCache = false ;
}
