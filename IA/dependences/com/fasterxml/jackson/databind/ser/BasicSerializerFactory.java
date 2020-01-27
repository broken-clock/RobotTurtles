// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.databind.ser.std.TokenBufferSerializer;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import com.fasterxml.jackson.databind.ser.std.StdJdkSerializers;
import java.math.BigDecimal;
import java.math.BigInteger;
import com.fasterxml.jackson.databind.ser.std.BooleanSerializer;
import com.fasterxml.jackson.databind.ser.std.NumberSerializers;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.EnumSerializer;
import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import com.fasterxml.jackson.databind.ser.std.IterableSerializer;
import com.fasterxml.jackson.databind.ser.impl.IteratorSerializer;
import com.fasterxml.jackson.databind.ser.std.AtomicReferenceSerializer;
import java.util.concurrent.atomic.AtomicReference;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.ser.std.ObjectArraySerializer;
import com.fasterxml.jackson.databind.ser.std.StdArraySerializers;
import com.fasterxml.jackson.databind.ser.impl.StringArraySerializer;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.ser.impl.MapEntrySerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ser.std.MapSerializer;
import com.fasterxml.jackson.databind.ser.std.EnumSetSerializer;
import com.fasterxml.jackson.databind.ser.std.CollectionSerializer;
import com.fasterxml.jackson.databind.ser.impl.IndexedListSerializer;
import java.util.RandomAccess;
import com.fasterxml.jackson.databind.ser.impl.StringCollectionSerializer;
import com.fasterxml.jackson.databind.ser.impl.IndexedStringListSerializer;
import java.util.EnumSet;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.ext.OptionalHandlerFactory;
import com.fasterxml.jackson.databind.ser.std.NumberSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.nio.charset.Charset;
import com.fasterxml.jackson.databind.ser.std.TimeZoneSerializer;
import java.util.TimeZone;
import com.fasterxml.jackson.databind.ser.std.InetSocketAddressSerializer;
import java.net.InetSocketAddress;
import com.fasterxml.jackson.databind.ser.std.InetAddressSerializer;
import java.net.InetAddress;
import com.fasterxml.jackson.databind.ser.std.ByteBufferSerializer;
import java.nio.ByteBuffer;
import java.util.Map;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import java.util.Date;
import com.fasterxml.jackson.databind.ser.std.CalendarSerializer;
import java.util.Calendar;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.ser.std.SerializableSerializer;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import java.util.Collection;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import java.util.Iterator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.ser.std.JsonValueSerializer;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.cfg.SerializerFactoryConfig;
import com.fasterxml.jackson.databind.JsonSerializer;
import java.util.HashMap;
import java.io.Serializable;

public abstract class BasicSerializerFactory extends SerializerFactory implements Serializable
{
    protected static final HashMap<String, JsonSerializer<?>> _concrete;
    protected static final HashMap<String, Class<? extends JsonSerializer<?>>> _concreteLazy;
    protected final SerializerFactoryConfig _factoryConfig;
    
    protected BasicSerializerFactory(final SerializerFactoryConfig config) {
        this._factoryConfig = ((config == null) ? new SerializerFactoryConfig() : config);
    }
    
    public SerializerFactoryConfig getFactoryConfig() {
        return this._factoryConfig;
    }
    
    public abstract SerializerFactory withConfig(final SerializerFactoryConfig p0);
    
    @Override
    public final SerializerFactory withAdditionalSerializers(final Serializers additional) {
        return this.withConfig(this._factoryConfig.withAdditionalSerializers(additional));
    }
    
    @Override
    public final SerializerFactory withAdditionalKeySerializers(final Serializers additional) {
        return this.withConfig(this._factoryConfig.withAdditionalKeySerializers(additional));
    }
    
    @Override
    public final SerializerFactory withSerializerModifier(final BeanSerializerModifier modifier) {
        return this.withConfig(this._factoryConfig.withSerializerModifier(modifier));
    }
    
    @Override
    public abstract JsonSerializer<Object> createSerializer(final SerializerProvider p0, final JavaType p1) throws JsonMappingException;
    
