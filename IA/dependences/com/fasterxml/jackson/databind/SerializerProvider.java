// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.databind.ser.impl.FailingSerializer;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.ser.ResolvableSerializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import java.lang.reflect.Type;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.util.Date;
import java.io.IOException;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.ser.impl.UnknownSerializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.impl.TypeWrappedSerializer;
import com.fasterxml.jackson.databind.ser.impl.WritableObjectId;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import java.util.TimeZone;
import java.util.Locale;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import java.text.DateFormat;
import com.fasterxml.jackson.databind.ser.impl.ReadOnlyClassToSerializerMap;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import com.fasterxml.jackson.databind.ser.SerializerCache;
import com.fasterxml.jackson.databind.ser.SerializerFactory;

public abstract class SerializerProvider extends DatabindContext
{
    protected static final boolean CACHE_UNKNOWN_MAPPINGS = false;
    public static final JsonSerializer<Object> DEFAULT_NULL_KEY_SERIALIZER;
    protected static final JsonSerializer<Object> DEFAULT_UNKNOWN_SERIALIZER;
    protected final SerializationConfig _config;
    protected final Class<?> _serializationView;
    protected final SerializerFactory _serializerFactory;
    protected final SerializerCache _serializerCache;
    protected transient ContextAttributes _attributes;
    protected JsonSerializer<Object> _unknownTypeSerializer;
    protected JsonSerializer<Object> _keySerializer;
    protected JsonSerializer<Object> _nullValueSerializer;
    protected JsonSerializer<Object> _nullKeySerializer;
    protected final ReadOnlyClassToSerializerMap _knownSerializers;
    protected DateFormat _dateFormat;
    protected final boolean _stdNullValueSerializer;
    
    public SerializerProvider() {
        this._unknownTypeSerializer = SerializerProvider.DEFAULT_UNKNOWN_SERIALIZER;
        this._nullValueSerializer = NullSerializer.instance;
        this._nullKeySerializer = SerializerProvider.DEFAULT_NULL_KEY_SERIALIZER;
        this._config = null;
        this._serializerFactory = null;
        this._serializerCache = new SerializerCache();
        this._knownSerializers = null;
        this._serializationView = null;
        this._attributes = null;
        this._stdNullValueSerializer = true;
    }
    
    protected SerializerProvider(final SerializerProvider src, final SerializationConfig config, final SerializerFactory f) {
        this._unknownTypeSerializer = SerializerProvider.DEFAULT_UNKNOWN_SERIALIZER;
        this._nullValueSerializer = NullSerializer.instance;
        this._nullKeySerializer = SerializerProvider.DEFAULT_NULL_KEY_SERIALIZER;
        this._serializerFactory = f;
        this._config = config;
        this._serializerCache = src._serializerCache;
        this._unknownTypeSerializer = src._unknownTypeSerializer;
        this._keySerializer = src._keySerializer;
        this._nullValueSerializer = src._nullValueSerializer;
        this._nullKeySerializer = src._nullKeySerializer;
        this._stdNullValueSerializer = (this._nullValueSerializer == SerializerProvider.DEFAULT_NULL_KEY_SERIALIZER);
        this._serializationView = config.getActiveView();
        this._attributes = config.getAttributes();
        this._knownSerializers = this._serializerCache.getReadOnlyLookupMap();
    }
    
    protected SerializerProvider(final SerializerProvider src) {
        this._unknownTypeSerializer = SerializerProvider.DEFAULT_UNKNOWN_SERIALIZER;
        this._nullValueSerializer = NullSerializer.instance;
        this._nullKeySerializer = SerializerProvider.DEFAULT_NULL_KEY_SERIALIZER;
        this._config = null;
        this._serializationView = null;
        this._serializerFactory = null;
        this._knownSerializers = null;
        this._serializerCache = new SerializerCache();
        this._unknownTypeSerializer = src._unknownTypeSerializer;
        this._keySerializer = src._keySerializer;
        this._nullValueSerializer = src._nullValueSerializer;
        this._nullKeySerializer = src._nullKeySerializer;
        this._stdNullValueSerializer = src._stdNullValueSerializer;
    }
    
