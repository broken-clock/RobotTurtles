// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.util;

import java.io.IOException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;

public class ConstantValueInstantiator extends ValueInstantiator
{
    protected final Object _value;
    
    public ConstantValueInstantiator(final Object value) {
        this._value = value;
    }
    
    @Override
    public Class<?> getValueClass() {
        return this._value.getClass();
    }
    
    @Override
    public boolean canInstantiate() {
        return true;
    }
    
    @Override
    public boolean canCreateUsingDefault() {
        return true;
    }
    
    @Override
    public Object createUsingDefault(final DeserializationContext ctxt) throws IOException {
        return this._value;
    }
}
