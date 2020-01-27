// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.annotation;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Import;
import java.lang.annotation.Documented;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ CachingConfigurationSelector.class })
public @interface EnableCaching {
    boolean proxyTargetClass() default false;
    
    AdviceMode mode() default AdviceMode.PROXY;
    
    int order() default Integer.MAX_VALUE;
}
