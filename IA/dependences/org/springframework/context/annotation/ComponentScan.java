// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import org.springframework.beans.factory.support.BeanNameGenerator;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Annotation;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Documented
public @interface ComponentScan {
    String[] value() default {};
    
    String[] basePackages() default {};
    
    Class<?>[] basePackageClasses() default {};
    
    Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;
    
    Class<? extends ScopeMetadataResolver> scopeResolver() default AnnotationScopeMetadataResolver.class;
    
    ScopedProxyMode scopedProxy() default ScopedProxyMode.DEFAULT;
    
    String resourcePattern() default "**/*.class";
    
    boolean useDefaultFilters() default true;
    
    Filter[] includeFilters() default {};
    
    Filter[] excludeFilters() default {};
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({})
    public @interface Filter {
        FilterType type() default FilterType.ANNOTATION;
        
        Class<?>[] value() default {};
        
        String[] pattern() default {};
    }
}
