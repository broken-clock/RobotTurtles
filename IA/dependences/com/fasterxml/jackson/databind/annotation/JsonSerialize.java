// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.annotation;

import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.annotation.JacksonAnnotation;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonSerialize {
    Class<? extends JsonSerializer> using() default JsonSerializer.None.class;
    
    Class<? extends JsonSerializer> contentUsing() default JsonSerializer.None.class;
    
    Class<? extends JsonSerializer> keyUsing() default JsonSerializer.None.class;
    
    Class<? extends JsonSerializer> nullsUsing() default JsonSerializer.None.class;
    
    Class<?> as() default Void.class;
    
    Class<?> keyAs() default Void.class;
    
    Class<?> contentAs() default Void.class;
    
    Typing typing() default Typing.DEFAULT_TYPING;
    
    Class<? extends Converter> converter() default Converter.None.class;
    
    Class<? extends Converter> contentConverter() default Converter.None.class;
    
    @Deprecated
    Inclusion include() default Inclusion.DEFAULT_INCLUSION;
    
    @Deprecated
    public enum Inclusion
    {
        ALWAYS, 
        NON_NULL, 
        NON_DEFAULT, 
        NON_EMPTY, 
        DEFAULT_INCLUSION;
    }
    
    public enum Typing
    {
        DYNAMIC, 
        STATIC, 
        DEFAULT_TYPING;
    }
}
