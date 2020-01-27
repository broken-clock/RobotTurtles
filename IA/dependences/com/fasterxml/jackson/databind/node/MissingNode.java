// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;

public final class MissingNode extends ValueNode
{
    private static final MissingNode instance;
    
    protected MissingNode() {
    }
    
    @Override
    public boolean isMissingNode() {
        return true;
    }
    
    @Override
    public <T extends JsonNode> T deepCopy() {
        return (T)this;
    }
    
    public static MissingNode getInstance() {
        return MissingNode.instance;
    }
    
    @Override
    public JsonNodeType getNodeType() {
        return JsonNodeType.MISSING;
    }
    
    @Override
    public JsonToken asToken() {
        return JsonToken.NOT_AVAILABLE;
    }
    
    @Override
    public String asText() {
        return "";
    }
    
    @Override
    public String asText(final String defaultValue) {
        return defaultValue;
    }
    
    @Override
    public final void serialize(final JsonGenerator jg, final SerializerProvider provider) throws IOException, JsonProcessingException {
        jg.writeNull();
    }
    
    @Override
    public void serializeWithType(final JsonGenerator g, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException, JsonProcessingException {
        g.writeNull();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this;
    }
    
    @Override
    public String toString() {
        return "";
    }
    
    @Override
    public int hashCode() {
        return JsonNodeType.MISSING.ordinal();
    }
    
    static {
        instance = new MissingNode();
    }
}
