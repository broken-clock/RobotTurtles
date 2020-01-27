// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.Serializable;
import com.fasterxml.jackson.databind.deser.NullValueProvider;

public class NullsAsEmptyProvider implements NullValueProvider, Serializable
{
    private static final long serialVersionUID = 1L;
    protected final JsonDeserializer<?> _deserializer;
    
    public NullsAsEmptyProvider(final JsonDeserializer<?> deser) {
        this._deserializer = deser;
    }
    
    @Override
    public AccessPattern getNullAccessPattern() {
        return AccessPattern.DYNAMIC;
    }
    
    @Override
    public Object getNullValue(final DeserializationContext ctxt) throws JsonMappingException {
        return this._deserializer.getEmptyValue(ctxt);
    }
}
