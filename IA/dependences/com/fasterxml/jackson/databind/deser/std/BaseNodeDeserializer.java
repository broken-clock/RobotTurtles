// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.databind.util.RawValue;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.DeserializationConfig;
import java.io.IOException;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;

abstract class BaseNodeDeserializer<T extends JsonNode> extends StdDeserializer<T>
{
    protected final Boolean _supportsUpdates;
    
    public BaseNodeDeserializer(final Class<T> vc, final Boolean supportsUpdates) {
        super(vc);
        this._supportsUpdates = supportsUpdates;
    }
    
    @Override
    public Object deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromAny(p, ctxt);
    }
    
    @Override
    public boolean isCachable() {
        return true;
    }
    
    @Override
    public Boolean supportsUpdate(final DeserializationConfig config) {
        return this._supportsUpdates;
    }
    
    protected void _handleDuplicateField(final JsonParser p, final DeserializationContext ctxt, final JsonNodeFactory nodeFactory, final String fieldName, final ObjectNode objectNode, final JsonNode oldValue, final JsonNode newValue) throws JsonProcessingException {
        if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY)) {
            ctxt.reportInputMismatch(JsonNode.class, "Duplicate field '%s' for ObjectNode: not allowed when FAIL_ON_READING_DUP_TREE_KEY enabled", fieldName);
        }
    }
    
    protected final ObjectNode deserializeObject(final JsonParser p, final DeserializationContext ctxt, final JsonNodeFactory nodeFactory) throws IOException {
        final ObjectNode node = nodeFactory.objectNode();
        for (String key = p.nextFieldName(); key != null; key = p.nextFieldName()) {
            JsonToken t = p.nextToken();
            if (t == null) {
                t = JsonToken.NOT_AVAILABLE;
            }
            JsonNode value = null;
            switch (t.id()) {
                case 1: {
                    value = this.deserializeObject(p, ctxt, nodeFactory);
                    break;
                }
                case 3: {
                    value = this.deserializeArray(p, ctxt, nodeFactory);
                    break;
                }
                case 12: {
                    value = this._fromEmbedded(p, ctxt, nodeFactory);
                    break;
                }
                case 6: {
                    value = nodeFactory.textNode(p.getText());
                    break;
                }
                case 7: {
                    value = this._fromInt(p, ctxt, nodeFactory);
                    break;
                }
                case 9: {
                    value = nodeFactory.booleanNode(true);
                    break;
                }
                case 10: {
                    value = nodeFactory.booleanNode(false);
                    break;
                }
                case 11: {
                    value = nodeFactory.nullNode();
                    break;
                }
                default: {
                    value = this.deserializeAny(p, ctxt, nodeFactory);
                    break;
                }
            }
            final JsonNode old = node.replace(key, value);
            if (old != null) {
                this._handleDuplicateField(p, ctxt, nodeFactory, key, node, old, value);
            }
        }
        return node;
    }
    
    protected final ObjectNode deserializeObjectAtName(final JsonParser p, final DeserializationContext ctxt, final JsonNodeFactory nodeFactory) throws IOException {
        final ObjectNode node = nodeFactory.objectNode();
        for (String key = p.getCurrentName(); key != null; key = p.nextFieldName()) {
            JsonToken t = p.nextToken();
            if (t == null) {
                t = JsonToken.NOT_AVAILABLE;
            }
            JsonNode value = null;
            switch (t.id()) {
                case 1: {
                    value = this.deserializeObject(p, ctxt, nodeFactory);
                    break;
                }
                case 3: {
                    value = this.deserializeArray(p, ctxt, nodeFactory);
                    break;
                }
                case 12: {
                    value = this._fromEmbedded(p, ctxt, nodeFactory);
                    break;
                }
                case 6: {
                    value = nodeFactory.textNode(p.getText());
                    break;
                }
                case 7: {
                    value = this._fromInt(p, ctxt, nodeFactory);
                    break;
                }
                case 9: {
                    value = nodeFactory.booleanNode(true);
                    break;
                }
                case 10: {
                    value = nodeFactory.booleanNode(false);
                    break;
                }
                case 11: {
                    value = nodeFactory.nullNode();
                    break;
                }
                default: {
                    value = this.deserializeAny(p, ctxt, nodeFactory);
                    break;
                }
            }
            final JsonNode old = node.replace(key, value);
            if (old != null) {
                this._handleDuplicateField(p, ctxt, nodeFactory, key, node, old, value);
            }
        }
        return node;
    }
    
    protected final JsonNode updateObject(final JsonParser p, final DeserializationContext ctxt, final ObjectNode node) throws IOException {
        String key;
        if (p.isExpectedStartObjectToken()) {
            key = p.nextFieldName();
        }
        else {
            if (!p.hasToken(JsonToken.FIELD_NAME)) {
                return this.deserialize(p, ctxt);
            }
            key = p.getCurrentName();
        }
        while (key != null) {
            JsonToken t = p.nextToken();
            final JsonNode old = node.get(key);
            Label_0365: {
                if (old != null) {
                    if (old instanceof ObjectNode) {
                        final JsonNode newValue = this.updateObject(p, ctxt, (ObjectNode)old);
                        if (newValue != old) {
                            node.set(key, newValue);
                        }
                        break Label_0365;
                    }
                    else if (old instanceof ArrayNode) {
                        final JsonNode newValue = this.updateArray(p, ctxt, (ArrayNode)old);
                        if (newValue != old) {
                            node.set(key, newValue);
                        }
                        break Label_0365;
                    }
                }
                if (t == null) {
                    t = JsonToken.NOT_AVAILABLE;
                }
                final JsonNodeFactory nodeFactory = ctxt.getNodeFactory();
                JsonNode value = null;
                switch (t.id()) {
                    case 1: {
                        value = this.deserializeObject(p, ctxt, nodeFactory);
                        break;
                    }
                    case 3: {
                        value = this.deserializeArray(p, ctxt, nodeFactory);
                        break;
                    }
                    case 12: {
                        value = this._fromEmbedded(p, ctxt, nodeFactory);
                        break;
                    }
                    case 6: {
                        value = nodeFactory.textNode(p.getText());
                        break;
                    }
                    case 7: {
                        value = this._fromInt(p, ctxt, nodeFactory);
                        break;
                    }
                    case 9: {
                        value = nodeFactory.booleanNode(true);
                        break;
                    }
                    case 10: {
                        value = nodeFactory.booleanNode(false);
                        break;
                    }
                    case 11: {
                        value = nodeFactory.nullNode();
                        break;
                    }
                    default: {
                        value = this.deserializeAny(p, ctxt, nodeFactory);
                        break;
                    }
                }
                if (old != null) {
                    this._handleDuplicateField(p, ctxt, nodeFactory, key, node, old, value);
                }
                node.set(key, value);
            }
            key = p.nextFieldName();
        }
        return node;
    }
    
    protected final ArrayNode deserializeArray(final JsonParser p, final DeserializationContext ctxt, final JsonNodeFactory nodeFactory) throws IOException {
        final ArrayNode node = nodeFactory.arrayNode();
    Label_0112:
        while (true) {
            final JsonToken t = p.nextToken();
            switch (t.id()) {
                case 1: {
                    node.add(this.deserializeObject(p, ctxt, nodeFactory));
                    continue;
                }
                case 3: {
                    node.add(this.deserializeArray(p, ctxt, nodeFactory));
                    continue;
                }
                case 4: {
                    break Label_0112;
                }
                case 12: {
                    node.add(this._fromEmbedded(p, ctxt, nodeFactory));
                    continue;
                }
                case 6: {
                    node.add(nodeFactory.textNode(p.getText()));
                    continue;
                }
                case 7: {
                    node.add(this._fromInt(p, ctxt, nodeFactory));
                    continue;
                }
                case 9: {
                    node.add(nodeFactory.booleanNode(true));
                    continue;
                }
                case 10: {
                    node.add(nodeFactory.booleanNode(false));
                    continue;
                }
                case 11: {
                    node.add(nodeFactory.nullNode());
                    continue;
                }
                default: {
                    node.add(this.deserializeAny(p, ctxt, nodeFactory));
                    continue;
                }
            }
        }
        return node;
    }
    
    protected final JsonNode updateArray(final JsonParser p, final DeserializationContext ctxt, final ArrayNode node) throws IOException {
        final JsonNodeFactory nodeFactory = ctxt.getNodeFactory();
    Label_0112:
        while (true) {
            final JsonToken t = p.nextToken();
            switch (t.id()) {
                case 1: {
                    node.add(this.deserializeObject(p, ctxt, nodeFactory));
                    continue;
                }
                case 3: {
                    node.add(this.deserializeArray(p, ctxt, nodeFactory));
                    continue;
                }
                case 4: {
                    break Label_0112;
                }
                case 12: {
                    node.add(this._fromEmbedded(p, ctxt, nodeFactory));
                    continue;
                }
                case 6: {
                    node.add(nodeFactory.textNode(p.getText()));
                    continue;
                }
                case 7: {
                    node.add(this._fromInt(p, ctxt, nodeFactory));
                    continue;
                }
                case 9: {
                    node.add(nodeFactory.booleanNode(true));
                    continue;
                }
                case 10: {
                    node.add(nodeFactory.booleanNode(false));
                    continue;
                }
                case 11: {
                    node.add(nodeFactory.nullNode());
                    continue;
                }
                default: {
                    node.add(this.deserializeAny(p, ctxt, nodeFactory));
                    continue;
                }
            }
        }
        return node;
    }
    
    protected final JsonNode deserializeAny(final JsonParser p, final DeserializationContext ctxt, final JsonNodeFactory nodeFactory) throws IOException {
        switch (p.getCurrentTokenId()) {
            case 2: {
                return nodeFactory.objectNode();
            }
            case 5: {
                return this.deserializeObjectAtName(p, ctxt, nodeFactory);
            }
            case 12: {
                return this._fromEmbedded(p, ctxt, nodeFactory);
            }
            case 6: {
                return nodeFactory.textNode(p.getText());
            }
            case 7: {
                return this._fromInt(p, ctxt, nodeFactory);
            }
            case 8: {
                return this._fromFloat(p, ctxt, nodeFactory);
            }
            case 9: {
                return nodeFactory.booleanNode(true);
            }
            case 10: {
                return nodeFactory.booleanNode(false);
            }
            case 11: {
                return nodeFactory.nullNode();
            }
            default: {
                return (JsonNode)ctxt.handleUnexpectedToken(this.handledType(), p);
            }
        }
    }
    
    protected final JsonNode _fromInt(final JsonParser p, final DeserializationContext ctxt, final JsonNodeFactory nodeFactory) throws IOException {
        final int feats = ctxt.getDeserializationFeatures();
        JsonParser.NumberType nt;
        if ((feats & BaseNodeDeserializer.F_MASK_INT_COERCIONS) != 0x0) {
            if (DeserializationFeature.USE_BIG_INTEGER_FOR_INTS.enabledIn(feats)) {
                nt = JsonParser.NumberType.BIG_INTEGER;
            }
            else if (DeserializationFeature.USE_LONG_FOR_INTS.enabledIn(feats)) {
                nt = JsonParser.NumberType.LONG;
            }
            else {
                nt = p.getNumberType();
            }
        }
        else {
            nt = p.getNumberType();
        }
        if (nt == JsonParser.NumberType.INT) {
            return nodeFactory.numberNode(p.getIntValue());
        }
        if (nt == JsonParser.NumberType.LONG) {
            return nodeFactory.numberNode(p.getLongValue());
        }
        return nodeFactory.numberNode(p.getBigIntegerValue());
    }
    
    protected final JsonNode _fromFloat(final JsonParser p, final DeserializationContext ctxt, final JsonNodeFactory nodeFactory) throws IOException {
        final JsonParser.NumberType nt = p.getNumberType();
        if (nt == JsonParser.NumberType.BIG_DECIMAL) {
            return nodeFactory.numberNode(p.getDecimalValue());
        }
        if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
            if (p.isNaN()) {
                return nodeFactory.numberNode(p.getDoubleValue());
            }
            return nodeFactory.numberNode(p.getDecimalValue());
        }
        else {
            if (nt == JsonParser.NumberType.FLOAT) {
                return nodeFactory.numberNode(p.getFloatValue());
            }
            return nodeFactory.numberNode(p.getDoubleValue());
        }
    }
    
    protected final JsonNode _fromEmbedded(final JsonParser p, final DeserializationContext ctxt, final JsonNodeFactory nodeFactory) throws IOException {
        final Object ob = p.getEmbeddedObject();
        if (ob == null) {
            return nodeFactory.nullNode();
        }
        final Class<?> type = ob.getClass();
        if (type == byte[].class) {
            return nodeFactory.binaryNode((byte[])ob);
        }
        if (ob instanceof RawValue) {
            return nodeFactory.rawValueNode((RawValue)ob);
        }
        if (ob instanceof JsonNode) {
            return (JsonNode)ob;
        }
        return nodeFactory.pojoNode(ob);
    }
}
