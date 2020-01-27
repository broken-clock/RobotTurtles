// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.jsonschema;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonSerializableSchema {
    public static final String NO_VALUE = "##irrelevant";
    
    String id() default "";
    
    String schemaType() default "any";
    
    @Deprecated
    String schemaObjectPropertiesDefinition() default "##irrelevant";
    
    @Deprecated
    String schemaItemDefinition() default "##irrelevant";
}
