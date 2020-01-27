// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JavaType;
import java.io.IOException;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

public abstract class DeserializationProblemHandler
{
    public static final Object NOT_HANDLED;
    
    public boolean handleUnknownProperty(final DeserializationContext ctxt, final JsonParser p, final JsonDeserializer<?> deserializer, final Object beanOrClass, final String propertyName) throws IOException {
        return false;
    }
    
    public Object handleWeirdKey(final DeserializationContext ctxt, final Class<?> rawKeyType, final String keyValue, final String failureMsg) throws IOException {
        return DeserializationProblemHandler.NOT_HANDLED;
    }
    
    public Object handleWeirdStringValue(final DeserializationContext ctxt, final Class<?> targetType, final String valueToConvert, final String failureMsg) throws IOException {
        return DeserializationProblemHandler.NOT_HANDLED;
    }
    
    public Object handleWeirdNumberValue(final DeserializationContext ctxt, final Class<?> targetType, final Number valueToConvert, final String failureMsg) throws IOException {
        return DeserializationProblemHandler.NOT_HANDLED;
    }
    
    public Object handleWeirdNativeValue(final DeserializationContext ctxt, final JavaType targetType, final Object valueToConvert, final JsonParser p) throws IOException {
        return DeserializationProblemHandler.NOT_HANDLED;
    }
    
    public Object handleUnexpectedToken(final DeserializationContext ctxt, final Class<?> targetType, final JsonToken t, final JsonParser p, final String failureMsg) throws IOException {
        return DeserializationProblemHandler.NOT_HANDLED;
    }
    
    public Object handleInstantiationProblem(final DeserializationContext ctxt, final Class<?> instClass, final Object argument, final Throwable t) throws IOException {
        return DeserializationProblemHandler.NOT_HANDLED;
    }
    
    public Object handleMissingInstantiator(final DeserializationContext ctxt, final Class<?> instClass, final ValueInstantiator valueInsta, final JsonParser p, final String msg) throws IOException {
        return this.handleMissingInstantiator(ctxt, instClass, p, msg);
    }
    
    public JavaType handleUnknownTypeId(final DeserializationContext ctxt, final JavaType baseType, final String subTypeId, final TypeIdResolver idResolver, final String failureMsg) throws IOException {
        return null;
    }
    
    public JavaType handleMissingTypeId(final DeserializationContext ctxt, final JavaType baseType, final TypeIdResolver idResolver, final String failureMsg) throws IOException {
        return null;
    }
    
    @Deprecated
    public Object handleMissingInstantiator(final DeserializationContext ctxt, final Class<?> instClass, final JsonParser p, final String msg) throws IOException {
        return DeserializationProblemHandler.NOT_HANDLED;
    }
    
    static {
        NOT_HANDLED = new Object();
    }
}
