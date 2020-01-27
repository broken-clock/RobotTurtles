// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ext;

import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.nio.file.Path;
import java.beans.ConstructorProperties;
import java.beans.Transient;

public class Java7SupportImpl extends Java7Support
{
    private final Class<?> _bogus;
    
    public Java7SupportImpl() {
        Class<?> cls = Transient.class;
        cls = ConstructorProperties.class;
        this._bogus = cls;
    }
    
    @Override
    public Class<?> getClassJavaNioFilePath() {
        return Path.class;
    }
    
    @Override
    public JsonDeserializer<?> getDeserializerForJavaNioFilePath(final Class<?> rawType) {
        if (rawType == Path.class) {
            return new NioPathDeserializer();
        }
        return null;
    }
    
    @Override
    public JsonSerializer<?> getSerializerForJavaNioFilePath(final Class<?> rawType) {
        if (Path.class.isAssignableFrom(rawType)) {
            return new NioPathSerializer();
        }
        return null;
    }
    
    @Override
    public Boolean findTransient(final Annotated a) {
        final Transient t = a.getAnnotation(Transient.class);
        if (t != null) {
            return t.value();
        }
        return null;
    }
    
    @Override
    public Boolean hasCreatorAnnotation(final Annotated a) {
        final ConstructorProperties props = a.getAnnotation(ConstructorProperties.class);
        if (props != null) {
            return Boolean.TRUE;
        }
        return null;
    }
    
    @Override
    public PropertyName findConstructorName(final AnnotatedParameter p) {
        final AnnotatedWithParams ctor = p.getOwner();
        if (ctor != null) {
            final ConstructorProperties props = ctor.getAnnotation(ConstructorProperties.class);
            if (props != null) {
                final String[] names = props.value();
                final int ix = p.getIndex();
                if (ix < names.length) {
                    return PropertyName.construct(names[ix]);
                }
            }
        }
        return null;
    }
}
