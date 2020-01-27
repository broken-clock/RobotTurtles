// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.io.NumberOutput;
import java.math.BigInteger;
import java.math.BigDecimal;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class IntNode extends NumericNode
{
    static final int MIN_CANONICAL = -1;
    static final int MAX_CANONICAL = 10;
    private static final IntNode[] CANONICALS;
    protected final int _value;
    
    public IntNode(final int v) {
        this._value = v;
    }
    
    public static IntNode valueOf(final int i) {
        if (i > 10 || i < -1) {
            return new IntNode(i);
        }
        return IntNode.CANONICALS[i + 1];
    }
    
    @Override
    public JsonToken asToken() {
        return JsonToken.VALUE_NUMBER_INT;
    }
    
    @Override
    public JsonParser.NumberType numberType() {
        return JsonParser.NumberType.INT;
    }
    
    @Override
    public boolean isIntegralNumber() {
        return true;
    }
    
    @Override
    public boolean isInt() {
        return true;
    }
    
    @Override
    public boolean canConvertToInt() {
        return true;
    }
    
    @Override
    public boolean canConvertToLong() {
        return true;
    }
    
    @Override
    public Number numberValue() {
        return this._value;
    }
    
    @Override
    public short shortValue() {
        return (short)this._value;
    }
    
    @Override
    public int intValue() {
        return this._value;
    }
    
    @Override
    public long longValue() {
        return this._value;
    }
    
    @Override
    public float floatValue() {
        return (float)this._value;
    }
    
    @Override
    public double doubleValue() {
        return this._value;
    }
    
    @Override
    public BigDecimal decimalValue() {
        return BigDecimal.valueOf(this._value);
    }
    
    @Override
    public BigInteger bigIntegerValue() {
        return BigInteger.valueOf(this._value);
    }
    
    @Override
    public String asText() {
        return NumberOutput.toString(this._value);
    }
    
    @Override
    public boolean asBoolean(final boolean defaultValue) {
        return this._value != 0;
    }
    
    @Override
    public final void serialize(final JsonGenerator jg, final SerializerProvider provider) throws IOException, JsonProcessingException {
        jg.writeNumber(this._value);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o != null && o instanceof IntNode && ((IntNode)o)._value == this._value);
    }
    
    @Override
    public int hashCode() {
        return this._value;
    }
    
    static {
        final int count = 12;
        CANONICALS = new IntNode[count];
        for (int i = 0; i < count; ++i) {
            IntNode.CANONICALS[i] = new IntNode(-1 + i);
        }
    }
}