    public void setDefaultKeySerializer(final JsonSerializer<Object> ks) {
        if (ks == null) {
            throw new IllegalArgumentException("Cannot pass null JsonSerializer");
        }
        this._keySerializer = ks;
    }
    
    public void setNullValueSerializer(final JsonSerializer<Object> nvs) {
        if (nvs == null) {
            throw new IllegalArgumentException("Cannot pass null JsonSerializer");
        }
        this._nullValueSerializer = nvs;
    }
    
    public void setNullKeySerializer(final JsonSerializer<Object> nks) {
        if (nks == null) {
            throw new IllegalArgumentException("Cannot pass null JsonSerializer");
        }
        this._nullKeySerializer = nks;
    }
    
    @Override
    public final SerializationConfig getConfig() {
        return this._config;
    }
    
    @Override
    public final AnnotationIntrospector getAnnotationIntrospector() {
        return this._config.getAnnotationIntrospector();
    }
    
    @Override
    public final TypeFactory getTypeFactory() {
        return this._config.getTypeFactory();
    }
    
    @Override
    public final Class<?> getActiveView() {
        return this._serializationView;
    }
    
    @Deprecated
    public final Class<?> getSerializationView() {
        return this._serializationView;
    }
    
    @Override
    public final boolean canOverrideAccessModifiers() {
        return this._config.canOverrideAccessModifiers();
    }
    
    @Override
    public final boolean isEnabled(final MapperFeature feature) {
        return this._config.isEnabled(feature);
    }
    
    @Override
    public final JsonFormat.Value getDefaultPropertyFormat(final Class<?> baseType) {
        return this._config.getDefaultPropertyFormat(baseType);
    }
    
    public final JsonInclude.Value getDefaultPropertyInclusion(final Class<?> baseType) {
        return this._config.getDefaultPropertyInclusion();
    }
    
    @Override
    public Locale getLocale() {
        return this._config.getLocale();
    }
    
    @Override
    public TimeZone getTimeZone() {
        return this._config.getTimeZone();
    }
    
    @Override
    public Object getAttribute(final Object key) {
        return this._attributes.getAttribute(key);
    }
    
    @Override
    public SerializerProvider setAttribute(final Object key, final Object value) {
        this._attributes = this._attributes.withPerCallAttribute(key, value);
        return this;
    }
    
    public final boolean isEnabled(final SerializationFeature feature) {
        return this._config.isEnabled(feature);
    }
    
    public final boolean hasSerializationFeatures(final int featureMask) {
        return this._config.hasSerializationFeatures(featureMask);
    }
    
    public final FilterProvider getFilterProvider() {
        return this._config.getFilterProvider();
    }
    
    public JsonGenerator getGenerator() {
        return null;
    }
    
    public abstract WritableObjectId findObjectId(final Object p0, final ObjectIdGenerator<?> p1);
    
    public JsonSerializer<Object> findValueSerializer(final Class<?> valueType, final BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
        if (ser == null) {
            ser = this._serializerCache.untypedValueSerializer(valueType);
            if (ser == null) {
                ser = this._serializerCache.untypedValueSerializer(this._config.constructType(valueType));
                if (ser == null) {
                    ser = this._createAndCacheUntypedSerializer(valueType);
                    if (ser == null) {
                        ser = this.getUnknownTypeSerializer(valueType);
                        return ser;
                    }
                }
            }
        }
        return (JsonSerializer<Object>)this.handleSecondaryContextualization(ser, property);
    }
    
