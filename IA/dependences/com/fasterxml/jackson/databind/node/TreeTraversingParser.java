// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.node;

import java.io.OutputStream;
import com.fasterxml.jackson.core.Base64Variant;
import java.math.BigDecimal;
import java.math.BigInteger;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParseException;
import java.io.IOException;
import com.fasterxml.jackson.databind.cfg.PackageVersion;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.base.ParserMinimalBase;

public class TreeTraversingParser extends ParserMinimalBase
{
    protected ObjectCodec _objectCodec;
    protected NodeCursor _nodeCursor;
    protected JsonToken _nextToken;
    protected boolean _startContainer;
    protected boolean _closed;
    
    public TreeTraversingParser(final JsonNode n) {
        this(n, null);
    }
    
    public TreeTraversingParser(final JsonNode n, final ObjectCodec codec) {
        super(0);
        this._objectCodec = codec;
        if (n.isArray()) {
            this._nextToken = JsonToken.START_ARRAY;
            this._nodeCursor = new NodeCursor.ArrayCursor(n, null);
        }
        else if (n.isObject()) {
            this._nextToken = JsonToken.START_OBJECT;
            this._nodeCursor = new NodeCursor.ObjectCursor(n, null);
        }
        else {
            this._nodeCursor = new NodeCursor.RootCursor(n, null);
        }
    }
    
    @Override
    public void setCodec(final ObjectCodec c) {
        this._objectCodec = c;
    }
    
