// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import java.util.Collection;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

public abstract class DelegatingDeserializer extends StdDeserializer<Object> implements ContextualDeserializer, ResolvableDeserializer
{
    private static final long serialVersionUID = 1L;
    protected final JsonDeserializer<?> _delegatee;
    
    public DelegatingDeserializer(final JsonDeserializer<?> d) {
        super(d.handledType());
        this._delegatee = d;
    }
    
    protected abstract JsonDeserializer<?> newDelegatingInstance(final JsonDeserializer<?> p0);
    
    @Override
    public void resolve(final DeserializationContext ctxt) throws JsonMappingException {
        if (this._delegatee instanceof ResolvableDeserializer) {
            ((ResolvableDeserializer)this._delegatee).resolve(ctxt);
        }
    }
    
    @Override
    public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
        final JavaType vt = ctxt.constructType(this._delegatee.handledType());
        final JsonDeserializer<?> del = ctxt.handleSecondaryContextualization(this._delegatee, property, vt);
        if (del == this._delegatee) {
            return this;
        }
        return this.newDelegatingInstance(del);
    }
    
    @Override
    public JsonDeserializer<?> replaceDelegatee(final JsonDeserializer<?> delegatee) {
        if (delegatee == this._delegatee) {
            return this;
        }
        return this.newDelegatingInstance(delegatee);
    }
    
    @Override
    public Object deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        return this._delegatee.deserialize(p, ctxt);
    }
    
    @Override
    public Object deserialize(final JsonParser p, final DeserializationContext ctxt, final Object intoValue) throws IOException {
        return this._delegatee.deserialize(p, ctxt, intoValue);
    }
    
    @Override
    public Object deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        return this._delegatee.deserializeWithType(p, ctxt, typeDeserializer);
    }
    
    @Override
    public boolean isCachable() {
        return this._delegatee.isCachable();
    }
    
    @Override
    public Boolean supportsUpdate(final DeserializationConfig config) {
        return this._delegatee.supportsUpdate(config);
    }
    
    @Override
    public JsonDeserializer<?> getDelegatee() {
        return this._delegatee;
    }
    
    @Override
    public SettableBeanProperty findBackReference(final String logicalName) {
        return this._delegatee.findBackReference(logicalName);
    }
    
    @Override
    public AccessPattern getNullAccessPattern() {
        return this._delegatee.getNullAccessPattern();
    }
    
    @Override
    public Object getNullValue(final DeserializationContext ctxt) throws JsonMappingException {
        return this._delegatee.getNullValue(ctxt);
    }
    
    @Override
    public Object getEmptyValue(final DeserializationContext ctxt) throws JsonMappingException {
        return this._delegatee.getEmptyValue(ctxt);
    }
    
    @Override
    public Collection<Object> getKnownPropertyNames() {
        return this._delegatee.getKnownPropertyNames();
    }
    
    @Override
    public ObjectIdReader getObjectIdReader() {
        return this._delegatee.getObjectIdReader();
    }
}
