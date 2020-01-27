// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ext;

import java.util.logging.Logger;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.Annotated;

public abstract class Java7Support
{
    private static final Java7Support IMPL;
    
    public static Java7Support instance() {
        return Java7Support.IMPL;
    }
    
    public abstract Boolean findTransient(final Annotated p0);
    
    public abstract Boolean hasCreatorAnnotation(final Annotated p0);
    
    public abstract PropertyName findConstructorName(final AnnotatedParameter p0);
    
    public abstract Class<?> getClassJavaNioFilePath();
    
    public abstract JsonDeserializer<?> getDeserializerForJavaNioFilePath(final Class<?> p0);
    
    public abstract JsonSerializer<?> getSerializerForJavaNioFilePath(final Class<?> p0);
    
    static {
        Java7Support impl = null;
        try {
            final Class<?> cls = Class.forName("com.fasterxml.jackson.databind.ext.Java7SupportImpl");
            impl = ClassUtil.createInstance(cls, false);
        }
        catch (Throwable t) {
            Logger.getLogger(Java7Support.class.getName()).warning("Unable to load JDK7 types (annotations, java.nio.file.Path): no Java7 support added");
        }
        IMPL = impl;
    }
}
