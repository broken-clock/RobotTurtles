// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.annotation;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface NumberFormat {
    Style style() default Style.NUMBER;
    
    String pattern() default "";
    
    public enum Style
    {
        NUMBER, 
        CURRENCY, 
        PERCENT;
    }
}