    @Override
    public JsonSerializer<Object> createKeySerializer(final SerializationConfig config, final JavaType keyType, final JsonSerializer<Object> defaultImpl) {
        BeanDescription beanDesc = config.introspectClassAnnotations(keyType.getRawClass());
        JsonSerializer<?> ser = null;
        if (this._factoryConfig.hasKeySerializers()) {
            for (final Serializers serializers : this._factoryConfig.keySerializers()) {
                ser = serializers.findSerializer(config, keyType, beanDesc);
                if (ser != null) {
                    break;
                }
            }
        }
        if (ser == null) {
            ser = defaultImpl;
            if (ser == null) {
                ser = StdKeySerializers.getStdKeySerializer(config, keyType.getRawClass(), false);
                if (ser == null) {
                    beanDesc = config.introspect(keyType);
                    final AnnotatedMember am = beanDesc.findJsonValueAccessor();
                    if (am != null) {
                        final Class<?> rawType = am.getRawType();
                        final JsonSerializer<?> delegate = StdKeySerializers.getStdKeySerializer(config, rawType, true);
                        if (config.canOverrideAccessModifiers()) {
                            ClassUtil.checkAndFixAccess(am.getMember(), config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
                        }
                        ser = new JsonValueSerializer(am, delegate);
                    }
                    else {
                        ser = StdKeySerializers.getFallbackKeySerializer(config, keyType.getRawClass());
                    }
                }
            }
        }
        if (this._factoryConfig.hasSerializerModifiers()) {
            for (final BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                ser = mod.modifyKeySerializer(config, keyType, beanDesc, ser);
            }
        }
        return (JsonSerializer<Object>)ser;
    }
    
    @Override
    public TypeSerializer createTypeSerializer(final SerializationConfig config, final JavaType baseType) {
        final BeanDescription bean = config.introspectClassAnnotations(baseType.getRawClass());
        final AnnotatedClass ac = bean.getClassInfo();
        final AnnotationIntrospector ai = config.getAnnotationIntrospector();
        TypeResolverBuilder<?> b = ai.findTypeResolver(config, ac, baseType);
        Collection<NamedType> subtypes = null;
        if (b == null) {
            b = config.getDefaultTyper(baseType);
        }
        else {
            subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByClass(config, ac);
        }
        if (b == null) {
            return null;
        }
        return b.buildTypeSerializer(config, baseType, subtypes);
    }
    
    protected abstract Iterable<Serializers> customSerializers();
    
    protected final JsonSerializer<?> findSerializerByLookup(final JavaType type, final SerializationConfig config, final BeanDescription beanDesc, final boolean staticTyping) {
        final Class<?> raw = type.getRawClass();
        final String clsName = raw.getName();
        final JsonSerializer<?> ser = BasicSerializerFactory._concrete.get(clsName);
        if (ser == null) {
            final Class<? extends JsonSerializer<?>> serClass = BasicSerializerFactory._concreteLazy.get(clsName);
            if (serClass != null) {
                return ClassUtil.createInstance(serClass, false);
            }
        }
        return ser;
    }
    
    protected final JsonSerializer<?> findSerializerByAnnotations(final SerializerProvider prov, final JavaType type, final BeanDescription beanDesc) throws JsonMappingException {
        final Class<?> raw = type.getRawClass();
        if (JsonSerializable.class.isAssignableFrom(raw)) {
            return SerializableSerializer.instance;
        }
        final AnnotatedMember valueAccessor = beanDesc.findJsonValueAccessor();
        if (valueAccessor != null) {
            if (prov.canOverrideAccessModifiers()) {
                ClassUtil.checkAndFixAccess(valueAccessor.getMember(), prov.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
            }
            final JsonSerializer<Object> ser = this.findSerializerFromAnnotation(prov, valueAccessor);
            return new JsonValueSerializer(valueAccessor, ser);
        }
        return null;
    }
    
    protected final JsonSerializer<?> findSerializerByPrimaryType(final SerializerProvider prov, final JavaType type, final BeanDescription beanDesc, final boolean staticTyping) throws JsonMappingException {
        final Class<?> raw = type.getRawClass();
        final JsonSerializer<?> ser = this.findOptionalStdSerializer(prov, type, beanDesc, staticTyping);
        if (ser != null) {
            return ser;
        }
        if (Calendar.class.isAssignableFrom(raw)) {
            return CalendarSerializer.instance;
        }
        if (Date.class.isAssignableFrom(raw)) {
            return DateSerializer.instance;
        }
        if (Map.Entry.class.isAssignableFrom(raw)) {
            final JavaType mapEntryType = type.findSuperType(Map.Entry.class);
            final JavaType kt = mapEntryType.containedTypeOrUnknown(0);
            final JavaType vt = mapEntryType.containedTypeOrUnknown(1);
            return this.buildMapEntrySerializer(prov, type, beanDesc, staticTyping, kt, vt);
        }
        if (ByteBuffer.class.isAssignableFrom(raw)) {
            return new ByteBufferSerializer();
        }
        if (InetAddress.class.isAssignableFrom(raw)) {
            return new InetAddressSerializer();
        }
        if (InetSocketAddress.class.isAssignableFrom(raw)) {
            return new InetSocketAddressSerializer();
        }
        if (TimeZone.class.isAssignableFrom(raw)) {
            return new TimeZoneSerializer();
        }
        if (Charset.class.isAssignableFrom(raw)) {
            return ToStringSerializer.instance;
        }
        if (Number.class.isAssignableFrom(raw)) {
            final JsonFormat.Value format = beanDesc.findExpectedFormat(null);
            if (format != null) {
                switch (format.getShape()) {
                    case STRING: {
                        return ToStringSerializer.instance;
                    }
                    case OBJECT:
                    case ARRAY: {
                        return null;
                    }
                }
            }
            return NumberSerializer.instance;
        }
        if (Enum.class.isAssignableFrom(raw)) {
            return this.buildEnumSerializer(prov.getConfig(), type, beanDesc);
        }
        return null;
    }
    
    protected JsonSerializer<?> findOptionalStdSerializer(final SerializerProvider prov, final JavaType type, final BeanDescription beanDesc, final boolean staticTyping) throws JsonMappingException {
        return OptionalHandlerFactory.instance.findSerializer(prov.getConfig(), type, beanDesc);
    }
    
    protected final JsonSerializer<?> findSerializerByAddonType(final SerializationConfig config, final JavaType javaType, final BeanDescription beanDesc, final boolean staticTyping) throws JsonMappingException {
        final Class<?> rawType = javaType.getRawClass();
        if (Iterator.class.isAssignableFrom(rawType)) {
            final JavaType[] params = config.getTypeFactory().findTypeParameters(javaType, Iterator.class);
            final JavaType vt = (params == null || params.length != 1) ? TypeFactory.unknownType() : params[0];
            return this.buildIteratorSerializer(config, javaType, beanDesc, staticTyping, vt);
        }
        if (Iterable.class.isAssignableFrom(rawType)) {
            final JavaType[] params = config.getTypeFactory().findTypeParameters(javaType, Iterable.class);
            final JavaType vt = (params == null || params.length != 1) ? TypeFactory.unknownType() : params[0];
            return this.buildIterableSerializer(config, javaType, beanDesc, staticTyping, vt);
        }
        if (CharSequence.class.isAssignableFrom(rawType)) {
            return ToStringSerializer.instance;
        }
        return null;
    }
    
    protected JsonSerializer<Object> findSerializerFromAnnotation(final SerializerProvider prov, final Annotated a) throws JsonMappingException {
        final Object serDef = prov.getAnnotationIntrospector().findSerializer(a);
        if (serDef == null) {
            return null;
        }
        final JsonSerializer<Object> ser = prov.serializerInstance(a, serDef);
        return (JsonSerializer<Object>)this.findConvertingSerializer(prov, a, ser);
    }
    
    protected JsonSerializer<?> findConvertingSerializer(final SerializerProvider prov, final Annotated a, final JsonSerializer<?> ser) throws JsonMappingException {
        final Converter<Object, Object> conv = this.findConverter(prov, a);
        if (conv == null) {
            return ser;
        }
        final JavaType delegateType = conv.getOutputType(prov.getTypeFactory());
        return new StdDelegatingSerializer(conv, delegateType, ser);
    }
    
    protected Converter<Object, Object> findConverter(final SerializerProvider prov, final Annotated a) throws JsonMappingException {
        final Object convDef = prov.getAnnotationIntrospector().findSerializationConverter(a);
        if (convDef == null) {
            return null;
        }
        return prov.converterInstance(a, convDef);
    }
    
    protected JsonSerializer<?> buildContainerSerializer(final SerializerProvider prov, final JavaType type, final BeanDescription beanDesc, boolean staticTyping) throws JsonMappingException {
        final SerializationConfig config = prov.getConfig();
        if (!staticTyping && type.useStaticType() && (!type.isContainerType() || !type.getContentType().isJavaLangObject())) {
            staticTyping = true;
        }
        final JavaType elementType = type.getContentType();
        final TypeSerializer elementTypeSerializer = this.createTypeSerializer(config, elementType);
        if (elementTypeSerializer != null) {
            staticTyping = false;
        }
        final JsonSerializer<Object> elementValueSerializer = this._findContentSerializer(prov, beanDesc.getClassInfo());
        if (type.isMapLikeType()) {
            final MapLikeType mlt = (MapLikeType)type;
            final JsonSerializer<Object> keySerializer = this._findKeySerializer(prov, beanDesc.getClassInfo());
            if (mlt.isTrueMapType()) {
                return this.buildMapSerializer(prov, (MapType)mlt, beanDesc, staticTyping, keySerializer, elementTypeSerializer, elementValueSerializer);
            }
            JsonSerializer<?> ser = null;
            final MapLikeType mlType = (MapLikeType)type;
            for (final Serializers serializers : this.customSerializers()) {
                ser = serializers.findMapLikeSerializer(config, mlType, beanDesc, keySerializer, elementTypeSerializer, elementValueSerializer);
                if (ser != null) {
                    break;
                }
            }
            if (ser == null) {
                ser = this.findSerializerByAnnotations(prov, type, beanDesc);
            }
            if (ser != null && this._factoryConfig.hasSerializerModifiers()) {
                for (final BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                    ser = mod.modifyMapLikeSerializer(config, mlType, beanDesc, ser);
                }
            }
            return ser;
        }
        else if (type.isCollectionLikeType()) {
            final CollectionLikeType clt = (CollectionLikeType)type;
            if (clt.isTrueCollectionType()) {
                return this.buildCollectionSerializer(prov, (CollectionType)clt, beanDesc, staticTyping, elementTypeSerializer, elementValueSerializer);
            }
            JsonSerializer<?> ser2 = null;
            final CollectionLikeType clType = (CollectionLikeType)type;
            for (final Serializers serializers2 : this.customSerializers()) {
                ser2 = serializers2.findCollectionLikeSerializer(config, clType, beanDesc, elementTypeSerializer, elementValueSerializer);
                if (ser2 != null) {
                    break;
                }
            }
            if (ser2 == null) {
                ser2 = this.findSerializerByAnnotations(prov, type, beanDesc);
            }
            if (ser2 != null && this._factoryConfig.hasSerializerModifiers()) {
                for (final BeanSerializerModifier mod2 : this._factoryConfig.serializerModifiers()) {
                    ser2 = mod2.modifyCollectionLikeSerializer(config, clType, beanDesc, ser2);
                }
            }
            return ser2;
        }
        else {
            if (type.isArrayType()) {
                return this.buildArraySerializer(prov, (ArrayType)type, beanDesc, staticTyping, elementTypeSerializer, elementValueSerializer);
            }
            return null;
        }
    }
    
    protected JsonSerializer<?> buildCollectionSerializer(final SerializerProvider prov, final CollectionType type, final BeanDescription beanDesc, final boolean staticTyping, final TypeSerializer elementTypeSerializer, final JsonSerializer<Object> elementValueSerializer) throws JsonMappingException {
        final SerializationConfig config = prov.getConfig();
        JsonSerializer<?> ser = null;
        for (final Serializers serializers : this.customSerializers()) {
            ser = serializers.findCollectionSerializer(config, type, beanDesc, elementTypeSerializer, elementValueSerializer);
            if (ser != null) {
                break;
            }
        }
        if (ser == null) {
            ser = this.findSerializerByAnnotations(prov, type, beanDesc);
            if (ser == null) {
                final JsonFormat.Value format = beanDesc.findExpectedFormat(null);
                if (format != null && format.getShape() == JsonFormat.Shape.OBJECT) {
                    return null;
                }
                final Class<?> raw = type.getRawClass();
                if (EnumSet.class.isAssignableFrom(raw)) {
                    JavaType enumType = type.getContentType();
                    if (!enumType.isEnumType()) {
                        enumType = null;
                    }
                    ser = this.buildEnumSetSerializer(enumType);
                }
                else {
                    final Class<?> elementRaw = type.getContentType().getRawClass();
                    if (this.isIndexedList(raw)) {
                        if (elementRaw == String.class) {
                            if (ClassUtil.isJacksonStdImpl(elementValueSerializer)) {
                                ser = IndexedStringListSerializer.instance;
                            }
                        }
                        else {
                            ser = this.buildIndexedListSerializer(type.getContentType(), staticTyping, elementTypeSerializer, elementValueSerializer);
                        }
                    }
                    else if (elementRaw == String.class && ClassUtil.isJacksonStdImpl(elementValueSerializer)) {
                        ser = StringCollectionSerializer.instance;
                    }
                    if (ser == null) {
                        ser = this.buildCollectionSerializer(type.getContentType(), staticTyping, elementTypeSerializer, elementValueSerializer);
                    }
                }
            }
        }
        if (this._factoryConfig.hasSerializerModifiers()) {
            for (final BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                ser = mod.modifyCollectionSerializer(config, type, beanDesc, ser);
            }
        }
        return ser;
    }
    
    protected boolean isIndexedList(final Class<?> cls) {
        return RandomAccess.class.isAssignableFrom(cls);
    }
    
    public ContainerSerializer<?> buildIndexedListSerializer(final JavaType elemType, final boolean staticTyping, final TypeSerializer vts, final JsonSerializer<Object> valueSerializer) {
        return new IndexedListSerializer(elemType, staticTyping, vts, valueSerializer);
    }
    
    public ContainerSerializer<?> buildCollectionSerializer(final JavaType elemType, final boolean staticTyping, final TypeSerializer vts, final JsonSerializer<Object> valueSerializer) {
        return new CollectionSerializer(elemType, staticTyping, vts, valueSerializer);
    }
    
    public JsonSerializer<?> buildEnumSetSerializer(final JavaType enumType) {
        return new EnumSetSerializer(enumType);
    }
    
    protected JsonSerializer<?> buildMapSerializer(final SerializerProvider prov, final MapType type, final BeanDescription beanDesc, final boolean staticTyping, final JsonSerializer<Object> keySerializer, final TypeSerializer elementTypeSerializer, final JsonSerializer<Object> elementValueSerializer) throws JsonMappingException {
        final JsonFormat.Value format = beanDesc.findExpectedFormat(null);
        if (format != null && format.getShape() == JsonFormat.Shape.OBJECT) {
            return null;
        }
        JsonSerializer<?> ser = null;
        final SerializationConfig config = prov.getConfig();
        for (final Serializers serializers : this.customSerializers()) {
            ser = serializers.findMapSerializer(config, type, beanDesc, keySerializer, elementTypeSerializer, elementValueSerializer);
            if (ser != null) {
                break;
            }
        }
        if (ser == null) {
            ser = this.findSerializerByAnnotations(prov, type, beanDesc);
            if (ser == null) {
                final Object filterId = this.findFilterId(config, beanDesc);
                final JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(Map.class, beanDesc.getClassInfo());
                final Set<String> ignored = (ignorals == null) ? null : ignorals.findIgnoredForSerialization();
                final MapSerializer mapSer = MapSerializer.construct(ignored, type, staticTyping, elementTypeSerializer, keySerializer, elementValueSerializer, filterId);
                ser = this._checkMapContentInclusion(prov, beanDesc, mapSer);
            }
        }
        if (this._factoryConfig.hasSerializerModifiers()) {
            for (final BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                ser = mod.modifyMapSerializer(config, type, beanDesc, ser);
            }
        }
        return ser;
    }
    
    protected MapSerializer _checkMapContentInclusion(final SerializerProvider prov, final BeanDescription beanDesc, final MapSerializer mapSer) throws JsonMappingException {
        final JavaType contentType = mapSer.getContentType();
        final JsonInclude.Value inclV = this._findInclusionWithContent(prov, beanDesc, contentType, Map.class);
        final JsonInclude.Include incl = (inclV == null) ? JsonInclude.Include.USE_DEFAULTS : inclV.getContentInclusion();
        if (incl != JsonInclude.Include.USE_DEFAULTS && incl != JsonInclude.Include.ALWAYS) {
            boolean suppressNulls = true;
            Object valueToSuppress = null;
            switch (incl) {
                case NON_DEFAULT: {
                    valueToSuppress = BeanUtil.getDefaultValue(contentType);
                    if (valueToSuppress != null && valueToSuppress.getClass().isArray()) {
                        valueToSuppress = ArrayBuilders.getArrayComparator(valueToSuppress);
                        break;
                    }
                    break;
                }
                case NON_ABSENT: {
                    valueToSuppress = (contentType.isReferenceType() ? MapSerializer.MARKER_FOR_EMPTY : null);
                    break;
                }
                case NON_EMPTY: {
                    valueToSuppress = MapSerializer.MARKER_FOR_EMPTY;
                    break;
                }
                case CUSTOM: {
                    valueToSuppress = prov.includeFilterInstance(null, inclV.getContentFilter());
                    suppressNulls = (valueToSuppress == null || prov.includeFilterSuppressNulls(valueToSuppress));
                    break;
                }
                default: {
                    valueToSuppress = null;
                    break;
                }
            }
            return mapSer.withContentInclusion(valueToSuppress, suppressNulls);
        }
        if (!prov.isEnabled(SerializationFeature.WRITE_NULL_MAP_VALUES)) {
            return mapSer.withContentInclusion(null, true);
        }
        return mapSer;
    }
    
    protected JsonSerializer<?> buildMapEntrySerializer(final SerializerProvider prov, final JavaType type, final BeanDescription beanDesc, final boolean staticTyping, final JavaType keyType, final JavaType valueType) throws JsonMappingException {
        final JsonFormat.Value formatOverride = prov.getDefaultPropertyFormat(Map.Entry.class);
        final JsonFormat.Value formatFromAnnotation = beanDesc.findExpectedFormat(null);
        final JsonFormat.Value format = JsonFormat.Value.merge(formatFromAnnotation, formatOverride);
        if (format.getShape() == JsonFormat.Shape.OBJECT) {
            return null;
        }
        final MapEntrySerializer ser = new MapEntrySerializer(valueType, keyType, valueType, staticTyping, this.createTypeSerializer(prov.getConfig(), valueType), null);
        final JavaType contentType = ser.getContentType();
        final JsonInclude.Value inclV = this._findInclusionWithContent(prov, beanDesc, contentType, Map.Entry.class);
        final JsonInclude.Include incl = (inclV == null) ? JsonInclude.Include.USE_DEFAULTS : inclV.getContentInclusion();
        if (incl == JsonInclude.Include.USE_DEFAULTS || incl == JsonInclude.Include.ALWAYS) {
            return ser;
        }
        boolean suppressNulls = true;
        Object valueToSuppress = null;
        switch (incl) {
            case NON_DEFAULT: {
                valueToSuppress = BeanUtil.getDefaultValue(contentType);
                if (valueToSuppress != null && valueToSuppress.getClass().isArray()) {
                    valueToSuppress = ArrayBuilders.getArrayComparator(valueToSuppress);
                    break;
                }
                break;
            }
            case NON_ABSENT: {
                valueToSuppress = (contentType.isReferenceType() ? MapSerializer.MARKER_FOR_EMPTY : null);
                break;
            }
            case NON_EMPTY: {
                valueToSuppress = MapSerializer.MARKER_FOR_EMPTY;
                break;
            }
            case CUSTOM: {
                valueToSuppress = prov.includeFilterInstance(null, inclV.getContentFilter());
                suppressNulls = (valueToSuppress == null || prov.includeFilterSuppressNulls(valueToSuppress));
                break;
            }
            default: {
                valueToSuppress = null;
                break;
            }
        }
        return ser.withContentInclusion(valueToSuppress, suppressNulls);
    }
    
    protected JsonInclude.Value _findInclusionWithContent(final SerializerProvider prov, final BeanDescription beanDesc, final JavaType contentType, final Class<?> configType) throws JsonMappingException {
        final SerializationConfig config = prov.getConfig();
        JsonInclude.Value inclV = beanDesc.findPropertyInclusion(config.getDefaultPropertyInclusion());
        inclV = config.getDefaultPropertyInclusion(configType, inclV);
        final JsonInclude.Value valueIncl = config.getDefaultPropertyInclusion(contentType.getRawClass(), null);
        if (valueIncl != null) {
            switch (valueIncl.getValueInclusion()) {
                case USE_DEFAULTS: {
                    break;
                }
                case CUSTOM: {
                    inclV = inclV.withContentFilter(valueIncl.getContentFilter());
                    break;
                }
                default: {
                    inclV = inclV.withContentInclusion(valueIncl.getValueInclusion());
                    break;
                }
            }
        }
        return inclV;
    }
    
    protected JsonSerializer<?> buildArraySerializer(final SerializerProvider prov, final ArrayType type, final BeanDescription beanDesc, final boolean staticTyping, final TypeSerializer elementTypeSerializer, final JsonSerializer<Object> elementValueSerializer) throws JsonMappingException {
        final SerializationConfig config = prov.getConfig();
        JsonSerializer<?> ser = null;
        for (final Serializers serializers : this.customSerializers()) {
            ser = serializers.findArraySerializer(config, type, beanDesc, elementTypeSerializer, elementValueSerializer);
            if (ser != null) {
                break;
            }
        }
        if (ser == null) {
            final Class<?> raw = type.getRawClass();
            if (elementValueSerializer == null || ClassUtil.isJacksonStdImpl(elementValueSerializer)) {
                if (String[].class == raw) {
                    ser = StringArraySerializer.instance;
                }
                else {
                    ser = StdArraySerializers.findStandardImpl(raw);
                }
            }
            if (ser == null) {
                ser = new ObjectArraySerializer(type.getContentType(), staticTyping, elementTypeSerializer, elementValueSerializer);
            }
        }
        if (this._factoryConfig.hasSerializerModifiers()) {
            for (final BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                ser = mod.modifyArraySerializer(config, type, beanDesc, ser);
            }
        }
        return ser;
    }
    
    public JsonSerializer<?> findReferenceSerializer(final SerializerProvider prov, final ReferenceType refType, final BeanDescription beanDesc, final boolean staticTyping) throws JsonMappingException {
        final JavaType contentType = refType.getContentType();
        TypeSerializer contentTypeSerializer = contentType.getTypeHandler();
        final SerializationConfig config = prov.getConfig();
        if (contentTypeSerializer == null) {
            contentTypeSerializer = this.createTypeSerializer(config, contentType);
        }
        final JsonSerializer<Object> contentSerializer = contentType.getValueHandler();
        for (final Serializers serializers : this.customSerializers()) {
            final JsonSerializer<?> ser = serializers.findReferenceSerializer(config, refType, beanDesc, contentTypeSerializer, contentSerializer);
            if (ser != null) {
                return ser;
            }
        }
        if (refType.isTypeOrSubTypeOf(AtomicReference.class)) {
            return this.buildAtomicReferenceSerializer(prov, refType, beanDesc, staticTyping, contentTypeSerializer, contentSerializer);
        }
        return null;
    }
    
    protected JsonSerializer<?> buildAtomicReferenceSerializer(final SerializerProvider prov, final ReferenceType refType, final BeanDescription beanDesc, final boolean staticTyping, final TypeSerializer contentTypeSerializer, final JsonSerializer<Object> contentSerializer) throws JsonMappingException {
        final JavaType contentType = refType.getReferencedType();
        final JsonInclude.Value inclV = this._findInclusionWithContent(prov, beanDesc, contentType, AtomicReference.class);
        final JsonInclude.Include incl = (inclV == null) ? JsonInclude.Include.USE_DEFAULTS : inclV.getContentInclusion();
        Object valueToSuppress = null;
        boolean suppressNulls;
        if (incl == JsonInclude.Include.USE_DEFAULTS || incl == JsonInclude.Include.ALWAYS) {
            valueToSuppress = null;
            suppressNulls = false;
        }
        else {
            suppressNulls = true;
            switch (incl) {
                case NON_DEFAULT: {
                    valueToSuppress = BeanUtil.getDefaultValue(contentType);
                    if (valueToSuppress != null && valueToSuppress.getClass().isArray()) {
                        valueToSuppress = ArrayBuilders.getArrayComparator(valueToSuppress);
                        break;
                    }
                    break;
                }
                case NON_ABSENT: {
                    valueToSuppress = (contentType.isReferenceType() ? MapSerializer.MARKER_FOR_EMPTY : null);
                    break;
                }
                case NON_EMPTY: {
                    valueToSuppress = MapSerializer.MARKER_FOR_EMPTY;
                    break;
                }
                case CUSTOM: {
                    valueToSuppress = prov.includeFilterInstance(null, inclV.getContentFilter());
                    suppressNulls = (valueToSuppress == null || prov.includeFilterSuppressNulls(valueToSuppress));
                    break;
                }
                default: {
                    valueToSuppress = null;
                    break;
                }
            }
        }
        final AtomicReferenceSerializer ser = new AtomicReferenceSerializer(refType, staticTyping, contentTypeSerializer, contentSerializer);
        return ser.withContentInclusion(valueToSuppress, suppressNulls);
    }
    
    protected JsonSerializer<?> buildIteratorSerializer(final SerializationConfig config, final JavaType type, final BeanDescription beanDesc, final boolean staticTyping, final JavaType valueType) throws JsonMappingException {
        return new IteratorSerializer(valueType, staticTyping, this.createTypeSerializer(config, valueType));
    }
    
    protected JsonSerializer<?> buildIterableSerializer(final SerializationConfig config, final JavaType type, final BeanDescription beanDesc, final boolean staticTyping, final JavaType valueType) throws JsonMappingException {
        return new IterableSerializer(valueType, staticTyping, this.createTypeSerializer(config, valueType));
    }
    
    protected JsonSerializer<?> buildEnumSerializer(final SerializationConfig config, final JavaType type, final BeanDescription beanDesc) throws JsonMappingException {
        final JsonFormat.Value format = beanDesc.findExpectedFormat(null);
        if (format != null && format.getShape() == JsonFormat.Shape.OBJECT) {
            ((BasicBeanDescription)beanDesc).removeProperty("declaringClass");
            return null;
        }
        final Class<Enum<?>> enumClass = (Class<Enum<?>>)type.getRawClass();
        JsonSerializer<?> ser = EnumSerializer.construct(enumClass, config, beanDesc, format);
        if (this._factoryConfig.hasSerializerModifiers()) {
            for (final BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                ser = mod.modifyEnumSerializer(config, type, beanDesc, ser);
            }
        }
        return ser;
    }
    
    protected JsonSerializer<Object> _findKeySerializer(final SerializerProvider prov, final Annotated a) throws JsonMappingException {
        final AnnotationIntrospector intr = prov.getAnnotationIntrospector();
        final Object serDef = intr.findKeySerializer(a);
        if (serDef != null) {
            return prov.serializerInstance(a, serDef);
        }
        return null;
    }
    
    protected JsonSerializer<Object> _findContentSerializer(final SerializerProvider prov, final Annotated a) throws JsonMappingException {
        final AnnotationIntrospector intr = prov.getAnnotationIntrospector();
        final Object serDef = intr.findContentSerializer(a);
        if (serDef != null) {
            return prov.serializerInstance(a, serDef);
        }
        return null;
    }
    
    protected Object findFilterId(final SerializationConfig config, final BeanDescription beanDesc) {
        return config.getAnnotationIntrospector().findFilterId(beanDesc.getClassInfo());
    }
    
    protected boolean usesStaticTyping(final SerializationConfig config, final BeanDescription beanDesc, final TypeSerializer typeSer) {
        if (typeSer != null) {
            return false;
        }
        final AnnotationIntrospector intr = config.getAnnotationIntrospector();
        final JsonSerialize.Typing t = intr.findSerializationTyping(beanDesc.getClassInfo());
        if (t != null && t != JsonSerialize.Typing.DEFAULT_TYPING) {
            return t == JsonSerialize.Typing.STATIC;
        }
        return config.isEnabled(MapperFeature.USE_STATIC_TYPING);
    }
    
    static {
        final HashMap<String, Class<? extends JsonSerializer<?>>> concLazy = new HashMap<String, Class<? extends JsonSerializer<?>>>();
        final HashMap<String, JsonSerializer<?>> concrete = new HashMap<String, JsonSerializer<?>>();
        concrete.put(String.class.getName(), new StringSerializer());
        final ToStringSerializer sls = ToStringSerializer.instance;
        concrete.put(StringBuffer.class.getName(), sls);
        concrete.put(StringBuilder.class.getName(), sls);
        concrete.put(Character.class.getName(), sls);
        concrete.put(Character.TYPE.getName(), sls);
        NumberSerializers.addAll(concrete);
        concrete.put(Boolean.TYPE.getName(), new BooleanSerializer(true));
        concrete.put(Boolean.class.getName(), new BooleanSerializer(false));
        concrete.put(BigInteger.class.getName(), new NumberSerializer(BigInteger.class));
        concrete.put(BigDecimal.class.getName(), new NumberSerializer(BigDecimal.class));
        concrete.put(Calendar.class.getName(), CalendarSerializer.instance);
        concrete.put(Date.class.getName(), DateSerializer.instance);
        for (final Map.Entry<Class<?>, Object> en : StdJdkSerializers.all()) {
            final Object value = en.getValue();
            if (value instanceof JsonSerializer) {
                concrete.put(en.getKey().getName(), (JsonSerializer<?>)value);
            }
            else {
                final Class<? extends JsonSerializer<?>> cls = (Class<? extends JsonSerializer<?>>)value;
                concLazy.put(en.getKey().getName(), cls);
            }
        }
        concLazy.put(TokenBuffer.class.getName(), TokenBufferSerializer.class);
        _concrete = concrete;
        _concreteLazy = concLazy;
    }
}
