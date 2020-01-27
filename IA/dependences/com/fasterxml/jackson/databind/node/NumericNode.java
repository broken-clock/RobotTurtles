// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.node;

import java.math.BigInteger;
import java.math.BigDecimal;
import com.fasterxml.jackson.core.JsonParser;

public abstract class NumericNode extends ValueNode
{
    protected NumericNode() {
    }
    
    @Override
    public final JsonNodeType getNodeType() {
        return JsonNodeType.NUMBER;
    }
    
    @Override
    public abstract JsonParser.NumberType numberType();
    
    @Override
    public abstract Number numberValue();
    
    @Override
    public abstract int intValue();
    
    @Override
    public abstract long longValue();
    
    @Override
    public abstract double doubleValue();
    
    @Override
    public abstract BigDecimal decimalValue();
    
    @Override
    public abstract BigInteger bigIntegerValue();
    
    @Override
    public abstract boolean canConvertToInt();
    
    @Override
    public abstract boolean canConvertToLong();
    
    @Override
    public abstract String asText();
    
    @Override
    public final int asInt() {
        return this.intValue();
    }
    
    @Override
    public final int asInt(final int defaultValue) {
        return this.intValue();
    }
    
    @Override
    public final long asLong() {
        return this.longValue();
    }
    
    @Override
    public final long asLong(final long defaultValue) {
        return this.longValue();
    }
    
    @Override
    public final double asDouble() {
        return this.doubleValue();
    }
    
    @Override
    public final double asDouble(final double defaultValue) {
        return this.doubleValue();
    }
    
    public boolean isNaN() {
        return false;
    }
}
