// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ LoadTimeWeavingConfiguration.class })
public @interface EnableLoadTimeWeaving {
    AspectJWeaving aspectjWeaving() default AspectJWeaving.AUTODETECT;
    
    public enum AspectJWeaving
    {
        ENABLED, 
        DISABLED, 
        AUTODETECT;
    }
}
