// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind;

import java.util.Comparator;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.io.IOException;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.util.Iterator;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.core.TreeNode;

public abstract class JsonNode extends JsonSerializable.Base implements TreeNode, Iterable<JsonNode>
{
    protected JsonNode() {
    }
    
    public abstract <T extends JsonNode> T deepCopy();
    
    @Override
    public int size() {
        return 0;
    }
    
    @Override
    public final boolean isValueNode() {
        switch (this.getNodeType()) {
            case ARRAY:
            case OBJECT:
            case MISSING: {
                return false;
            }
            default: {
                return true;
            }
        }
    }
    
    @Override
    public final boolean isContainerNode() {
        final JsonNodeType type = this.getNodeType();
        return type == JsonNodeType.OBJECT || type == JsonNodeType.ARRAY;
    }
    
    @Override
    public boolean isMissingNode() {
        return false;
    }
    
    @Override
    public boolean isArray() {
        return false;
    }
    
    @Override
    public boolean isObject() {
        return false;
    }
    
    @Override
    public abstract JsonNode get(final int p0);
    
    @Override
    public JsonNode get(final String fieldName) {
        return null;
    }
    
    @Override
    public abstract JsonNode path(final String p0);
    
    @Override
    public abstract JsonNode path(final int p0);
    
    @Override
    public Iterator<String> fieldNames() {
        return ClassUtil.emptyIterator();
    }
    
    @Override
    public final JsonNode at(final JsonPointer ptr) {
        if (ptr.matches()) {
            return this;
        }
        final JsonNode n = this._at(ptr);
        if (n == null) {
            return MissingNode.getInstance();
        }
        return n.at(ptr.tail());
    }
    
    @Override
    public final JsonNode at(final String jsonPtrExpr) {
        return this.at(JsonPointer.compile(jsonPtrExpr));
    }
    
    protected abstract JsonNode _at(final JsonPointer p0);
    
    public abstract JsonNodeType getNodeType();
    
    public final boolean isPojo() {
        return this.getNodeType() == JsonNodeType.POJO;
    }
    
    public final boolean isNumber() {
        return this.getNodeType() == JsonNodeType.NUMBER;
    }
    
    public boolean isIntegralNumber() {
        return false;
    }
    
    public boolean isFloatingPointNumber() {
        return false;
    }
    
    public boolean isShort() {
        return false;
    }
    
    public boolean isInt() {
        return false;
    }
    
    public boolean isLong() {
        return false;
    }
    
    public boolean isFloat() {
        return false;
    }
    
    public boolean isDouble() {
        return false;
    }
    
    public boolean isBigDecimal() {
        return false;
    }
    
    public boolean isBigInteger() {
        return false;
    }
    
    public final boolean isTextual() {
        return this.getNodeType() == JsonNodeType.STRING;
    }
    
    public final boolean isBoolean() {
        return this.getNodeType() == JsonNodeType.BOOLEAN;
    }
    
    public final boolean isNull() {
        return this.getNodeType() == JsonNodeType.NULL;
    }
    
    public final boolean isBinary() {
        return this.getNodeType() == JsonNodeType.BINARY;
    }
    
    public boolean canConvertToInt() {
        return false;
    }
    
    public boolean canConvertToLong() {
        return false;
    }
    
    public String textValue() {
        return null;
    }
    
    public byte[] binaryValue() throws IOException {
        return null;
    }
    
    public boolean booleanValue() {
        return false;
    }
    
    public Number numberValue() {
        return null;
    }
    
    public short shortValue() {
        return 0;
    }
    
    public int intValue() {
        return 0;
    }
    
    public long longValue() {
        return 0L;
    }
    
    public float floatValue() {
        return 0.0f;
    }
    
    public double doubleValue() {
        return 0.0;
    }
    
    public BigDecimal decimalValue() {
        return BigDecimal.ZERO;
    }
    
    public BigInteger bigIntegerValue() {
        return BigInteger.ZERO;
    }
    
    public abstract String asText();
    
    public String asText(final String defaultValue) {
        final String str = this.asText();
        return (str == null) ? defaultValue : str;
    }
    
    public int asInt() {
        return this.asInt(0);
    }
    
    public int asInt(final int defaultValue) {
        return defaultValue;
    }
    
    public long asLong() {
        return this.asLong(0L);
    }
    
    public long asLong(final long defaultValue) {
        return defaultValue;
    }
    
    public double asDouble() {
        return this.asDouble(0.0);
    }
    
    public double asDouble(final double defaultValue) {
        return defaultValue;
    }
    
    public boolean asBoolean() {
        return this.asBoolean(false);
    }
    
    public boolean asBoolean(final boolean defaultValue) {
        return defaultValue;
    }
    
    public boolean has(final String fieldName) {
        return this.get(fieldName) != null;
    }
    
    public boolean has(final int index) {
        return this.get(index) != null;
    }
    
    public boolean hasNonNull(final String fieldName) {
        final JsonNode n = this.get(fieldName);
        return n != null && !n.isNull();
    }
    
    public boolean hasNonNull(final int index) {
        final JsonNode n = this.get(index);
        return n != null && !n.isNull();
    }
    
    @Override
    public final Iterator<JsonNode> iterator() {
        return this.elements();
    }
    
    public Iterator<JsonNode> elements() {
        return ClassUtil.emptyIterator();
    }
    
    public Iterator<Map.Entry<String, JsonNode>> fields() {
        return ClassUtil.emptyIterator();
    }
    
    public abstract JsonNode findValue(final String p0);
    
    public final List<JsonNode> findValues(final String fieldName) {
        final List<JsonNode> result = this.findValues(fieldName, null);
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }
    
    public final List<String> findValuesAsText(final String fieldName) {
        final List<String> result = this.findValuesAsText(fieldName, null);
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }
    
    public abstract JsonNode findPath(final String p0);
    
    public abstract JsonNode findParent(final String p0);
    
    public final List<JsonNode> findParents(final String fieldName) {
        final List<JsonNode> result = this.findParents(fieldName, null);
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }
    
    public abstract List<JsonNode> findValues(final String p0, final List<JsonNode> p1);
    
    public abstract List<String> findValuesAsText(final String p0, final List<String> p1);
    
    public abstract List<JsonNode> findParents(final String p0, final List<JsonNode> p1);
    
    public JsonNode with(final String propertyName) {
        throw new UnsupportedOperationException("JsonNode not of type ObjectNode (but " + this.getClass().getName() + "), cannot call with() on it");
    }
    
    public JsonNode withArray(final String propertyName) {
        throw new UnsupportedOperationException("JsonNode not of type ObjectNode (but " + this.getClass().getName() + "), cannot call withArray() on it");
    }
    
    public boolean equals(final Comparator<JsonNode> comparator, final JsonNode other) {
        return comparator.compare(this, other) == 0;
    }
    
    @Override
    public abstract String toString();
    
    @Override
    public abstract boolean equals(final Object p0);
}
