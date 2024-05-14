package com.sandy.capitalyst.algofoundry.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.File;

@Configuration( "config" )
@PropertySource( "classpath:algo-foundry.properties" )
@ConfigurationProperties( "algofoundry" )
@Data
public class AlgoFoundryConfig {

    private File workspacePath = null ;
    private String serverName = null ;
    private boolean workOffline = false ;
    private int dateWindowSize = 260 ;
}
