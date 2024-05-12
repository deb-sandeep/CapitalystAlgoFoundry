package com.sandy.capitalyst.algofoundry.ui.panel.sim;

import com.sandy.capitalyst.algofoundry.apiclient.histeod.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.apiclient.histeod.EquityHistEODAPIClient;
import com.sandy.capitalyst.algofoundry.core.ui.IndicatorChart;
import lombok.extern.slf4j.Slf4j;
import org.jfree.data.time.TimeSeries;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.sandy.capitalyst.algofoundry.core.ui.SwingUtils.getNewJPanel;
import static com.sandy.capitalyst.algofoundry.core.ui.SwingUtils.initPanelUI;
import static com.sandy.capitalyst.algofoundry.ui.SeriesUtil.*;

@Slf4j
public class SimPanel extends JPanel {
    
    private static final int XAXIS_WINDOW_SZ = 260 ;
    
    private final EquityEODHistory history ;
    
    private final IndicatorChart  mainChart ;
    private final FooterChartsPanel footerChartsPanel ;
    private final SimControlPanel controlPanel ;
    private final List<String>    seriesKeys = new ArrayList<>() ;
    
    private int curBarSeriesIndex = 0 ;
    
    public SimPanel( String symbol ) throws Exception {
        this.history = new EquityHistEODAPIClient().getEquityEODHistory( symbol ) ;
        this.mainChart = new IndicatorChart( symbol, "Price", XAXIS_WINDOW_SZ ) ;
        this.footerChartsPanel = new FooterChartsPanel( XAXIS_WINDOW_SZ ) ;
        this.controlPanel = new SimControlPanel( this ) ;
        
        addSeriesKey( SN_CLOSING_PRICE ) ;
        addSeriesKey( SN_BOLLINGER_LOW ) ;
        addSeriesKey( SN_BOLLINGER_UP  ) ;
        addSeriesKey( SN_BOLLINGER_MID ) ;
        
        setUpUI() ;
    }
    
    public synchronized void addSeriesKey( String seriesKey ) {
        this.seriesKeys.add( seriesKey ) ;
        this.mainChart.addSeries( new TimeSeries( seriesKey ) ) ;
    }
    
    public synchronized void removeSeriesKey( String seriesKey ) {
        this.seriesKeys.remove( seriesKey ) ;
        this.mainChart.removeSeries( seriesKey ) ;
    }
    
    public synchronized void clearChart() {
        for( String seriesKey : this.seriesKeys ) {
            this.mainChart.clearSeries( seriesKey ) ;
        }
    }
    
    private void setUpUI() {
        
        initPanelUI( this ) ;
        add( getChartPanel(), BorderLayout.CENTER ) ;
        add( controlPanel, BorderLayout.EAST ) ;
    }
    
    private JPanel getChartPanel() {
        JPanel panel = getNewJPanel() ;
        panel.add( mainChart, BorderLayout.CENTER ) ;
        panel.add( footerChartsPanel, BorderLayout.SOUTH ) ;
        return panel ;
    }
    
    public boolean playCurrentBarSeriesData() {
        
        BarSeries barSeries = history.getBarSeries() ;
        if( this.curBarSeriesIndex < barSeries.getBarCount() ) {
            Bar bar = barSeries.getBar( this.curBarSeriesIndex ) ;
            Date date = Date.from( bar.getEndTime().toInstant() ) ;
            
            synchronized( SimPanel.this ) {
                seriesKeys.forEach( k -> {
                    Indicator<Num> ind = history.getIndicator( k ) ;
                    mainChart.addValue( k, date, ind.getValue( this.curBarSeriesIndex )
                                                    .doubleValue() ) ;
                } ) ;
            }
            this.curBarSeriesIndex++ ;
            return true ;
        }
        return false ;
    }
    
    public void restartSimulation() {
        this.curBarSeriesIndex = 0 ;
        this.clearChart() ;
    }
}
