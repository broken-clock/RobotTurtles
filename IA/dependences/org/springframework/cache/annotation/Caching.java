// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Caching {
    Cacheable[] cacheable() default {};
    
    CachePut[] put() default {};
    
    CacheEvict[] evict() default {};
}
