package com.sandy.capitalyst.algofoundry.ui.indchart;

import com.sandy.capitalyst.algofoundry.apiclient.histeod.EquityEODHistory;
import com.sandy.capitalyst.algofoundry.apiclient.histeod.payload.AbstractDayValuePayload;

import java.util.ArrayList;
import java.util.List;

public class MACDChart extends IndicatorChart {
    
    private final List<EquityEODHistory.PayloadType> consumedPayloadTypes = new ArrayList<>() ;

    public MACDChart( String symbol ) {
        super( symbol, "MACD" );
    }
    
    @Override
    protected void handleDayValuePayload( AbstractDayValuePayload payload ) {}
    
    @Override
    public List<EquityEODHistory.PayloadType> getConsumedPayloadTypes() {
        return consumedPayloadTypes ;
    }
}
