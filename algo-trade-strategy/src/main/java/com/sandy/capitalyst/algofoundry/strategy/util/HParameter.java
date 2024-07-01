package com.sandy.capitalyst.algofoundry.strategy.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( ElementType.FIELD )
@Retention( RetentionPolicy.RUNTIME )
public @interface HParameter {

    float min() ;
    float max() ;
    float step() default 1.0F ;
}
