// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.impl;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.util.Collection;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.Serializable;
import com.fasterxml.jackson.databind.JsonDeserializer;

public final class TypeWrappedDeserializer extends JsonDeserializer<Object> implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final TypeDeserializer _typeDeserializer;
    protected final JsonDeserializer<Object> _deserializer;
    
    public TypeWrappedDeserializer(final TypeDeserializer typeDeser, final JsonDeserializer<?> deser) {
        this._typeDeserializer = typeDeser;
        this._deserializer = (JsonDeserializer<Object>)deser;
    }
    
    @Override
    public Class<?> handledType() {
        return this._deserializer.handledType();
    }
    
    @Override
    public Boolean supportsUpdate(final DeserializationConfig config) {
        return this._deserializer.supportsUpdate(config);
    }
    
    @Override
    public JsonDeserializer<?> getDelegatee() {
        return this._deserializer.getDelegatee();
    }
    
    @Override
    public Collection<Object> getKnownPropertyNames() {
        return this._deserializer.getKnownPropertyNames();
    }
    
    @Override
    public Object getNullValue(final DeserializationContext ctxt) throws JsonMappingException {
        return this._deserializer.getNullValue(ctxt);
    }
    
    @Override
    public Object getEmptyValue(final DeserializationContext ctxt) throws JsonMappingException {
        return this._deserializer.getEmptyValue(ctxt);
    }
    
    @Override
    public Object deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        return this._deserializer.deserializeWithType(p, ctxt, this._typeDeserializer);
    }
    
    @Override
    public Object deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        throw new IllegalStateException("Type-wrapped deserializer's deserializeWithType should never get called");
    }
    
    @Override
    public Object deserialize(final JsonParser p, final DeserializationContext ctxt, final Object intoValue) throws IOException {
        return this._deserializer.deserialize(p, ctxt, intoValue);
    }
}
