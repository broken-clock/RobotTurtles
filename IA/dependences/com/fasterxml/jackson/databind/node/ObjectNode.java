// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.TreeNode;
import java.math.BigInteger;
import java.math.BigDecimal;
import com.fasterxml.jackson.databind.util.RawValue;
import java.util.Arrays;
import java.util.Collection;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.JsonGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.util.Iterator;
import com.fasterxml.jackson.core.JsonPointer;
import java.util.LinkedHashMap;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;

public class ObjectNode extends ContainerNode<ObjectNode>
{
    protected final Map<String, JsonNode> _children;
    
    public ObjectNode(final JsonNodeFactory nc) {
        super(nc);
        this._children = new LinkedHashMap<String, JsonNode>();
    }
    
    public ObjectNode(final JsonNodeFactory nc, final Map<String, JsonNode> kids) {
        super(nc);
        this._children = kids;
    }
    
    @Override
    protected JsonNode _at(final JsonPointer ptr) {
        return this.get(ptr.getMatchingProperty());
    }
    
    @Override
    public ObjectNode deepCopy() {
        final ObjectNode ret = new ObjectNode(this._nodeFactory);
        for (final Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
            ret._children.put(entry.getKey(), entry.getValue().deepCopy());
        }
        return ret;
    }
    
    @Override
    public boolean isEmpty(final SerializerProvider serializers) {
        return this._children.isEmpty();
    }
    
    @Override
    public JsonNodeType getNodeType() {
        return JsonNodeType.OBJECT;
    }
    
    @Override
    public final boolean isObject() {
        return true;
    }
    
    @Override
    public JsonToken asToken() {
        return JsonToken.START_OBJECT;
    }
    
    @Override
    public int size() {
        return this._children.size();
    }
    
    @Override
    public Iterator<JsonNode> elements() {
        return this._children.values().iterator();
    }
    
    @Override
    public JsonNode get(final int index) {
        return null;
    }
    
    @Override
    public JsonNode get(final String fieldName) {
        return this._children.get(fieldName);
    }
    
    @Override
    public Iterator<String> fieldNames() {
        return this._children.keySet().iterator();
    }
    
    @Override
    public JsonNode path(final int index) {
        return MissingNode.getInstance();
    }
    
    @Override
    public JsonNode path(final String fieldName) {
        final JsonNode n = this._children.get(fieldName);
        if (n != null) {
            return n;
        }
        return MissingNode.getInstance();
    }
    
    @Override
    public Iterator<Map.Entry<String, JsonNode>> fields() {
        return this._children.entrySet().iterator();
    }
    
    @Override
    public ObjectNode with(final String propertyName) {
        final JsonNode n = this._children.get(propertyName);
        if (n == null) {
            final ObjectNode result = this.objectNode();
            this._children.put(propertyName, result);
            return result;
        }
        if (n instanceof ObjectNode) {
            return (ObjectNode)n;
        }
        throw new UnsupportedOperationException("Property '" + propertyName + "' has value that is not of type ObjectNode (but " + n.getClass().getName() + ")");
    }
    
    @Override
    public ArrayNode withArray(final String propertyName) {
        final JsonNode n = this._children.get(propertyName);
        if (n == null) {
            final ArrayNode result = this.arrayNode();
            this._children.put(propertyName, result);
            return result;
        }
        if (n instanceof ArrayNode) {
            return (ArrayNode)n;
        }
        throw new UnsupportedOperationException("Property '" + propertyName + "' has value that is not of type ArrayNode (but " + n.getClass().getName() + ")");
    }
    
