// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.node;

import java.io.IOException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;

public class BooleanNode extends ValueNode
{
    public static final BooleanNode TRUE;
    public static final BooleanNode FALSE;
    private final boolean _value;
    
    protected BooleanNode(final boolean v) {
        this._value = v;
    }
    
    public static BooleanNode getTrue() {
        return BooleanNode.TRUE;
    }
    
    public static BooleanNode getFalse() {
        return BooleanNode.FALSE;
    }
    
    public static BooleanNode valueOf(final boolean b) {
        return b ? BooleanNode.TRUE : BooleanNode.FALSE;
    }
    
    @Override
    public JsonNodeType getNodeType() {
        return JsonNodeType.BOOLEAN;
    }
    
    @Override
    public JsonToken asToken() {
        return this._value ? JsonToken.VALUE_TRUE : JsonToken.VALUE_FALSE;
    }
    
    @Override
    public boolean booleanValue() {
        return this._value;
    }
    
    @Override
    public String asText() {
        return this._value ? "true" : "false";
    }
    
    @Override
    public boolean asBoolean() {
        return this._value;
    }
    
    @Override
    public boolean asBoolean(final boolean defaultValue) {
        return this._value;
    }
    
    @Override
    public int asInt(final int defaultValue) {
        return this._value ? 1 : 0;
    }
    
    @Override
    public long asLong(final long defaultValue) {
        return this._value ? 1 : 0;
    }
    
    @Override
    public double asDouble(final double defaultValue) {
        return this._value ? 1.0 : 0.0;
    }
    
    @Override
    public final void serialize(final JsonGenerator g, final SerializerProvider provider) throws IOException {
        g.writeBoolean(this._value);
    }
    
    @Override
    public int hashCode() {
        return this._value ? 3 : 1;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o != null && o instanceof BooleanNode && this._value == ((BooleanNode)o)._value);
    }
    
    static {
        TRUE = new BooleanNode(true);
        FALSE = new BooleanNode(false);
    }
}
