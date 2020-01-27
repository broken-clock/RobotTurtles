// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidNullException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.PropertyName;
import java.io.Serializable;
import com.fasterxml.jackson.databind.deser.NullValueProvider;

public class NullsFailProvider implements NullValueProvider, Serializable
{
    private static final long serialVersionUID = 1L;
    protected final PropertyName _name;
    protected final JavaType _type;
    
    protected NullsFailProvider(final PropertyName name, final JavaType type) {
        this._name = name;
        this._type = type;
    }
    
    public static NullsFailProvider constructForProperty(final BeanProperty prop) {
        return new NullsFailProvider(prop.getFullName(), prop.getType());
    }
    
    public static NullsFailProvider constructForRootValue(final JavaType t) {
        return new NullsFailProvider(null, t);
    }
    
    @Override
    public AccessPattern getNullAccessPattern() {
        return AccessPattern.DYNAMIC;
    }
    
    @Override
    public Object getNullValue(final DeserializationContext ctxt) throws JsonMappingException {
        throw InvalidNullException.from(ctxt, this._name, this._type);
    }
}
