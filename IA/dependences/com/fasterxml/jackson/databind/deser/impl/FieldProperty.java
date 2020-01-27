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
import java.lang.reflect.Member;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import java.lang.reflect.Field;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;

public final class FieldProperty extends SettableBeanProperty
{
    private static final long serialVersionUID = 1L;
    protected final AnnotatedField _annotated;
    protected final transient Field _field;
    protected final boolean _skipNulls;
    
    public FieldProperty(final BeanPropertyDefinition propDef, final JavaType type, final TypeDeserializer typeDeser, final Annotations contextAnnotations, final AnnotatedField field) {
        super(propDef, type, typeDeser, contextAnnotations);
        this._annotated = field;
        this._field = field.getAnnotated();
        this._skipNulls = NullsConstantProvider.isSkipper(this._nullProvider);
    }
    
    protected FieldProperty(final FieldProperty src, final JsonDeserializer<?> deser, final NullValueProvider nva) {
        super(src, deser, nva);
        this._annotated = src._annotated;
        this._field = src._field;
        this._skipNulls = NullsConstantProvider.isSkipper(nva);
    }
    
    protected FieldProperty(final FieldProperty src, final PropertyName newName) {
        super(src, newName);
        this._annotated = src._annotated;
        this._field = src._field;
        this._skipNulls = src._skipNulls;
    }
    
    protected FieldProperty(final FieldProperty src) {
        super(src);
        this._annotated = src._annotated;
        final Field f = this._annotated.getAnnotated();
        if (f == null) {
            throw new IllegalArgumentException("Missing field (broken JDK (de)serialization?)");
        }
        this._field = f;
        this._skipNulls = src._skipNulls;
    }
    
    @Override
    public SettableBeanProperty withName(final PropertyName newName) {
        return new FieldProperty(this, newName);
    }
    
    @Override
    public SettableBeanProperty withValueDeserializer(final JsonDeserializer<?> deser) {
        if (this._valueDeserializer == deser) {
            return this;
        }
        return new FieldProperty(this, deser, this._nullProvider);
    }
    
    @Override
    public SettableBeanProperty withNullProvider(final NullValueProvider nva) {
        return new FieldProperty(this, this._valueDeserializer, nva);
    }
    
    @Override
    public void fixAccess(final DeserializationConfig config) {
        ClassUtil.checkAndFixAccess(this._field, config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
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
            this._field.set(instance, value);
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
            this._field.set(instance, value);
        }
        catch (Exception e) {
            this._throwAsIOE(p, e, value);
        }
        return instance;
    }
    
    @Override
    public void set(final Object instance, final Object value) throws IOException {
        try {
            this._field.set(instance, value);
        }
        catch (Exception e) {
            this._throwAsIOE(e, value);
        }
    }
    
    @Override
    public Object setAndReturn(final Object instance, final Object value) throws IOException {
        try {
            this._field.set(instance, value);
        }
        catch (Exception e) {
            this._throwAsIOE(e, value);
        }
        return instance;
    }
    
    Object readResolve() {
        return new FieldProperty(this);
    }
}