    @Override
    public ObjectCodec getCodec() {
        return this._objectCodec;
    }
    
    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }
    
    @Override
    public void close() throws IOException {
        if (!this._closed) {
            this._closed = true;
            this._nodeCursor = null;
            this._currToken = null;
        }
    }
    
    @Override
    public JsonToken nextToken() throws IOException, JsonParseException {
        if (this._nextToken != null) {
            this._currToken = this._nextToken;
            this._nextToken = null;
            return this._currToken;
        }
        if (this._startContainer) {
            this._startContainer = false;
            if (!this._nodeCursor.currentHasChildren()) {
                return this._currToken = ((this._currToken == JsonToken.START_OBJECT) ? JsonToken.END_OBJECT : JsonToken.END_ARRAY);
            }
            this._nodeCursor = this._nodeCursor.iterateChildren();
            this._currToken = this._nodeCursor.nextToken();
            if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
                this._startContainer = true;
            }
            return this._currToken;
        }
        else {
            if (this._nodeCursor == null) {
                this._closed = true;
                return null;
            }
            this._currToken = this._nodeCursor.nextToken();
            if (this._currToken != null) {
                if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
                    this._startContainer = true;
                }
                return this._currToken;
            }
            this._currToken = this._nodeCursor.endToken();
            this._nodeCursor = this._nodeCursor.getParent();
            return this._currToken;
        }
    }
    
    @Override
    public JsonParser skipChildren() throws IOException, JsonParseException {
        if (this._currToken == JsonToken.START_OBJECT) {
            this._startContainer = false;
            this._currToken = JsonToken.END_OBJECT;
        }
        else if (this._currToken == JsonToken.START_ARRAY) {
            this._startContainer = false;
            this._currToken = JsonToken.END_ARRAY;
        }
        return this;
    }
    
    @Override
    public boolean isClosed() {
        return this._closed;
    }
    
    @Override
    public String getCurrentName() {
        return (this._nodeCursor == null) ? null : this._nodeCursor.getCurrentName();
    }
    
    @Override
    public void overrideCurrentName(final String name) {
        if (this._nodeCursor != null) {
            this._nodeCursor.overrideCurrentName(name);
        }
    }
    
    @Override
    public JsonStreamContext getParsingContext() {
        return this._nodeCursor;
    }
    
    @Override
    public JsonLocation getTokenLocation() {
        return JsonLocation.NA;
    }
    
    @Override
    public JsonLocation getCurrentLocation() {
        return JsonLocation.NA;
    }
    
    @Override
    public String getText() {
        if (this._closed) {
            return null;
        }
        switch (this._currToken) {
            case FIELD_NAME: {
                return this._nodeCursor.getCurrentName();
            }
            case VALUE_STRING: {
                return this.currentNode().textValue();
            }
            case VALUE_NUMBER_INT:
            case VALUE_NUMBER_FLOAT: {
                return String.valueOf(this.currentNode().numberValue());
            }
            case VALUE_EMBEDDED_OBJECT: {
                final JsonNode n = this.currentNode();
                if (n != null && n.isBinary()) {
                    return n.asText();
                }
                break;
            }
        }
        return (this._currToken == null) ? null : this._currToken.asString();
    }
    
    @Override
    public char[] getTextCharacters() throws IOException, JsonParseException {
        return this.getText().toCharArray();
    }
    
    @Override
    public int getTextLength() throws IOException, JsonParseException {
        return this.getText().length();
    }
    
    @Override
    public int getTextOffset() throws IOException, JsonParseException {
        return 0;
    }
    
    @Override
    public boolean hasTextCharacters() {
        return false;
    }
    
    @Override
    public NumberType getNumberType() throws IOException, JsonParseException {
        final JsonNode n = this.currentNumericNode();
        return (n == null) ? null : n.numberType();
    }
    
    @Override
    public BigInteger getBigIntegerValue() throws IOException, JsonParseException {
        return this.currentNumericNode().bigIntegerValue();
    }
    
    @Override
    public BigDecimal getDecimalValue() throws IOException, JsonParseException {
        return this.currentNumericNode().decimalValue();
    }
    
    @Override
    public double getDoubleValue() throws IOException, JsonParseException {
        return this.currentNumericNode().doubleValue();
    }
    
    @Override
    public float getFloatValue() throws IOException, JsonParseException {
        return (float)this.currentNumericNode().doubleValue();
    }
    
    @Override
    public long getLongValue() throws IOException, JsonParseException {
        return this.currentNumericNode().longValue();
    }
    
    @Override
    public int getIntValue() throws IOException, JsonParseException {
        return this.currentNumericNode().intValue();
    }
    
    @Override
    public Number getNumberValue() throws IOException, JsonParseException {
        return this.currentNumericNode().numberValue();
    }
    
    @Override
    public Object getEmbeddedObject() {
        if (!this._closed) {
            final JsonNode n = this.currentNode();
            if (n != null) {
                if (n.isPojo()) {
                    return ((POJONode)n).getPojo();
                }
                if (n.isBinary()) {
                    return ((BinaryNode)n).binaryValue();
                }
            }
        }
        return null;
    }
    
    @Override
    public boolean isNaN() {
        if (!this._closed) {
            final JsonNode n = this.currentNode();
            if (n instanceof NumericNode) {
                return ((NumericNode)n).isNaN();
            }
        }
        return false;
    }
    
    @Override
    public byte[] getBinaryValue(final Base64Variant b64variant) throws IOException, JsonParseException {
        final JsonNode n = this.currentNode();
        if (n == null) {
            return null;
        }
        if (n instanceof TextNode) {
            return ((TextNode)n).getBinaryValue(b64variant);
        }
        return n.binaryValue();
    }
    
    @Override
    public int readBinaryValue(final Base64Variant b64variant, final OutputStream out) throws IOException, JsonParseException {
        final byte[] data = this.getBinaryValue(b64variant);
        if (data != null) {
            out.write(data, 0, data.length);
            return data.length;
        }
        return 0;
    }
    
    protected JsonNode currentNode() {
        if (this._closed || this._nodeCursor == null) {
            return null;
        }
        return this._nodeCursor.currentNode();
    }
    
    protected JsonNode currentNumericNode() throws JsonParseException {
        final JsonNode n = this.currentNode();
        if (n == null || !n.isNumber()) {
            final JsonToken t = (n == null) ? null : n.asToken();
            throw this._constructError("Current token (" + t + ") not numeric, cannot use numeric value accessors");
        }
        return n;
    }
    
    @Override
    protected void _handleEOF() throws JsonParseException {
        this._throwInternal();
    }
}
