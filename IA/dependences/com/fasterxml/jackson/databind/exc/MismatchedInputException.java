// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.exc;

import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Closeable;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;

public class MismatchedInputException extends JsonMappingException
{
    protected Class<?> _targetType;
    
    protected MismatchedInputException(final JsonParser p, final String msg) {
        this(p, msg, (JavaType)null);
    }
    
    protected MismatchedInputException(final JsonParser p, final String msg, final JsonLocation loc) {
        super(p, msg, loc);
    }
    
    protected MismatchedInputException(final JsonParser p, final String msg, final Class<?> targetType) {
        super(p, msg);
        this._targetType = targetType;
    }
    
    protected MismatchedInputException(final JsonParser p, final String msg, final JavaType targetType) {
        super(p, msg);
        this._targetType = ClassUtil.rawClass(targetType);
    }
    
    @Deprecated
    public static MismatchedInputException from(final JsonParser p, final String msg) {
        return from(p, (Class<?>)null, msg);
    }
    
    public static MismatchedInputException from(final JsonParser p, final JavaType targetType, final String msg) {
        return new MismatchedInputException(p, msg, targetType);
    }
    
    public static MismatchedInputException from(final JsonParser p, final Class<?> targetType, final String msg) {
        return new MismatchedInputException(p, msg, targetType);
    }
    
    public MismatchedInputException setTargetType(final JavaType t) {
        this._targetType = t.getRawClass();
        return this;
    }
    
    public Class<?> getTargetType() {
        return this._targetType;
    }
}
