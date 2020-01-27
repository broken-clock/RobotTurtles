// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.BeanProperty;
import java.io.IOException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.JsonSerializer;

public final class TypeWrappedSerializer extends JsonSerializer<Object> implements ContextualSerializer
{
    protected final TypeSerializer _typeSerializer;
    protected final JsonSerializer<Object> _serializer;
    
    public TypeWrappedSerializer(final TypeSerializer typeSer, final JsonSerializer<?> ser) {
        this._typeSerializer = typeSer;
        this._serializer = (JsonSerializer<Object>)ser;
    }
    
    @Override
    public void serialize(final Object value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
        this._serializer.serializeWithType(value, g, provider, this._typeSerializer);
    }
    
    @Override
    public void serializeWithType(final Object value, final JsonGenerator g, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
        this._serializer.serializeWithType(value, g, provider, typeSer);
    }
    
    @Override
    public Class<Object> handledType() {
        return Object.class;
    }
    
    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider provider, final BeanProperty property) throws JsonMappingException {
        JsonSerializer<?> ser = this._serializer;
        if (ser instanceof ContextualSerializer) {
            ser = provider.handleSecondaryContextualization(ser, property);
        }
        if (ser == this._serializer) {
            return this;
        }
        return new TypeWrappedSerializer(this._typeSerializer, ser);
    }
    
    public JsonSerializer<Object> valueSerializer() {
        return this._serializer;
    }
    
    public TypeSerializer typeSerializer() {
        return this._typeSerializer;
    }
}
