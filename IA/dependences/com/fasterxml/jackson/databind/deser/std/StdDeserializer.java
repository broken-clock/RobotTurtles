// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.impl.NullsAsEmptyProvider;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.deser.impl.NullsFailProvider;
import com.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.BeanProperty;
import java.util.Map;
import java.util.Collection;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.core.JsonParseException;
import java.util.Date;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.JavaType;
import java.io.Serializable;
import com.fasterxml.jackson.databind.JsonDeserializer;

public abstract class StdDeserializer<T> extends JsonDeserializer<T> implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected static final int F_MASK_INT_COERCIONS;
    protected static final int F_MASK_ACCEPT_ARRAYS;
    protected final Class<?> _valueClass;
    
    protected StdDeserializer(final Class<?> vc) {
        this._valueClass = vc;
    }
    
    protected StdDeserializer(final JavaType valueType) {
        this._valueClass = ((valueType == null) ? Object.class : valueType.getRawClass());
    }
    
    protected StdDeserializer(final StdDeserializer<?> src) {
        this._valueClass = src._valueClass;
    }
    
    @Override
    public Class<?> handledType() {
        return this._valueClass;
    }
    
    @Deprecated
    public final Class<?> getValueClass() {
        return this._valueClass;
    }
    
    public JavaType getValueType() {
        return null;
    }
    
    protected boolean isDefaultDeserializer(final JsonDeserializer<?> deserializer) {
        return ClassUtil.isJacksonStdImpl(deserializer);
    }
    
    protected boolean isDefaultKeyDeserializer(final KeyDeserializer keyDeser) {
        return ClassUtil.isJacksonStdImpl(keyDeser);
    }
    
    @Override
    public Object deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromAny(p, ctxt);
    }
    
    protected final boolean _parseBooleanPrimitive(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final JsonToken t = p.getCurrentToken();
        if (t == JsonToken.VALUE_TRUE) {
            return true;
        }
        if (t == JsonToken.VALUE_FALSE) {
            return false;
        }
        if (t == JsonToken.VALUE_NULL) {
            this._verifyNullForPrimitive(ctxt);
            return false;
        }
        if (t == JsonToken.VALUE_NUMBER_INT) {
            return this._parseBooleanFromInt(p, ctxt);
        }
        if (t == JsonToken.VALUE_STRING) {
            final String text = p.getText().trim();
            if ("true".equals(text) || "True".equals(text)) {
                return true;
            }
            if ("false".equals(text) || "False".equals(text)) {
                return false;
            }
            if (this._isEmptyOrTextualNull(text)) {
                this._verifyNullForPrimitiveCoercion(ctxt, text);
                return false;
            }
            final Boolean b = (Boolean)ctxt.handleWeirdStringValue(this._valueClass, text, "only \"true\" or \"false\" recognized", new Object[0]);
            return Boolean.TRUE.equals(b);
        }
        else {
            if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                p.nextToken();
                final boolean parsed = this._parseBooleanPrimitive(p, ctxt);
                this._verifyEndArrayForSingle(p, ctxt);
                return parsed;
            }
            return (boolean)ctxt.handleUnexpectedToken(this._valueClass, p);
        }
    }
    
    protected boolean _parseBooleanFromInt(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        this._verifyNumberForScalarCoercion(ctxt, p);
        return !"0".equals(p.getText());
    }
    
    protected final byte _parseBytePrimitive(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final int value = this._parseIntPrimitive(p, ctxt);
        if (this._byteOverflow(value)) {
            final Number v = (Number)ctxt.handleWeirdStringValue(this._valueClass, String.valueOf(value), "overflow, value cannot be represented as 8-bit value", new Object[0]);
            return this._nonNullNumber(v).byteValue();
        }
        return (byte)value;
    }
    
    protected final short _parseShortPrimitive(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final int value = this._parseIntPrimitive(p, ctxt);
        if (this._shortOverflow(value)) {
            final Number v = (Number)ctxt.handleWeirdStringValue(this._valueClass, String.valueOf(value), "overflow, value cannot be represented as 16-bit value", new Object[0]);
            return this._nonNullNumber(v).shortValue();
        }
        return (short)value;
    }
    
    protected final int _parseIntPrimitive(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            return p.getIntValue();
        }
        switch (p.getCurrentTokenId()) {
            case 6: {
                final String text = p.getText().trim();
                if (this._isEmptyOrTextualNull(text)) {
                    this._verifyNullForPrimitiveCoercion(ctxt, text);
                    return 0;
                }
                return this._parseIntPrimitive(ctxt, text);
            }
            case 8: {
                if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
                    this._failDoubleToIntCoercion(p, ctxt, "int");
                }
                return p.getValueAsInt();
            }
            case 11: {
                this._verifyNullForPrimitive(ctxt);
                return 0;
            }
            case 3: {
                if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                    p.nextToken();
                    final int parsed = this._parseIntPrimitive(p, ctxt);
                    this._verifyEndArrayForSingle(p, ctxt);
                    return parsed;
                }
                break;
            }
        }
        return ((Number)ctxt.handleUnexpectedToken(this._valueClass, p)).intValue();
    }
    
    protected final int _parseIntPrimitive(final DeserializationContext ctxt, final String text) throws IOException {
        try {
            if (text.length() <= 9) {
                return NumberInput.parseInt(text);
            }
            final long l = Long.parseLong(text);
            if (this._intOverflow(l)) {
                final Number v = (Number)ctxt.handleWeirdStringValue(this._valueClass, text, "Overflow: numeric value (%s) out of range of int (%d -%d)", text, Integer.MIN_VALUE, Integer.MAX_VALUE);
                return this._nonNullNumber(v).intValue();
            }
            return (int)l;
        }
        catch (IllegalArgumentException iae) {
            final Number v2 = (Number)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid int value", new Object[0]);
            return this._nonNullNumber(v2).intValue();
        }
    }
    
    protected final long _parseLongPrimitive(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            return p.getLongValue();
        }
        switch (p.getCurrentTokenId()) {
            case 6: {
                final String text = p.getText().trim();
                if (this._isEmptyOrTextualNull(text)) {
                    this._verifyNullForPrimitiveCoercion(ctxt, text);
                    return 0L;
                }
                return this._parseLongPrimitive(ctxt, text);
            }
            case 8: {
                if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
                    this._failDoubleToIntCoercion(p, ctxt, "long");
                }
                return p.getValueAsLong();
            }
            case 11: {
                this._verifyNullForPrimitive(ctxt);
                return 0L;
            }
            case 3: {
                if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                    p.nextToken();
                    final long parsed = this._parseLongPrimitive(p, ctxt);
                    this._verifyEndArrayForSingle(p, ctxt);
                    return parsed;
                }
                break;
            }
        }
        return ((Number)ctxt.handleUnexpectedToken(this._valueClass, p)).longValue();
    }
    
    protected final long _parseLongPrimitive(final DeserializationContext ctxt, final String text) throws IOException {
        try {
            return NumberInput.parseLong(text);
        }
        catch (IllegalArgumentException ex) {
            final Number v = (Number)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid long value", new Object[0]);
            return this._nonNullNumber(v).longValue();
        }
    }
    
    protected final float _parseFloatPrimitive(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
            return p.getFloatValue();
        }
        switch (p.getCurrentTokenId()) {
            case 6: {
                final String text = p.getText().trim();
                if (this._isEmptyOrTextualNull(text)) {
                    this._verifyNullForPrimitiveCoercion(ctxt, text);
                    return 0.0f;
                }
                return this._parseFloatPrimitive(ctxt, text);
            }
            case 7: {
                return p.getFloatValue();
            }
            case 11: {
                this._verifyNullForPrimitive(ctxt);
                return 0.0f;
            }
            case 3: {
                if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                    p.nextToken();
                    final float parsed = this._parseFloatPrimitive(p, ctxt);
                    this._verifyEndArrayForSingle(p, ctxt);
                    return parsed;
                }
                break;
            }
        }
        return ((Number)ctxt.handleUnexpectedToken(this._valueClass, p)).floatValue();
    }
    
    protected final float _parseFloatPrimitive(final DeserializationContext ctxt, final String text) throws IOException {
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
        try {
            return Float.parseFloat(text);
        }
        catch (IllegalArgumentException ex) {
            final Number v = (Number)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid float value", new Object[0]);
            return this._nonNullNumber(v).floatValue();
        }
    }
    
    protected final double _parseDoublePrimitive(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
            return p.getDoubleValue();
        }
        switch (p.getCurrentTokenId()) {
            case 6: {
                final String text = p.getText().trim();
                if (this._isEmptyOrTextualNull(text)) {
                    this._verifyNullForPrimitiveCoercion(ctxt, text);
                    return 0.0;
                }
                return this._parseDoublePrimitive(ctxt, text);
            }
            case 7: {
                return p.getDoubleValue();
            }
            case 11: {
                this._verifyNullForPrimitive(ctxt);
                return 0.0;
            }
            case 3: {
                if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                    p.nextToken();
                    final double parsed = this._parseDoublePrimitive(p, ctxt);
                    this._verifyEndArrayForSingle(p, ctxt);
                    return parsed;
                }
                break;
            }
        }
        return ((Number)ctxt.handleUnexpectedToken(this._valueClass, p)).doubleValue();
    }
    
    protected final double _parseDoublePrimitive(final DeserializationContext ctxt, final String text) throws IOException {
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
        try {
            return parseDouble(text);
        }
        catch (IllegalArgumentException ex) {
            final Number v = (Number)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid double value (as String to convert)", new Object[0]);
            return this._nonNullNumber(v).doubleValue();
        }
    }
    
    protected Date _parseDate(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        switch (p.getCurrentTokenId()) {
            case 6: {
                return this._parseDate(p.getText().trim(), ctxt);
            }
            case 7: {
                long ts;
                try {
                    ts = p.getLongValue();
                }
                catch (JsonParseException e) {
                    final Number v = (Number)ctxt.handleWeirdNumberValue(this._valueClass, p.getNumberValue(), "not a valid 64-bit long for creating `java.util.Date`", new Object[0]);
                    ts = v.longValue();
                }
                return new Date(ts);
            }
            case 11: {
                return this.getNullValue(ctxt);
            }
            case 3: {
                return this._parseDateFromArray(p, ctxt);
            }
            default: {
                return (Date)ctxt.handleUnexpectedToken(this._valueClass, p);
            }
        }
    }
    
    protected Date _parseDateFromArray(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        JsonToken t;
        if (ctxt.hasSomeOfFeatures(StdDeserializer.F_MASK_ACCEPT_ARRAYS)) {
            t = p.nextToken();
            if (t == JsonToken.END_ARRAY && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)) {
                return this.getNullValue(ctxt);
            }
            if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                final Date parsed = this._parseDate(p, ctxt);
                this._verifyEndArrayForSingle(p, ctxt);
                return parsed;
            }
        }
        else {
            t = p.getCurrentToken();
        }
        return (Date)ctxt.handleUnexpectedToken(this._valueClass, t, p, null, new Object[0]);
    }
    
    protected Date _parseDate(final String value, final DeserializationContext ctxt) throws IOException {
        try {
            if (this._isEmptyOrTextualNull(value)) {
                return this.getNullValue(ctxt);
            }
            return ctxt.parseDate(value);
        }
        catch (IllegalArgumentException iae) {
            return (Date)ctxt.handleWeirdStringValue(this._valueClass, value, "not a valid representation (error: %s)", ClassUtil.exceptionMessage(iae));
        }
    }
    
    protected static final double parseDouble(final String numStr) throws NumberFormatException {
        if ("2.2250738585072012e-308".equals(numStr)) {
            return Double.MIN_NORMAL;
        }
        return Double.parseDouble(numStr);
    }
    
    protected final String _parseString(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final JsonToken t = p.getCurrentToken();
        if (t == JsonToken.VALUE_STRING) {
            return p.getText();
        }
        final String value = p.getValueAsString();
        if (value != null) {
            return value;
        }
        return (String)ctxt.handleUnexpectedToken(String.class, p);
    }
    
    protected T _deserializeFromEmpty(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        JsonToken t = p.getCurrentToken();
        if (t == JsonToken.START_ARRAY) {
            if (ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)) {
                t = p.nextToken();
                if (t == JsonToken.END_ARRAY) {
                    return null;
                }
                return (T)ctxt.handleUnexpectedToken(this.handledType(), p);
            }
        }
        else if (t == JsonToken.VALUE_STRING && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
            final String str = p.getText().trim();
            if (str.isEmpty()) {
                return null;
            }
        }
        return (T)ctxt.handleUnexpectedToken(this.handledType(), p);
    }
    
    protected boolean _hasTextualNull(final String value) {
        return "null".equals(value);
    }
    
    protected boolean _isEmptyOrTextualNull(final String value) {
        return value.isEmpty() || "null".equals(value);
    }
    
    protected final boolean _isNegInf(final String text) {
        return "-Infinity".equals(text) || "-INF".equals(text);
    }
    
    protected final boolean _isPosInf(final String text) {
        return "Infinity".equals(text) || "INF".equals(text);
    }
    
    protected final boolean _isNaN(final String text) {
        return "NaN".equals(text);
    }
    
    protected T _deserializeFromArray(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        JsonToken t;
        if (ctxt.hasSomeOfFeatures(StdDeserializer.F_MASK_ACCEPT_ARRAYS)) {
            t = p.nextToken();
            if (t == JsonToken.END_ARRAY && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)) {
                return this.getNullValue(ctxt);
            }
            if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                final T parsed = this.deserialize(p, ctxt);
                if (p.nextToken() != JsonToken.END_ARRAY) {
                    this.handleMissingEndArrayForSingle(p, ctxt);
                }
                return parsed;
            }
        }
        else {
            t = p.getCurrentToken();
        }
        final T result = (T)ctxt.handleUnexpectedToken(this._valueClass, t, p, null, new Object[0]);
        return result;
    }
    
    protected T _deserializeWrappedValue(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.START_ARRAY)) {
            final String msg = String.format("Cannot deserialize instance of %s out of %s token: nested Arrays not allowed with %s", ClassUtil.nameOf(this._valueClass), JsonToken.START_ARRAY, "DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS");
            final T result = (T)ctxt.handleUnexpectedToken(this._valueClass, p.getCurrentToken(), p, msg, new Object[0]);
            return result;
        }
        return this.deserialize(p, ctxt);
    }
    
    protected void _failDoubleToIntCoercion(final JsonParser p, final DeserializationContext ctxt, final String type) throws IOException {
        ctxt.reportInputMismatch(this.handledType(), "Cannot coerce a floating-point value ('%s') into %s (enable `DeserializationFeature.ACCEPT_FLOAT_AS_INT` to allow)", p.getValueAsString(), type);
    }
    
    protected Object _coerceIntegral(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final int feats = ctxt.getDeserializationFeatures();
        if (DeserializationFeature.USE_BIG_INTEGER_FOR_INTS.enabledIn(feats)) {
            return p.getBigIntegerValue();
        }
        if (DeserializationFeature.USE_LONG_FOR_INTS.enabledIn(feats)) {
            return p.getLongValue();
        }
        return p.getBigIntegerValue();
    }
    
    protected Object _coerceNullToken(final DeserializationContext ctxt, final boolean isPrimitive) throws JsonMappingException {
        if (isPrimitive) {
            this._verifyNullForPrimitive(ctxt);
        }
        return this.getNullValue(ctxt);
    }
    
    protected Object _coerceTextualNull(final DeserializationContext ctxt, final boolean isPrimitive) throws JsonMappingException {
        Enum<?> feat;
        boolean enable;
        if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
            feat = MapperFeature.ALLOW_COERCION_OF_SCALARS;
            enable = true;
        }
        else {
            if (!isPrimitive || !ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
                return this.getNullValue(ctxt);
            }
            feat = DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
            enable = false;
        }
        this._reportFailedNullCoerce(ctxt, enable, feat, "String \"null\"");
        return null;
    }
    
    protected Object _coerceEmptyString(final DeserializationContext ctxt, final boolean isPrimitive) throws JsonMappingException {
        Enum<?> feat;
        boolean enable;
        if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
            feat = MapperFeature.ALLOW_COERCION_OF_SCALARS;
            enable = true;
        }
        else {
            if (!isPrimitive || !ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
                return this.getNullValue(ctxt);
            }
            feat = DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
            enable = false;
        }
        this._reportFailedNullCoerce(ctxt, enable, feat, "empty String (\"\")");
        return null;
    }
    
    protected final void _verifyNullForPrimitive(final DeserializationContext ctxt) throws JsonMappingException {
        if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
            ctxt.reportInputMismatch(this, "Cannot coerce `null` %s (disable `DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES` to allow)", this._coercedTypeDesc());
        }
    }
    
    protected final void _verifyNullForPrimitiveCoercion(final DeserializationContext ctxt, final String str) throws JsonMappingException {
        Enum<?> feat;
        boolean enable;
        if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
            feat = MapperFeature.ALLOW_COERCION_OF_SCALARS;
            enable = true;
        }
        else {
            if (!ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
                return;
            }
            feat = DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES;
            enable = false;
        }
        final String strDesc = str.isEmpty() ? "empty String (\"\")" : String.format("String \"%s\"", str);
        this._reportFailedNullCoerce(ctxt, enable, feat, strDesc);
    }
    
    protected final void _verifyNullForScalarCoercion(final DeserializationContext ctxt, final String str) throws JsonMappingException {
        if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
            final String strDesc = str.isEmpty() ? "empty String (\"\")" : String.format("String \"%s\"", str);
            this._reportFailedNullCoerce(ctxt, true, MapperFeature.ALLOW_COERCION_OF_SCALARS, strDesc);
        }
    }
    
    protected void _verifyStringForScalarCoercion(final DeserializationContext ctxt, final String str) throws JsonMappingException {
        final MapperFeature feat = MapperFeature.ALLOW_COERCION_OF_SCALARS;
        if (!ctxt.isEnabled(feat)) {
            ctxt.reportInputMismatch(this, "Cannot coerce String \"%s\" %s (enable `%s.%s` to allow)", str, this._coercedTypeDesc(), feat.getClass().getSimpleName(), feat.name());
        }
    }
    
    protected void _verifyNumberForScalarCoercion(final DeserializationContext ctxt, final JsonParser p) throws IOException {
        final MapperFeature feat = MapperFeature.ALLOW_COERCION_OF_SCALARS;
        if (!ctxt.isEnabled(feat)) {
            final String valueDesc = p.getText();
            ctxt.reportInputMismatch(this, "Cannot coerce Number (%s) %s (enable `%s.%s` to allow)", valueDesc, this._coercedTypeDesc(), feat.getClass().getSimpleName(), feat.name());
        }
    }
    
    protected void _reportFailedNullCoerce(final DeserializationContext ctxt, final boolean state, final Enum<?> feature, final String inputDesc) throws JsonMappingException {
        final String enableDesc = state ? "enable" : "disable";
        ctxt.reportInputMismatch(this, "Cannot coerce %s to Null value %s (%s `%s.%s` to allow)", inputDesc, this._coercedTypeDesc(), enableDesc, feature.getClass().getSimpleName(), feature.name());
    }
    
    protected String _coercedTypeDesc() {
        final JavaType t = this.getValueType();
        boolean structured;
        String typeDesc;
        if (t != null && !t.isPrimitive()) {
            structured = (t.isContainerType() || t.isReferenceType());
            typeDesc = "'" + t.toString() + "'";
        }
        else {
            final Class<?> cls = this.handledType();
            structured = (cls.isArray() || Collection.class.isAssignableFrom(cls) || Map.class.isAssignableFrom(cls));
            typeDesc = ClassUtil.nameOf(cls);
        }
        if (structured) {
            return "as content of type " + typeDesc;
        }
        return "for type " + typeDesc;
    }
    
    protected JsonDeserializer<Object> findDeserializer(final DeserializationContext ctxt, final JavaType type, final BeanProperty property) throws JsonMappingException {
        return ctxt.findContextualValueDeserializer(type, property);
    }
    
    protected final boolean _isIntNumber(final String text) {
        final int len = text.length();
        if (len > 0) {
            final char c = text.charAt(0);
            for (int i = (c == '-' || c == '+') ? 1 : 0; i < len; ++i) {
                final int ch = text.charAt(i);
                if (ch > 57 || ch < 48) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    protected JsonDeserializer<?> findConvertingContentDeserializer(final DeserializationContext ctxt, final BeanProperty prop, JsonDeserializer<?> existingDeserializer) throws JsonMappingException {
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (_neitherNull(intr, prop)) {
            final AnnotatedMember member = prop.getMember();
            if (member != null) {
                final Object convDef = intr.findDeserializationContentConverter(member);
                if (convDef != null) {
                    final Converter<Object, Object> conv = ctxt.converterInstance(prop.getMember(), convDef);
                    final JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
                    if (existingDeserializer == null) {
                        existingDeserializer = ctxt.findContextualValueDeserializer(delegateType, prop);
                    }
                    return new StdDelegatingDeserializer<Object>(conv, delegateType, existingDeserializer);
                }
            }
        }
        return existingDeserializer;
    }
    
    protected JsonFormat.Value findFormatOverrides(final DeserializationContext ctxt, final BeanProperty prop, final Class<?> typeForDefaults) {
        if (prop != null) {
            return prop.findPropertyFormat(ctxt.getConfig(), typeForDefaults);
        }
        return ctxt.getDefaultPropertyFormat(typeForDefaults);
    }
    
    protected Boolean findFormatFeature(final DeserializationContext ctxt, final BeanProperty prop, final Class<?> typeForDefaults, final JsonFormat.Feature feat) {
        final JsonFormat.Value format = this.findFormatOverrides(ctxt, prop, typeForDefaults);
        if (format != null) {
            return format.getFeature(feat);
        }
        return null;
    }
    
    protected final NullValueProvider findValueNullProvider(final DeserializationContext ctxt, final SettableBeanProperty prop, final PropertyMetadata propMetadata) throws JsonMappingException {
        if (prop != null) {
            return this._findNullProvider(ctxt, prop, propMetadata.getValueNulls(), prop.getValueDeserializer());
        }
        return null;
    }
    
    protected NullValueProvider findContentNullProvider(final DeserializationContext ctxt, final BeanProperty prop, final JsonDeserializer<?> valueDeser) throws JsonMappingException {
        final Nulls nulls = this.findContentNullStyle(ctxt, prop);
        if (nulls == Nulls.SKIP) {
            return NullsConstantProvider.skipper();
        }
        final NullValueProvider prov = this._findNullProvider(ctxt, prop, nulls, valueDeser);
        if (prov != null) {
            return prov;
        }
        return valueDeser;
    }
    
    protected Nulls findContentNullStyle(final DeserializationContext ctxt, final BeanProperty prop) throws JsonMappingException {
        if (prop != null) {
            return prop.getMetadata().getContentNulls();
        }
        return null;
    }
    
    protected final NullValueProvider _findNullProvider(final DeserializationContext ctxt, final BeanProperty prop, final Nulls nulls, final JsonDeserializer<?> valueDeser) throws JsonMappingException {
        if (nulls == Nulls.FAIL) {
            if (prop == null) {
                return NullsFailProvider.constructForRootValue(ctxt.constructType(valueDeser.handledType()));
            }
            return NullsFailProvider.constructForProperty(prop);
        }
        else if (nulls == Nulls.AS_EMPTY) {
            if (valueDeser == null) {
                return null;
            }
            if (valueDeser instanceof BeanDeserializerBase) {
                final ValueInstantiator vi = ((BeanDeserializerBase)valueDeser).getValueInstantiator();
                if (!vi.canCreateUsingDefault()) {
                    final JavaType type = prop.getType();
                    ctxt.reportBadDefinition(type, String.format("Cannot create empty instance of %s, no default Creator", type));
                }
            }
            final AccessPattern access = valueDeser.getEmptyAccessPattern();
            if (access == AccessPattern.ALWAYS_NULL) {
                return NullsConstantProvider.nuller();
            }
            if (access == AccessPattern.CONSTANT) {
                return NullsConstantProvider.forValue(valueDeser.getEmptyValue(ctxt));
            }
            return new NullsAsEmptyProvider(valueDeser);
        }
        else {
            if (nulls == Nulls.SKIP) {
                return NullsConstantProvider.skipper();
            }
            return null;
        }
    }
    
    protected void handleUnknownProperty(final JsonParser p, final DeserializationContext ctxt, Object instanceOrClass, final String propName) throws IOException {
        if (instanceOrClass == null) {
            instanceOrClass = this.handledType();
        }
        if (ctxt.handleUnknownProperty(p, this, instanceOrClass, propName)) {
            return;
        }
        p.skipChildren();
    }
    
    protected void handleMissingEndArrayForSingle(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        ctxt.reportWrongTokenException(this, JsonToken.END_ARRAY, "Attempted to unwrap '%s' value from an array (with `DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS`) but it contains more than one value", this.handledType().getName());
    }
    
    protected void _verifyEndArrayForSingle(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final JsonToken t = p.nextToken();
        if (t != JsonToken.END_ARRAY) {
            this.handleMissingEndArrayForSingle(p, ctxt);
        }
    }
    
    protected static final boolean _neitherNull(final Object a, final Object b) {
        return a != null && b != null;
    }
    
    protected final boolean _byteOverflow(final int value) {
        return value < -128 || value > 255;
    }
    
    protected final boolean _shortOverflow(final int value) {
        return value < -32768 || value > 32767;
    }
    
    protected final boolean _intOverflow(final long value) {
        return value < -2147483648L || value > 2147483647L;
    }
    
    protected Number _nonNullNumber(Number n) {
        if (n == null) {
            n = 0;
        }
        return n;
    }
    
    static {
        F_MASK_INT_COERCIONS = (DeserializationFeature.USE_BIG_INTEGER_FOR_INTS.getMask() | DeserializationFeature.USE_LONG_FOR_INTS.getMask());
        F_MASK_ACCEPT_ARRAYS = (DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS.getMask() | DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT.getMask());
    }
}