    public JsonSerializer<Object> findValueSerializer(final JavaType valueType, final BeanProperty property) throws JsonMappingException {
        if (valueType == null) {
            this.reportMappingProblem("Null passed for `valueType` of `findValueSerializer()`", new Object[0]);
        }
        JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
        if (ser == null) {
            ser = this._serializerCache.untypedValueSerializer(valueType);
            if (ser == null) {
                ser = this._createAndCacheUntypedSerializer(valueType);
                if (ser == null) {
                    ser = this.getUnknownTypeSerializer(valueType.getRawClass());
                    return ser;
                }
            }
        }
        return (JsonSerializer<Object>)this.handleSecondaryContextualization(ser, property);
    }
    
    public JsonSerializer<Object> findValueSerializer(final Class<?> valueType) throws JsonMappingException {
        JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
        if (ser == null) {
            ser = this._serializerCache.untypedValueSerializer(valueType);
            if (ser == null) {
                ser = this._serializerCache.untypedValueSerializer(this._config.constructType(valueType));
                if (ser == null) {
                    ser = this._createAndCacheUntypedSerializer(valueType);
                    if (ser == null) {
                        ser = this.getUnknownTypeSerializer(valueType);
                    }
                }
            }
        }
        return ser;
    }
    
    public JsonSerializer<Object> findValueSerializer(final JavaType valueType) throws JsonMappingException {
        JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
        if (ser == null) {
            ser = this._serializerCache.untypedValueSerializer(valueType);
            if (ser == null) {
                ser = this._createAndCacheUntypedSerializer(valueType);
                if (ser == null) {
                    ser = this.getUnknownTypeSerializer(valueType.getRawClass());
                }
            }
        }
        return ser;
    }
    
    public JsonSerializer<Object> findPrimaryPropertySerializer(final JavaType valueType, final BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
        if (ser == null) {
            ser = this._serializerCache.untypedValueSerializer(valueType);
            if (ser == null) {
                ser = this._createAndCacheUntypedSerializer(valueType);
                if (ser == null) {
                    ser = this.getUnknownTypeSerializer(valueType.getRawClass());
                    return ser;
                }
            }
        }
        return (JsonSerializer<Object>)this.handlePrimaryContextualization(ser, property);
    }
    
    public JsonSerializer<Object> findPrimaryPropertySerializer(final Class<?> valueType, final BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
        if (ser == null) {
            ser = this._serializerCache.untypedValueSerializer(valueType);
            if (ser == null) {
                ser = this._serializerCache.untypedValueSerializer(this._config.constructType(valueType));
                if (ser == null) {
                    ser = this._createAndCacheUntypedSerializer(valueType);
                    if (ser == null) {
                        ser = this.getUnknownTypeSerializer(valueType);
                        return ser;
                    }
                }
            }
        }
        return (JsonSerializer<Object>)this.handlePrimaryContextualization(ser, property);
    }
    
    public JsonSerializer<Object> findTypedValueSerializer(final Class<?> valueType, final boolean cache, final BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> ser = this._knownSerializers.typedValueSerializer(valueType);
        if (ser != null) {
            return ser;
        }
        ser = this._serializerCache.typedValueSerializer(valueType);
        if (ser != null) {
            return ser;
        }
        ser = this.findValueSerializer(valueType, property);
        TypeSerializer typeSer = this._serializerFactory.createTypeSerializer(this._config, this._config.constructType(valueType));
        if (typeSer != null) {
            typeSer = typeSer.forProperty(property);
            ser = new TypeWrappedSerializer(typeSer, ser);
        }
        if (cache) {
            this._serializerCache.addTypedSerializer(valueType, ser);
        }
        return ser;
    }
    
    public JsonSerializer<Object> findTypedValueSerializer(final JavaType valueType, final boolean cache, final BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> ser = this._knownSerializers.typedValueSerializer(valueType);
        if (ser != null) {
            return ser;
        }
        ser = this._serializerCache.typedValueSerializer(valueType);
        if (ser != null) {
            return ser;
        }
        ser = this.findValueSerializer(valueType, property);
        TypeSerializer typeSer = this._serializerFactory.createTypeSerializer(this._config, valueType);
        if (typeSer != null) {
            typeSer = typeSer.forProperty(property);
            ser = new TypeWrappedSerializer(typeSer, ser);
        }
        if (cache) {
            this._serializerCache.addTypedSerializer(valueType, ser);
        }
        return ser;
    }
    
