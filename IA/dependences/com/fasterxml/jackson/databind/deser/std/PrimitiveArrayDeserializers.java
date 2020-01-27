// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Arrays;
import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.exc.InvalidNullException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.core.JsonToken;
import java.lang.reflect.Array;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.DeserializationConfig;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.impl.NullsFailProvider;
import com.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

public abstract class PrimitiveArrayDeserializers<T> extends StdDeserializer<T> implements ContextualDeserializer
{
    protected final Boolean _unwrapSingle;
    private transient Object _emptyValue;
    protected final NullValueProvider _nuller;
    
    protected PrimitiveArrayDeserializers(final Class<T> cls) {
        super(cls);
        this._unwrapSingle = null;
        this._nuller = null;
    }
    
    protected PrimitiveArrayDeserializers(final PrimitiveArrayDeserializers<?> base, final NullValueProvider nuller, final Boolean unwrapSingle) {
        super(base._valueClass);
        this._unwrapSingle = unwrapSingle;
        this._nuller = nuller;
    }
    
    public static JsonDeserializer<?> forType(final Class<?> rawType) {
        if (rawType == Integer.TYPE) {
            return IntDeser.instance;
        }
        if (rawType == Long.TYPE) {
            return LongDeser.instance;
        }
        if (rawType == Byte.TYPE) {
            return new ByteDeser();
        }
        if (rawType == Short.TYPE) {
            return new ShortDeser();
        }
        if (rawType == Float.TYPE) {
            return new FloatDeser();
        }
        if (rawType == Double.TYPE) {
            return new DoubleDeser();
        }
        if (rawType == Boolean.TYPE) {
            return new BooleanDeser();
        }
        if (rawType == Character.TYPE) {
            return new CharDeser();
        }
        throw new IllegalStateException();
    }
    
    @Override
    public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
        final Boolean unwrapSingle = this.findFormatFeature(ctxt, property, this._valueClass, JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        NullValueProvider nuller = null;
        final Nulls nullStyle = this.findContentNullStyle(ctxt, property);
        if (nullStyle == Nulls.SKIP) {
            nuller = NullsConstantProvider.skipper();
        }
        else if (nullStyle == Nulls.FAIL) {
            if (property == null) {
                nuller = NullsFailProvider.constructForRootValue(ctxt.constructType(this._valueClass));
            }
            else {
                nuller = NullsFailProvider.constructForProperty(property);
            }
        }
        if (unwrapSingle == this._unwrapSingle && nuller == this._nuller) {
            return this;
        }
        return this.withResolved(nuller, unwrapSingle);
    }
    
    protected abstract T _concat(final T p0, final T p1);
    
    protected abstract T handleSingleElementUnwrapped(final JsonParser p0, final DeserializationContext p1) throws IOException;
    
    protected abstract PrimitiveArrayDeserializers<?> withResolved(final NullValueProvider p0, final Boolean p1);
    
    protected abstract T _constructEmpty();
    
    @Override
    public Boolean supportsUpdate(final DeserializationConfig config) {
        return Boolean.TRUE;
    }
    
    @Override
    public AccessPattern getEmptyAccessPattern() {
        return AccessPattern.CONSTANT;
    }
    
    @Override
    public Object getEmptyValue(final DeserializationContext ctxt) throws JsonMappingException {
        Object empty = this._emptyValue;
        if (empty == null) {
            empty = (this._emptyValue = this._constructEmpty());
        }
        return empty;
    }
    
