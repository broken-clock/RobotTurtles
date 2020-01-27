// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.TreeNode;
import java.math.BigInteger;
import java.math.BigDecimal;
import com.fasterxml.jackson.databind.util.RawValue;
import java.util.Collection;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import java.util.Comparator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.util.Iterator;
import com.fasterxml.jackson.core.JsonPointer;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

public class ArrayNode extends ContainerNode<ArrayNode>
{
    private final List<JsonNode> _children;
    
    public ArrayNode(final JsonNodeFactory nf) {
        super(nf);
        this._children = new ArrayList<JsonNode>();
    }
    
    public ArrayNode(final JsonNodeFactory nf, final int capacity) {
        super(nf);
        this._children = new ArrayList<JsonNode>(capacity);
    }
    
    public ArrayNode(final JsonNodeFactory nf, final List<JsonNode> children) {
        super(nf);
        this._children = children;
    }
    
    @Override
    protected JsonNode _at(final JsonPointer ptr) {
        return this.get(ptr.getMatchingIndex());
    }
    
    @Override
    public ArrayNode deepCopy() {
        final ArrayNode ret = new ArrayNode(this._nodeFactory);
        for (final JsonNode element : this._children) {
            ret._children.add(element.deepCopy());
        }
        return ret;
    }
    
    @Override
    public boolean isEmpty(final SerializerProvider serializers) {
        return this._children.isEmpty();
    }
    
    @Override
    public JsonNodeType getNodeType() {
        return JsonNodeType.ARRAY;
    }
    
    @Override
    public boolean isArray() {
        return true;
    }
    
    @Override
    public JsonToken asToken() {
        return JsonToken.START_ARRAY;
    }
    
    @Override
    public int size() {
        return this._children.size();
    }
    
    @Override
    public Iterator<JsonNode> elements() {
        return this._children.iterator();
    }
    
    @Override
    public JsonNode get(final int index) {
        if (index >= 0 && index < this._children.size()) {
            return this._children.get(index);
        }
        return null;
    }
    
    @Override
    public JsonNode get(final String fieldName) {
        return null;
    }
    
    @Override
    public JsonNode path(final String fieldName) {
        return MissingNode.getInstance();
    }
    
    @Override
    public JsonNode path(final int index) {
        if (index >= 0 && index < this._children.size()) {
            return this._children.get(index);
        }
        return MissingNode.getInstance();
    }
    
