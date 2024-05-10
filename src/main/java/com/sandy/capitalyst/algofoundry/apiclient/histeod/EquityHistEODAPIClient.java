package com.sandy.capitalyst.algofoundry.apiclient.histeod;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandy.capitalyst.algofoundry.AlgoFoundry;
import com.sandy.capitalyst.algofoundry.apiclient.reco.EquityReco;
import com.sandy.capitalyst.algofoundry.core.util.CapitalystServerUtil;
import com.sandy.capitalyst.algofoundry.dao.equity.HistoricEQData;
import com.sandy.capitalyst.algofoundry.dao.equity.repo.HistoricEQDataRepo;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import lombok.extern.slf4j.Slf4j;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.sandy.capitalyst.algofoundry.AlgoFoundry.* ;

@Slf4j
public class EquityHistEODAPIClient {
    
    private static final String           RECO_URL = "http://{server}/Equity/HistoricData/{symbol}" ;
    private static       SimpleDateFormat SDF      = new SimpleDateFormat( "dd-MMM-yyyy" ) ;

    public List<DayCandle> getHistoricCandles( String symbol ) throws Exception {
        
        List<DayCandle> allCandles = new ArrayList<>() ;
        List<DayCandle> serverCandles = new ArrayList<>() ;
        List<DayCandle> prehistCandles ;
                
                String histCsv = CapitalystServerUtil.getResource(
                RECO_URL.replace( "{symbol}", symbol )
        ) ;
        
        List<String[]> candleRecords = parseCsv( histCsv ) ;
        for( int i=1; i<candleRecords.size(); i++ ) {
            serverCandles.add( buildCandle( candleRecords.get( i ) ) ) ;
        }
        
        DayCandle earliestCandle = serverCandles.get( 0 ) ;
        prehistCandles = getPrehistoicDayCandles( symbol, earliestCandle.getDate() ) ;
        
        allCandles.addAll( prehistCandles ) ;
        allCandles.addAll( serverCandles ) ;
        
        return allCandles ;
    }
    
    public BarSeries getHistoricBarSeries( String symbol ) throws Exception {
        
        BarSeries series = new BaseBarSeries( symbol ) ;
        List<DayCandle> candles = getHistoricCandles( symbol ) ;
        candles.forEach( c -> series.addBar( c.toBar() ) ) ;
        return series ;
    }
    
    private List<DayCandle> getPrehistoicDayCandles( String symbol, Date endDate ){
        
        HistoricEQDataRepo eodRepo ;
        List<HistoricEQData> histRecords ;
        List<DayCandle> candles = new ArrayList<>() ;

        eodRepo = getBean( HistoricEQDataRepo.class ) ;
        histRecords = eodRepo.getHistoricData( symbol, endDate ) ;
        
        histRecords.forEach( hr -> candles.add( hr.toDayCandle() ) ) ;
        return candles ;
    }
    
    private List<String[]> parseCsv( String csvContent ) {
        
        CsvParserSettings settings = new CsvParserSettings() ;
        CsvParser         parser   = new CsvParser( settings ) ;
        List<String[]>    records ;
        
        records = parser.parseAll( new StringReader( csvContent ) ) ;
        log.info( records.size() + " records found." ) ;
        return records ;
    }
    
    private DayCandle buildCandle( String[] values )
        throws Exception {
        
        DayCandle candle = new DayCandle() ;
        
        candle.setDate  ( SDF.parse( values[1] ) ) ;
        candle.setOpen  ( Float.parseFloat( values[2] ) ) ;
        candle.setHigh  ( Float.parseFloat( values[3] ) ) ;
        candle.setLow   ( Float.parseFloat( values[4] ) ) ;
        candle.setClose ( Float.parseFloat( values[5] ) ) ;
        candle.setVolume( Long.parseLong  ( values[6] ) ) ;
        
        return candle ;
    }
}
