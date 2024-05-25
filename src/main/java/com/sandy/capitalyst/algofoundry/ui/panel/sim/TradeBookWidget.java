package com.sandy.capitalyst.algofoundry.ui.panel.sim;

import com.sandy.capitalyst.algofoundry.core.ui.UITheme;
import com.sandy.capitalyst.algofoundry.tradebook.TradeBook;
import com.sandy.capitalyst.algofoundry.tradebook.TradeBookListener;
import info.clearthought.layout.TableLayout;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

import static com.sandy.capitalyst.algofoundry.core.ui.SwingUtils.getNewJLabel;
import static com.sandy.capitalyst.algofoundry.core.ui.SwingUtils.initPanelUI;

@Slf4j
public class TradeBookWidget extends SimControlPanel.SimControlWidget
    implements TradeBookListener {
    
    private static final DecimalFormat PCT_DF = new DecimalFormat( "##0.0" ) ;
    private static final DecimalFormat DF = new DecimalFormat( "##0.0" ) ;
    
    private final TradeBook tradeBook ;
    
    private JLabel holdingQtyValLabel   = null ;
    private JLabel profitPctValLabel    = null ;
    private JLabel avgCostPriceLabel    = null ;
    private JLabel notProfitPctValLabel = null ;
    
    public TradeBookWidget( SimPanel simPanel) {
        super( simPanel ) ;
        this.tradeBook = simPanel.getTradeBook() ;
        this.tradeBook.addListener( this ) ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        initPanelUI( this ) ;
        double size[][] = {
            { 0.7, 0.3 },
            { 0.25, 0.25, 0.25, 0.25 }
        } ;
        super.setLayout( new TableLayout( size ) ) ;
        
        JLabel holdingQty   = getAttributeLabel( "Holding quantity" ) ;
        JLabel avgCostPrice = getAttributeLabel( "Avg. cost price" ) ;
        JLabel profitPct    = getAttributeLabel( "Total Profit %" ) ;
        JLabel notProfitPct = getAttributeLabel( "Notional Profit %" ) ;
        
        holdingQtyValLabel   = getValueLabel() ;
        profitPctValLabel    = getValueLabel() ;
        avgCostPriceLabel    = getValueLabel() ;
        notProfitPctValLabel = getValueLabel() ;
        
        add( profitPct,  "0, 0" ) ;
        add( profitPctValLabel, "1, 0" ) ;
        
        add( holdingQty, "0, 1" ) ;
        add( holdingQtyValLabel, "1, 1" ) ;
        
        add( avgCostPrice, "0, 2" ) ;
        add( avgCostPriceLabel, "1, 2" ) ;
        
        add( notProfitPct,  "0, 3" ) ;
        add( notProfitPctValLabel, "1, 3" ) ;
        
        setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
    }
    
    private JLabel getAttributeLabel( String text ) {
        JLabel label = getNewJLabel( text ) ;
        label.setFont( UITheme.TRADE_BOOK_LABEL_FONT ) ;
        label.setBorder( BorderFactory.createEmptyBorder( 5, 2, 5, 2 ) ) ;
        return label ;
    }
    
    private JLabel getValueLabel() {
        JLabel label = getNewJLabel( "0" ) ;
        label.setFont( UITheme.TRADE_BOOK_LABEL_FONT ) ;
        label.setHorizontalAlignment( SwingConstants.RIGHT ); ;
        label.setBorder( BorderFactory.createEmptyBorder( 5, 2, 5, 2 ) ) ;
        return label ;
    }
    
    @Override
    public void tradeBookUpdated( TradeBook tradeBook ) {
        
        double profitPct    = tradeBook.getTotalProfitPct() ;
        double notProfitPct = tradeBook.getNotionalProfitPct() ;
        
        holdingQtyValLabel.setText( String.valueOf( tradeBook.getHoldingQty() ) ) ;
        profitPctValLabel.setText( PCT_DF.format( profitPct ) + " %" ) ;
        avgCostPriceLabel.setText( DF.format( tradeBook.getAvgCostPrice() ) ) ;
        notProfitPctValLabel.setText( PCT_DF.format( notProfitPct ) + " %" ) ;
        
        profitPctValLabel.setForeground( profitPct > 0 ? Color.GREEN : Color.RED ) ;
        notProfitPctValLabel.setForeground( notProfitPct > 0 ? Color.GREEN : Color.RED ) ;
    }
}
