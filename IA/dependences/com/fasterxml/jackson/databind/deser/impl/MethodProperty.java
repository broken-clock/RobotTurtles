// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.impl;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import java.lang.annotation.Annotation;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import java.lang.reflect.Method;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;

public final class MethodProperty extends SettableBeanProperty
{
    private static final long serialVersionUID = 1L;
    protected final AnnotatedMethod _annotated;
    protected final transient Method _setter;
    protected final boolean _skipNulls;
    
    public MethodProperty(final BeanPropertyDefinition propDef, final JavaType type, final TypeDeserializer typeDeser, final Annotations contextAnnotations, final AnnotatedMethod method) {
        super(propDef, type, typeDeser, contextAnnotations);
        this._annotated = method;
        this._setter = method.getAnnotated();
        this._skipNulls = NullsConstantProvider.isSkipper(this._nullProvider);
    }
    
    protected MethodProperty(final MethodProperty src, final JsonDeserializer<?> deser, final NullValueProvider nva) {
        super(src, deser, nva);
        this._annotated = src._annotated;
        this._setter = src._setter;
        this._skipNulls = NullsConstantProvider.isSkipper(nva);
    }
    
    protected MethodProperty(final MethodProperty src, final PropertyName newName) {
        super(src, newName);
        this._annotated = src._annotated;
        this._setter = src._setter;
        this._skipNulls = src._skipNulls;
    }
    
    protected MethodProperty(final MethodProperty src, final Method m) {
        super(src);
        this._annotated = src._annotated;
        this._setter = m;
        this._skipNulls = src._skipNulls;
    }
    
    @Override
    public SettableBeanProperty withName(final PropertyName newName) {
        return new MethodProperty(this, newName);
    }
    
    @Override
    public SettableBeanProperty withValueDeserializer(final JsonDeserializer<?> deser) {
        if (this._valueDeserializer == deser) {
            return this;
        }
        return new MethodProperty(this, deser, this._nullProvider);
    }
    
    @Override
    public SettableBeanProperty withNullProvider(final NullValueProvider nva) {
        return new MethodProperty(this, this._valueDeserializer, nva);
    }
    
    @Override
    public void fixAccess(final DeserializationConfig config) {
        this._annotated.fixAccess(config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
    }
    
    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> acls) {
        return (A)((this._annotated == null) ? null : this._annotated.getAnnotation(acls));
    }
    
    @Override
    public AnnotatedMember getMember() {
        return this._annotated;
    }
    
    @Override
    public void deserializeAndSet(final JsonParser p, final DeserializationContext ctxt, final Object instance) throws IOException {
        Object value;
        if (p.hasToken(JsonToken.VALUE_NULL)) {
            if (this._skipNulls) {
                return;
            }
            value = this._nullProvider.getNullValue(ctxt);
        }
        else if (this._valueTypeDeserializer == null) {
            value = this._valueDeserializer.deserialize(p, ctxt);
            if (value == null) {
                if (this._skipNulls) {
                    return;
                }
                value = this._nullProvider.getNullValue(ctxt);
            }
        }
        else {
            value = this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer);
        }
        try {
            this._setter.invoke(instance, value);
        }
        catch (Exception e) {
            this._throwAsIOE(p, e, value);
        }
    }
    
    @Override
    public Object deserializeSetAndReturn(final JsonParser p, final DeserializationContext ctxt, final Object instance) throws IOException {
        Object value;
        if (p.hasToken(JsonToken.VALUE_NULL)) {
            if (this._skipNulls) {
                return instance;
            }
            value = this._nullProvider.getNullValue(ctxt);
        }
        else if (this._valueTypeDeserializer == null) {
            value = this._valueDeserializer.deserialize(p, ctxt);
            if (value == null) {
                if (this._skipNulls) {
                    return instance;
                }
                value = this._nullProvider.getNullValue(ctxt);
            }
        }
        else {
            value = this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer);
        }
        try {
            final Object result = this._setter.invoke(instance, value);
            return (result == null) ? instance : result;
        }
        catch (Exception e) {
            this._throwAsIOE(p, e, value);
            return null;
        }
    }
    
    @Override
    public final void set(final Object instance, final Object value) throws IOException {
        try {
            this._setter.invoke(instance, value);
        }
        catch (Exception e) {
            this._throwAsIOE(e, value);
        }
    }
    
    @Override
    public Object setAndReturn(final Object instance, final Object value) throws IOException {
        try {
            final Object result = this._setter.invoke(instance, value);
            return (result == null) ? instance : result;
        }
        catch (Exception e) {
            this._throwAsIOE(e, value);
            return null;
        }
    }
    
    Object readResolve() {
        return new MethodProperty(this, this._annotated.getAnnotated());
    }
}
