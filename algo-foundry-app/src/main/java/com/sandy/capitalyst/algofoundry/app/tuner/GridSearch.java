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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class GridSearch {
    
    private final MyStrategyConfig baseConfig;
    private final List<EquityMeta> equityMetaList ;

    @Getter
    private final HyperParameterGroup hyperParameters ;
    
    private final Map<String, List<Candle>> stockCandlesMap = new LinkedHashMap<>() ;
    
    public GridSearch( List<EquityMeta> equityMetaList, MyStrategyConfig baseConfig ) {
        this.baseConfig = baseConfig;
        this.equityMetaList = equityMetaList ;
        this.hyperParameters = new HyperParameterGroup( extractHyperParameters() ) ;
        
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
        
        Field[] fields = type.getDeclaredFields() ;
        for( Field field : fields ) {
            Annotation hpAnnot = field.getAnnotation( HParameter.class ) ;
            if( hpAnnot != null ) {
                hpFields.add( field ) ;
            }
        }
        return hpFields ;
    }
    
    private void loadCandles() {
    
    }
    
    public MyStrategyConfig tuneHyperParameters() throws Exception {
        
        MyStrategyConfig goldenConfig = null ;
        
        MyStrategyConfig cfgClone = new MyStrategyConfig() ;
        StrategyConfigUtil.populateStrategyConfig( cfgClone, this.baseConfig ) ;
        
        int numGridPoints = 1 ;
        while( !hyperParameters.isGridExplorationComplete() ) {
            hyperParameters.populateNextGridValue( cfgClone ) ;
            numGridPoints++ ;
        }
        return goldenConfig ;
    }
}
