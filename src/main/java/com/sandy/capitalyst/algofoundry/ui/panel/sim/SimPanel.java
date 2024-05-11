package com.sandy.capitalyst.algofoundry.ui.panel.sim;

import com.sandy.capitalyst.algofoundry.apiclient.histeod.EquityHistEODAPIClient;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.Num;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class SimPanel extends JPanel {
    
    private final String symbol ;
    private final BarSeries series ;
    
    public SimPanel( String symbol ) throws Exception {
        this.symbol = symbol ;
        this.series = new EquityHistEODAPIClient().getHistoricBarSeries( symbol ) ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        
        // Close price
        ClosePriceIndicator        closePrice = new ClosePriceIndicator(series);
        EMAIndicator               avg14 = new EMAIndicator(closePrice, 14);
        StandardDeviationIndicator sd14  = new StandardDeviationIndicator(closePrice, 14);
        
        // Bollinger bands
        BollingerBandsMiddleIndicator middleBBand = new BollingerBandsMiddleIndicator(avg14);
        BollingerBandsLowerIndicator  lowBBand    = new BollingerBandsLowerIndicator(middleBBand, sd14);
        BollingerBandsUpperIndicator  upBBand     = new BollingerBandsUpperIndicator(middleBBand, sd14);
        
        /*
         * Building chart dataset
         */
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(buildChartBarSeries(series, closePrice, "Apple Inc. (AAPL) - NASDAQ GS"));
        dataset.addSeries(buildChartBarSeries(series, lowBBand, "Low Bollinger Band"));
        dataset.addSeries(buildChartBarSeries(series, upBBand, "High Bollinger Band"));
        
        /*
         * Creating the chart
         */
        JFreeChart chart = ChartFactory.createTimeSeriesChart("Apple Inc. 2013 Close Prices", // title
                "Date", // x-axis label
                "Price Per Unit", // y-axis label
                dataset, // data
                true, // create legend?
                true, // generate tooltips?
                false // generate URLs?
        );
        XYPlot   plot = (XYPlot) chart.getPlot();
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd"));

        ChartPanel panel = new ChartPanel( chart ) ;
        panel.setFillZoomRectangle( true ) ;
        panel.setMouseWheelEnabled( true ) ;
        
        setLayout( new BorderLayout() ) ;
        add( panel, BorderLayout.CENTER ) ;
    }
    
    private static TimeSeries buildChartBarSeries( BarSeries barSeries, Indicator<Num> indicator,
                                                   String name) {
        org.jfree.data.time.TimeSeries chartTimeSeries = new org.jfree.data.time.TimeSeries(name);
        for (int i = 0; i < barSeries.getBarCount(); i++) {
            Bar bar = barSeries.getBar(i);
            chartTimeSeries.add(new Day( Date.from(bar.getEndTime().toInstant())), indicator.getValue(i).doubleValue());
        }
        return chartTimeSeries;
    }
}