    @Override
    public Object deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromArray(p, ctxt);
    }
    
    @Override
    public T deserialize(final JsonParser p, final DeserializationContext ctxt, final T existing) throws IOException {
        final T newValue = this.deserialize(p, ctxt);
        if (existing == null) {
            return newValue;
        }
        final int len = Array.getLength(existing);
        if (len == 0) {
            return newValue;
        }
        return this._concat(existing, newValue);
    }
    
    protected T handleNonArray(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_STRING) && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT) && p.getText().length() == 0) {
            return null;
        }
        final boolean canWrap = this._unwrapSingle == Boolean.TRUE || (this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY));
        if (canWrap) {
            return this.handleSingleElementUnwrapped(p, ctxt);
        }
        return (T)ctxt.handleUnexpectedToken(this._valueClass, p);
    }
    
    protected void _failOnNull(final DeserializationContext ctxt) throws IOException {
        throw InvalidNullException.from(ctxt, null, ctxt.constructType(this._valueClass));
    }
    
    @JacksonStdImpl
    static final class CharDeser extends PrimitiveArrayDeserializers<char[]>
    {
        private static final long serialVersionUID = 1L;
        
        public CharDeser() {
            super(char[].class);
        }
        
        protected CharDeser(final CharDeser base, final NullValueProvider nuller, final Boolean unwrapSingle) {
            super(base, nuller, unwrapSingle);
        }
        
        @Override
        protected PrimitiveArrayDeserializers<?> withResolved(final NullValueProvider nuller, final Boolean unwrapSingle) {
            return this;
        }
        
        @Override
        protected char[] _constructEmpty() {
            return new char[0];
        }
        
        @Override
        public char[] deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            JsonToken t = p.getCurrentToken();
            if (t == JsonToken.VALUE_STRING) {
                final char[] buffer = p.getTextCharacters();
                final int offset = p.getTextOffset();
                final int len = p.getTextLength();
                final char[] result = new char[len];
                System.arraycopy(buffer, offset, result, 0, len);
                return result;
            }
            if (p.isExpectedStartArrayToken()) {
                final StringBuilder sb = new StringBuilder(64);
                while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
                    String str;
                    if (t == JsonToken.VALUE_STRING) {
                        str = p.getText();
                    }
                    else if (t == JsonToken.VALUE_NULL) {
                        if (this._nuller != null) {
                            this._nuller.getNullValue(ctxt);
                            continue;
                        }
                        this._verifyNullForPrimitive(ctxt);
                        str = "\u0000";
                    }
                    else {
                        final CharSequence cs = (CharSequence)ctxt.handleUnexpectedToken(Character.TYPE, p);
                        str = cs.toString();
                    }
                    if (str.length() != 1) {
                        ctxt.reportInputMismatch(this, "Cannot convert a JSON String of length %d into a char element of char array", str.length());
                    }
                    sb.append(str.charAt(0));
                }
                return sb.toString().toCharArray();
            }
            if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
                final Object ob = p.getEmbeddedObject();
                if (ob == null) {
                    return null;
                }
                if (ob instanceof char[]) {
                    return (char[])ob;
                }
                if (ob instanceof String) {
                    return ((String)ob).toCharArray();
                }
                if (ob instanceof byte[]) {
                    return Base64Variants.getDefaultVariant().encode((byte[])ob, false).toCharArray();
                }
            }
            return (char[])ctxt.handleUnexpectedToken(this._valueClass, p);
        }
        
        @Override
        protected char[] handleSingleElementUnwrapped(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            return (char[])ctxt.handleUnexpectedToken(this._valueClass, p);
        }
        
        @Override
        protected char[] _concat(final char[] oldValue, final char[] newValue) {
            final int len1 = oldValue.length;
            final int len2 = newValue.length;
            final char[] result = Arrays.copyOf(oldValue, len1 + len2);
            System.arraycopy(newValue, 0, result, len1, len2);
            return result;
        }
    }
    
    @JacksonStdImpl
    static final class BooleanDeser extends PrimitiveArrayDeserializers<boolean[]>
    {
        private static final long serialVersionUID = 1L;
        
        public BooleanDeser() {
            super(boolean[].class);
        }
        
        protected BooleanDeser(final BooleanDeser base, final NullValueProvider nuller, final Boolean unwrapSingle) {
            super(base, nuller, unwrapSingle);
        }
        
        @Override
        protected PrimitiveArrayDeserializers<?> withResolved(final NullValueProvider nuller, final Boolean unwrapSingle) {
            return new BooleanDeser(this, nuller, unwrapSingle);
        }
        
        @Override
        protected boolean[] _constructEmpty() {
            return new boolean[0];
        }
        
        @Override
        public boolean[] deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            if (!p.isExpectedStartArrayToken()) {
                return this.handleNonArray(p, ctxt);
            }
            final ArrayBuilders.BooleanBuilder builder = ctxt.getArrayBuilders().getBooleanBuilder();
            boolean[] chunk = builder.resetAndStart();
            int ix = 0;
            try {
                JsonToken t;
                while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
                    boolean value;
                    if (t == JsonToken.VALUE_TRUE) {
                        value = true;
                    }
                    else if (t == JsonToken.VALUE_FALSE) {
                        value = false;
                    }
                    else if (t == JsonToken.VALUE_NULL) {
                        if (this._nuller != null) {
                            this._nuller.getNullValue(ctxt);
                            continue;
                        }
                        this._verifyNullForPrimitive(ctxt);
                        value = false;
                    }
                    else {
                        value = this._parseBooleanPrimitive(p, ctxt);
                    }
                    if (ix >= chunk.length) {
                        chunk = builder.appendCompletedChunk(chunk, ix);
                        ix = 0;
                    }
                    chunk[ix++] = value;
                }
            }
            catch (Exception e) {
                throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
            }
            return builder.completeAndClearBuffer(chunk, ix);
        }
        
        @Override
        protected boolean[] handleSingleElementUnwrapped(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            return new boolean[] { this._parseBooleanPrimitive(p, ctxt) };
        }
        
        @Override
        protected boolean[] _concat(final boolean[] oldValue, final boolean[] newValue) {
            final int len1 = oldValue.length;
            final int len2 = newValue.length;
            final boolean[] result = Arrays.copyOf(oldValue, len1 + len2);
            System.arraycopy(newValue, 0, result, len1, len2);
            return result;
        }
    }
    
    @JacksonStdImpl
    static final class ByteDeser extends PrimitiveArrayDeserializers<byte[]>
    {
        private static final long serialVersionUID = 1L;
        
        public ByteDeser() {
            super(byte[].class);
        }
        
        protected ByteDeser(final ByteDeser base, final NullValueProvider nuller, final Boolean unwrapSingle) {
            super(base, nuller, unwrapSingle);
        }
        
        @Override
        protected PrimitiveArrayDeserializers<?> withResolved(final NullValueProvider nuller, final Boolean unwrapSingle) {
            return new ByteDeser(this, nuller, unwrapSingle);
        }
        
        @Override
        protected byte[] _constructEmpty() {
            return new byte[0];
        }
        
        @Override
        public byte[] deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            JsonToken t = p.getCurrentToken();
            if (t == JsonToken.VALUE_STRING) {
                try {
                    return p.getBinaryValue(ctxt.getBase64Variant());
                }
                catch (JsonParseException e) {
                    final String msg = e.getOriginalMessage();
                    if (msg.contains("base64")) {
                        return (byte[])ctxt.handleWeirdStringValue(byte[].class, p.getText(), msg, new Object[0]);
                    }
                }
            }
            if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
                final Object ob = p.getEmbeddedObject();
                if (ob == null) {
                    return null;
                }
                if (ob instanceof byte[]) {
                    return (byte[])ob;
                }
            }
            if (!p.isExpectedStartArrayToken()) {
                return this.handleNonArray(p, ctxt);
            }
            final ArrayBuilders.ByteBuilder builder = ctxt.getArrayBuilders().getByteBuilder();
            byte[] chunk = builder.resetAndStart();
            int ix = 0;
            try {
                while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
                    byte value;
                    if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
                        value = p.getByteValue();
                    }
                    else if (t == JsonToken.VALUE_NULL) {
                        if (this._nuller != null) {
                            this._nuller.getNullValue(ctxt);
                            continue;
                        }
                        this._verifyNullForPrimitive(ctxt);
                        value = 0;
                    }
                    else {
                        value = this._parseBytePrimitive(p, ctxt);
                    }
                    if (ix >= chunk.length) {
                        chunk = builder.appendCompletedChunk(chunk, ix);
                        ix = 0;
                    }
                    chunk[ix++] = value;
                }
            }
            catch (Exception e2) {
                throw JsonMappingException.wrapWithPath(e2, chunk, builder.bufferedSize() + ix);
            }
            return builder.completeAndClearBuffer(chunk, ix);
        }
        
        @Override
        protected byte[] handleSingleElementUnwrapped(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            final JsonToken t = p.getCurrentToken();
            byte value;
            if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
                value = p.getByteValue();
            }
            else if (t == JsonToken.VALUE_NULL) {
                if (this._nuller != null) {
                    this._nuller.getNullValue(ctxt);
                    return (byte[])this.getEmptyValue(ctxt);
                }
                this._verifyNullForPrimitive(ctxt);
                return null;
            }
            else {
                final Number n = (Number)ctxt.handleUnexpectedToken(this._valueClass.getComponentType(), p);
                value = n.byteValue();
            }
            return new byte[] { value };
        }
        
        @Override
        protected byte[] _concat(final byte[] oldValue, final byte[] newValue) {
            final int len1 = oldValue.length;
            final int len2 = newValue.length;
            final byte[] result = Arrays.copyOf(oldValue, len1 + len2);
            System.arraycopy(newValue, 0, result, len1, len2);
            return result;
        }
    }
    
    @JacksonStdImpl
    static final class ShortDeser extends PrimitiveArrayDeserializers<short[]>
    {
        private static final long serialVersionUID = 1L;
        
        public ShortDeser() {
            super(short[].class);
        }
        
        protected ShortDeser(final ShortDeser base, final NullValueProvider nuller, final Boolean unwrapSingle) {
            super(base, nuller, unwrapSingle);
        }
        
        @Override
        protected PrimitiveArrayDeserializers<?> withResolved(final NullValueProvider nuller, final Boolean unwrapSingle) {
            return new ShortDeser(this, nuller, unwrapSingle);
        }
        
        @Override
        protected short[] _constructEmpty() {
            return new short[0];
        }
        
        @Override
        public short[] deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            if (!p.isExpectedStartArrayToken()) {
                return this.handleNonArray(p, ctxt);
            }
            final ArrayBuilders.ShortBuilder builder = ctxt.getArrayBuilders().getShortBuilder();
            short[] chunk = builder.resetAndStart();
            int ix = 0;
            try {
                JsonToken t;
                while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
                    short value;
                    if (t == JsonToken.VALUE_NULL) {
                        if (this._nuller != null) {
                            this._nuller.getNullValue(ctxt);
                            continue;
                        }
                        this._verifyNullForPrimitive(ctxt);
                        value = 0;
                    }
                    else {
                        value = this._parseShortPrimitive(p, ctxt);
                    }
                    if (ix >= chunk.length) {
                        chunk = builder.appendCompletedChunk(chunk, ix);
                        ix = 0;
                    }
                    chunk[ix++] = value;
                }
            }
            catch (Exception e) {
                throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
            }
            return builder.completeAndClearBuffer(chunk, ix);
        }
        
        @Override
        protected short[] handleSingleElementUnwrapped(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            return new short[] { this._parseShortPrimitive(p, ctxt) };
        }
        
        @Override
        protected short[] _concat(final short[] oldValue, final short[] newValue) {
            final int len1 = oldValue.length;
            final int len2 = newValue.length;
            final short[] result = Arrays.copyOf(oldValue, len1 + len2);
            System.arraycopy(newValue, 0, result, len1, len2);
            return result;
        }
    }
    
    @JacksonStdImpl
    static final class IntDeser extends PrimitiveArrayDeserializers<int[]>
    {
        private static final long serialVersionUID = 1L;
        public static final IntDeser instance;
        
        public IntDeser() {
            super(int[].class);
        }
        
        protected IntDeser(final IntDeser base, final NullValueProvider nuller, final Boolean unwrapSingle) {
            super(base, nuller, unwrapSingle);
        }
        
        @Override
        protected PrimitiveArrayDeserializers<?> withResolved(final NullValueProvider nuller, final Boolean unwrapSingle) {
            return new IntDeser(this, nuller, unwrapSingle);
        }
        
        @Override
        protected int[] _constructEmpty() {
            return new int[0];
        }
        
        @Override
        public int[] deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            if (!p.isExpectedStartArrayToken()) {
                return this.handleNonArray(p, ctxt);
            }
            final ArrayBuilders.IntBuilder builder = ctxt.getArrayBuilders().getIntBuilder();
            int[] chunk = builder.resetAndStart();
            int ix = 0;
            try {
                JsonToken t;
                while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
                    int value;
                    if (t == JsonToken.VALUE_NUMBER_INT) {
                        value = p.getIntValue();
                    }
                    else if (t == JsonToken.VALUE_NULL) {
                        if (this._nuller != null) {
                            this._nuller.getNullValue(ctxt);
                            continue;
                        }
                        this._verifyNullForPrimitive(ctxt);
                        value = 0;
                    }
                    else {
                        value = this._parseIntPrimitive(p, ctxt);
                    }
                    if (ix >= chunk.length) {
                        chunk = builder.appendCompletedChunk(chunk, ix);
                        ix = 0;
                    }
                    chunk[ix++] = value;
                }
            }
            catch (Exception e) {
                throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
            }
            return builder.completeAndClearBuffer(chunk, ix);
        }
        
        @Override
        protected int[] handleSingleElementUnwrapped(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            return new int[] { this._parseIntPrimitive(p, ctxt) };
        }
        
        @Override
        protected int[] _concat(final int[] oldValue, final int[] newValue) {
            final int len1 = oldValue.length;
            final int len2 = newValue.length;
            final int[] result = Arrays.copyOf(oldValue, len1 + len2);
            System.arraycopy(newValue, 0, result, len1, len2);
            return result;
        }
        
        static {
            instance = new IntDeser();
        }
    }
    
    @JacksonStdImpl
    static final class LongDeser extends PrimitiveArrayDeserializers<long[]>
    {
        private static final long serialVersionUID = 1L;
        public static final LongDeser instance;
        
        public LongDeser() {
            super(long[].class);
        }
        
        protected LongDeser(final LongDeser base, final NullValueProvider nuller, final Boolean unwrapSingle) {
            super(base, nuller, unwrapSingle);
        }
        
        @Override
        protected PrimitiveArrayDeserializers<?> withResolved(final NullValueProvider nuller, final Boolean unwrapSingle) {
            return new LongDeser(this, nuller, unwrapSingle);
        }
        
        @Override
        protected long[] _constructEmpty() {
            return new long[0];
        }
        
        @Override
        public long[] deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            if (!p.isExpectedStartArrayToken()) {
                return this.handleNonArray(p, ctxt);
            }
            final ArrayBuilders.LongBuilder builder = ctxt.getArrayBuilders().getLongBuilder();
            long[] chunk = builder.resetAndStart();
            int ix = 0;
            try {
                JsonToken t;
                while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
                    long value;
                    if (t == JsonToken.VALUE_NUMBER_INT) {
                        value = p.getLongValue();
                    }
                    else if (t == JsonToken.VALUE_NULL) {
                        if (this._nuller != null) {
                            this._nuller.getNullValue(ctxt);
                            continue;
                        }
                        this._verifyNullForPrimitive(ctxt);
                        value = 0L;
                    }
                    else {
                        value = this._parseLongPrimitive(p, ctxt);
                    }
                    if (ix >= chunk.length) {
                        chunk = builder.appendCompletedChunk(chunk, ix);
                        ix = 0;
                    }
                    chunk[ix++] = value;
                }
            }
            catch (Exception e) {
                throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
            }
            return builder.completeAndClearBuffer(chunk, ix);
        }
        
        @Override
        protected long[] handleSingleElementUnwrapped(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            return new long[] { this._parseLongPrimitive(p, ctxt) };
        }
        
        @Override
        protected long[] _concat(final long[] oldValue, final long[] newValue) {
            final int len1 = oldValue.length;
            final int len2 = newValue.length;
            final long[] result = Arrays.copyOf(oldValue, len1 + len2);
            System.arraycopy(newValue, 0, result, len1, len2);
            return result;
        }
        
        static {
            instance = new LongDeser();
        }
    }
    
    @JacksonStdImpl
    static final class FloatDeser extends PrimitiveArrayDeserializers<float[]>
    {
        private static final long serialVersionUID = 1L;
        
        public FloatDeser() {
            super(float[].class);
        }
        
        protected FloatDeser(final FloatDeser base, final NullValueProvider nuller, final Boolean unwrapSingle) {
            super(base, nuller, unwrapSingle);
        }
        
        @Override
        protected PrimitiveArrayDeserializers<?> withResolved(final NullValueProvider nuller, final Boolean unwrapSingle) {
            return new FloatDeser(this, nuller, unwrapSingle);
        }
        
        @Override
        protected float[] _constructEmpty() {
            return new float[0];
        }
        
        @Override
        public float[] deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            if (!p.isExpectedStartArrayToken()) {
                return this.handleNonArray(p, ctxt);
            }
            final ArrayBuilders.FloatBuilder builder = ctxt.getArrayBuilders().getFloatBuilder();
            float[] chunk = builder.resetAndStart();
            int ix = 0;
            try {
                JsonToken t;
                while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
                    if (t == JsonToken.VALUE_NULL && this._nuller != null) {
                        this._nuller.getNullValue(ctxt);
                    }
                    else {
                        final float value = this._parseFloatPrimitive(p, ctxt);
                        if (ix >= chunk.length) {
                            chunk = builder.appendCompletedChunk(chunk, ix);
                            ix = 0;
                        }
                        chunk[ix++] = value;
                    }
                }
            }
            catch (Exception e) {
                throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
            }
            return builder.completeAndClearBuffer(chunk, ix);
        }
        
        @Override
        protected float[] handleSingleElementUnwrapped(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            return new float[] { this._parseFloatPrimitive(p, ctxt) };
        }
        
        @Override
        protected float[] _concat(final float[] oldValue, final float[] newValue) {
            final int len1 = oldValue.length;
            final int len2 = newValue.length;
            final float[] result = Arrays.copyOf(oldValue, len1 + len2);
            System.arraycopy(newValue, 0, result, len1, len2);
            return result;
        }
    }
    
    @JacksonStdImpl
    static final class DoubleDeser extends PrimitiveArrayDeserializers<double[]>
    {
        private static final long serialVersionUID = 1L;
        
        public DoubleDeser() {
            super(double[].class);
        }
        
        protected DoubleDeser(final DoubleDeser base, final NullValueProvider nuller, final Boolean unwrapSingle) {
            super(base, nuller, unwrapSingle);
        }
        
        @Override
        protected PrimitiveArrayDeserializers<?> withResolved(final NullValueProvider nuller, final Boolean unwrapSingle) {
            return new DoubleDeser(this, nuller, unwrapSingle);
        }
        
        @Override
        protected double[] _constructEmpty() {
            return new double[0];
        }
        
        @Override
        public double[] deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            if (!p.isExpectedStartArrayToken()) {
                return this.handleNonArray(p, ctxt);
            }
            final ArrayBuilders.DoubleBuilder builder = ctxt.getArrayBuilders().getDoubleBuilder();
            double[] chunk = builder.resetAndStart();
            int ix = 0;
            try {
                JsonToken t;
                while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
                    if (t == JsonToken.VALUE_NULL && this._nuller != null) {
                        this._nuller.getNullValue(ctxt);
                    }
                    else {
                        final double value = this._parseDoublePrimitive(p, ctxt);
                        if (ix >= chunk.length) {
                            chunk = builder.appendCompletedChunk(chunk, ix);
                            ix = 0;
                        }
                        chunk[ix++] = value;
                    }
                }
            }
            catch (Exception e) {
                throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
            }
            return builder.completeAndClearBuffer(chunk, ix);
        }
        
        @Override
        protected double[] handleSingleElementUnwrapped(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            return new double[] { this._parseDoublePrimitive(p, ctxt) };
        }
        
        @Override
        protected double[] _concat(final double[] oldValue, final double[] newValue) {
            final int len1 = oldValue.length;
            final int len2 = newValue.length;
            final double[] result = Arrays.copyOf(oldValue, len1 + len2);
            System.arraycopy(newValue, 0, result, len1, len2);
            return result;
        }
    }
}