    public TypeSerializer findTypeSerializer(final JavaType javaType) throws JsonMappingException {
        return this._serializerFactory.createTypeSerializer(this._config, javaType);
    }
    
    public JsonSerializer<Object> findKeySerializer(final JavaType keyType, final BeanProperty property) throws JsonMappingException {
        final JsonSerializer<Object> ser = this._serializerFactory.createKeySerializer(this._config, keyType, this._keySerializer);
        return this._handleContextualResolvable(ser, property);
    }
    
    public JsonSerializer<Object> findKeySerializer(final Class<?> rawKeyType, final BeanProperty property) throws JsonMappingException {
        return this.findKeySerializer(this._config.constructType(rawKeyType), property);
    }
    
    public JsonSerializer<Object> getDefaultNullKeySerializer() {
        return this._nullKeySerializer;
    }
    
    public JsonSerializer<Object> getDefaultNullValueSerializer() {
        return this._nullValueSerializer;
    }
    
    public JsonSerializer<Object> findNullKeySerializer(final JavaType serializationType, final BeanProperty property) throws JsonMappingException {
        return this._nullKeySerializer;
    }
    
    public JsonSerializer<Object> findNullValueSerializer(final BeanProperty property) throws JsonMappingException {
        return this._nullValueSerializer;
    }
    
    public JsonSerializer<Object> getUnknownTypeSerializer(final Class<?> unknownType) {
        if (unknownType == Object.class) {
            return this._unknownTypeSerializer;
        }
        return new UnknownSerializer(unknownType);
    }
    
