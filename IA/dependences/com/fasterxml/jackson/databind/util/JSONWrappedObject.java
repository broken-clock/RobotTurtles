// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializable;

public class JSONWrappedObject implements JsonSerializable
{
    protected final String _prefix;
    protected final String _suffix;
    protected final Object _value;
    protected final JavaType _serializationType;
    
    public JSONWrappedObject(final String prefix, final String suffix, final Object value) {
        this(prefix, suffix, value, null);
    }
    
    public JSONWrappedObject(final String prefix, final String suffix, final Object value, final JavaType asType) {
        this._prefix = prefix;
        this._suffix = suffix;
        this._value = value;
        this._serializationType = asType;
    }
    
    @Override
    public void serializeWithType(final JsonGenerator jgen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException, JsonProcessingException {
        this.serialize(jgen, provider);
    }
    
    @Override
    public void serialize(final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonProcessingException {
        if (this._prefix != null) {
            jgen.writeRaw(this._prefix);
        }
        if (this._value == null) {
            provider.defaultSerializeNull(jgen);
        }
        else if (this._serializationType != null) {
            provider.findTypedValueSerializer(this._serializationType, true, null).serialize(this._value, jgen, provider);
        }
        else {
            final Class<?> cls = this._value.getClass();
            provider.findTypedValueSerializer(cls, true, null).serialize(this._value, jgen, provider);
        }
        if (this._suffix != null) {
            jgen.writeRaw(this._suffix);
        }
    }
    
    public String getPrefix() {
        return this._prefix;
    }
    
    public String getSuffix() {
        return this._suffix;
    }
    
    public Object getValue() {
        return this._value;
    }
    
    public JavaType getSerializationType() {
        return this._serializationType;
    }
}
