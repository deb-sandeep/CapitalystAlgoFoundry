package com.sandy.capitalyst.algofoundry.app;

import com.sandy.capitalyst.algofoundry.app.core.bus.Event;
import com.sandy.capitalyst.algofoundry.app.core.bus.EventBus;
import com.sandy.capitalyst.algofoundry.app.bt.gui.AlgoFoundryFrame;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.swing.*;
import java.lang.ref.Cleaner;

@Slf4j
@SpringBootApplication
public class AlgoFoundry
        implements ApplicationContextAware, WebMvcConfigurer {

    private static final EventBus GLOBAL_EVENT_BUS = new EventBus() ;

    private static ConfigurableApplicationContext APP_CTX = null ;
    private static AlgoFoundry                    APP     = null ;
    private static Cleaner                        CLEANER = Cleaner.create() ;

    public static AlgoFoundry getApp() {
        return APP;
    }

    public static ApplicationContext getAppCtx() {
        return APP_CTX ;
    }

    public static EventBus getBus() {
        return GLOBAL_EVENT_BUS ;
    }
    
    public static <T> T getBean( Class<T> type ) {
        return APP_CTX.getBean( type ) ;
    }
    
    public static Cleaner getCleaner() { return CLEANER ; }
    
    public static AlgoFoundryConfig getConfig() { return APP.config() ; }
    
    // ---------------- Instance methods start ---------------------------------

    private AlgoFoundryFrame  frame   = null ;
    private AlgoFoundryConfig cfg     = null ;

    public AlgoFoundry() {
        APP = this;
    }

    @Override
    public void setApplicationContext( @NotNull ApplicationContext applicationContext )
            throws BeansException {
        APP_CTX = ( ConfigurableApplicationContext )applicationContext;
    }

    public void initialize() throws Exception {

        log.debug( "## Initializing AlgoFoundry app. >" ) ;

        log.debug( "- Initializing AlgoFoundryFrame" ) ;
        SwingUtilities.invokeLater( () -> {
            this.frame = new AlgoFoundryFrame();
            //Event e = new Event( EventCatalog.EVT_SHOW_STOCK_SIM_PANEL, "RECLTD" ) ;
            //this.frame.handleEvent( e ) ;
        } ) ;

        log.debug( "<< ## AlgoFoundry initialization complete" ) ;
    }

    private AlgoFoundryConfig config() {
        if( cfg == null ) {
            if( APP_CTX != null ) {
                cfg = ( AlgoFoundryConfig )APP_CTX.getBean("config");
            }
            else {
                cfg = new AlgoFoundryConfig() ;
            }
        }
        return cfg ;
    }

    // --------------------- Main method ---------------------------------------
    public static void main( String[] args ) {

        log.debug( "Starting Spring Booot..." ) ;

        System.setProperty( "java.awt.headless", "false" ) ;
        SpringApplication.run( AlgoFoundry.class, args ) ;

        log.debug( "Starting AlgoFoundry.." ) ;
        AlgoFoundry app = AlgoFoundry.getAppCtx().getBean( AlgoFoundry.class ) ;
        try {
            app.initialize() ;
        }
        catch( Exception e ) {
            log.error( "Exception while initializing AlgoFoundry.", e ) ;
        }
    }
}
