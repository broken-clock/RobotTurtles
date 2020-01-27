// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.DeserializationConfig;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class JsonNodeDeserializer extends BaseNodeDeserializer<JsonNode>
{
    private static final JsonNodeDeserializer instance;
    
    protected JsonNodeDeserializer() {
        super(JsonNode.class, null);
    }
    
    public static JsonDeserializer<? extends JsonNode> getDeserializer(final Class<?> nodeClass) {
        if (nodeClass == ObjectNode.class) {
            return ObjectDeserializer.getInstance();
        }
        if (nodeClass == ArrayNode.class) {
            return ArrayDeserializer.getInstance();
        }
        return JsonNodeDeserializer.instance;
    }
    
    @Override
    public JsonNode getNullValue(final DeserializationContext ctxt) {
        return NullNode.getInstance();
    }
    
    @Override
    public JsonNode deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        switch (p.getCurrentTokenId()) {
            case 1: {
                return this.deserializeObject(p, ctxt, ctxt.getNodeFactory());
            }
            case 3: {
                return this.deserializeArray(p, ctxt, ctxt.getNodeFactory());
            }
            default: {
                return this.deserializeAny(p, ctxt, ctxt.getNodeFactory());
            }
        }
    }
    
    static {
        instance = new JsonNodeDeserializer();
    }
    
    static final class ObjectDeserializer extends BaseNodeDeserializer<ObjectNode>
    {
        private static final long serialVersionUID = 1L;
        protected static final ObjectDeserializer _instance;
        
        protected ObjectDeserializer() {
            super(ObjectNode.class, true);
        }
        
        public static ObjectDeserializer getInstance() {
            return ObjectDeserializer._instance;
        }
        
        @Override
        public ObjectNode deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            if (p.isExpectedStartObjectToken()) {
                return this.deserializeObject(p, ctxt, ctxt.getNodeFactory());
            }
            if (p.hasToken(JsonToken.FIELD_NAME)) {
                return this.deserializeObjectAtName(p, ctxt, ctxt.getNodeFactory());
            }
            if (p.hasToken(JsonToken.END_OBJECT)) {
                return ctxt.getNodeFactory().objectNode();
            }
            return (ObjectNode)ctxt.handleUnexpectedToken(ObjectNode.class, p);
        }
        
        @Override
        public ObjectNode deserialize(final JsonParser p, final DeserializationContext ctxt, final ObjectNode node) throws IOException {
            if (p.isExpectedStartObjectToken() || p.hasToken(JsonToken.FIELD_NAME)) {
                return (ObjectNode)this.updateObject(p, ctxt, node);
            }
            return (ObjectNode)ctxt.handleUnexpectedToken(ObjectNode.class, p);
        }
        
        static {
            _instance = new ObjectDeserializer();
        }
    }
    
    static final class ArrayDeserializer extends BaseNodeDeserializer<ArrayNode>
    {
        private static final long serialVersionUID = 1L;
        protected static final ArrayDeserializer _instance;
        
        protected ArrayDeserializer() {
            super(ArrayNode.class, true);
        }
        
        public static ArrayDeserializer getInstance() {
            return ArrayDeserializer._instance;
        }
        
        @Override
        public ArrayNode deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            if (p.isExpectedStartArrayToken()) {
                return this.deserializeArray(p, ctxt, ctxt.getNodeFactory());
            }
            return (ArrayNode)ctxt.handleUnexpectedToken(ArrayNode.class, p);
        }
        
        @Override
        public ArrayNode deserialize(final JsonParser p, final DeserializationContext ctxt, final ArrayNode node) throws IOException {
            if (p.isExpectedStartArrayToken()) {
                return (ArrayNode)this.updateArray(p, ctxt, node);
            }
            return (ArrayNode)ctxt.handleUnexpectedToken(ArrayNode.class, p);
        }
        
        static {
            _instance = new ArrayDeserializer();
        }
    }
}
