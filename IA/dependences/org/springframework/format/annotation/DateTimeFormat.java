// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.format.annotation;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface DateTimeFormat {
    String style() default "SS";
    
    ISO iso() default ISO.NONE;
    
    String pattern() default "";
    
    public enum ISO
    {
        DATE, 
        TIME, 
        DATE_TIME, 
        NONE;
    }
}
