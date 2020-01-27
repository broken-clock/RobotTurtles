// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Type;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.jsonschema.SchemaAware;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import com.fasterxml.jackson.databind.ser.ResolvableSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

public class StdDelegatingSerializer extends StdSerializer<Object> implements ContextualSerializer, ResolvableSerializer, JsonFormatVisitable, SchemaAware
{
    protected final Converter<Object, ?> _converter;
    protected final JavaType _delegateType;
    protected final JsonSerializer<Object> _delegateSerializer;
    
    public StdDelegatingSerializer(final Converter<?, ?> converter) {
        super(Object.class);
        this._converter = (Converter<Object, ?>)converter;
        this._delegateType = null;
        this._delegateSerializer = null;
    }
    
    public <T> StdDelegatingSerializer(final Class<T> cls, final Converter<T, ?> converter) {
        super(cls, false);
        this._converter = (Converter<Object, ?>)converter;
        this._delegateType = null;
        this._delegateSerializer = null;
    }
    
    public StdDelegatingSerializer(final Converter<Object, ?> converter, final JavaType delegateType, final JsonSerializer<?> delegateSerializer) {
        super(delegateType);
        this._converter = converter;
        this._delegateType = delegateType;
        this._delegateSerializer = (JsonSerializer<Object>)delegateSerializer;
    }
    
    protected StdDelegatingSerializer withDelegate(final Converter<Object, ?> converter, final JavaType delegateType, final JsonSerializer<?> delegateSerializer) {
        ClassUtil.verifyMustOverride(StdDelegatingSerializer.class, this, "withDelegate");
        return new StdDelegatingSerializer(converter, delegateType, delegateSerializer);
    }
    
    @Override
    public void resolve(final SerializerProvider provider) throws JsonMappingException {
        if (this._delegateSerializer != null && this._delegateSerializer instanceof ResolvableSerializer) {
            ((ResolvableSerializer)this._delegateSerializer).resolve(provider);
        }
    }
    
    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider provider, final BeanProperty property) throws JsonMappingException {
        JsonSerializer<?> delSer = this._delegateSerializer;
        JavaType delegateType = this._delegateType;
        if (delSer == null) {
            if (delegateType == null) {
                delegateType = this._converter.getOutputType(provider.getTypeFactory());
            }
            if (!delegateType.isJavaLangObject()) {
                delSer = provider.findValueSerializer(delegateType);
            }
        }
        if (delSer instanceof ContextualSerializer) {
            delSer = provider.handleSecondaryContextualization(delSer, property);
        }
        if (delSer == this._delegateSerializer && delegateType == this._delegateType) {
            return this;
        }
        return this.withDelegate(this._converter, delegateType, delSer);
    }
    
    protected Converter<Object, ?> getConverter() {
        return this._converter;
    }
    
    @Override
    public JsonSerializer<?> getDelegatee() {
        return this._delegateSerializer;
    }
    
    @Override
    public void serialize(final Object value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        final Object delegateValue = this.convertValue(value);
        if (delegateValue == null) {
            provider.defaultSerializeNull(gen);
            return;
        }
        JsonSerializer<Object> ser = this._delegateSerializer;
        if (ser == null) {
            ser = this._findSerializer(delegateValue, provider);
        }
        ser.serialize(delegateValue, gen, provider);
    }
    
    @Override
    public void serializeWithType(final Object value, final JsonGenerator gen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
        final Object delegateValue = this.convertValue(value);
        JsonSerializer<Object> ser = this._delegateSerializer;
        if (ser == null) {
            ser = this._findSerializer(value, provider);
        }
        ser.serializeWithType(delegateValue, gen, provider, typeSer);
    }
    
    @Override
    public boolean isEmpty(final SerializerProvider prov, final Object value) {
        final Object delegateValue = this.convertValue(value);
        if (delegateValue == null) {
            return true;
        }
        if (this._delegateSerializer == null) {
            return value == null;
        }
        return this._delegateSerializer.isEmpty(prov, delegateValue);
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) throws JsonMappingException {
        if (this._delegateSerializer instanceof SchemaAware) {
            return ((SchemaAware)this._delegateSerializer).getSchema(provider, typeHint);
        }
        return super.getSchema(provider, typeHint);
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint, final boolean isOptional) throws JsonMappingException {
        if (this._delegateSerializer instanceof SchemaAware) {
            return ((SchemaAware)this._delegateSerializer).getSchema(provider, typeHint, isOptional);
        }
        return super.getSchema(provider, typeHint);
    }
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        if (this._delegateSerializer != null) {
            this._delegateSerializer.acceptJsonFormatVisitor(visitor, typeHint);
        }
    }
    
    protected Object convertValue(final Object value) {
        return this._converter.convert(value);
    }
    
    protected JsonSerializer<Object> _findSerializer(final Object value, final SerializerProvider serializers) throws JsonMappingException {
        return serializers.findValueSerializer(value.getClass());
    }
}
