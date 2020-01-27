// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.util;

import java.util.TreeMap;
import java.io.OutputStream;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.core.base.ParserMinimalBase;
import java.io.InputStream;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.core.JsonGenerationException;
import java.math.BigDecimal;
import java.math.BigInteger;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.cfg.PackageVersion;
import com.fasterxml.jackson.core.Version;
import java.io.IOException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.DupDetector;
import com.fasterxml.jackson.core.json.JsonWriteContext;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.JsonGenerator;

public class TokenBuffer extends JsonGenerator
{
    protected static final int DEFAULT_GENERATOR_FEATURES;
    protected ObjectCodec _objectCodec;
    protected JsonStreamContext _parentContext;
    protected int _generatorFeatures;
    protected boolean _closed;
    protected boolean _hasNativeTypeIds;
    protected boolean _hasNativeObjectIds;
    protected boolean _mayHaveNativeIds;
    protected boolean _forceBigDecimal;
    protected Segment _first;
    protected Segment _last;
    protected int _appendAt;
    protected Object _typeId;
    protected Object _objectId;
    protected boolean _hasNativeId;
    protected JsonWriteContext _writeContext;
    
    public TokenBuffer(final ObjectCodec codec, final boolean hasNativeIds) {
        this._hasNativeId = false;
        this._objectCodec = codec;
        this._generatorFeatures = TokenBuffer.DEFAULT_GENERATOR_FEATURES;
        this._writeContext = JsonWriteContext.createRootContext(null);
        final Segment segment = new Segment();
        this._last = segment;
        this._first = segment;
        this._appendAt = 0;
        this._hasNativeTypeIds = hasNativeIds;
        this._hasNativeObjectIds = hasNativeIds;
        this._mayHaveNativeIds = (this._hasNativeTypeIds | this._hasNativeObjectIds);
    }
    
    public TokenBuffer(final JsonParser p) {
        this(p, null);
    }
    
