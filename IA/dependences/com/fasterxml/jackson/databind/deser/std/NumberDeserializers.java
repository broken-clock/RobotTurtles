// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.util.AccessPattern;
import java.math.BigInteger;
import java.math.BigDecimal;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.util.HashSet;

public class NumberDeserializers
{
    private static final HashSet<String> _classNames;
    
    public static JsonDeserializer<?> find(final Class<?> rawType, final String clsName) {
        if (rawType.isPrimitive()) {
            if (rawType == Integer.TYPE) {
                return IntegerDeserializer.primitiveInstance;
            }
            if (rawType == Boolean.TYPE) {
                return BooleanDeserializer.primitiveInstance;
            }
            if (rawType == Long.TYPE) {
                return LongDeserializer.primitiveInstance;
            }
            if (rawType == Double.TYPE) {
                return DoubleDeserializer.primitiveInstance;
            }
            if (rawType == Character.TYPE) {
                return CharacterDeserializer.primitiveInstance;
            }
            if (rawType == Byte.TYPE) {
                return ByteDeserializer.primitiveInstance;
            }
            if (rawType == Short.TYPE) {
                return ShortDeserializer.primitiveInstance;
            }
            if (rawType == Float.TYPE) {
                return FloatDeserializer.primitiveInstance;
            }
        }
        else {
            if (!NumberDeserializers._classNames.contains(clsName)) {
                return null;
            }
            if (rawType == Integer.class) {
                return IntegerDeserializer.wrapperInstance;
            }
            if (rawType == Boolean.class) {
                return BooleanDeserializer.wrapperInstance;
            }
            if (rawType == Long.class) {
                return LongDeserializer.wrapperInstance;
            }
            if (rawType == Double.class) {
                return DoubleDeserializer.wrapperInstance;
            }
            if (rawType == Character.class) {
                return CharacterDeserializer.wrapperInstance;
            }
            if (rawType == Byte.class) {
                return ByteDeserializer.wrapperInstance;
            }
            if (rawType == Short.class) {
                return ShortDeserializer.wrapperInstance;
            }
            if (rawType == Float.class) {
                return FloatDeserializer.wrapperInstance;
            }
            if (rawType == Number.class) {
                return NumberDeserializer.instance;
            }
            if (rawType == BigDecimal.class) {
                return BigDecimalDeserializer.instance;
            }
            if (rawType == BigInteger.class) {
                return BigIntegerDeserializer.instance;
            }
        }
        throw new IllegalArgumentException("Internal error: can't find deserializer for " + rawType.getName());
    }
    
    static {
        _classNames = new HashSet<String>();
        final Class[] array;
        final Class<?>[] numberTypes = (Class<?>[])(array = new Class[] { Boolean.class, Byte.class, Short.class, Character.class, Integer.class, Long.class, Float.class, Double.class, Number.class, BigDecimal.class, BigInteger.class });
        for (final Class<?> cls : array) {
            NumberDeserializers._classNames.add(cls.getName());
        }
    }
    
    protected abstract static class PrimitiveOrWrapperDeserializer<T> extends StdScalarDeserializer<T>
    {
        private static final long serialVersionUID = 1L;
        protected final T _nullValue;
        protected final T _emptyValue;
        protected final boolean _primitive;
        
        protected PrimitiveOrWrapperDeserializer(final Class<T> vc, final T nvl, final T empty) {
            super(vc);
            this._nullValue = nvl;
            this._emptyValue = empty;
            this._primitive = vc.isPrimitive();
        }
        
        @Override
        public AccessPattern getNullAccessPattern() {
            if (this._primitive) {
                return AccessPattern.DYNAMIC;
            }
            if (this._nullValue == null) {
                return AccessPattern.ALWAYS_NULL;
            }
            return AccessPattern.CONSTANT;
        }
        
        @Override
        public final T getNullValue(final DeserializationContext ctxt) throws JsonMappingException {
            if (this._primitive && ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
                ctxt.reportInputMismatch(this, "Cannot map `null` into type %s (set DeserializationConfig.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES to 'false' to allow)", this.handledType().toString());
            }
            return this._nullValue;
        }
        