    @Override
    public boolean equals(final Comparator<JsonNode> comparator, final JsonNode o) {
        if (!(o instanceof ArrayNode)) {
            return false;
        }
        final ArrayNode other = (ArrayNode)o;
        final int len = this._children.size();
        if (other.size() != len) {
            return false;
        }
        final List<JsonNode> l1 = this._children;
        final List<JsonNode> l2 = other._children;
        for (int i = 0; i < len; ++i) {
            if (!l1.get(i).equals(comparator, l2.get(i))) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void serialize(final JsonGenerator f, final SerializerProvider provider) throws IOException {
        final List<JsonNode> c = this._children;
        final int size = c.size();
        f.writeStartArray(size);
        for (int i = 0; i < size; ++i) {
            final JsonNode n = c.get(i);
            ((BaseJsonNode)n).serialize(f, provider);
        }
        f.writeEndArray();
    }
    
    @Override
    public void serializeWithType(final JsonGenerator g, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
        final WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(this, JsonToken.START_ARRAY));
        for (final JsonNode n : this._children) {
            ((BaseJsonNode)n).serialize(g, provider);
        }
        typeSer.writeTypeSuffix(g, typeIdDef);
    }
    
    @Override
    public JsonNode findValue(final String fieldName) {
        for (final JsonNode node : this._children) {
            final JsonNode value = node.findValue(fieldName);
            if (value != null) {
                return value;
            }
        }
        return null;
    }
    
    @Override
    public List<JsonNode> findValues(final String fieldName, List<JsonNode> foundSoFar) {
        for (final JsonNode node : this._children) {
            foundSoFar = node.findValues(fieldName, foundSoFar);
        }
        return foundSoFar;
    }
    
    @Override
    public List<String> findValuesAsText(final String fieldName, List<String> foundSoFar) {
        for (final JsonNode node : this._children) {
            foundSoFar = node.findValuesAsText(fieldName, foundSoFar);
        }
        return foundSoFar;
    }
    
    @Override
    public ObjectNode findParent(final String fieldName) {
        for (final JsonNode node : this._children) {
            final JsonNode parent = node.findParent(fieldName);
            if (parent != null) {
                return (ObjectNode)parent;
            }
        }
        return null;
    }
    
    @Override
    public List<JsonNode> findParents(final String fieldName, List<JsonNode> foundSoFar) {
        for (final JsonNode node : this._children) {
            foundSoFar = node.findParents(fieldName, foundSoFar);
        }
        return foundSoFar;
    }
    
    public JsonNode set(final int index, JsonNode value) {
        if (value == null) {
            value = this.nullNode();
        }
        if (index < 0 || index >= this._children.size()) {
            throw new IndexOutOfBoundsException("Illegal index " + index + ", array size " + this.size());
        }
        return this._children.set(index, value);
    }
    
    public ArrayNode add(JsonNode value) {
        if (value == null) {
            value = this.nullNode();
        }
        this._add(value);
        return this;
    }
    
    public ArrayNode addAll(final ArrayNode other) {
        this._children.addAll(other._children);
        return this;
    }
    
    public ArrayNode addAll(final Collection<? extends JsonNode> nodes) {
        this._children.addAll(nodes);
        return this;
    }
    
    public ArrayNode insert(final int index, JsonNode value) {
        if (value == null) {
            value = this.nullNode();
        }
        this._insert(index, value);
        return this;
    }
    
    public JsonNode remove(final int index) {
        if (index >= 0 && index < this._children.size()) {
            return this._children.remove(index);
        }
        return null;
    }
    
    @Override
    public ArrayNode removeAll() {
        this._children.clear();
        return this;
    }
    
    public ArrayNode addArray() {
        final ArrayNode n = this.arrayNode();
        this._add(n);
        return n;
    }
    
    public ObjectNode addObject() {
        final ObjectNode n = this.objectNode();
        this._add(n);
        return n;
    }
    
    public ArrayNode addPOJO(final Object value) {
        if (value == null) {
            this.addNull();
        }
        else {
            this._add(this.pojoNode(value));
        }
        return this;
    }
    
    public ArrayNode addRawValue(final RawValue raw) {
        if (raw == null) {
            this.addNull();
        }
        else {
            this._add(this.rawValueNode(raw));
        }
        return this;
    }
    
    public ArrayNode addNull() {
        this._add(this.nullNode());
        return this;
    }
    
    public ArrayNode add(final int v) {
        this._add(this.numberNode(v));
        return this;
    }
    
    public ArrayNode add(final Integer value) {
        if (value == null) {
            return this.addNull();
        }
        return this._add(this.numberNode((int)value));
    }
    
    public ArrayNode add(final long v) {
        return this._add(this.numberNode(v));
    }
    
    public ArrayNode add(final Long value) {
        if (value == null) {
            return this.addNull();
        }
        return this._add(this.numberNode((long)value));
    }
    
    public ArrayNode add(final float v) {
        return this._add(this.numberNode(v));
    }
    
    public ArrayNode add(final Float value) {
        if (value == null) {
            return this.addNull();
        }
        return this._add(this.numberNode((float)value));
    }
    
    public ArrayNode add(final double v) {
        return this._add(this.numberNode(v));
    }
    
    public ArrayNode add(final Double value) {
        if (value == null) {
            return this.addNull();
        }
        return this._add(this.numberNode((double)value));
    }
    
    public ArrayNode add(final BigDecimal v) {
        if (v == null) {
            return this.addNull();
        }
        return this._add(this.numberNode(v));
    }
    
    public ArrayNode add(final BigInteger v) {
        if (v == null) {
            return this.addNull();
        }
        return this._add(this.numberNode(v));
    }
    
    public ArrayNode add(final String v) {
        if (v == null) {
            return this.addNull();
        }
        return this._add(this.textNode(v));
    }
    
    public ArrayNode add(final boolean v) {
        return this._add(this.booleanNode(v));
    }
    
    public ArrayNode add(final Boolean value) {
        if (value == null) {
            return this.addNull();
        }
        return this._add(this.booleanNode(value));
    }
    
    public ArrayNode add(final byte[] v) {
        if (v == null) {
            return this.addNull();
        }
        return this._add(this.binaryNode(v));
    }
    
    public ArrayNode insertArray(final int index) {
        final ArrayNode n = this.arrayNode();
        this._insert(index, n);
        return n;
    }
    
    public ObjectNode insertObject(final int index) {
        final ObjectNode n = this.objectNode();
        this._insert(index, n);
        return n;
    }
    
    public ArrayNode insertPOJO(final int index, final Object value) {
        if (value == null) {
            return this.insertNull(index);
        }
        return this._insert(index, this.pojoNode(value));
    }
    
    public ArrayNode insertNull(final int index) {
        this._insert(index, this.nullNode());
        return this;
    }
    
    public ArrayNode insert(final int index, final int v) {
        this._insert(index, this.numberNode(v));
        return this;
    }
    
    public ArrayNode insert(final int index, final Integer value) {
        if (value == null) {
            this.insertNull(index);
        }
        else {
            this._insert(index, this.numberNode((int)value));
        }
        return this;
    }
    
    public ArrayNode insert(final int index, final long v) {
        return this._insert(index, this.numberNode(v));
    }
    
    public ArrayNode insert(final int index, final Long value) {
        if (value == null) {
            return this.insertNull(index);
        }
        return this._insert(index, this.numberNode((long)value));
    }
    
    public ArrayNode insert(final int index, final float v) {
        return this._insert(index, this.numberNode(v));
    }
    
    public ArrayNode insert(final int index, final Float value) {
        if (value == null) {
            return this.insertNull(index);
        }
        return this._insert(index, this.numberNode((float)value));
    }
    
    public ArrayNode insert(final int index, final double v) {
        return this._insert(index, this.numberNode(v));
    }
    
    public ArrayNode insert(final int index, final Double value) {
        if (value == null) {
            return this.insertNull(index);
        }
        return this._insert(index, this.numberNode((double)value));
    }
    
    public ArrayNode insert(final int index, final BigDecimal v) {
        if (v == null) {
            return this.insertNull(index);
        }
        return this._insert(index, this.numberNode(v));
    }
    
    public ArrayNode insert(final int index, final BigInteger v) {
        if (v == null) {
            return this.insertNull(index);
        }
        return this._insert(index, this.numberNode(v));
    }
    
    public ArrayNode insert(final int index, final String v) {
        if (v == null) {
            return this.insertNull(index);
        }
        return this._insert(index, this.textNode(v));
    }
    
    public ArrayNode insert(final int index, final boolean v) {
        return this._insert(index, this.booleanNode(v));
    }
    
    public ArrayNode insert(final int index, final Boolean value) {
        if (value == null) {
            return this.insertNull(index);
        }
        return this._insert(index, this.booleanNode(value));
    }
    
    public ArrayNode insert(final int index, final byte[] v) {
        if (v == null) {
            return this.insertNull(index);
        }
        return this._insert(index, this.binaryNode(v));
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o != null && o instanceof ArrayNode && this._children.equals(((ArrayNode)o)._children));
    }
    
    protected boolean _childrenEqual(final ArrayNode other) {
        return this._children.equals(other._children);
    }
    
    @Override
    public int hashCode() {
        return this._children.hashCode();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(16 + (this.size() << 4));
        sb.append('[');
        for (int i = 0, len = this._children.size(); i < len; ++i) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(this._children.get(i).toString());
        }
        sb.append(']');
        return sb.toString();
    }
    
    protected ArrayNode _add(final JsonNode node) {
        this._children.add(node);
        return this;
    }
    
    protected ArrayNode _insert(final int index, final JsonNode node) {
        if (index < 0) {
            this._children.add(0, node);
        }
        else if (index >= this._children.size()) {
            this._children.add(node);
        }
        else {
            this._children.add(index, node);
        }
        return this;
    }
}