    public TokenBuffer(final JsonParser p, final DeserializationContext ctxt) {
        this._hasNativeId = false;
        this._objectCodec = p.getCodec();
        this._parentContext = p.getParsingContext();
        this._generatorFeatures = TokenBuffer.DEFAULT_GENERATOR_FEATURES;
        this._writeContext = JsonWriteContext.createRootContext(null);
        final Segment segment = new Segment();
        this._last = segment;
        this._first = segment;
        this._appendAt = 0;
        this._hasNativeTypeIds = p.canReadTypeId();
        this._hasNativeObjectIds = p.canReadObjectId();
        this._mayHaveNativeIds = (this._hasNativeTypeIds | this._hasNativeObjectIds);
        this._forceBigDecimal = (ctxt != null && ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS));
    }
    
    public static TokenBuffer asCopyOfValue(final JsonParser p) throws IOException {
        final TokenBuffer b = new TokenBuffer(p);
        b.copyCurrentStructure(p);
        return b;
    }
    
    public TokenBuffer overrideParentContext(final JsonStreamContext ctxt) {
        this._parentContext = ctxt;
        return this;
    }
    
    public TokenBuffer forceUseOfBigDecimal(final boolean b) {
        this._forceBigDecimal = b;
        return this;
    }
    
    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }
    
    public JsonParser asParser() {
        return this.asParser(this._objectCodec);
    }
    
    public JsonParser asParserOnFirstToken() throws IOException {
        final JsonParser p = this.asParser(this._objectCodec);
        p.nextToken();
        return p;
    }
    
    public JsonParser asParser(final ObjectCodec codec) {
        return new Parser(this._first, codec, this._hasNativeTypeIds, this._hasNativeObjectIds, this._parentContext);
    }
    
    public JsonParser asParser(final JsonParser src) {
        final Parser p = new Parser(this._first, src.getCodec(), this._hasNativeTypeIds, this._hasNativeObjectIds, this._parentContext);
        p.setLocation(src.getTokenLocation());
        return p;
    }
    
    public JsonToken firstToken() {
        return this._first.type(0);
    }
    
    public TokenBuffer append(final TokenBuffer other) throws IOException {
        if (!this._hasNativeTypeIds) {
            this._hasNativeTypeIds = other.canWriteTypeId();
        }
        if (!this._hasNativeObjectIds) {
            this._hasNativeObjectIds = other.canWriteObjectId();
        }
        this._mayHaveNativeIds = (this._hasNativeTypeIds | this._hasNativeObjectIds);
        final JsonParser p = other.asParser();
        while (p.nextToken() != null) {
            this.copyCurrentStructure(p);
        }
        return this;
    }
    
    public void serialize(final JsonGenerator gen) throws IOException {
        Segment segment = this._first;
        int ptr = -1;
        final boolean checkIds = this._mayHaveNativeIds;
        boolean hasIds = checkIds && segment.hasIds();
        while (true) {
            if (++ptr >= 16) {
                ptr = 0;
                segment = segment.next();
                if (segment == null) {
                    break;
                }
                hasIds = (checkIds && segment.hasIds());
            }
            final JsonToken t = segment.type(ptr);
            if (t == null) {
                break;
            }
            if (hasIds) {
                Object id = segment.findObjectId(ptr);
                if (id != null) {
                    gen.writeObjectId(id);
                }
                id = segment.findTypeId(ptr);
                if (id != null) {
                    gen.writeTypeId(id);
                }
            }
            switch (t) {
                case START_OBJECT: {
                    gen.writeStartObject();
                    continue;
                }
                case END_OBJECT: {
                    gen.writeEndObject();
                    continue;
                }
                case START_ARRAY: {
                    gen.writeStartArray();
                    continue;
                }
                case END_ARRAY: {
                    gen.writeEndArray();
                    continue;
                }
                case FIELD_NAME: {
                    final Object ob = segment.get(ptr);
                    if (ob instanceof SerializableString) {
                        gen.writeFieldName((SerializableString)ob);
                    }
                    else {
                        gen.writeFieldName((String)ob);
                    }
                    continue;
                }
                case VALUE_STRING: {
                    final Object ob = segment.get(ptr);
                    if (ob instanceof SerializableString) {
                        gen.writeString((SerializableString)ob);
                    }
                    else {
                        gen.writeString((String)ob);
                    }
                    continue;
                }
                case VALUE_NUMBER_INT: {
                    final Object n = segment.get(ptr);
                    if (n instanceof Integer) {
                        gen.writeNumber((int)n);
                    }
                    else if (n instanceof BigInteger) {
                        gen.writeNumber((BigInteger)n);
                    }
                    else if (n instanceof Long) {
                        gen.writeNumber((long)n);
                    }
                    else if (n instanceof Short) {
                        gen.writeNumber((short)n);
                    }
                    else {
                        gen.writeNumber(((Number)n).intValue());
                    }
                    continue;
                }
                case VALUE_NUMBER_FLOAT: {
                    final Object n = segment.get(ptr);
                    if (n instanceof Double) {
                        gen.writeNumber((double)n);
                    }
                    else if (n instanceof BigDecimal) {
                        gen.writeNumber((BigDecimal)n);
                    }
                    else if (n instanceof Float) {
                        gen.writeNumber((float)n);
                    }
                    else if (n == null) {
                        gen.writeNull();
                    }
                    else {
                        if (!(n instanceof String)) {
                            throw new JsonGenerationException(String.format("Unrecognized value type for VALUE_NUMBER_FLOAT: %s, cannot serialize", n.getClass().getName()), gen);
                        }
                        gen.writeNumber((String)n);
                    }
                    continue;
                }
                case VALUE_TRUE: {
                    gen.writeBoolean(true);
                    continue;
                }
                case VALUE_FALSE: {
                    gen.writeBoolean(false);
                    continue;
                }
                case VALUE_NULL: {
                    gen.writeNull();
                    continue;
                }
                case VALUE_EMBEDDED_OBJECT: {
                    final Object value = segment.get(ptr);
                    if (value instanceof RawValue) {
                        ((RawValue)value).serialize(gen);
                    }
                    else if (value instanceof JsonSerializable) {
                        gen.writeObject(value);
                    }
                    else {
                        gen.writeEmbeddedObject(value);
                    }
                    continue;
                }
                default: {
                    throw new RuntimeException("Internal error: should never end up through this code path");
                }
            }
        }
    }
    
    public TokenBuffer deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (p.getCurrentTokenId() != JsonToken.FIELD_NAME.id()) {
            this.copyCurrentStructure(p);
            return this;
        }
        this.writeStartObject();
        JsonToken t;
        do {
            this.copyCurrentStructure(p);
        } while ((t = p.nextToken()) == JsonToken.FIELD_NAME);
        if (t != JsonToken.END_OBJECT) {
            ctxt.reportWrongTokenException(TokenBuffer.class, JsonToken.END_OBJECT, "Expected END_OBJECT after copying contents of a JsonParser into TokenBuffer, got " + t, new Object[0]);
        }
        this.writeEndObject();
        return this;
    }
    
    @Override
    public String toString() {
        final int MAX_COUNT = 100;
        final StringBuilder sb = new StringBuilder();
        sb.append("[TokenBuffer: ");
        final JsonParser jp = this.asParser();
        int count = 0;
        final boolean hasNativeIds = this._hasNativeTypeIds || this._hasNativeObjectIds;
        while (true) {
            try {
                final JsonToken t = jp.nextToken();
                if (t == null) {
                    break;
                }
                if (hasNativeIds) {
                    this._appendNativeIds(sb);
                }
                if (count < 100) {
                    if (count > 0) {
                        sb.append(", ");
                    }
                    sb.append(t.toString());
                    if (t == JsonToken.FIELD_NAME) {
                        sb.append('(');
                        sb.append(jp.getCurrentName());
                        sb.append(')');
                    }
                }
            }
            catch (IOException ioe) {
                throw new IllegalStateException(ioe);
            }
            ++count;
        }
        if (count >= 100) {
            sb.append(" ... (truncated ").append(count - 100).append(" entries)");
        }
        sb.append(']');
        return sb.toString();
    }
    
    private final void _appendNativeIds(final StringBuilder sb) {
        final Object objectId = this._last.findObjectId(this._appendAt - 1);
        if (objectId != null) {
            sb.append("[objectId=").append(String.valueOf(objectId)).append(']');
        }
        final Object typeId = this._last.findTypeId(this._appendAt - 1);
        if (typeId != null) {
            sb.append("[typeId=").append(String.valueOf(typeId)).append(']');
        }
    }
    
    @Override
    public JsonGenerator enable(final Feature f) {
        this._generatorFeatures |= f.getMask();
        return this;
    }
    
    @Override
    public JsonGenerator disable(final Feature f) {
        this._generatorFeatures &= ~f.getMask();
        return this;
    }
    
    @Override
    public boolean isEnabled(final Feature f) {
        return (this._generatorFeatures & f.getMask()) != 0x0;
    }
    
    @Override
    public int getFeatureMask() {
        return this._generatorFeatures;
    }
    
    @Deprecated
    @Override
    public JsonGenerator setFeatureMask(final int mask) {
        this._generatorFeatures = mask;
        return this;
    }
    
    @Override
    public JsonGenerator overrideStdFeatures(final int values, final int mask) {
        final int oldState = this.getFeatureMask();
        this._generatorFeatures = ((oldState & ~mask) | (values & mask));
        return this;
    }
    
    @Override
    public JsonGenerator useDefaultPrettyPrinter() {
        return this;
    }
    
    @Override
    public JsonGenerator setCodec(final ObjectCodec oc) {
        this._objectCodec = oc;
        return this;
    }
    
    @Override
    public ObjectCodec getCodec() {
        return this._objectCodec;
    }
    
    @Override
    public final JsonWriteContext getOutputContext() {
        return this._writeContext;
    }
    
    @Override
    public boolean canWriteBinaryNatively() {
        return true;
    }
    
    @Override
    public void flush() throws IOException {
    }
    
    @Override
    public void close() throws IOException {
        this._closed = true;
    }
    
    @Override
    public boolean isClosed() {
        return this._closed;
    }
    
    @Override
    public final void writeStartArray() throws IOException {
        this._writeContext.writeValue();
        this._append(JsonToken.START_ARRAY);
        this._writeContext = this._writeContext.createChildArrayContext();
    }
    
    @Override
    public final void writeEndArray() throws IOException {
        this._append(JsonToken.END_ARRAY);
        final JsonWriteContext c = this._writeContext.getParent();
        if (c != null) {
            this._writeContext = c;
        }
    }
    
    @Override
    public final void writeStartObject() throws IOException {
        this._writeContext.writeValue();
        this._append(JsonToken.START_OBJECT);
        this._writeContext = this._writeContext.createChildObjectContext();
    }
    
    @Override
    public void writeStartObject(final Object forValue) throws IOException {
        this._writeContext.writeValue();
        this._append(JsonToken.START_OBJECT);
        final JsonWriteContext ctxt = this._writeContext.createChildObjectContext();
        this._writeContext = ctxt;
        if (forValue != null) {
            ctxt.setCurrentValue(forValue);
        }
    }
    
    @Override
    public final void writeEndObject() throws IOException {
        this._append(JsonToken.END_OBJECT);
        final JsonWriteContext c = this._writeContext.getParent();
        if (c != null) {
            this._writeContext = c;
        }
    }
    
    @Override
    public final void writeFieldName(final String name) throws IOException {
        this._writeContext.writeFieldName(name);
        this._append(JsonToken.FIELD_NAME, name);
    }
    
    @Override
    public void writeFieldName(final SerializableString name) throws IOException {
        this._writeContext.writeFieldName(name.getValue());
        this._append(JsonToken.FIELD_NAME, name);
    }
    
    @Override
    public void writeString(final String text) throws IOException {
        if (text == null) {
            this.writeNull();
        }
        else {
            this._appendValue(JsonToken.VALUE_STRING, text);
        }
    }
    
    @Override
    public void writeString(final char[] text, final int offset, final int len) throws IOException {
        this.writeString(new String(text, offset, len));
    }
    
    @Override
    public void writeString(final SerializableString text) throws IOException {
        if (text == null) {
            this.writeNull();
        }
        else {
            this._appendValue(JsonToken.VALUE_STRING, text);
        }
    }
    
    @Override
    public void writeRawUTF8String(final byte[] text, final int offset, final int length) throws IOException {
        this._reportUnsupportedOperation();
    }
    
    @Override
    public void writeUTF8String(final byte[] text, final int offset, final int length) throws IOException {
        this._reportUnsupportedOperation();
    }
    
    @Override
    public void writeRaw(final String text) throws IOException {
        this._reportUnsupportedOperation();
    }
    
    @Override
    public void writeRaw(final String text, final int offset, final int len) throws IOException {
        this._reportUnsupportedOperation();
    }
    
    @Override
    public void writeRaw(final SerializableString text) throws IOException {
        this._reportUnsupportedOperation();
    }
    
    @Override
    public void writeRaw(final char[] text, final int offset, final int len) throws IOException {
        this._reportUnsupportedOperation();
    }
    
    @Override
    public void writeRaw(final char c) throws IOException {
        this._reportUnsupportedOperation();
    }
    
    @Override
    public void writeRawValue(final String text) throws IOException {
        this._appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, new RawValue(text));
    }
    
    @Override
    public void writeRawValue(String text, final int offset, final int len) throws IOException {
        if (offset > 0 || len != text.length()) {
            text = text.substring(offset, offset + len);
        }
        this._appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, new RawValue(text));
    }
    
    @Override
    public void writeRawValue(final char[] text, final int offset, final int len) throws IOException {
        this._appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, new String(text, offset, len));
    }
    
    @Override
    public void writeNumber(final short i) throws IOException {
        this._appendValue(JsonToken.VALUE_NUMBER_INT, i);
    }
    
    @Override
    public void writeNumber(final int i) throws IOException {
        this._appendValue(JsonToken.VALUE_NUMBER_INT, i);
    }
    
    @Override
    public void writeNumber(final long l) throws IOException {
        this._appendValue(JsonToken.VALUE_NUMBER_INT, l);
    }
    
    @Override
    public void writeNumber(final double d) throws IOException {
        this._appendValue(JsonToken.VALUE_NUMBER_FLOAT, d);
    }
    
    @Override
    public void writeNumber(final float f) throws IOException {
        this._appendValue(JsonToken.VALUE_NUMBER_FLOAT, f);
    }
    
    @Override
    public void writeNumber(final BigDecimal dec) throws IOException {
        if (dec == null) {
            this.writeNull();
        }
        else {
            this._appendValue(JsonToken.VALUE_NUMBER_FLOAT, dec);
        }
    }
    
    @Override
    public void writeNumber(final BigInteger v) throws IOException {
        if (v == null) {
            this.writeNull();
        }
        else {
            this._appendValue(JsonToken.VALUE_NUMBER_INT, v);
        }
    }
    
    @Override
    public void writeNumber(final String encodedValue) throws IOException {
        this._appendValue(JsonToken.VALUE_NUMBER_FLOAT, encodedValue);
    }
    
    @Override
    public void writeBoolean(final boolean state) throws IOException {
        this._appendValue(state ? JsonToken.VALUE_TRUE : JsonToken.VALUE_FALSE);
    }
    
    @Override
    public void writeNull() throws IOException {
        this._appendValue(JsonToken.VALUE_NULL);
    }
    
    @Override
    public void writeObject(final Object value) throws IOException {
        if (value == null) {
            this.writeNull();
            return;
        }
        final Class<?> raw = value.getClass();
        if (raw == byte[].class || value instanceof RawValue) {
            this._appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, value);
            return;
        }
        if (this._objectCodec == null) {
            this._appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, value);
        }
        else {
            this._objectCodec.writeValue(this, value);
        }
    }
    
    @Override
    public void writeTree(final TreeNode node) throws IOException {
        if (node == null) {
            this.writeNull();
            return;
        }
        if (this._objectCodec == null) {
            this._appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, node);
        }
        else {
            this._objectCodec.writeTree(this, node);
        }
    }
    
    @Override
    public void writeBinary(final Base64Variant b64variant, final byte[] data, final int offset, final int len) throws IOException {
        final byte[] copy = new byte[len];
        System.arraycopy(data, offset, copy, 0, len);
        this.writeObject(copy);
    }
    
    @Override
    public int writeBinary(final Base64Variant b64variant, final InputStream data, final int dataLength) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean canWriteTypeId() {
        return this._hasNativeTypeIds;
    }
    
    @Override
    public boolean canWriteObjectId() {
        return this._hasNativeObjectIds;
    }
    
    @Override
    public void writeTypeId(final Object id) {
        this._typeId = id;
        this._hasNativeId = true;
    }
    
    @Override
    public void writeObjectId(final Object id) {
        this._objectId = id;
        this._hasNativeId = true;
    }
    
    @Override
    public void writeEmbeddedObject(final Object object) throws IOException {
        this._appendValue(JsonToken.VALUE_EMBEDDED_OBJECT, object);
    }
    
    @Override
    public void copyCurrentEvent(final JsonParser p) throws IOException {
        if (this._mayHaveNativeIds) {
            this._checkNativeIds(p);
        }
        Label_0361: {
            switch (p.getCurrentToken()) {
                case START_OBJECT: {
                    this.writeStartObject();
                    break;
                }
                case END_OBJECT: {
                    this.writeEndObject();
                    break;
                }
                case START_ARRAY: {
                    this.writeStartArray();
                    break;
                }
                case END_ARRAY: {
                    this.writeEndArray();
                    break;
                }
                case FIELD_NAME: {
                    this.writeFieldName(p.getCurrentName());
                    break;
                }
                case VALUE_STRING: {
                    if (p.hasTextCharacters()) {
                        this.writeString(p.getTextCharacters(), p.getTextOffset(), p.getTextLength());
                        break;
                    }
                    this.writeString(p.getText());
                    break;
                }
                case VALUE_NUMBER_INT: {
                    switch (p.getNumberType()) {
                        case INT: {
                            this.writeNumber(p.getIntValue());
                            break Label_0361;
                        }
                        case BIG_INTEGER: {
                            this.writeNumber(p.getBigIntegerValue());
                            break Label_0361;
                        }
                        default: {
                            this.writeNumber(p.getLongValue());
                            break Label_0361;
                        }
                    }
                    break;
                }
                case VALUE_NUMBER_FLOAT: {
                    if (this._forceBigDecimal) {
                        this.writeNumber(p.getDecimalValue());
                        break;
                    }
                    switch (p.getNumberType()) {
                        case BIG_DECIMAL: {
                            this.writeNumber(p.getDecimalValue());
                            break Label_0361;
                        }
                        case FLOAT: {
                            this.writeNumber(p.getFloatValue());
                            break Label_0361;
                        }
                        default: {
                            this.writeNumber(p.getDoubleValue());
                            break Label_0361;
                        }
                    }
                    break;
                }
                case VALUE_TRUE: {
                    this.writeBoolean(true);
                    break;
                }
                case VALUE_FALSE: {
                    this.writeBoolean(false);
                    break;
                }
                case VALUE_NULL: {
                    this.writeNull();
                    break;
                }
                case VALUE_EMBEDDED_OBJECT: {
                    this.writeObject(p.getEmbeddedObject());
                    break;
                }
                default: {
                    throw new RuntimeException("Internal error: should never end up through this code path");
                }
            }
        }
    }
    
    @Override
    public void copyCurrentStructure(final JsonParser p) throws IOException {
        JsonToken t = p.getCurrentToken();
        if (t == JsonToken.FIELD_NAME) {
            if (this._mayHaveNativeIds) {
                this._checkNativeIds(p);
            }
            this.writeFieldName(p.getCurrentName());
            t = p.nextToken();
        }
        if (this._mayHaveNativeIds) {
            this._checkNativeIds(p);
        }
        switch (t) {
            case START_ARRAY: {
                this.writeStartArray();
                while (p.nextToken() != JsonToken.END_ARRAY) {
                    this.copyCurrentStructure(p);
                }
                this.writeEndArray();
                break;
            }
            case START_OBJECT: {
                this.writeStartObject();
                while (p.nextToken() != JsonToken.END_OBJECT) {
                    this.copyCurrentStructure(p);
                }
                this.writeEndObject();
                break;
            }
            default: {
                this.copyCurrentEvent(p);
                break;
            }
        }
    }
    
    private final void _checkNativeIds(final JsonParser jp) throws IOException {
        final Object typeId = jp.getTypeId();
        this._typeId = typeId;
        if (typeId != null) {
            this._hasNativeId = true;
        }
        if ((this._objectId = jp.getObjectId()) != null) {
            this._hasNativeId = true;
        }
    }
    
    protected final void _append(final JsonToken type) {
        final Segment next = this._hasNativeId ? this._last.append(this._appendAt, type, this._objectId, this._typeId) : this._last.append(this._appendAt, type);
        if (next == null) {
            ++this._appendAt;
        }
        else {
            this._last = next;
            this._appendAt = 1;
        }
    }
    
    protected final void _append(final JsonToken type, final Object value) {
        final Segment next = this._hasNativeId ? this._last.append(this._appendAt, type, value, this._objectId, this._typeId) : this._last.append(this._appendAt, type, value);
        if (next == null) {
            ++this._appendAt;
        }
        else {
            this._last = next;
            this._appendAt = 1;
        }
    }
    
    protected final void _appendValue(final JsonToken type) {
        this._writeContext.writeValue();
        final Segment next = this._hasNativeId ? this._last.append(this._appendAt, type, this._objectId, this._typeId) : this._last.append(this._appendAt, type);
        if (next == null) {
            ++this._appendAt;
        }
        else {
            this._last = next;
            this._appendAt = 1;
        }
    }
    
    protected final void _appendValue(final JsonToken type, final Object value) {
        this._writeContext.writeValue();
        final Segment next = this._hasNativeId ? this._last.append(this._appendAt, type, value, this._objectId, this._typeId) : this._last.append(this._appendAt, type, value);
        if (next == null) {
            ++this._appendAt;
        }
        else {
            this._last = next;
            this._appendAt = 1;
        }
    }
    
    @Override
    protected void _reportUnsupportedOperation() {
        throw new UnsupportedOperationException("Called operation not supported for TokenBuffer");
    }
    
    static {
        DEFAULT_GENERATOR_FEATURES = Feature.collectDefaults();
    }
    
    protected static final class Parser extends ParserMinimalBase
    {
        protected ObjectCodec _codec;
        protected final boolean _hasNativeTypeIds;
        protected final boolean _hasNativeObjectIds;
        protected final boolean _hasNativeIds;
        protected Segment _segment;
        protected int _segmentPtr;
        protected TokenBufferReadContext _parsingContext;
        protected boolean _closed;
        protected transient ByteArrayBuilder _byteBuilder;
        protected JsonLocation _location;
        
        @Deprecated
        public Parser(final Segment firstSeg, final ObjectCodec codec, final boolean hasNativeTypeIds, final boolean hasNativeObjectIds) {
            this(firstSeg, codec, hasNativeTypeIds, hasNativeObjectIds, null);
        }
        
        public Parser(final Segment firstSeg, final ObjectCodec codec, final boolean hasNativeTypeIds, final boolean hasNativeObjectIds, final JsonStreamContext parentContext) {
            super(0);
            this._location = null;
            this._segment = firstSeg;
            this._segmentPtr = -1;
            this._codec = codec;
            this._parsingContext = TokenBufferReadContext.createRootContext(parentContext);
            this._hasNativeTypeIds = hasNativeTypeIds;
            this._hasNativeObjectIds = hasNativeObjectIds;
            this._hasNativeIds = (hasNativeTypeIds | hasNativeObjectIds);
        }
        
        public void setLocation(final JsonLocation l) {
            this._location = l;
        }
        
        @Override
        public ObjectCodec getCodec() {
            return this._codec;
        }
        
        @Override
        public void setCodec(final ObjectCodec c) {
            this._codec = c;
        }
        
        @Override
        public Version version() {
            return PackageVersion.VERSION;
        }
        
        public JsonToken peekNextToken() throws IOException {
            if (this._closed) {
                return null;
            }
            Segment seg = this._segment;
            int ptr = this._segmentPtr + 1;
            if (ptr >= 16) {
                ptr = 0;
                seg = ((seg == null) ? null : seg.next());
            }
            return (seg == null) ? null : seg.type(ptr);
        }
        
        @Override
        public void close() throws IOException {
            if (!this._closed) {
                this._closed = true;
            }
        }
        
        @Override
        public JsonToken nextToken() throws IOException {
            if (this._closed || this._segment == null) {
                return null;
            }
            if (++this._segmentPtr >= 16) {
                this._segmentPtr = 0;
                this._segment = this._segment.next();
                if (this._segment == null) {
                    return null;
                }
            }
            this._currToken = this._segment.type(this._segmentPtr);
            if (this._currToken == JsonToken.FIELD_NAME) {
                final Object ob = this._currentObject();
                final String name = (String)((ob instanceof String) ? ob : ob.toString());
                this._parsingContext.setCurrentName(name);
            }
            else if (this._currToken == JsonToken.START_OBJECT) {
                this._parsingContext = this._parsingContext.createChildObjectContext();
            }
            else if (this._currToken == JsonToken.START_ARRAY) {
                this._parsingContext = this._parsingContext.createChildArrayContext();
            }
            else if (this._currToken == JsonToken.END_OBJECT || this._currToken == JsonToken.END_ARRAY) {
                this._parsingContext = this._parsingContext.parentOrCopy();
            }
            return this._currToken;
        }
        
        @Override
        public String nextFieldName() throws IOException {
            if (this._closed || this._segment == null) {
                return null;
            }
            final int ptr = this._segmentPtr + 1;
            if (ptr < 16 && this._segment.type(ptr) == JsonToken.FIELD_NAME) {
                this._segmentPtr = ptr;
                this._currToken = JsonToken.FIELD_NAME;
                final Object ob = this._segment.get(ptr);
                final String name = (String)((ob instanceof String) ? ob : ob.toString());
                this._parsingContext.setCurrentName(name);
                return name;
            }
            return (this.nextToken() == JsonToken.FIELD_NAME) ? this.getCurrentName() : null;
        }
        
        @Override
        public boolean isClosed() {
            return this._closed;
        }
        
        @Override
        public JsonStreamContext getParsingContext() {
            return this._parsingContext;
        }
        
        @Override
        public JsonLocation getTokenLocation() {
            return this.getCurrentLocation();
        }
        
        @Override
        public JsonLocation getCurrentLocation() {
            return (this._location == null) ? JsonLocation.NA : this._location;
        }
        
        @Override
        public String getCurrentName() {
            if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
                final JsonStreamContext parent = this._parsingContext.getParent();
                return parent.getCurrentName();
            }
            return this._parsingContext.getCurrentName();
        }
        
        @Override
        public void overrideCurrentName(final String name) {
            JsonStreamContext ctxt = this._parsingContext;
            if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
                ctxt = ctxt.getParent();
            }
            if (ctxt instanceof TokenBufferReadContext) {
                try {
                    ((TokenBufferReadContext)ctxt).setCurrentName(name);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        
        @Override
        public String getText() {
            if (this._currToken == JsonToken.VALUE_STRING || this._currToken == JsonToken.FIELD_NAME) {
                final Object ob = this._currentObject();
                if (ob instanceof String) {
                    return (String)ob;
                }
                return ClassUtil.nullOrToString(ob);
            }
            else {
                if (this._currToken == null) {
                    return null;
                }
                switch (this._currToken) {
                    case VALUE_NUMBER_INT:
                    case VALUE_NUMBER_FLOAT: {
                        return ClassUtil.nullOrToString(this._currentObject());
                    }
                    default: {
                        return this._currToken.asString();
                    }
                }
            }
        }
        
        @Override
        public char[] getTextCharacters() {
            final String str = this.getText();
            return (char[])((str == null) ? null : str.toCharArray());
        }
        
        @Override
        public int getTextLength() {
            final String str = this.getText();
            return (str == null) ? 0 : str.length();
        }
        
        @Override
        public int getTextOffset() {
            return 0;
        }
        
        @Override
        public boolean hasTextCharacters() {
            return false;
        }
        
        @Override
        public boolean isNaN() {
            if (this._currToken == JsonToken.VALUE_NUMBER_FLOAT) {
                final Object value = this._currentObject();
                if (value instanceof Double) {
                    final Double v = (Double)value;
                    return v.isNaN() || v.isInfinite();
                }
                if (value instanceof Float) {
                    final Float v2 = (Float)value;
                    return v2.isNaN() || v2.isInfinite();
                }
            }
            return false;
        }
        
        @Override
        public BigInteger getBigIntegerValue() throws IOException {
            final Number n = this.getNumberValue();
            if (n instanceof BigInteger) {
                return (BigInteger)n;
            }
            if (this.getNumberType() == NumberType.BIG_DECIMAL) {
                return ((BigDecimal)n).toBigInteger();
            }
            return BigInteger.valueOf(n.longValue());
        }
        
        @Override
        public BigDecimal getDecimalValue() throws IOException {
            final Number n = this.getNumberValue();
            if (n instanceof BigDecimal) {
                return (BigDecimal)n;
            }
            switch (this.getNumberType()) {
                case INT:
                case LONG: {
                    return BigDecimal.valueOf(n.longValue());
                }
                case BIG_INTEGER: {
                    return new BigDecimal((BigInteger)n);
                }
                default: {
                    return BigDecimal.valueOf(n.doubleValue());
                }
            }
        }
        
        @Override
        public double getDoubleValue() throws IOException {
            return this.getNumberValue().doubleValue();
        }
        
        @Override
        public float getFloatValue() throws IOException {
            return this.getNumberValue().floatValue();
        }
        
        @Override
        public int getIntValue() throws IOException {
            final Number n = (Number)((this._currToken == JsonToken.VALUE_NUMBER_INT) ? this._currentObject() : this.getNumberValue());
            if (n instanceof Integer || this._smallerThanInt(n)) {
                return n.intValue();
            }
            return this._convertNumberToInt(n);
        }
        
        @Override
        public long getLongValue() throws IOException {
            final Number n = (Number)((this._currToken == JsonToken.VALUE_NUMBER_INT) ? this._currentObject() : this.getNumberValue());
            if (n instanceof Long || this._smallerThanLong(n)) {
                return n.longValue();
            }
            return this._convertNumberToLong(n);
        }
        
        @Override
        public NumberType getNumberType() throws IOException {
            final Number n = this.getNumberValue();
            if (n instanceof Integer) {
                return NumberType.INT;
            }
            if (n instanceof Long) {
                return NumberType.LONG;
            }
            if (n instanceof Double) {
                return NumberType.DOUBLE;
            }
            if (n instanceof BigDecimal) {
                return NumberType.BIG_DECIMAL;
            }
            if (n instanceof BigInteger) {
                return NumberType.BIG_INTEGER;
            }
            if (n instanceof Float) {
                return NumberType.FLOAT;
            }
            if (n instanceof Short) {
                return NumberType.INT;
            }
            return null;
        }
        
        @Override
        public final Number getNumberValue() throws IOException {
            this._checkIsNumber();
            final Object value = this._currentObject();
            if (value instanceof Number) {
                return (Number)value;
            }
            if (value instanceof String) {
                final String str = (String)value;
                if (str.indexOf(46) >= 0) {
                    return Double.parseDouble(str);
                }
                return Long.parseLong(str);
            }
            else {
                if (value == null) {
                    return null;
                }
                throw new IllegalStateException("Internal error: entry should be a Number, but is of type " + value.getClass().getName());
            }
        }
        
        private final boolean _smallerThanInt(final Number n) {
            return n instanceof Short || n instanceof Byte;
        }
        
        private final boolean _smallerThanLong(final Number n) {
            return n instanceof Integer || n instanceof Short || n instanceof Byte;
        }
        
        protected int _convertNumberToInt(final Number n) throws IOException {
            if (n instanceof Long) {
                final long l = n.longValue();
                final int result = (int)l;
                if (result != l) {
                    this.reportOverflowInt();
                }
                return result;
            }
            if (n instanceof BigInteger) {
                final BigInteger big = (BigInteger)n;
                if (Parser.BI_MIN_INT.compareTo(big) > 0 || Parser.BI_MAX_INT.compareTo(big) < 0) {
                    this.reportOverflowInt();
                }
            }
            else {
                if (n instanceof Double || n instanceof Float) {
                    final double d = n.doubleValue();
                    if (d < -2.147483648E9 || d > 2.147483647E9) {
                        this.reportOverflowInt();
                    }
                    return (int)d;
                }
                if (n instanceof BigDecimal) {
                    final BigDecimal big2 = (BigDecimal)n;
                    if (Parser.BD_MIN_INT.compareTo(big2) > 0 || Parser.BD_MAX_INT.compareTo(big2) < 0) {
                        this.reportOverflowInt();
                    }
                }
                else {
                    this._throwInternal();
                }
            }
            return n.intValue();
        }
        
        protected long _convertNumberToLong(final Number n) throws IOException {
            if (n instanceof BigInteger) {
                final BigInteger big = (BigInteger)n;
                if (Parser.BI_MIN_LONG.compareTo(big) > 0 || Parser.BI_MAX_LONG.compareTo(big) < 0) {
                    this.reportOverflowLong();
                }
            }
            else {
                if (n instanceof Double || n instanceof Float) {
                    final double d = n.doubleValue();
                    if (d < -9.223372036854776E18 || d > 9.223372036854776E18) {
                        this.reportOverflowLong();
                    }
                    return (long)d;
                }
                if (n instanceof BigDecimal) {
                    final BigDecimal big2 = (BigDecimal)n;
                    if (Parser.BD_MIN_LONG.compareTo(big2) > 0 || Parser.BD_MAX_LONG.compareTo(big2) < 0) {
                        this.reportOverflowLong();
                    }
                }
                else {
                    this._throwInternal();
                }
            }
            return n.longValue();
        }
        
        @Override
        public Object getEmbeddedObject() {
            if (this._currToken == JsonToken.VALUE_EMBEDDED_OBJECT) {
                return this._currentObject();
            }
            return null;
        }
        
        @Override
        public byte[] getBinaryValue(final Base64Variant b64variant) throws IOException, JsonParseException {
            if (this._currToken == JsonToken.VALUE_EMBEDDED_OBJECT) {
                final Object ob = this._currentObject();
                if (ob instanceof byte[]) {
                    return (byte[])ob;
                }
            }
            if (this._currToken != JsonToken.VALUE_STRING) {
                throw this._constructError("Current token (" + this._currToken + ") not VALUE_STRING (or VALUE_EMBEDDED_OBJECT with byte[]), cannot access as binary");
            }
            final String str = this.getText();
            if (str == null) {
                return null;
            }
            ByteArrayBuilder builder = this._byteBuilder;
            if (builder == null) {
                builder = (this._byteBuilder = new ByteArrayBuilder(100));
            }
            else {
                this._byteBuilder.reset();
            }
            this._decodeBase64(str, builder, b64variant);
            return builder.toByteArray();
        }
        
        @Override
        public int readBinaryValue(final Base64Variant b64variant, final OutputStream out) throws IOException {
            final byte[] data = this.getBinaryValue(b64variant);
            if (data != null) {
                out.write(data, 0, data.length);
                return data.length;
            }
            return 0;
        }
        
        @Override
        public boolean canReadObjectId() {
            return this._hasNativeObjectIds;
        }
        
        @Override
        public boolean canReadTypeId() {
            return this._hasNativeTypeIds;
        }
        
        @Override
        public Object getTypeId() {
            return this._segment.findTypeId(this._segmentPtr);
        }
        
        @Override
        public Object getObjectId() {
            return this._segment.findObjectId(this._segmentPtr);
        }
        
        protected final Object _currentObject() {
            return this._segment.get(this._segmentPtr);
        }
        
        protected final void _checkIsNumber() throws JsonParseException {
            if (this._currToken == null || !this._currToken.isNumeric()) {
                throw this._constructError("Current token (" + this._currToken + ") not numeric, cannot use numeric value accessors");
            }
        }
        
        @Override
        protected void _handleEOF() throws JsonParseException {
            this._throwInternal();
        }
    }
    
    protected static final class Segment
    {
        public static final int TOKENS_PER_SEGMENT = 16;
        private static final JsonToken[] TOKEN_TYPES_BY_INDEX;
        protected Segment _next;
        protected long _tokenTypes;
        protected final Object[] _tokens;
        protected TreeMap<Integer, Object> _nativeIds;
        
        public Segment() {
            this._tokens = new Object[16];
        }
        
        public JsonToken type(final int index) {
            long l = this._tokenTypes;
            if (index > 0) {
                l >>= index << 2;
            }
            final int ix = (int)l & 0xF;
            return Segment.TOKEN_TYPES_BY_INDEX[ix];
        }
        
        public int rawType(final int index) {
            long l = this._tokenTypes;
            if (index > 0) {
                l >>= index << 2;
            }
            final int ix = (int)l & 0xF;
            return ix;
        }
        
        public Object get(final int index) {
            return this._tokens[index];
        }
        
        public Segment next() {
            return this._next;
        }
        
        public boolean hasIds() {
            return this._nativeIds != null;
        }
        
        public Segment append(final int index, final JsonToken tokenType) {
            if (index < 16) {
                this.set(index, tokenType);
                return null;
            }
            (this._next = new Segment()).set(0, tokenType);
            return this._next;
        }
        
        public Segment append(final int index, final JsonToken tokenType, final Object objectId, final Object typeId) {
            if (index < 16) {
                this.set(index, tokenType, objectId, typeId);
                return null;
            }
            (this._next = new Segment()).set(0, tokenType, objectId, typeId);
            return this._next;
        }
        
        public Segment append(final int index, final JsonToken tokenType, final Object value) {
            if (index < 16) {
                this.set(index, tokenType, value);
                return null;
            }
            (this._next = new Segment()).set(0, tokenType, value);
            return this._next;
        }
        
        public Segment append(final int index, final JsonToken tokenType, final Object value, final Object objectId, final Object typeId) {
            if (index < 16) {
                this.set(index, tokenType, value, objectId, typeId);
                return null;
            }
            (this._next = new Segment()).set(0, tokenType, value, objectId, typeId);
            return this._next;
        }
        
        private void set(final int index, final JsonToken tokenType) {
            long typeCode = tokenType.ordinal();
            if (index > 0) {
                typeCode <<= index << 2;
            }
            this._tokenTypes |= typeCode;
        }
        
        private void set(final int index, final JsonToken tokenType, final Object objectId, final Object typeId) {
            long typeCode = tokenType.ordinal();
            if (index > 0) {
                typeCode <<= index << 2;
            }
            this._tokenTypes |= typeCode;
            this.assignNativeIds(index, objectId, typeId);
        }
        
        private void set(final int index, final JsonToken tokenType, final Object value) {
            this._tokens[index] = value;
            long typeCode = tokenType.ordinal();
            if (index > 0) {
                typeCode <<= index << 2;
            }
            this._tokenTypes |= typeCode;
        }
        
        private void set(final int index, final JsonToken tokenType, final Object value, final Object objectId, final Object typeId) {
            this._tokens[index] = value;
            long typeCode = tokenType.ordinal();
            if (index > 0) {
                typeCode <<= index << 2;
            }
            this._tokenTypes |= typeCode;
            this.assignNativeIds(index, objectId, typeId);
        }
        
        private final void assignNativeIds(final int index, final Object objectId, final Object typeId) {
            if (this._nativeIds == null) {
                this._nativeIds = new TreeMap<Integer, Object>();
            }
            if (objectId != null) {
                this._nativeIds.put(this._objectIdIndex(index), objectId);
            }
            if (typeId != null) {
                this._nativeIds.put(this._typeIdIndex(index), typeId);
            }
        }
        
        private Object findObjectId(final int index) {
            return (this._nativeIds == null) ? null : this._nativeIds.get(this._objectIdIndex(index));
        }
        
        private Object findTypeId(final int index) {
            return (this._nativeIds == null) ? null : this._nativeIds.get(this._typeIdIndex(index));
        }
        
        private final int _typeIdIndex(final int i) {
            return i + i;
        }
        
        private final int _objectIdIndex(final int i) {
            return i + i + 1;
        }
        
        static {
            TOKEN_TYPES_BY_INDEX = new JsonToken[16];
            final JsonToken[] t = JsonToken.values();
            System.arraycopy(t, 1, Segment.TOKEN_TYPES_BY_INDEX, 1, Math.min(15, t.length - 1));
        }
    }
}
