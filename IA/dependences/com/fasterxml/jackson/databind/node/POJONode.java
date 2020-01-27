// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.databind.util.RawValue;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonToken;

public class POJONode extends ValueNode
{
    protected final Object _value;
    
    public POJONode(final Object v) {
        this._value = v;
    }
    
    @Override
    public JsonNodeType getNodeType() {
        return JsonNodeType.POJO;
    }
    
    @Override
    public JsonToken asToken() {
        return JsonToken.VALUE_EMBEDDED_OBJECT;
    }
    
    @Override
    public byte[] binaryValue() throws IOException {
        if (this._value instanceof byte[]) {
            return (byte[])this._value;
        }
        return super.binaryValue();
    }
    
    @Override
    public String asText() {
        return (this._value == null) ? "null" : this._value.toString();
    }
    
    @Override
    public String asText(final String defaultValue) {
        return (this._value == null) ? defaultValue : this._value.toString();
    }
    
    @Override
    public boolean asBoolean(final boolean defaultValue) {
        if (this._value != null && this._value instanceof Boolean) {
            return (boolean)this._value;
        }
        return defaultValue;
    }
    
    @Override
    public int asInt(final int defaultValue) {
        if (this._value instanceof Number) {
            return ((Number)this._value).intValue();
        }
        return defaultValue;
    }
    
    @Override
    public long asLong(final long defaultValue) {
        if (this._value instanceof Number) {
            return ((Number)this._value).longValue();
        }
        return defaultValue;
    }
    
    @Override
    public double asDouble(final double defaultValue) {
        if (this._value instanceof Number) {
            return ((Number)this._value).doubleValue();
        }
        return defaultValue;
    }
    
    @Override
    public final void serialize(final JsonGenerator gen, final SerializerProvider ctxt) throws IOException {
        if (this._value == null) {
            ctxt.defaultSerializeNull(gen);
        }
        else if (this._value instanceof JsonSerializable) {
            ((JsonSerializable)this._value).serialize(gen, ctxt);
        }
        else {
            ctxt.defaultSerializeValue(this._value, gen);
        }
    }
    
    public Object getPojo() {
        return this._value;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o != null && o instanceof POJONode && this._pojoEquals((POJONode)o));
    }
    
    protected boolean _pojoEquals(final POJONode other) {
        if (this._value == null) {
            return other._value == null;
        }
        return this._value.equals(other._value);
    }
    
    @Override
    public int hashCode() {
        return this._value.hashCode();
    }
    
    @Override
    public String toString() {
        if (this._value instanceof byte[]) {
            return String.format("(binary value of %d bytes)", ((byte[])this._value).length);
        }
        if (this._value instanceof RawValue) {
            return String.format("(raw value '%s')", ((RawValue)this._value).toString());
        }
        return String.valueOf(this._value);
    }
}
