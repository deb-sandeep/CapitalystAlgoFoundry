package com.sandy.capitalyst.algofoundry.app.tuner;

import com.sandy.capitalyst.algofoundry.app.apiclient.equitymeta.EquityMeta;
import com.sandy.capitalyst.algofoundry.strategy.impl.MyStrategyConfig;
import com.sandy.capitalyst.algofoundry.strategy.series.candleseries.Candle;
import com.sandy.capitalyst.algofoundry.strategy.util.HParameter;
import com.sandy.capitalyst.algofoundry.strategy.util.StrategyConfigUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class GridSearch {
    
    public interface GridSearchListener {
        void parametersUpdated() ;
    }
    
    private final MyStrategyConfig baseConfig;
    private final List<EquityMeta> equityMetaList ;
    
    private final GridSearchListener listener ;
    
    private boolean killSwitchOn = false ;
    
    @Getter
    private final HyperParameterGroup hyperParameters ;
    
    private final Map<String, List<Candle>> stockCandlesMap = new LinkedHashMap<>() ;
    
    public GridSearch( List<EquityMeta> equityMetaList, MyStrategyConfig baseConfig,
                       GridSearchListener listener ) {
        this.baseConfig = baseConfig;
        this.equityMetaList = equityMetaList ;
        this.hyperParameters = new HyperParameterGroup( extractHyperParameters() ) ;
        this.listener = listener ;
        
        loadCandles() ;
    }
    
    private List<HyperParameter> extractHyperParameters() {
        
        List<HyperParameter> hParamList = new ArrayList<>() ;
        List<Field> hpFields = getHyperParameterFields( new ArrayList<>(),
                                                        this.baseConfig.getClass() ) ;
        for( Field hpField : hpFields ) {
            HParameter     hp     = hpField.getAnnotation( HParameter.class ) ;
            HyperParameter hParam = new HyperParameter( hpField.getName(), hp.min(), hp.max(), hp.step() ) ;
            hParamList.add( hParam ) ;
        }
        return hParamList ;
    }
    
    private List<Field> getHyperParameterFields( List<Field> hpFields, Class<?> type ) {
        
        if( type != null ) {
            getHyperParameterFields( hpFields, type.getSuperclass() ) ;
            Field[] fields = type.getDeclaredFields() ;
            for( Field field : fields ) {
                Annotation hpAnnot = field.getAnnotation( HParameter.class ) ;
                if( hpAnnot != null ) {
                    hpFields.add( field ) ;
                }
            }
        }
        return hpFields ;
    }
    
    private void loadCandles() {
    }
    
    public void setKillSwitch() {
        killSwitchOn = true ;
    }
    
    public MyStrategyConfig tuneHyperParameters() throws Exception {
        
        MyStrategyConfig goldenConfig = null ;
        
        MyStrategyConfig cfgClone = new MyStrategyConfig() ;
        StrategyConfigUtil.populateStrategyConfig( cfgClone, this.baseConfig ) ;
        
        while( !hyperParameters.isGridExplorationComplete() && !killSwitchOn ) {
            //hyperParameters.populateNextSequentialGridValue( cfgClone ) ;
            if( !hyperParameters.randomizeGridPoint( cfgClone ) ) {
                killSwitchOn = true ;
                log.debug( "Could not find a fresh grid point." ) ;
            }
            else {
                if( listener != null ) listener.parametersUpdated() ;
                Thread.sleep( 1 ) ;
            }
        }
        
        killSwitchOn = false ;
        return goldenConfig ;
    }
}
