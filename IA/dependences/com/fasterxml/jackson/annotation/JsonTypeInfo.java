// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.annotation;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonTypeInfo {
    Id use();
    
    As include() default As.PROPERTY;
    
    String property() default "";
    
    Class<?> defaultImpl() default JsonTypeInfo.class;
    
    boolean visible() default false;
    
    public enum Id
    {
        NONE((String)null), 
        CLASS("@class"), 
        MINIMAL_CLASS("@c"), 
        NAME("@type"), 
        CUSTOM((String)null);
        
        private final String _defaultPropertyName;
        
        private Id(final String defProp) {
            this._defaultPropertyName = defProp;
        }
        
        public String getDefaultPropertyName() {
            return this._defaultPropertyName;
        }
    }
    
    public enum As
    {
        PROPERTY, 
        WRAPPER_OBJECT, 
        WRAPPER_ARRAY, 
        EXTERNAL_PROPERTY, 
        EXISTING_PROPERTY;
    }
    
    @Deprecated
    public abstract static class None
    {
    }
}
