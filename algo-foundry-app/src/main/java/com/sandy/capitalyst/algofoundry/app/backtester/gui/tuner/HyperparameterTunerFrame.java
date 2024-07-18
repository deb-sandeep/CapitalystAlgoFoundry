package com.sandy.capitalyst.algofoundry.app.backtester.gui.tuner;

import com.sandy.capitalyst.algofoundry.app.AlgoFoundry;
import com.sandy.capitalyst.algofoundry.app.apiclient.equitymeta.EquityMeta;
import com.sandy.capitalyst.algofoundry.app.core.ui.SwingUtils;
import com.sandy.capitalyst.algofoundry.app.tuner.GridSearch;
import com.sandy.capitalyst.algofoundry.app.tuner.HyperParameter;
import com.sandy.capitalyst.algofoundry.app.tuner.HyperParameterGroup;
import com.sandy.capitalyst.algofoundry.strategy.impl.MyStrategyConfig;
import com.sandy.capitalyst.algofoundry.strategy.tradebook.TradeBook;
import com.sandy.capitalyst.algofoundry.strategy.util.StrategyConfigUtil;
import com.sandy.capitalyst.algofoundry.strategy.util.StringUtil;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.sandy.capitalyst.algofoundry.app.core.util.StringUtil.fmtDbl;
import static javax.swing.BorderFactory.createLineBorder;

