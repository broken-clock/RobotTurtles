// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import java.math.BigDecimal;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import java.math.BigInteger;

public class BigIntegerNode extends NumericNode
{
    private static final BigInteger MIN_INTEGER;
    private static final BigInteger MAX_INTEGER;
    private static final BigInteger MIN_LONG;
    private static final BigInteger MAX_LONG;
    protected final BigInteger _value;
    
    public BigIntegerNode(final BigInteger v) {
        this._value = v;
    }
    
    public static BigIntegerNode valueOf(final BigInteger v) {
        return new BigIntegerNode(v);
    }
    
    @Override
    public JsonToken asToken() {
        return JsonToken.VALUE_NUMBER_INT;
    }
    
    @Override
    public JsonParser.NumberType numberType() {
        return JsonParser.NumberType.BIG_INTEGER;
    }
    
    @Override
    public boolean isIntegralNumber() {
        return true;
    }
    
    @Override
    public boolean isBigInteger() {
        return true;
    }
    
    @Override
    public boolean canConvertToInt() {
        return this._value.compareTo(BigIntegerNode.MIN_INTEGER) >= 0 && this._value.compareTo(BigIntegerNode.MAX_INTEGER) <= 0;
    }
    
    @Override
    public boolean canConvertToLong() {
        return this._value.compareTo(BigIntegerNode.MIN_LONG) >= 0 && this._value.compareTo(BigIntegerNode.MAX_LONG) <= 0;
    }
    
    @Override
    public Number numberValue() {
        return this._value;
    }
    
    @Override
    public short shortValue() {
        return this._value.shortValue();
    }
    
    @Override
    public int intValue() {
        return this._value.intValue();
    }
    
    @Override
    public long longValue() {
        return this._value.longValue();
    }
    
    @Override
    public BigInteger bigIntegerValue() {
        return this._value;
    }
    
    @Override
    public float floatValue() {
        return this._value.floatValue();
    }
    
    @Override
    public double doubleValue() {
        return this._value.doubleValue();
    }
    
    @Override
    public BigDecimal decimalValue() {
        return new BigDecimal(this._value);
    }
    
    @Override
    public String asText() {
        return this._value.toString();
    }
    
    @Override
    public boolean asBoolean(final boolean defaultValue) {
        return !BigInteger.ZERO.equals(this._value);
    }
    
    @Override
    public final void serialize(final JsonGenerator jg, final SerializerProvider provider) throws IOException, JsonProcessingException {
        jg.writeNumber(this._value);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o != null && o instanceof BigIntegerNode && ((BigIntegerNode)o)._value.equals(this._value));
    }
    
    @Override
    public int hashCode() {
        return this._value.hashCode();
    }
    
    static {
        MIN_INTEGER = BigInteger.valueOf(-2147483648L);
        MAX_INTEGER = BigInteger.valueOf(2147483647L);
        MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);
        MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
    }
}
