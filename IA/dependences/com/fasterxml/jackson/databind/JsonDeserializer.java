// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.util.AccessPattern;
import java.util.Collection;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.deser.NullValueProvider;

public abstract class JsonDeserializer<T> implements NullValueProvider
{
    public abstract T deserialize(final JsonParser p0, final DeserializationContext p1) throws IOException, JsonProcessingException;
    
    public T deserialize(final JsonParser p, final DeserializationContext ctxt, final T intoValue) throws IOException {
        if (ctxt.isEnabled(MapperFeature.IGNORE_MERGE_FOR_UNMERGEABLE)) {
            return this.deserialize(p, ctxt);
        }
        throw new UnsupportedOperationException("Cannot update object of type " + intoValue.getClass().getName() + " (by deserializer of type " + this.getClass().getName() + ")");
    }
    
    public Object deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromAny(p, ctxt);
    }
    
    public JsonDeserializer<T> unwrappingDeserializer(final NameTransformer unwrapper) {
        return this;
    }
    
    public JsonDeserializer<?> replaceDelegatee(final JsonDeserializer<?> delegatee) {
        throw new UnsupportedOperationException();
    }
    
    public Class<?> handledType() {
        return null;
    }
    
    public boolean isCachable() {
        return false;
    }
    
    public JsonDeserializer<?> getDelegatee() {
        return null;
    }
    
    public Collection<Object> getKnownPropertyNames() {
        return null;
    }
    
    @Override
    public T getNullValue(final DeserializationContext ctxt) throws JsonMappingException {
        return this.getNullValue();
    }
    
    @Override
    public AccessPattern getNullAccessPattern() {
        return AccessPattern.CONSTANT;
    }
    
    public AccessPattern getEmptyAccessPattern() {
        return AccessPattern.DYNAMIC;
    }
    
    public Object getEmptyValue(final DeserializationContext ctxt) throws JsonMappingException {
        return this.getNullValue(ctxt);
    }
    
    public ObjectIdReader getObjectIdReader() {
        return null;
    }
    
    public SettableBeanProperty findBackReference(final String refName) {
        throw new IllegalArgumentException("Cannot handle managed/back reference '" + refName + "': type: value deserializer of type " + this.getClass().getName() + " does not support them");
    }
    
    public Boolean supportsUpdate(final DeserializationConfig config) {
        return null;
    }
    
    @Deprecated
    public T getNullValue() {
        return null;
    }
    
    @Deprecated
    public Object getEmptyValue() {
        return this.getNullValue();
    }
    
    public abstract static class None extends JsonDeserializer<Object>
    {
        private None() {
        }
    }
}
