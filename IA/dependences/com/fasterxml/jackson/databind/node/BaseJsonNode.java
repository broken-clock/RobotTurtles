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
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.JsonNode;

public abstract class BaseJsonNode extends JsonNode implements JsonSerializable
{
    protected BaseJsonNode() {
    }
    
    @Override
    public final JsonNode findPath(final String fieldName) {
        final JsonNode value = this.findValue(fieldName);
        if (value == null) {
            return MissingNode.getInstance();
        }
        return value;
    }
    
    @Override
    public abstract int hashCode();
    
    @Override
    public JsonParser traverse() {
        return new TreeTraversingParser(this);
    }
    
    @Override
    public JsonParser traverse(final ObjectCodec codec) {
        return new TreeTraversingParser(this, codec);
    }
    
    @Override
    public abstract JsonToken asToken();
    
    @Override
    public JsonParser.NumberType numberType() {
        return null;
    }
    
    @Override
    public abstract void serialize(final JsonGenerator p0, final SerializerProvider p1) throws IOException, JsonProcessingException;
    
    @Override
    public abstract void serializeWithType(final JsonGenerator p0, final SerializerProvider p1, final TypeSerializer p2) throws IOException, JsonProcessingException;
}
