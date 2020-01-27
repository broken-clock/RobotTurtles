// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.util.AccessPattern;
import java.io.Serializable;
import com.fasterxml.jackson.databind.deser.NullValueProvider;

public class NullsConstantProvider implements NullValueProvider, Serializable
{
    private static final long serialVersionUID = 1L;
    private static final NullsConstantProvider SKIPPER;
    private static final NullsConstantProvider NULLER;
    protected final Object _nullValue;
    protected final AccessPattern _access;
    
    protected NullsConstantProvider(final Object nvl) {
        this._nullValue = nvl;
        this._access = ((this._nullValue == null) ? AccessPattern.ALWAYS_NULL : AccessPattern.CONSTANT);
    }
    
    public static NullsConstantProvider skipper() {
        return NullsConstantProvider.SKIPPER;
    }
    
    public static NullsConstantProvider nuller() {
        return NullsConstantProvider.NULLER;
    }
    
    public static NullsConstantProvider forValue(final Object nvl) {
        if (nvl == null) {
            return NullsConstantProvider.NULLER;
        }
        return new NullsConstantProvider(nvl);
    }
    
    public static boolean isSkipper(final NullValueProvider p) {
        return p == NullsConstantProvider.SKIPPER;
    }
    
    public static boolean isNuller(final NullValueProvider p) {
        return p == NullsConstantProvider.NULLER;
    }
    
    @Override
    public AccessPattern getNullAccessPattern() {
        return this._access;
    }
    
    @Override
    public Object getNullValue(final DeserializationContext ctxt) {
        return this._nullValue;
    }
    
    static {
        SKIPPER = new NullsConstantProvider(null);
        NULLER = new NullsConstantProvider(null);
    }
}
