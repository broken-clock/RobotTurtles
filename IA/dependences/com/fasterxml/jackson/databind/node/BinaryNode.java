// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.node;

import java.util.Arrays;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.JsonToken;

public class BinaryNode extends ValueNode
{
    static final BinaryNode EMPTY_BINARY_NODE;
    protected final byte[] _data;
    
    public BinaryNode(final byte[] data) {
        this._data = data;
    }
    
    public BinaryNode(final byte[] data, final int offset, final int length) {
        if (offset == 0 && length == data.length) {
            this._data = data;
        }
        else {
            System.arraycopy(data, offset, this._data = new byte[length], 0, length);
        }
    }
    
    public static BinaryNode valueOf(final byte[] data) {
        if (data == null) {
            return null;
        }
        if (data.length == 0) {
            return BinaryNode.EMPTY_BINARY_NODE;
        }
        return new BinaryNode(data);
    }
    
    public static BinaryNode valueOf(final byte[] data, final int offset, final int length) {
        if (data == null) {
            return null;
        }
        if (length == 0) {
            return BinaryNode.EMPTY_BINARY_NODE;
        }
        return new BinaryNode(data, offset, length);
    }
    
    @Override
    public JsonNodeType getNodeType() {
        return JsonNodeType.BINARY;
    }
    
    @Override
    public JsonToken asToken() {
        return JsonToken.VALUE_EMBEDDED_OBJECT;
    }
    
    @Override
    public byte[] binaryValue() {
        return this._data;
    }
    
    @Override
    public String asText() {
        return Base64Variants.getDefaultVariant().encode(this._data, false);
    }
    
    @Override
    public final void serialize(final JsonGenerator jg, final SerializerProvider provider) throws IOException, JsonProcessingException {
        jg.writeBinary(provider.getConfig().getBase64Variant(), this._data, 0, this._data.length);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o != null && o instanceof BinaryNode && Arrays.equals(((BinaryNode)o)._data, this._data));
    }
    
    @Override
    public int hashCode() {
        return (this._data == null) ? -1 : this._data.length;
    }
    
    @Override
    public String toString() {
        return Base64Variants.getDefaultVariant().encode(this._data, true);
    }
    
    static {
        EMPTY_BINARY_NODE = new BinaryNode(new byte[0]);
    }
}
