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

public class LongNode extends NumericNode
{
    protected final long _value;
    
    public LongNode(final long v) {
        this._value = v;
    }
    
    public static LongNode valueOf(final long l) {
        return new LongNode(l);
    }
    
    @Override
    public JsonToken asToken() {
        return JsonToken.VALUE_NUMBER_INT;
    }
    
    @Override
    public JsonParser.NumberType numberType() {
        return JsonParser.NumberType.LONG;
    }
    
    @Override
    public boolean isIntegralNumber() {
        return true;
    }
    
    @Override
    public boolean isLong() {
        return true;
    }
    
    @Override
    public boolean canConvertToInt() {
        return this._value >= -2147483648L && this._value <= 2147483647L;
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
        return (int)this._value;
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
        return (double)this._value;
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
        return this._value != 0L;
    }
    
    @Override
    public final void serialize(final JsonGenerator jg, final SerializerProvider provider) throws IOException, JsonProcessingException {
        jg.writeNumber(this._value);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o != null && o instanceof LongNode && ((LongNode)o)._value == this._value);
    }
    
    @Override
    public int hashCode() {
        return (int)this._value ^ (int)(this._value >> 32);
    }
}
