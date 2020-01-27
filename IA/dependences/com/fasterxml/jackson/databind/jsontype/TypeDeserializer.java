// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.jsontype;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JavaType;
import java.io.IOException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.BeanProperty;

public abstract class TypeDeserializer
{
    public abstract TypeDeserializer forProperty(final BeanProperty p0);
    
    public abstract JsonTypeInfo.As getTypeInclusion();
    
    public abstract String getPropertyName();
    
    public abstract TypeIdResolver getTypeIdResolver();
    
    public abstract Class<?> getDefaultImpl();
    
    public abstract Object deserializeTypedFromObject(final JsonParser p0, final DeserializationContext p1) throws IOException;
    
    public abstract Object deserializeTypedFromArray(final JsonParser p0, final DeserializationContext p1) throws IOException;
    
    public abstract Object deserializeTypedFromScalar(final JsonParser p0, final DeserializationContext p1) throws IOException;
    
    public abstract Object deserializeTypedFromAny(final JsonParser p0, final DeserializationContext p1) throws IOException;
    
    public static Object deserializeIfNatural(final JsonParser p, final DeserializationContext ctxt, final JavaType baseType) throws IOException {
        return deserializeIfNatural(p, ctxt, baseType.getRawClass());
    }
    
    public static Object deserializeIfNatural(final JsonParser p, final DeserializationContext ctxt, final Class<?> base) throws IOException {
        final JsonToken t = p.getCurrentToken();
        if (t == null) {
            return null;
        }
        switch (t) {
            case VALUE_STRING: {
                if (base.isAssignableFrom(String.class)) {
                    return p.getText();
                }
                break;
            }
            case VALUE_NUMBER_INT: {
                if (base.isAssignableFrom(Integer.class)) {
                    return p.getIntValue();
                }
                break;
            }
            case VALUE_NUMBER_FLOAT: {
                if (base.isAssignableFrom(Double.class)) {
                    return p.getDoubleValue();
                }
                break;
            }
            case VALUE_TRUE: {
                if (base.isAssignableFrom(Boolean.class)) {
                    return Boolean.TRUE;
                }
                break;
            }
            case VALUE_FALSE: {
                if (base.isAssignableFrom(Boolean.class)) {
                    return Boolean.FALSE;
                }
                break;
            }
        }
        return null;
    }
}