        @Override
        public Object getEmptyValue(final DeserializationContext ctxt) throws JsonMappingException {
            return this._emptyValue;
        }
    }
    
    @JacksonStdImpl
    public static final class BooleanDeserializer extends PrimitiveOrWrapperDeserializer<Boolean>
    {
        private static final long serialVersionUID = 1L;
        static final BooleanDeserializer primitiveInstance;
        static final BooleanDeserializer wrapperInstance;
        
        public BooleanDeserializer(final Class<Boolean> cls, final Boolean nvl) {
            super(cls, nvl, Boolean.FALSE);
        }
        
        @Override
        public Boolean deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            final JsonToken t = p.getCurrentToken();
            if (t == JsonToken.VALUE_TRUE) {
                return Boolean.TRUE;
            }
            if (t == JsonToken.VALUE_FALSE) {
                return Boolean.FALSE;
            }
            return this._parseBoolean(p, ctxt);
        }
        
        @Override
        public Boolean deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
            final JsonToken t = p.getCurrentToken();
            if (t == JsonToken.VALUE_TRUE) {
                return Boolean.TRUE;
            }
            if (t == JsonToken.VALUE_FALSE) {
                return Boolean.FALSE;
            }
            return this._parseBoolean(p, ctxt);
        }
        
        protected final Boolean _parseBoolean(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            final JsonToken t = p.getCurrentToken();
            if (t == JsonToken.VALUE_NULL) {
                return (Boolean)this._coerceNullToken(ctxt, this._primitive);
            }
            if (t == JsonToken.START_ARRAY) {
                return this._deserializeFromArray(p, ctxt);
            }
            if (t == JsonToken.VALUE_NUMBER_INT) {
                return this._parseBooleanFromInt(p, ctxt);
            }
            if (t == JsonToken.VALUE_STRING) {
                final String text = p.getText().trim();
                if ("true".equals(text) || "True".equals(text)) {
                    this._verifyStringForScalarCoercion(ctxt, text);
                    return Boolean.TRUE;
                }
                if ("false".equals(text) || "False".equals(text)) {
                    this._verifyStringForScalarCoercion(ctxt, text);
                    return Boolean.FALSE;
                }
                if (text.length() == 0) {
                    return (Boolean)this._coerceEmptyString(ctxt, this._primitive);
                }
                if (this._hasTextualNull(text)) {
                    return (Boolean)this._coerceTextualNull(ctxt, this._primitive);
                }
                return (Boolean)ctxt.handleWeirdStringValue(this._valueClass, text, "only \"true\" or \"false\" recognized", new Object[0]);
            }
            else {
                if (t == JsonToken.VALUE_TRUE) {
                    return Boolean.TRUE;
                }
                if (t == JsonToken.VALUE_FALSE) {
                    return Boolean.FALSE;
                }
                return (Boolean)ctxt.handleUnexpectedToken(this._valueClass, p);
            }
        }
        
        static {
            primitiveInstance = new BooleanDeserializer(Boolean.TYPE, Boolean.FALSE);
            wrapperInstance = new BooleanDeserializer(Boolean.class, null);
        }
    }
    
    @JacksonStdImpl
    public static class ByteDeserializer extends PrimitiveOrWrapperDeserializer<Byte>
    {
        private static final long serialVersionUID = 1L;
        static final ByteDeserializer primitiveInstance;
        static final ByteDeserializer wrapperInstance;
        
        public ByteDeserializer(final Class<Byte> cls, final Byte nvl) {
            super(cls, nvl, (Byte)0);
        }
        
        @Override
        public Byte deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                return p.getByteValue();
            }
            return this._parseByte(p, ctxt);
        }
        
        protected Byte _parseByte(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            final JsonToken t = p.getCurrentToken();
            if (t == JsonToken.VALUE_STRING) {
                final String text = p.getText().trim();
                if (this._hasTextualNull(text)) {
                    return (Byte)this._coerceTextualNull(ctxt, this._primitive);
                }
                final int len = text.length();
                if (len == 0) {
                    return (Byte)this._coerceEmptyString(ctxt, this._primitive);
                }
                this._verifyStringForScalarCoercion(ctxt, text);
                int value;
                try {
                    value = NumberInput.parseInt(text);
                }
                catch (IllegalArgumentException iae) {
                    return (Byte)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid Byte value", new Object[0]);
                }
                if (this._byteOverflow(value)) {
                    return (Byte)ctxt.handleWeirdStringValue(this._valueClass, text, "overflow, value cannot be represented as 8-bit value", new Object[0]);
                }
                return (byte)value;
            }
            else {
                if (t == JsonToken.VALUE_NUMBER_FLOAT) {
                    if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
                        this._failDoubleToIntCoercion(p, ctxt, "Byte");
                    }
                    return p.getByteValue();
                }
                if (t == JsonToken.VALUE_NULL) {
                    return (Byte)this._coerceNullToken(ctxt, this._primitive);
                }
                if (t == JsonToken.START_ARRAY) {
                    return this._deserializeFromArray(p, ctxt);
                }
                if (t == JsonToken.VALUE_NUMBER_INT) {
                    return p.getByteValue();
                }
                return (Byte)ctxt.handleUnexpectedToken(this._valueClass, p);
            }
        }
        
        static {
            primitiveInstance = new ByteDeserializer(Byte.TYPE, (Byte)0);
            wrapperInstance = new ByteDeserializer(Byte.class, null);
        }
    }
    
    @JacksonStdImpl
    public static class ShortDeserializer extends PrimitiveOrWrapperDeserializer<Short>
    {
        private static final long serialVersionUID = 1L;
        static final ShortDeserializer primitiveInstance;
        static final ShortDeserializer wrapperInstance;
        
        public ShortDeserializer(final Class<Short> cls, final Short nvl) {
            super(cls, nvl, (Short)0);
        }
        
        @Override
        public Short deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            return this._parseShort(p, ctxt);
        }
        
        protected Short _parseShort(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            final JsonToken t = p.getCurrentToken();
            if (t == JsonToken.VALUE_NUMBER_INT) {
                return p.getShortValue();
            }
            if (t == JsonToken.VALUE_STRING) {
                final String text = p.getText().trim();
                final int len = text.length();
                if (len == 0) {
                    return (Short)this._coerceEmptyString(ctxt, this._primitive);
                }
                if (this._hasTextualNull(text)) {
                    return (Short)this._coerceTextualNull(ctxt, this._primitive);
                }
                this._verifyStringForScalarCoercion(ctxt, text);
                int value;
                try {
                    value = NumberInput.parseInt(text);
                }
                catch (IllegalArgumentException iae) {
                    return (Short)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid Short value", new Object[0]);
                }
                if (this._shortOverflow(value)) {
                    return (Short)ctxt.handleWeirdStringValue(this._valueClass, text, "overflow, value cannot be represented as 16-bit value", new Object[0]);
                }
                return (short)value;
            }
            else {
                if (t == JsonToken.VALUE_NUMBER_FLOAT) {
                    if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
                        this._failDoubleToIntCoercion(p, ctxt, "Short");
                    }
                    return p.getShortValue();
                }
                if (t == JsonToken.VALUE_NULL) {
                    return (Short)this._coerceNullToken(ctxt, this._primitive);
                }
                if (t == JsonToken.START_ARRAY) {
                    return this._deserializeFromArray(p, ctxt);
                }
                return (Short)ctxt.handleUnexpectedToken(this._valueClass, p);
            }
        }
        
        static {
            primitiveInstance = new ShortDeserializer(Short.TYPE, (Short)0);
            wrapperInstance = new ShortDeserializer(Short.class, null);
        }
    }
    
    @JacksonStdImpl
    public static class CharacterDeserializer extends PrimitiveOrWrapperDeserializer<Character>
    {
        private static final long serialVersionUID = 1L;
        static final CharacterDeserializer primitiveInstance;
        static final CharacterDeserializer wrapperInstance;
        
        public CharacterDeserializer(final Class<Character> cls, final Character nvl) {
            super(cls, nvl, '\0');
        }
        
        @Override
        public Character deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            switch (p.getCurrentTokenId()) {
                case 7: {
                    this._verifyNumberForScalarCoercion(ctxt, p);
                    final int value = p.getIntValue();
                    if (value >= 0 && value <= 65535) {
                        return (char)value;
                    }
                    break;
                }
                case 6: {
                    final String text = p.getText();
                    if (text.length() == 1) {
                        return text.charAt(0);
                    }
                    if (text.length() == 0) {
                        return (Character)this._coerceEmptyString(ctxt, this._primitive);
                    }
                    break;
                }
                case 11: {
                    return (Character)this._coerceNullToken(ctxt, this._primitive);
                }
                case 3: {
                    return this._deserializeFromArray(p, ctxt);
                }
            }
            return (Character)ctxt.handleUnexpectedToken(this._valueClass, p);
        }
        
        static {
            primitiveInstance = new CharacterDeserializer(Character.TYPE, '\0');
            wrapperInstance = new CharacterDeserializer(Character.class, null);
        }
    }
    
    @JacksonStdImpl
    public static final class IntegerDeserializer extends PrimitiveOrWrapperDeserializer<Integer>
    {
        private static final long serialVersionUID = 1L;
        static final IntegerDeserializer primitiveInstance;
        static final IntegerDeserializer wrapperInstance;
        
        public IntegerDeserializer(final Class<Integer> cls, final Integer nvl) {
            super(cls, nvl, 0);
        }
        
        @Override
        public boolean isCachable() {
            return true;
        }
        
        @Override
        public Integer deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                return p.getIntValue();
            }
            return this._parseInteger(p, ctxt);
        }
        
        @Override
        public Integer deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
            if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                return p.getIntValue();
            }
            return this._parseInteger(p, ctxt);
        }
        
        protected final Integer _parseInteger(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            switch (p.getCurrentTokenId()) {
                case 7: {
                    return p.getIntValue();
                }
                case 8: {
                    if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
                        this._failDoubleToIntCoercion(p, ctxt, "Integer");
                    }
                    return p.getValueAsInt();
                }
                case 6: {
                    final String text = p.getText().trim();
                    final int len = text.length();
                    if (len == 0) {
                        return (Integer)this._coerceEmptyString(ctxt, this._primitive);
                    }
                    if (this._hasTextualNull(text)) {
                        return (Integer)this._coerceTextualNull(ctxt, this._primitive);
                    }
                    this._verifyStringForScalarCoercion(ctxt, text);
                    try {
                        if (len <= 9) {
                            return NumberInput.parseInt(text);
                        }
                        final long l = Long.parseLong(text);
                        if (this._intOverflow(l)) {
                            return (Integer)ctxt.handleWeirdStringValue(this._valueClass, text, String.format("Overflow: numeric value (%s) out of range of Integer (%d - %d)", text, Integer.MIN_VALUE, Integer.MAX_VALUE), new Object[0]);
                        }
                        return (int)l;
                    }
                    catch (IllegalArgumentException iae) {
                        return (Integer)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid Integer value", new Object[0]);
                    }
                    return (Integer)this._coerceNullToken(ctxt, this._primitive);
                }
                case 11: {
                    return (Integer)this._coerceNullToken(ctxt, this._primitive);
                }
                case 3: {
                    return this._deserializeFromArray(p, ctxt);
                }
                default: {
                    return (Integer)ctxt.handleUnexpectedToken(this._valueClass, p);
                }
            }
        }
        
        static {
            primitiveInstance = new IntegerDeserializer(Integer.TYPE, 0);
            wrapperInstance = new IntegerDeserializer(Integer.class, null);
        }
    }
    
    @JacksonStdImpl
    public static final class LongDeserializer extends PrimitiveOrWrapperDeserializer<Long>
    {
        private static final long serialVersionUID = 1L;
        static final LongDeserializer primitiveInstance;
        static final LongDeserializer wrapperInstance;
        
        public LongDeserializer(final Class<Long> cls, final Long nvl) {
            super(cls, nvl, 0L);
        }
        
        @Override
        public boolean isCachable() {
            return true;
        }
        
        @Override
        public Long deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                return p.getLongValue();
            }
            return this._parseLong(p, ctxt);
        }
        
        protected final Long _parseLong(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            switch (p.getCurrentTokenId()) {
                case 7: {
                    return p.getLongValue();
                }
                case 8: {
                    if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
                        this._failDoubleToIntCoercion(p, ctxt, "Long");
                    }
                    return p.getValueAsLong();
                }
                case 6: {
                    final String text = p.getText().trim();
                    if (text.length() == 0) {
                        return (Long)this._coerceEmptyString(ctxt, this._primitive);
                    }
                    if (this._hasTextualNull(text)) {
                        return (Long)this._coerceTextualNull(ctxt, this._primitive);
                    }
                    this._verifyStringForScalarCoercion(ctxt, text);
                    try {
                        return NumberInput.parseLong(text);
                    }
                    catch (IllegalArgumentException ex) {
                        return (Long)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid Long value", new Object[0]);
                    }
                    return (Long)this._coerceNullToken(ctxt, this._primitive);
                }
                case 11: {
                    return (Long)this._coerceNullToken(ctxt, this._primitive);
                }
                case 3: {
                    return this._deserializeFromArray(p, ctxt);
                }
                default: {
                    return (Long)ctxt.handleUnexpectedToken(this._valueClass, p);
                }
            }
        }
        
        static {
            primitiveInstance = new LongDeserializer(Long.TYPE, 0L);
            wrapperInstance = new LongDeserializer(Long.class, null);
        }
    }
    
    @JacksonStdImpl
    public static class FloatDeserializer extends PrimitiveOrWrapperDeserializer<Float>
    {
        private static final long serialVersionUID = 1L;
        static final FloatDeserializer primitiveInstance;
        static final FloatDeserializer wrapperInstance;
        
        public FloatDeserializer(final Class<Float> cls, final Float nvl) {
            super(cls, nvl, 0.0f);
        }
        
        @Override
        public Float deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            return this._parseFloat(p, ctxt);
        }
        
        protected final Float _parseFloat(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            final JsonToken t = p.getCurrentToken();
            if (t == JsonToken.VALUE_NUMBER_FLOAT || t == JsonToken.VALUE_NUMBER_INT) {
                return p.getFloatValue();
            }
            if (t == JsonToken.VALUE_STRING) {
                final String text = p.getText().trim();
                if (text.length() == 0) {
                    return (Float)this._coerceEmptyString(ctxt, this._primitive);
                }
                if (this._hasTextualNull(text)) {
                    return (Float)this._coerceTextualNull(ctxt, this._primitive);
                }
                switch (text.charAt(0)) {
                    case 'I': {
                        if (this._isPosInf(text)) {
                            return Float.POSITIVE_INFINITY;
                        }
                        break;
                    }
                    case 'N': {
                        if (this._isNaN(text)) {
                            return Float.NaN;
                        }
                        break;
                    }
                    case '-': {
                        if (this._isNegInf(text)) {
                            return Float.NEGATIVE_INFINITY;
                        }
                        break;
                    }
                }
                this._verifyStringForScalarCoercion(ctxt, text);
                try {
                    return Float.parseFloat(text);
                }
                catch (IllegalArgumentException ex) {
                    return (Float)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid Float value", new Object[0]);
                }
            }
            if (t == JsonToken.VALUE_NULL) {
                return (Float)this._coerceNullToken(ctxt, this._primitive);
            }
            if (t == JsonToken.START_ARRAY) {
                return this._deserializeFromArray(p, ctxt);
            }
            return (Float)ctxt.handleUnexpectedToken(this._valueClass, p);
        }
        
        static {
            primitiveInstance = new FloatDeserializer(Float.TYPE, 0.0f);
            wrapperInstance = new FloatDeserializer(Float.class, null);
        }
    }
    
    @JacksonStdImpl
    public static class DoubleDeserializer extends PrimitiveOrWrapperDeserializer<Double>
    {
        private static final long serialVersionUID = 1L;
        static final DoubleDeserializer primitiveInstance;
        static final DoubleDeserializer wrapperInstance;
        
        public DoubleDeserializer(final Class<Double> cls, final Double nvl) {
            super(cls, nvl, 0.0);
        }
        
        @Override
        public Double deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            return this._parseDouble(p, ctxt);
        }
        
        @Override
        public Double deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
            return this._parseDouble(p, ctxt);
        }
        
        protected final Double _parseDouble(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            final JsonToken t = p.getCurrentToken();
            if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
                return p.getDoubleValue();
            }
            if (t == JsonToken.VALUE_STRING) {
                final String text = p.getText().trim();
                if (text.length() == 0) {
                    return (Double)this._coerceEmptyString(ctxt, this._primitive);
                }
                if (this._hasTextualNull(text)) {
                    return (Double)this._coerceTextualNull(ctxt, this._primitive);
                }
                switch (text.charAt(0)) {
                    case 'I': {
                        if (this._isPosInf(text)) {
                            return Double.POSITIVE_INFINITY;
                        }
                        break;
                    }
                    case 'N': {
                        if (this._isNaN(text)) {
                            return Double.NaN;
                        }
                        break;
                    }
                    case '-': {
                        if (this._isNegInf(text)) {
                            return Double.NEGATIVE_INFINITY;
                        }
                        break;
                    }
                }
                this._verifyStringForScalarCoercion(ctxt, text);
                try {
                    return StdDeserializer.parseDouble(text);
                }
                catch (IllegalArgumentException ex) {
                    return (Double)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid Double value", new Object[0]);
                }
            }
            if (t == JsonToken.VALUE_NULL) {
                return (Double)this._coerceNullToken(ctxt, this._primitive);
            }
            if (t == JsonToken.START_ARRAY) {
                return this._deserializeFromArray(p, ctxt);
            }
            return (Double)ctxt.handleUnexpectedToken(this._valueClass, p);
        }
        
        static {
            primitiveInstance = new DoubleDeserializer(Double.TYPE, 0.0);
            wrapperInstance = new DoubleDeserializer(Double.class, null);
        }
    }
    
    @JacksonStdImpl
    public static class NumberDeserializer extends StdScalarDeserializer<Object>
    {
        public static final NumberDeserializer instance;
        
        public NumberDeserializer() {
            super(Number.class);
        }
        
        @Override
        public Object deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            switch (p.getCurrentTokenId()) {
                case 7: {
                    if (ctxt.hasSomeOfFeatures(NumberDeserializer.F_MASK_INT_COERCIONS)) {
                        return this._coerceIntegral(p, ctxt);
                    }
                    return p.getNumberValue();
                }
                case 8: {
                    if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS) && !p.isNaN()) {
                        return p.getDecimalValue();
                    }
                    return p.getNumberValue();
                }
                case 6: {
                    final String text = p.getText().trim();
                    if (text.length() == 0) {
                        return this.getNullValue(ctxt);
                    }
                    if (this._hasTextualNull(text)) {
                        return this.getNullValue(ctxt);
                    }
                    if (this._isPosInf(text)) {
                        return Double.POSITIVE_INFINITY;
                    }
                    if (this._isNegInf(text)) {
                        return Double.NEGATIVE_INFINITY;
                    }
                    if (this._isNaN(text)) {
                        return Double.NaN;
                    }
                    this._verifyStringForScalarCoercion(ctxt, text);
                    try {
                        if (!this._isIntNumber(text)) {
                            if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                                return new BigDecimal(text);
                            }
                            return Double.valueOf(text);
                        }
                        else {
                            if (ctxt.isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)) {
                                return new BigInteger(text);
                            }
                            final long value = Long.parseLong(text);
                            if (!ctxt.isEnabled(DeserializationFeature.USE_LONG_FOR_INTS) && value <= 2147483647L && value >= -2147483648L) {
                                return (int)value;
                            }
                            return value;
                        }
                    }
                    catch (IllegalArgumentException iae) {
                        return ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid number", new Object[0]);
                    }
                    return this._deserializeFromArray(p, ctxt);
                }
                case 3: {
                    return this._deserializeFromArray(p, ctxt);
                }
                default: {
                    return ctxt.handleUnexpectedToken(this._valueClass, p);
                }
            }
        }
        
        @Override
        public Object deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
            switch (p.getCurrentTokenId()) {
                case 6:
                case 7:
                case 8: {
                    return this.deserialize(p, ctxt);
                }
                default: {
                    return typeDeserializer.deserializeTypedFromScalar(p, ctxt);
                }
            }
        }
        
        static {
            instance = new NumberDeserializer();
        }
    }
    
    @JacksonStdImpl
    public static class BigIntegerDeserializer extends StdScalarDeserializer<BigInteger>
    {
        public static final BigIntegerDeserializer instance;
        
        public BigIntegerDeserializer() {
            super(BigInteger.class);
        }
        
        @Override
        public Object getEmptyValue(final DeserializationContext ctxt) {
            return BigInteger.ZERO;
        }
        
        @Override
        public BigInteger deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            Label_0191: {
                switch (p.getCurrentTokenId()) {
                    case 7: {
                        switch (p.getNumberType()) {
                            case INT:
                            case LONG:
                            case BIG_INTEGER: {
                                return p.getBigIntegerValue();
                            }
                            default: {
                                break Label_0191;
                            }
                        }
                        break;
                    }
                    case 8: {
                        if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
                            this._failDoubleToIntCoercion(p, ctxt, "java.math.BigInteger");
                        }
                        return p.getDecimalValue().toBigInteger();
                    }
                    case 3: {
                        return this._deserializeFromArray(p, ctxt);
                    }
                    case 6: {
                        final String text = p.getText().trim();
                        if (this._isEmptyOrTextualNull(text)) {
                            this._verifyNullForScalarCoercion(ctxt, text);
                            return this.getNullValue(ctxt);
                        }
                        this._verifyStringForScalarCoercion(ctxt, text);
                        try {
                            return new BigInteger(text);
                        }
                        catch (IllegalArgumentException ex) {
                            return (BigInteger)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid representation", new Object[0]);
                        }
                        break;
                    }
                }
            }
            return (BigInteger)ctxt.handleUnexpectedToken(this._valueClass, p);
        }
        
        static {
            instance = new BigIntegerDeserializer();
        }
    }
    
    @JacksonStdImpl
    public static class BigDecimalDeserializer extends StdScalarDeserializer<BigDecimal>
    {
        public static final BigDecimalDeserializer instance;
        
        public BigDecimalDeserializer() {
            super(BigDecimal.class);
        }
        
        @Override
        public Object getEmptyValue(final DeserializationContext ctxt) {
            return BigDecimal.ZERO;
        }
        
        @Override
        public BigDecimal deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            switch (p.getCurrentTokenId()) {
                case 7:
                case 8: {
                    return p.getDecimalValue();
                }
                case 6: {
                    final String text = p.getText().trim();
                    if (this._isEmptyOrTextualNull(text)) {
                        this._verifyNullForScalarCoercion(ctxt, text);
                        return this.getNullValue(ctxt);
                    }
                    this._verifyStringForScalarCoercion(ctxt, text);
                    try {
                        return new BigDecimal(text);
                    }
                    catch (IllegalArgumentException ex) {
                        return (BigDecimal)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid representation", new Object[0]);
                    }
                    return this._deserializeFromArray(p, ctxt);
                }
                case 3: {
                    return this._deserializeFromArray(p, ctxt);
                }
                default: {
                    return (BigDecimal)ctxt.handleUnexpectedToken(this._valueClass, p);
                }
            }
        }
        
        static {
            instance = new BigDecimalDeserializer();
        }
    }
}