    public boolean isUnknownTypeSerializer(final JsonSerializer<?> ser) {
        return ser == this._unknownTypeSerializer || ser == null || (this.isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS) && ser.getClass() == UnknownSerializer.class);
    }
    
    public abstract JsonSerializer<Object> serializerInstance(final Annotated p0, final Object p1) throws JsonMappingException;
    
    public abstract Object includeFilterInstance(final BeanPropertyDefinition p0, final Class<?> p1) throws JsonMappingException;
    
    public abstract boolean includeFilterSuppressNulls(final Object p0) throws JsonMappingException;
    
    public JsonSerializer<?> handlePrimaryContextualization(JsonSerializer<?> ser, final BeanProperty property) throws JsonMappingException {
        if (ser != null && ser instanceof ContextualSerializer) {
            ser = ((ContextualSerializer)ser).createContextual(this, property);
        }
        return ser;
    }
    
    public JsonSerializer<?> handleSecondaryContextualization(JsonSerializer<?> ser, final BeanProperty property) throws JsonMappingException {
        if (ser != null && ser instanceof ContextualSerializer) {
            ser = ((ContextualSerializer)ser).createContextual(this, property);
        }
        return ser;
    }
    
    public final void defaultSerializeValue(final Object value, final JsonGenerator gen) throws IOException {
        if (value == null) {
            if (this._stdNullValueSerializer) {
                gen.writeNull();
            }
            else {
                this._nullValueSerializer.serialize(null, gen, this);
            }
        }
        else {
            final Class<?> cls = value.getClass();
            this.findTypedValueSerializer(cls, true, null).serialize(value, gen, this);
        }
    }
    
    public final void defaultSerializeField(final String fieldName, final Object value, final JsonGenerator gen) throws IOException {
        gen.writeFieldName(fieldName);
        if (value == null) {
            if (this._stdNullValueSerializer) {
                gen.writeNull();
            }
            else {
                this._nullValueSerializer.serialize(null, gen, this);
            }
        }
        else {
            final Class<?> cls = value.getClass();
            this.findTypedValueSerializer(cls, true, null).serialize(value, gen, this);
        }
    }
    
    public final void defaultSerializeDateValue(final long timestamp, final JsonGenerator gen) throws IOException {
        if (this.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)) {
            gen.writeNumber(timestamp);
        }
        else {
            gen.writeString(this._dateFormat().format(new Date(timestamp)));
        }
    }
    
    public final void defaultSerializeDateValue(final Date date, final JsonGenerator gen) throws IOException {
        if (this.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)) {
            gen.writeNumber(date.getTime());
        }
        else {
            gen.writeString(this._dateFormat().format(date));
        }
    }
    
    public void defaultSerializeDateKey(final long timestamp, final JsonGenerator gen) throws IOException {
        if (this.isEnabled(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)) {
            gen.writeFieldName(String.valueOf(timestamp));
        }
        else {
            gen.writeFieldName(this._dateFormat().format(new Date(timestamp)));
        }
    }
    
    public void defaultSerializeDateKey(final Date date, final JsonGenerator gen) throws IOException {
        if (this.isEnabled(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)) {
            gen.writeFieldName(String.valueOf(date.getTime()));
        }
        else {
            gen.writeFieldName(this._dateFormat().format(date));
        }
    }
    
    public final void defaultSerializeNull(final JsonGenerator gen) throws IOException {
        if (this._stdNullValueSerializer) {
            gen.writeNull();
        }
        else {
            this._nullValueSerializer.serialize(null, gen, this);
        }
    }
    
    public void reportMappingProblem(final String message, final Object... args) throws JsonMappingException {
        throw this.mappingException(message, args);
    }
    
    public <T> T reportBadTypeDefinition(final BeanDescription bean, String msg, final Object... msgArgs) throws JsonMappingException {
        String beanDesc = "N/A";
        if (bean != null) {
            beanDesc = ClassUtil.nameOf(bean.getBeanClass());
        }
        msg = String.format("Invalid type definition for type %s: %s", beanDesc, this._format(msg, msgArgs));
        throw InvalidDefinitionException.from(this.getGenerator(), msg, bean, null);
    }
    
    public <T> T reportBadPropertyDefinition(final BeanDescription bean, final BeanPropertyDefinition prop, String message, final Object... msgArgs) throws JsonMappingException {
        message = this._format(message, msgArgs);
        String propName = "N/A";
        if (prop != null) {
            propName = this._quotedString(prop.getName());
        }
        String beanDesc = "N/A";
        if (bean != null) {
            beanDesc = ClassUtil.nameOf(bean.getBeanClass());
        }
        message = String.format("Invalid definition for property %s (of type %s): %s", propName, beanDesc, message);
        throw InvalidDefinitionException.from(this.getGenerator(), message, bean, prop);
    }
    
    @Override
    public <T> T reportBadDefinition(final JavaType type, final String msg) throws JsonMappingException {
        throw InvalidDefinitionException.from(this.getGenerator(), msg, type);
    }
    
    public <T> T reportBadDefinition(final JavaType type, final String msg, final Throwable cause) throws JsonMappingException {
        final InvalidDefinitionException e = InvalidDefinitionException.from(this.getGenerator(), msg, type);
        e.initCause(cause);
        throw e;
    }
    
    public <T> T reportBadDefinition(final Class<?> raw, final String msg, final Throwable cause) throws JsonMappingException {
        final InvalidDefinitionException e = InvalidDefinitionException.from(this.getGenerator(), msg, this.constructType(raw));
        e.initCause(cause);
        throw e;
    }
    
    public void reportMappingProblem(final Throwable t, String message, final Object... msgArgs) throws JsonMappingException {
        message = this._format(message, msgArgs);
        throw JsonMappingException.from(this.getGenerator(), message, t);
    }
    
    public JsonMappingException invalidTypeIdException(final JavaType baseType, final String typeId, final String extraDesc) {
        final String msg = String.format("Could not resolve type id '%s' as a subtype of %s", typeId, baseType);
        return InvalidTypeIdException.from(null, this._colonConcat(msg, extraDesc), baseType, typeId);
    }
    
    @Deprecated
    public JsonMappingException mappingException(final String message, final Object... msgArgs) {
        return JsonMappingException.from(this.getGenerator(), this._format(message, msgArgs));
    }
    
    @Deprecated
    protected JsonMappingException mappingException(final Throwable t, final String message, final Object... msgArgs) {
        return JsonMappingException.from(this.getGenerator(), this._format(message, msgArgs), t);
    }
    
    protected void _reportIncompatibleRootType(final Object value, final JavaType rootType) throws IOException {
        if (rootType.isPrimitive()) {
            final Class<?> wrapperType = ClassUtil.wrapperType(rootType.getRawClass());
            if (wrapperType.isAssignableFrom(value.getClass())) {
                return;
            }
        }
        this.reportBadDefinition(rootType, String.format("Incompatible types: declared root type (%s) vs %s", rootType, ClassUtil.classNameOf(value)));
    }
    
    protected JsonSerializer<Object> _findExplicitUntypedSerializer(final Class<?> runtimeType) throws JsonMappingException {
        JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(runtimeType);
        if (ser == null) {
            ser = this._serializerCache.untypedValueSerializer(runtimeType);
            if (ser == null) {
                ser = this._createAndCacheUntypedSerializer(runtimeType);
            }
        }
        if (this.isUnknownTypeSerializer(ser)) {
            return null;
        }
        return ser;
    }
    
    protected JsonSerializer<Object> _createAndCacheUntypedSerializer(final Class<?> rawType) throws JsonMappingException {
        final JavaType fullType = this._config.constructType(rawType);
        JsonSerializer<Object> ser;
        try {
            ser = this._createUntypedSerializer(fullType);
        }
        catch (IllegalArgumentException iae) {
            ser = null;
            this.reportMappingProblem(iae, ClassUtil.exceptionMessage(iae), new Object[0]);
        }
        if (ser != null) {
            this._serializerCache.addAndResolveNonTypedSerializer(rawType, fullType, ser, this);
        }
        return ser;
    }
    
    protected JsonSerializer<Object> _createAndCacheUntypedSerializer(final JavaType type) throws JsonMappingException {
        JsonSerializer<Object> ser;
        try {
            ser = this._createUntypedSerializer(type);
        }
        catch (IllegalArgumentException iae) {
            ser = null;
            this.reportMappingProblem(iae, ClassUtil.exceptionMessage(iae), new Object[0]);
        }
        if (ser != null) {
            this._serializerCache.addAndResolveNonTypedSerializer(type, ser, this);
        }
        return ser;
    }
    
    protected JsonSerializer<Object> _createUntypedSerializer(final JavaType type) throws JsonMappingException {
        synchronized (this._serializerCache) {
            return this._serializerFactory.createSerializer(this, type);
        }
    }
    
    protected JsonSerializer<Object> _handleContextualResolvable(final JsonSerializer<?> ser, final BeanProperty property) throws JsonMappingException {
        if (ser instanceof ResolvableSerializer) {
            ((ResolvableSerializer)ser).resolve(this);
        }
        return (JsonSerializer<Object>)this.handleSecondaryContextualization(ser, property);
    }
    
    protected JsonSerializer<Object> _handleResolvable(final JsonSerializer<?> ser) throws JsonMappingException {
        if (ser instanceof ResolvableSerializer) {
            ((ResolvableSerializer)ser).resolve(this);
        }
        return (JsonSerializer<Object>)ser;
    }
    
    protected final DateFormat _dateFormat() {
        if (this._dateFormat != null) {
            return this._dateFormat;
        }
        DateFormat df = this._config.getDateFormat();
        df = (this._dateFormat = (DateFormat)df.clone());
        return df;
    }
    
    static {
        DEFAULT_NULL_KEY_SERIALIZER = new FailingSerializer("Null key for a Map not allowed in JSON (use a converting NullKeySerializer?)");
        DEFAULT_UNKNOWN_SERIALIZER = new UnknownSerializer();
    }
}
