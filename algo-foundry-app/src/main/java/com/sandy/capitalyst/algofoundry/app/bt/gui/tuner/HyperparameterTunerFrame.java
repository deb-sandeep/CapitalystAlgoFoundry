package com.sandy.capitalyst.algofoundry.app.bt.gui.tuner;

import com.sandy.capitalyst.algofoundry.app.AlgoFoundry;
import com.sandy.capitalyst.algofoundry.app.apiclient.equitymeta.EquityMeta;
import com.sandy.capitalyst.algofoundry.app.core.ui.SwingUtils;
import com.sandy.capitalyst.algofoundry.app.tuner.GridSearch;
import com.sandy.capitalyst.algofoundry.app.tuner.HyperParameter;
import com.sandy.capitalyst.algofoundry.app.tuner.HyperParameterGroup;
import com.sandy.capitalyst.algofoundry.strategy.impl.MyStrategyConfig;
import com.sandy.capitalyst.algofoundry.strategy.util.StrategyConfigUtil;
import com.sandy.capitalyst.algofoundry.strategy.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HyperparameterTunerFrame extends JFrame
    implements GridSearch.GridSearchListener {
    
    public static final Font PARAM_VALUE_LABEL_FONT = new Font( "Courier New", Font.PLAIN,  16 ) ;
    public static final Font PARAM_NAME_LABEL_FONT  = new Font( "Courier New", Font.PLAIN,  10 ) ;
    
    private final List<EquityMeta> metaList ;
    private final GridSearch gridSearch ;
    private final HyperParameterGroup parameters ;
    
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
    
    private void setUpUI() {
        
        toolBar = new TunerToolBar( this ) ;
        
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
        label.setBorder( BorderFactory.createLineBorder( Color.DARK_GRAY.darker(), 1 ) ) ;
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
}
