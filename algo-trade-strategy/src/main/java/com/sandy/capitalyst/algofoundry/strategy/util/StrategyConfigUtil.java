package com.sandy.capitalyst.algofoundry.strategy.util;

import com.sandy.capitalyst.algofoundry.strategy.StrategyConfig;
import org.apache.commons.beanutils.BeanUtilsBean;

import java.lang.reflect.InvocationTargetException;

public class StrategyConfigUtil {
    
    public static void populateStrategyConfig( StrategyConfig cfg, Object source )
            throws InvocationTargetException, IllegalAccessException {
        BeanUtilsBean.getInstance().copyProperties( cfg, source ) ;
    }
}
