package com.sandy.capitalyst.algofoundry.app.bt.gui.panel.eqmeta;

import com.sandy.capitalyst.algofoundry.app.AlgoFoundry;
import com.sandy.capitalyst.algofoundry.app.apiclient.equitymeta.EquityMeta;
import com.sandy.capitalyst.algofoundry.app.apiclient.equitymeta.EquityMetaAPIClient;
import com.sandy.capitalyst.algofoundry.app.apiclient.histeod.EquityHistEODAPIClient;
import com.sandy.capitalyst.algofoundry.app.core.ui.UITheme;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.sandy.capitalyst.algofoundry.app.AlgoFoundry.* ;

@Slf4j
public class EquityMetaTablePanel extends JPanel {
    
    private EquityMetaTable table ;
    
    @Getter private List<EquityMeta> metaList ;
    
    public EquityMetaTablePanel() {
        
        EquityMetaAPIClient    apiClient  = getBean( EquityMetaAPIClient.class ) ;
        EquityHistEODAPIClient histClient = getBean( EquityHistEODAPIClient.class ) ;
        
        try{
            log.debug( "Creating reco table panel" ) ;
            metaList = apiClient.getEquityMetaList() ;
            
            log.debug( "Fetched meta list. {} records.", metaList.size() );
            this.table = new EquityMetaTable( metaList ) ;
            
            if( AlgoFoundry.getConfig().isRefreshOfflineCache() ) {
                log.debug( "Refreshing entire offline cache." ) ;
                metaList.forEach( m -> {
                    try {
                        log.debug( "  Refreshing offline candles for {}", m.getSymbol() ) ;
                        histClient.getHistoricCandles( m.getSymbol() );
                    }
                    catch( Exception e ) {
                        throw new RuntimeException( e ) ;
                    }
                } );
            }
            
            setUpUI() ;
        }
        catch( Exception e ) {
            log.error( "Could not fetch Equity Meta list.", e ) ;
        }
    }
    
    private void setUpUI() {
        setLayout( new BorderLayout() ) ;
        setBackground( UITheme.BACKGROUND_COLOR ) ;
        
        final JScrollPane tableSP = new JScrollPane( this.table ) ;
        tableSP.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED ) ;
        
        add( tableSP, BorderLayout.CENTER ) ;
    }
}
