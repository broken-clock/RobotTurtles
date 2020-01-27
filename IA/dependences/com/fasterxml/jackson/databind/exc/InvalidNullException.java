// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.exc;

import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.PropertyName;

public class InvalidNullException extends MismatchedInputException
{
    private static final long serialVersionUID = 1L;
    protected final PropertyName _propertyName;
    
    protected InvalidNullException(final DeserializationContext ctxt, final String msg, final PropertyName pname) {
        super(ctxt.getParser(), msg);
        this._propertyName = pname;
    }
    
    public static InvalidNullException from(final DeserializationContext ctxt, final PropertyName name, final JavaType type) {
        final String msg = String.format("Invalid `null` value encountered for property %s", ClassUtil.quotedOr(name, "<UNKNOWN>"));
        final InvalidNullException exc = new InvalidNullException(ctxt, msg, name);
        if (type != null) {
            exc.setTargetType(type);
        }
        return exc;
    }
    
    public PropertyName getPropertyName() {
        return this._propertyName;
    }
}
