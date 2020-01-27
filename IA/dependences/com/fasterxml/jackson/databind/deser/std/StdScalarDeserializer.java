// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;

public abstract class StdScalarDeserializer<T> extends StdDeserializer<T>
{
    private static final long serialVersionUID = 1L;
    
    protected StdScalarDeserializer(final Class<?> vc) {
        super(vc);
    }
    
    protected StdScalarDeserializer(final JavaType valueType) {
        super(valueType);
    }
    
    protected StdScalarDeserializer(final StdScalarDeserializer<?> src) {
        super(src);
    }
    
    @Override
    public Object deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromScalar(p, ctxt);
    }
    
    @Override
    public T deserialize(final JsonParser p, final DeserializationContext ctxt, final T intoValue) throws IOException {
        ctxt.reportBadMerge(this);
        return this.deserialize(p, ctxt);
    }
    
    @Override
    public Boolean supportsUpdate(final DeserializationConfig config) {
        return Boolean.FALSE;
    }
    
    @Override
    public AccessPattern getNullAccessPattern() {
        return AccessPattern.ALWAYS_NULL;
    }
    
    @Override
    public AccessPattern getEmptyAccessPattern() {
        return AccessPattern.CONSTANT;
    }
}