    @Override
    public boolean equals(final Comparator<JsonNode> comparator, final JsonNode o) {
        if (!(o instanceof ObjectNode)) {
            return false;
        }
        final ObjectNode other = (ObjectNode)o;
        final Map<String, JsonNode> m1 = this._children;
        final Map<String, JsonNode> m2 = other._children;
        final int len = m1.size();
        if (m2.size() != len) {
            return false;
        }
        for (final Map.Entry<String, JsonNode> entry : m1.entrySet()) {
            final JsonNode v2 = m2.get(entry.getKey());
            if (v2 == null || !entry.getValue().equals(comparator, v2)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public JsonNode findValue(final String fieldName) {
        for (final Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
            if (fieldName.equals(entry.getKey())) {
                return entry.getValue();
            }
            final JsonNode value = entry.getValue().findValue(fieldName);
            if (value != null) {
                return value;
            }
        }
        return null;
    }
    
    @Override
    public List<JsonNode> findValues(final String fieldName, List<JsonNode> foundSoFar) {
        for (final Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
            if (fieldName.equals(entry.getKey())) {
                if (foundSoFar == null) {
                    foundSoFar = new ArrayList<JsonNode>();
                }
                foundSoFar.add(entry.getValue());
            }
            else {
                foundSoFar = entry.getValue().findValues(fieldName, foundSoFar);
            }
        }
        return foundSoFar;
    }
    
    @Override
    public List<String> findValuesAsText(final String fieldName, List<String> foundSoFar) {
        for (final Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
            if (fieldName.equals(entry.getKey())) {
                if (foundSoFar == null) {
                    foundSoFar = new ArrayList<String>();
                }
                foundSoFar.add(entry.getValue().asText());
            }
            else {
                foundSoFar = entry.getValue().findValuesAsText(fieldName, foundSoFar);
            }
        }
        return foundSoFar;
    }
    
    @Override
    public ObjectNode findParent(final String fieldName) {
        for (final Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
            if (fieldName.equals(entry.getKey())) {
                return this;
            }
            final JsonNode value = entry.getValue().findParent(fieldName);
            if (value != null) {
                return (ObjectNode)value;
            }
        }
        return null;
    }
    
    @Override
    public List<JsonNode> findParents(final String fieldName, List<JsonNode> foundSoFar) {
        for (final Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
            if (fieldName.equals(entry.getKey())) {
                if (foundSoFar == null) {
                    foundSoFar = new ArrayList<JsonNode>();
                }
                foundSoFar.add(this);
            }
            else {
                foundSoFar = entry.getValue().findParents(fieldName, foundSoFar);
            }
        }
        return foundSoFar;
    }
    
    @Override
    public void serialize(final JsonGenerator g, final SerializerProvider provider) throws IOException {
        final boolean trimEmptyArray = provider != null && !provider.isEnabled(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
        g.writeStartObject(this);
        for (final Map.Entry<String, JsonNode> en : this._children.entrySet()) {
            final BaseJsonNode value = en.getValue();
            if (trimEmptyArray && value.isArray() && value.isEmpty(provider)) {
                continue;
            }
            g.writeFieldName(en.getKey());
            value.serialize(g, provider);
        }
        g.writeEndObject();
    }
    
    @Override
    public void serializeWithType(final JsonGenerator g, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
        final boolean trimEmptyArray = provider != null && !provider.isEnabled(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
        final WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(this, JsonToken.START_OBJECT));
        for (final Map.Entry<String, JsonNode> en : this._children.entrySet()) {
            final BaseJsonNode value = en.getValue();
            if (trimEmptyArray && value.isArray() && value.isEmpty(provider)) {
                continue;
            }
            g.writeFieldName(en.getKey());
            value.serialize(g, provider);
        }
        typeSer.writeTypeSuffix(g, typeIdDef);
    }
    
    public JsonNode set(final String fieldName, JsonNode value) {
        if (value == null) {
            value = this.nullNode();
        }
        this._children.put(fieldName, value);
        return this;
    }
    
    public JsonNode setAll(final Map<String, ? extends JsonNode> properties) {
        for (final Map.Entry<String, ? extends JsonNode> en : properties.entrySet()) {
            JsonNode n = (JsonNode)en.getValue();
            if (n == null) {
                n = this.nullNode();
            }
            this._children.put(en.getKey(), n);
        }
        return this;
    }
    
    public JsonNode setAll(final ObjectNode other) {
        this._children.putAll(other._children);
        return this;
    }
    
    public JsonNode replace(final String fieldName, JsonNode value) {
        if (value == null) {
            value = this.nullNode();
        }
        return this._children.put(fieldName, value);
    }
    
    public JsonNode without(final String fieldName) {
        this._children.remove(fieldName);
        return this;
    }
    
    public ObjectNode without(final Collection<String> fieldNames) {
        this._children.keySet().removeAll(fieldNames);
        return this;
    }
    
    @Deprecated
    public JsonNode put(final String fieldName, JsonNode value) {
        if (value == null) {
            value = this.nullNode();
        }
        return this._children.put(fieldName, value);
    }
    
    public JsonNode remove(final String fieldName) {
        return this._children.remove(fieldName);
    }
    
    public ObjectNode remove(final Collection<String> fieldNames) {
        this._children.keySet().removeAll(fieldNames);
        return this;
    }
    
    @Override
    public ObjectNode removeAll() {
        this._children.clear();
        return this;
    }
    
    @Deprecated
    public JsonNode putAll(final Map<String, ? extends JsonNode> properties) {
        return this.setAll(properties);
    }
    
    @Deprecated
    public JsonNode putAll(final ObjectNode other) {
        return this.setAll(other);
    }
    
    public ObjectNode retain(final Collection<String> fieldNames) {
        this._children.keySet().retainAll(fieldNames);
        return this;
    }
    
    public ObjectNode retain(final String... fieldNames) {
        return this.retain(Arrays.asList(fieldNames));
    }
    
    public ArrayNode putArray(final String fieldName) {
        final ArrayNode n = this.arrayNode();
        this._put(fieldName, n);
        return n;
    }
    
    public ObjectNode putObject(final String fieldName) {
        final ObjectNode n = this.objectNode();
        this._put(fieldName, n);
        return n;
    }
    
    public ObjectNode putPOJO(final String fieldName, final Object pojo) {
        return this._put(fieldName, this.pojoNode(pojo));
    }
    
    public ObjectNode putRawValue(final String fieldName, final RawValue raw) {
        return this._put(fieldName, this.rawValueNode(raw));
    }
    
    public ObjectNode putNull(final String fieldName) {
        this._children.put(fieldName, this.nullNode());
        return this;
    }
    
    public ObjectNode put(final String fieldName, final short v) {
        return this._put(fieldName, this.numberNode(v));
    }
    
    public ObjectNode put(final String fieldName, final Short v) {
        return this._put(fieldName, (v == null) ? this.nullNode() : this.numberNode((short)v));
    }
    
    public ObjectNode put(final String fieldName, final int v) {
        return this._put(fieldName, this.numberNode(v));
    }
    
    public ObjectNode put(final String fieldName, final Integer v) {
        return this._put(fieldName, (v == null) ? this.nullNode() : this.numberNode((int)v));
    }
    
    public ObjectNode put(final String fieldName, final long v) {
        return this._put(fieldName, this.numberNode(v));
    }
    
    public ObjectNode put(final String fieldName, final Long v) {
        return this._put(fieldName, (v == null) ? this.nullNode() : this.numberNode((long)v));
    }
    
    public ObjectNode put(final String fieldName, final float v) {
        return this._put(fieldName, this.numberNode(v));
    }
    
    public ObjectNode put(final String fieldName, final Float v) {
        return this._put(fieldName, (v == null) ? this.nullNode() : this.numberNode((float)v));
    }
    
    public ObjectNode put(final String fieldName, final double v) {
        return this._put(fieldName, this.numberNode(v));
    }
    
    public ObjectNode put(final String fieldName, final Double v) {
        return this._put(fieldName, (v == null) ? this.nullNode() : this.numberNode((double)v));
    }
    
    public ObjectNode put(final String fieldName, final BigDecimal v) {
        return this._put(fieldName, (v == null) ? this.nullNode() : this.numberNode(v));
    }
    
    public ObjectNode put(final String fieldName, final BigInteger v) {
        return this._put(fieldName, (v == null) ? this.nullNode() : this.numberNode(v));
    }
    
    public ObjectNode put(final String fieldName, final String v) {
        return this._put(fieldName, (v == null) ? this.nullNode() : this.textNode(v));
    }
    
    public ObjectNode put(final String fieldName, final boolean v) {
        return this._put(fieldName, this.booleanNode(v));
    }
    
    public ObjectNode put(final String fieldName, final Boolean v) {
        return this._put(fieldName, (v == null) ? this.nullNode() : this.booleanNode(v));
    }
    
    public ObjectNode put(final String fieldName, final byte[] v) {
        return this._put(fieldName, (v == null) ? this.nullNode() : this.binaryNode(v));
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o != null && o instanceof ObjectNode && this._childrenEqual((ObjectNode)o));
    }
    
    protected boolean _childrenEqual(final ObjectNode other) {
        return this._children.equals(other._children);
    }
    
    @Override
    public int hashCode() {
        return this._children.hashCode();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(32 + (this.size() << 4));
        sb.append("{");
        int count = 0;
        for (final Map.Entry<String, JsonNode> en : this._children.entrySet()) {
            if (count > 0) {
                sb.append(",");
            }
            ++count;
            TextNode.appendQuoted(sb, en.getKey());
            sb.append(':');
            sb.append(en.getValue().toString());
        }
        sb.append("}");
        return sb.toString();
    }
    
    protected ObjectNode _put(final String fieldName, final JsonNode value) {
        this._children.put(fieldName, value);
        return this;
    }
}
