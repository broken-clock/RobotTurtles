// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.DeserializationConfig;

public abstract class ValueInstantiator
{
    public Class<?> getValueClass() {
        return Object.class;
    }
    
    public String getValueTypeDesc() {
        final Class<?> cls = this.getValueClass();
        if (cls == null) {
            return "UNKNOWN";
        }
        return cls.getName();
    }
    
    public boolean canInstantiate() {
        return this.canCreateUsingDefault() || this.canCreateUsingDelegate() || this.canCreateUsingArrayDelegate() || this.canCreateFromObjectWith() || this.canCreateFromString() || this.canCreateFromInt() || this.canCreateFromLong() || this.canCreateFromDouble() || this.canCreateFromBoolean();
    }
    
    public boolean canCreateFromString() {
        return false;
    }
    
    public boolean canCreateFromInt() {
        return false;
    }
    
    public boolean canCreateFromLong() {
        return false;
    }
    
    public boolean canCreateFromDouble() {
        return false;
    }
    
    public boolean canCreateFromBoolean() {
        return false;
    }
    
    public boolean canCreateUsingDefault() {
        return this.getDefaultCreator() != null;
    }
    
    public boolean canCreateUsingDelegate() {
        return false;
    }
    
    public boolean canCreateUsingArrayDelegate() {
        return false;
    }
    
    public boolean canCreateFromObjectWith() {
        return false;
    }
    
    public SettableBeanProperty[] getFromObjectArguments(final DeserializationConfig config) {
        return null;
    }
    
    public JavaType getDelegateType(final DeserializationConfig config) {
        return null;
    }
    
    public JavaType getArrayDelegateType(final DeserializationConfig config) {
        return null;
    }
    
    public Object createUsingDefault(final DeserializationContext ctxt) throws IOException {
        return ctxt.handleMissingInstantiator(this.getValueClass(), this, null, "no default no-arguments constructor found", new Object[0]);
    }
    
    public Object createFromObjectWith(final DeserializationContext ctxt, final Object[] args) throws IOException {
        return ctxt.handleMissingInstantiator(this.getValueClass(), this, null, "no creator with arguments specified", new Object[0]);
    }
    
    public Object createFromObjectWith(final DeserializationContext ctxt, final SettableBeanProperty[] props, final PropertyValueBuffer buffer) throws IOException {
        return this.createFromObjectWith(ctxt, buffer.getParameters(props));
    }
    
    public Object createUsingDelegate(final DeserializationContext ctxt, final Object delegate) throws IOException {
        return ctxt.handleMissingInstantiator(this.getValueClass(), this, null, "no delegate creator specified", new Object[0]);
    }
    
    public Object createUsingArrayDelegate(final DeserializationContext ctxt, final Object delegate) throws IOException {
        return ctxt.handleMissingInstantiator(this.getValueClass(), this, null, "no array delegate creator specified", new Object[0]);
    }
    
    public Object createFromString(final DeserializationContext ctxt, final String value) throws IOException {
        return this._createFromStringFallbacks(ctxt, value);
    }
    
    public Object createFromInt(final DeserializationContext ctxt, final int value) throws IOException {
        return ctxt.handleMissingInstantiator(this.getValueClass(), this, null, "no int/Int-argument constructor/factory method to deserialize from Number value (%s)", value);
    }
    
    public Object createFromLong(final DeserializationContext ctxt, final long value) throws IOException {
        return ctxt.handleMissingInstantiator(this.getValueClass(), this, null, "no long/Long-argument constructor/factory method to deserialize from Number value (%s)", value);
    }
    
    public Object createFromDouble(final DeserializationContext ctxt, final double value) throws IOException {
        return ctxt.handleMissingInstantiator(this.getValueClass(), this, null, "no double/Double-argument constructor/factory method to deserialize from Number value (%s)", value);
    }
    
    public Object createFromBoolean(final DeserializationContext ctxt, final boolean value) throws IOException {
        return ctxt.handleMissingInstantiator(this.getValueClass(), this, null, "no boolean/Boolean-argument constructor/factory method to deserialize from boolean value (%s)", value);
    }
    
    public AnnotatedWithParams getDefaultCreator() {
        return null;
    }
    
    public AnnotatedWithParams getDelegateCreator() {
        return null;
    }
    
    public AnnotatedWithParams getArrayDelegateCreator() {
        return null;
    }
    
    public AnnotatedWithParams getWithArgsCreator() {
        return null;
    }
    
    public AnnotatedParameter getIncompleteParameter() {
        return null;
    }
    
    protected Object _createFromStringFallbacks(final DeserializationContext ctxt, final String value) throws IOException {
        if (this.canCreateFromBoolean()) {
            final String str = value.trim();
            if ("true".equals(str)) {
                return this.createFromBoolean(ctxt, true);
            }
            if ("false".equals(str)) {
                return this.createFromBoolean(ctxt, false);
            }
        }
        if (value.length() == 0 && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
            return null;
        }
        return ctxt.handleMissingInstantiator(this.getValueClass(), this, ctxt.getParser(), "no String-argument constructor/factory method to deserialize from String value ('%s')", value);
    }
    
    public static class Base extends ValueInstantiator
    {
        protected final Class<?> _valueType;
        
        public Base(final Class<?> type) {
            this._valueType = type;
        }
        
        public Base(final JavaType type) {
            this._valueType = type.getRawClass();
        }
        
        @Override
        public String getValueTypeDesc() {
            return this._valueType.getName();
        }
        
        @Override
        public Class<?> getValueClass() {
            return this._valueType;
        }
    }
    
    public interface Gettable
    {
        ValueInstantiator getValueInstantiator();
    }
}
