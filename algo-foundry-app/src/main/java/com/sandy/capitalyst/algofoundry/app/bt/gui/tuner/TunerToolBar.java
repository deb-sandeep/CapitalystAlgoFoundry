package com.sandy.capitalyst.algofoundry.app.bt.gui.tuner;

import javax.swing.*;

public class TunerToolBar extends JToolBar {
    
    private final HyperparameterTunerFrame frame ;
    
    private JButton runBtn = null ;
    private JButton stopBtn = null ;
    
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
        
        add( runBtn ) ;
        add( Box.createHorizontalStrut( 10 ) ) ;
        add( stopBtn ) ;
    }
    
    public void setRunButtonEnabled( boolean enable ) {
        runBtn.setEnabled( enable ) ;
        stopBtn.setEnabled( !enable ) ;
    }
}
