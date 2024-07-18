package com.sandy.capitalyst.algofoundry.app.backtester.gui.tuner;

import com.sandy.capitalyst.algofoundry.app.tuner.GridSearch;
import com.sandy.capitalyst.algofoundry.app.tuner.HyperParameterGroup;
import com.sandy.capitalyst.algofoundry.strategy.impl.MyStrategyConfig;
import com.sandy.capitalyst.algofoundry.strategy.tradebook.TradeBook;
import lombok.extern.slf4j.Slf4j;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

import static com.sandy.capitalyst.algofoundry.app.core.util.StringUtil.fmtDbl;

@Slf4j
public class TunerToolBar extends JToolBar
        implements GridSearch.GridSearchListener {
    
    public static final Font PROFIT_LABEL_FONT = new Font( "Helvetica", Font.PLAIN, 14 ) ;
    
    private final HyperparameterTunerFrame frame ;
    
    private JButton runBtn = null ;
    private JButton stopBtn = null ;
    private JProgressBar progressBar = null ;
    private JLabel goldenProfitLabel = null ;
    
    public TunerToolBar( HyperparameterTunerFrame frame ) {
        this.frame = frame ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        runBtn = new JButton( "Run" ) ;
        runBtn.addActionListener( e -> frame.runTuner() ) ;
        
        stopBtn = new JButton( "Stop" ) ;
        stopBtn.addActionListener( e-> frame.stopTuner() ) ;
        stopBtn.setEnabled( false ) ;
        
        JLabel profitLabel = new JLabel( "Max profit = " ) ;
        profitLabel.setFont( PROFIT_LABEL_FONT ) ;
        
        goldenProfitLabel = new JLabel( "" ) ;
        goldenProfitLabel.setFont( PROFIT_LABEL_FONT ) ;
        
        progressBar = new JProgressBar() ;
        progressBar.setStringPainted( true ) ;
        
        add( runBtn ) ;
        add( Box.createHorizontalStrut( 10 ) ) ;
        add( stopBtn ) ;
        addSeparator() ;
        add( profitLabel ) ;
        add( goldenProfitLabel ) ;
        addSeparator() ;
        add( Box.createHorizontalGlue() ) ;
        add( progressBar ) ;
    }
    
    public void setRunButtonEnabled( boolean enable ) {
        runBtn.setEnabled( enable ) ;
        stopBtn.setEnabled( !enable ) ;
    }
    
    @Override
    public void candleLoadingStarted( int numSymbols ) {
        SwingUtilities.invokeLater( () -> {
            progressBar.setMinimum( 1 ) ;
            progressBar.setMaximum( numSymbols ) ;
        } ) ;
    }
    
    @Override
    public void loadingCandles( int index, int numSymbols, String symbol ) {
        SwingUtilities.invokeLater( () -> {
            progressBar.setValue( index ) ;
            progressBar.setString( "[" + index + "/" + numSymbols + "] Loading " + symbol ) ;
        } ) ;
    }
    
    @Override
    public void candleLoadingEnded() {
        SwingUtilities.invokeLater( () -> {
            progressBar.setValue( 0 ) ;
            progressBar.setString( "" ) ;
        } ) ;
    }
    
    @Override
    public void goldenParametersFound( double mktProfit,
                                       HyperParameterGroup parameters,
                                       MyStrategyConfig cfg,
                                       List<TradeBook> tradeBooks ) {
        
        goldenProfitLabel.setText( fmtDbl( mktProfit ) + "%" ) ;
        
        try {
            Clip clip = AudioSystem.getClip() ;
            AudioInputStream ais = AudioSystem.getAudioInputStream(
                Objects.requireNonNull(
                    getClass().getResourceAsStream( "/sound/bell.wav"
                )
            ) ) ;
            clip.open( ais ) ;
            clip.start() ;
        }
        catch( Exception e ) {
            throw new RuntimeException( e );
        }
    }
}
