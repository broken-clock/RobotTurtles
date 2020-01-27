// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.databind.util.RawValue;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.io.Serializable;

public class JsonNodeFactory implements Serializable, JsonNodeCreator
{
    private static final long serialVersionUID = 1L;
    private final boolean _cfgBigDecimalExact;
    private static final JsonNodeFactory decimalsNormalized;
    private static final JsonNodeFactory decimalsAsIs;
    public static final JsonNodeFactory instance;
    
    public JsonNodeFactory(final boolean bigDecimalExact) {
        this._cfgBigDecimalExact = bigDecimalExact;
    }
    
    protected JsonNodeFactory() {
        this(false);
    }
    
    public static JsonNodeFactory withExactBigDecimals(final boolean bigDecimalExact) {
        return bigDecimalExact ? JsonNodeFactory.decimalsAsIs : JsonNodeFactory.decimalsNormalized;
    }
    
    @Override
    public BooleanNode booleanNode(final boolean v) {
        return v ? BooleanNode.getTrue() : BooleanNode.getFalse();
    }
    
    @Override
    public NullNode nullNode() {
        return NullNode.getInstance();
    }
    
    @Override
    public NumericNode numberNode(final byte v) {
        return IntNode.valueOf(v);
    }
    
    @Override
    public ValueNode numberNode(final Byte value) {
        return (value == null) ? this.nullNode() : IntNode.valueOf(value);
    }
    
    @Override
    public NumericNode numberNode(final short v) {
        return ShortNode.valueOf(v);
    }
    
    @Override
    public ValueNode numberNode(final Short value) {
        return (value == null) ? this.nullNode() : ShortNode.valueOf(value);
    }
    
    @Override
    public NumericNode numberNode(final int v) {
        return IntNode.valueOf(v);
    }
    
    @Override
    public ValueNode numberNode(final Integer value) {
        return (value == null) ? this.nullNode() : IntNode.valueOf(value);
    }
    
    @Override
    public NumericNode numberNode(final long v) {
        return LongNode.valueOf(v);
    }
    
    @Override
    public ValueNode numberNode(final Long v) {
        if (v == null) {
            return this.nullNode();
        }
        return LongNode.valueOf(v);
    }
    
    @Override
    public ValueNode numberNode(final BigInteger v) {
        if (v == null) {
            return this.nullNode();
        }
        return BigIntegerNode.valueOf(v);
    }
    
    @Override
    public NumericNode numberNode(final float v) {
        return FloatNode.valueOf(v);
    }
    
    @Override
    public ValueNode numberNode(final Float value) {
        return (value == null) ? this.nullNode() : FloatNode.valueOf(value);
    }
    
    @Override
    public NumericNode numberNode(final double v) {
        return DoubleNode.valueOf(v);
    }
    
    @Override
    public ValueNode numberNode(final Double value) {
        return (value == null) ? this.nullNode() : DoubleNode.valueOf(value);
    }
    
    @Override
    public ValueNode numberNode(final BigDecimal v) {
        if (v == null) {
            return this.nullNode();
        }
        if (this._cfgBigDecimalExact) {
            return DecimalNode.valueOf(v);
        }
        return (v.compareTo(BigDecimal.ZERO) == 0) ? DecimalNode.ZERO : DecimalNode.valueOf(v.stripTrailingZeros());
    }
    
    @Override
    public TextNode textNode(final String text) {
        return TextNode.valueOf(text);
    }
    
    @Override
    public BinaryNode binaryNode(final byte[] data) {
        return BinaryNode.valueOf(data);
    }
    
    @Override
    public BinaryNode binaryNode(final byte[] data, final int offset, final int length) {
        return BinaryNode.valueOf(data, offset, length);
    }
    
    @Override
    public ArrayNode arrayNode() {
        return new ArrayNode(this);
    }
    
    @Override
    public ArrayNode arrayNode(final int capacity) {
        return new ArrayNode(this, capacity);
    }
    
    @Override
    public ObjectNode objectNode() {
        return new ObjectNode(this);
    }
    
    @Override
    public ValueNode pojoNode(final Object pojo) {
        return new POJONode(pojo);
    }
    
    @Override
    public ValueNode rawValueNode(final RawValue value) {
        return new POJONode(value);
    }
    
    protected boolean _inIntRange(final long l) {
        final int i = (int)l;
        final long l2 = i;
        return l2 == l;
    }
    
    static {
        decimalsNormalized = new JsonNodeFactory(false);
        decimalsAsIs = new JsonNodeFactory(true);
        instance = JsonNodeFactory.decimalsNormalized;
    }
}
