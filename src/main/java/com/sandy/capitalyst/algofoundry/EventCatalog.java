package com.sandy.capitalyst.algofoundry;

public class EventCatalog {

    public @interface Payload {
        Class<?> type() ;
    }

    public static final int CORE_EVENT_RANGE_MIN = 100 ;
    public static final int CORE_EVENT_RANGE_MAX = 200 ;

    // =============== Core Events [Start] =====================================
    
    // --------------- Core Events [End] ---------------------------------------
    
    public static final int EVT_SHOW_STOCK_SIM_PANEL = 201 ;
    public static final int EVT_INDICATOR_DAY_VALUE  = 202 ;
}
