// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.databind.JsonSerializable;

public class RawValue implements JsonSerializable
{
    protected Object _value;
    
    public RawValue(final String v) {
        this._value = v;
    }
    
    public RawValue(final SerializableString v) {
        this._value = v;
    }
    
    public RawValue(final JsonSerializable v) {
        this._value = v;
    }
    
    protected RawValue(final Object value, final boolean bogus) {
        this._value = value;
    }
    
    public Object rawValue() {
        return this._value;
    }
    
    @Override
    public void serialize(final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        if (this._value instanceof JsonSerializable) {
            ((JsonSerializable)this._value).serialize(gen, serializers);
        }
        else {
            this._serialize(gen);
        }
    }
    
    @Override
    public void serializeWithType(final JsonGenerator gen, final SerializerProvider serializers, final TypeSerializer typeSer) throws IOException {
        if (this._value instanceof JsonSerializable) {
            ((JsonSerializable)this._value).serializeWithType(gen, serializers, typeSer);
        }
        else if (this._value instanceof SerializableString) {
            this.serialize(gen, serializers);
        }
    }
    
    public void serialize(final JsonGenerator gen) throws IOException {
        if (this._value instanceof JsonSerializable) {
            gen.writeObject(this._value);
        }
        else {
            this._serialize(gen);
        }
    }
    
    protected void _serialize(final JsonGenerator gen) throws IOException {
        if (this._value instanceof SerializableString) {
            gen.writeRawValue((SerializableString)this._value);
        }
        else {
            gen.writeRawValue(String.valueOf(this._value));
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RawValue)) {
            return false;
        }
        final RawValue other = (RawValue)o;
        return this._value == other._value || (this._value != null && this._value.equals(other._value));
    }
    
    @Override
    public int hashCode() {
        return (this._value == null) ? 0 : this._value.hashCode();
    }
    
    @Override
    public String toString() {
        return String.format("[RawValue of type %s]", ClassUtil.classNameOf(this._value));
    }
}