@Slf4j
public class HyperparameterTunerFrame extends JFrame
    implements GridSearch.GridSearchListener {
    
    public static final Font PARAM_VALUE_LABEL_FONT = new Font( "Courier New", Font.PLAIN,  16 ) ;
    public static final Font PARAM_NAME_LABEL_FONT  = new Font( "Courier New", Font.PLAIN,  10 ) ;
    
    private static final Color GOLDEN_TILE_BORDER_COLOR = Color.YELLOW.darker().darker() ;
    private static final Color NORMAL_TILE_BORDER_COLOR = Color.DARK_GRAY.darker() ;
    
    private final List<EquityMeta> metaList ;
    private final GridSearch gridSearch ;
    private final HyperParameterGroup parameters ;
    private final File outputDir ;
    
    private final List<List<JLabel>> paramValueTiles = new ArrayList<>() ;
    private final int[] highlitedTilesIndexes ;
    
    private boolean tunerIsRunning = false ;
    
    private TunerToolBar toolBar ;
    
    public HyperparameterTunerFrame( List<EquityMeta> metaList )
        throws Exception {
        
        super( "AlgoFoundry Hyperparameter Tuner" ) ;
        
        MyStrategyConfig strategyConfig = new MyStrategyConfig() ;
        StrategyConfigUtil.populateStrategyConfig( strategyConfig, AlgoFoundry.getConfig() ) ;
        
        this.metaList = metaList ;
        this.gridSearch = new GridSearch( metaList, strategyConfig, this ) ;
        this.parameters = gridSearch.getHyperParameters() ;
        this.highlitedTilesIndexes = new int[this.parameters.getNumParameters()] ;
        this.outputDir = getOutputDirectory() ;
        
        log.debug( "Number of possible grid points = " + this.parameters.getTotalCombinations() ) ;
        
        setUpUI() ;
        SwingUtils.setMaximized( this ) ;
        resetTileHighlights() ;
        setWindowClosingBehavior() ;
        setVisible( true ) ;
    }
    
    private void setWindowClosingBehavior() {
        super.setDefaultCloseOperation( DO_NOTHING_ON_CLOSE ) ;
        super.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing( WindowEvent e ) {
                if( tunerIsRunning ) {
                    JOptionPane.showMessageDialog( HyperparameterTunerFrame.this, "Close the tuner first." ) ;
                }
                else {
                    HyperparameterTunerFrame.super.setVisible( false ) ;
                    HyperparameterTunerFrame.super.dispose() ;
                }
            }
        } ) ;
    }
    
    private void resetTileHighlights() {
        for( int paramIndex=0; paramIndex<parameters.getNumParameters(); paramIndex++ ) {
            if( highlitedTilesIndexes[paramIndex] != -1 ) {
                setTileHighlight( paramIndex, highlitedTilesIndexes[paramIndex], false ) ;
                highlitedTilesIndexes[paramIndex] = -1 ;
            }
        }
    }
    
    private void setTileHighlight( int paramIndex, int valueIndex, boolean highlight ) {
        JLabel label = paramValueTiles.get( paramIndex ).get( valueIndex ) ;
        label.setBackground( highlight ? Color.DARK_GRAY.darker() : Color.BLACK ) ;
        highlitedTilesIndexes[paramIndex] = highlight ? valueIndex : -1 ;
    }
    
    private void markTileAsGolden( int paramIndex, int goldenVIndex ) {
        
        List<JLabel> valueTiles = paramValueTiles.get( paramIndex ) ;
        for( int vIndex=0; vIndex<valueTiles.size(); vIndex++ ) {
            JLabel tile = valueTiles.get( vIndex ) ;
            if( vIndex == goldenVIndex ) {
                tile.setBorder( createLineBorder( GOLDEN_TILE_BORDER_COLOR, 1 ) ) ;
            }
            else {
                tile.setBorder( createLineBorder( NORMAL_TILE_BORDER_COLOR, 1 ) ) ;
            }
            tile.revalidate() ;
        }
    }
    
    private void setUpUI() {
        
        toolBar = new TunerToolBar( this ) ;
        gridSearch.addListener( toolBar ) ;
        
        Container contentPane = super.getContentPane() ;
        contentPane.setLayout( new BorderLayout() ) ;
        
        contentPane.add( toolBar, BorderLayout.NORTH ) ;
        contentPane.add( getTunerVisualPanel() ) ;
    }
    
    private JPanel getTunerVisualPanel() {
        
        JPanel hdrPanel = SwingUtils.getNewJPanel() ;
        hdrPanel.setBackground( Color.BLACK );
        hdrPanel.setLayout( new GridLayout( 1, parameters.getNumParameters() ) ) ;
        for( HyperParameter param : parameters.getParameters() ) {
            hdrPanel.add( createParamNameTile( param.getFieldName() ) ) ;
        }

        JPanel tilesPanel = SwingUtils.getNewJPanel() ;
        tilesPanel.setBackground( Color.BLACK ) ;
        tilesPanel.setLayout( new GridLayout( 1, parameters.getNumParameters() ) ) ;
        for( HyperParameter param : parameters.getParameters() ) {
            tilesPanel.add( createParamValuesTile( param ) ) ;
        }
        
        JPanel panel = SwingUtils.getNewJPanel() ;
        panel.setBackground( Color.BLACK ) ;
        panel.setLayout( new BorderLayout() ) ;
        panel.add( hdrPanel, BorderLayout.NORTH ) ;
        panel.add( tilesPanel, BorderLayout.CENTER ) ;
        
        return panel ;
    }
    
    private JPanel createParamValuesTile( HyperParameter param ) {
        
        JPanel panel = SwingUtils.getNewJPanel() ;
        panel.setBackground( Color.BLACK ) ;
        List<JLabel> labelTiles = new ArrayList<>() ;
        
        panel.setLayout( new GridLayout( param.getNumSteps(), 1 ) ) ;
        for( int i=0; i<param.getNumSteps(); i++ ) {
            JLabel label = createParamValueTile( param.getMin() + i* param.getStep() ) ;
            panel.add( label ) ;
            labelTiles.add( label ) ;
        }
        paramValueTiles.add( labelTiles ) ;
        return panel ;
    }
    
    private Component createParamNameTile( String fieldName ) {
        JLabel label = new JLabel( fieldName, SwingConstants.CENTER ) ;
        label.setOpaque( true ) ;
        label.setBackground( Color.BLACK ) ;
        label.setForeground( Color.LIGHT_GRAY.brighter() ) ;
        label.setFont( PARAM_NAME_LABEL_FONT ) ;
        return label ;
    }
    
    private JLabel createParamValueTile( float v ) {
        JLabel label = new JLabel( StringUtil.fmtDbl( v ), SwingConstants.CENTER ) ;
        label.setOpaque( true ) ;
        label.setBackground( Color.BLACK ) ;
        label.setForeground( Color.DARK_GRAY ) ;
        label.setFont( PARAM_VALUE_LABEL_FONT ) ;
        label.setBorder( createLineBorder( NORMAL_TILE_BORDER_COLOR, 1 ) ) ;
        return label ;
    }
    
    public void runTuner() {
        new Thread( () -> {
            try {
                toolBar.setRunButtonEnabled( false ) ;
                tunerIsRunning = true ;
                gridSearch.tuneHyperParameters();
                tunerIsRunning = false ;
                toolBar.setRunButtonEnabled( true ) ;
            }
            catch( Exception e ) {
                log.error( "Error while running tuner.", e ) ;
            }
        } ).start() ;
    }
    
    public void stopTuner() {
        if( tunerIsRunning ) {
            gridSearch.setKillSwitch() ;
            toolBar.setRunButtonEnabled( true ) ;
        }
    }
    
    @Override
    public void parametersUpdated() {
        
        SwingUtilities.invokeLater( this::resetTileHighlights ) ;
        
        for( int paramIdx=0; paramIdx < parameters.getNumParameters(); paramIdx++ ) {
            int curValueStep = parameters.getParameter( paramIdx ).getCurrentStep() ;
            int finalParamIdx = paramIdx;
            
            SwingUtilities.invokeLater( () ->
                setTileHighlight( finalParamIdx, curValueStep, true )
            ) ;
        }
    }
    
    @Override
    public void goldenParametersFound( double mktProfit,
                                       HyperParameterGroup parameters,
                                       MyStrategyConfig cfg,
                                       List<TradeBook> tradeBooks ) {
        
        for( int pIndex=0; pIndex<parameters.getNumParameters(); pIndex++ ) {
            HyperParameter p = parameters.getParameter( pIndex ) ;
            int stepIndex = p.getCurrentStep() ;
            markTileAsGolden( pIndex, stepIndex ) ;
        }
        
        try {
            saveGoldenParameterOutput( mktProfit, parameters, cfg, tradeBooks ) ;
        }
        catch( Exception e ) {
            log.error( "Could not save golden parameter output.", e ) ;
        }
    }
    
    private void saveGoldenParameterOutput( double mktProfit,
                                            HyperParameterGroup parameters,
                                            MyStrategyConfig cfg,
                                            List<TradeBook> tradeBooks )
        throws Exception {
        
        File outputDir = getGoldenParamOutputDir( mktProfit ) ;
        saveTradeBooks( outputDir, tradeBooks ) ;
        saveParameters( outputDir, mktProfit, parameters ) ;
    }
    
    private void saveParameters( File outputDir,
                                 double mktProfit,
                                 HyperParameterGroup parameters )
        throws Exception {
    
        StringBuilder sb = new StringBuilder() ;
        sb.append( "# Tuned properties for simulated profit = " + fmtDbl( mktProfit ) + "%\n#\n" ) ;
        for( HyperParameter param : parameters.getParameters() ) {
            sb.append( param.getFieldName() )
              .append( " = " )
              .append( ConvertUtils.convert( param.currentVal(), param.getFieldType() ) )
              .append( "\n" ) ;
        }
        
        File propFile = new File( outputDir, "config.properties" ) ;
        FileUtils.writeStringToFile( propFile, sb.toString(), "UTF-8" ) ;
    }
    
    private void saveTradeBooks( File outputDir, List<TradeBook> tradeBooks )
            throws IOException {
        
        List<String[]> csvData = new ArrayList<>() ;
        String[] csvHeaders = {
            "symbol", "total_profit_pct", "num_buy_trades", "num_sell_trades"
        } ;
        
        for( TradeBook book : tradeBooks ) {
            File file = new File( outputDir, "trades/" + book.getSymbol() +
                                  "_" + fmtDbl( book.getTotalProfitPct() ) + ".txt" ) ;
            FileUtils.writeStringToFile( file, book.toString(), "UTF-8" ) ;
            
            String[] csvRow = new String[4] ;
            csvRow[0] = book.getSymbol() ;
            csvRow[1] = fmtDbl( book.getTotalProfitPct() ) ;
            csvRow[2] = Integer.toString( book.getNumBuyTrades() ) ;
            csvRow[3] = Integer.toString( book.getNumSellTrades() ) ;
            
            csvData.add( csvRow ) ;
        }
        
        File csvOutputFile = new File( outputDir, "all-stocks-trades.csv" ) ;
        CsvWriter csvWriter = new CsvWriter( csvOutputFile, new CsvWriterSettings() ) ;
        csvWriter.writeHeaders( csvHeaders ) ;
        for( String[] row : csvData ) {
            csvWriter.writeRow( row ) ;
        }
        csvWriter.flush() ;
        csvWriter.close() ;
    }
    
    private File getOutputDirectory() {
        
        File outputDir = new File( AlgoFoundry.getConfig().getWorkspacePath(), "tuner-output" ) ;
        if( !outputDir.exists() ) {
            outputDir.mkdirs() ;
        }
        return outputDir ;
    }
    
    private File getGoldenParamOutputDir( double mktProfit ) throws Exception {
    
        String dirName = StringUtil.fmtDbl( mktProfit ).replace( '.', '_' ) ;
        File dir = new File( this.outputDir, dirName ) ;
        if( dir.exists() ) {
            FileUtils.deleteDirectory( dir ) ;
        }
        dir.mkdirs() ;
        return dir ;
    }
}
