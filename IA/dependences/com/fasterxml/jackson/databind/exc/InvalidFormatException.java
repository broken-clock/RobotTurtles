// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.exc;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;

public class InvalidFormatException extends MismatchedInputException
{
    private static final long serialVersionUID = 1L;
    protected final Object _value;
    
    @Deprecated
    public InvalidFormatException(final String msg, final Object value, final Class<?> targetType) {
        super(null, msg);
        this._value = value;
        this._targetType = targetType;
    }
    
    @Deprecated
    public InvalidFormatException(final String msg, final JsonLocation loc, final Object value, final Class<?> targetType) {
        super(null, msg, loc);
        this._value = value;
        this._targetType = targetType;
    }
    
    public InvalidFormatException(final JsonParser p, final String msg, final Object value, final Class<?> targetType) {
        super(p, msg, targetType);
        this._value = value;
    }
    
    public static InvalidFormatException from(final JsonParser p, final String msg, final Object value, final Class<?> targetType) {
        return new InvalidFormatException(p, msg, value, targetType);
    }
    
    public Object getValue() {
        return this._value;
    }
}
