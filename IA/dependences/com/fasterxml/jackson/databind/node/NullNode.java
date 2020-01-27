// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.node;

import java.io.IOException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;

public final class NullNode extends ValueNode
{
    public static final NullNode instance;
    
    protected NullNode() {
    }
    
    public static NullNode getInstance() {
        return NullNode.instance;
    }
    
    @Override
    public JsonNodeType getNodeType() {
        return JsonNodeType.NULL;
    }
    
    @Override
    public JsonToken asToken() {
        return JsonToken.VALUE_NULL;
    }
    
    @Override
    public String asText(final String defaultValue) {
        return defaultValue;
    }
    
    @Override
    public String asText() {
        return "null";
    }
    
    @Override
    public final void serialize(final JsonGenerator g, final SerializerProvider provider) throws IOException {
        provider.defaultSerializeNull(g);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this;
    }
    
    @Override
    public int hashCode() {
        return JsonNodeType.NULL.ordinal();
    }
    
    static {
        instance = new NullNode();
    }
}
