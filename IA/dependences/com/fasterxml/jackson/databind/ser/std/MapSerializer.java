// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.databind.type.TypeFactory;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.SortedMap;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonMapFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Type;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.util.Iterator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.HashSet;
import java.util.Collection;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.util.Set;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import java.util.Map;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;

@JacksonStdImpl
public class MapSerializer extends ContainerSerializer<Map<?, ?>> implements ContextualSerializer
{
    private static final long serialVersionUID = 1L;
    protected static final JavaType UNSPECIFIED_TYPE;
    public static final Object MARKER_FOR_EMPTY;
    protected final BeanProperty _property;
    protected final boolean _valueTypeIsStatic;
    protected final JavaType _keyType;
    protected final JavaType _valueType;
    protected JsonSerializer<Object> _keySerializer;
    protected JsonSerializer<Object> _valueSerializer;
    protected final TypeSerializer _valueTypeSerializer;
    protected PropertySerializerMap _dynamicValueSerializers;
    protected final Set<String> _ignoredEntries;
    protected final Object _filterId;
    protected final Object _suppressableValue;
    protected final boolean _suppressNulls;
    protected final boolean _sortKeys;
    
    protected MapSerializer(final Set<String> ignoredEntries, final JavaType keyType, final JavaType valueType, final boolean valueTypeIsStatic, final TypeSerializer vts, final JsonSerializer<?> keySerializer, final JsonSerializer<?> valueSerializer) {
        super(Map.class, false);
        this._ignoredEntries = ((ignoredEntries == null || ignoredEntries.isEmpty()) ? null : ignoredEntries);
        this._keyType = keyType;
        this._valueType = valueType;
        this._valueTypeIsStatic = valueTypeIsStatic;
        this._valueTypeSerializer = vts;
        this._keySerializer = (JsonSerializer<Object>)keySerializer;
        this._valueSerializer = (JsonSerializer<Object>)valueSerializer;
        this._dynamicValueSerializers = PropertySerializerMap.emptyForProperties();
        this._property = null;
        this._filterId = null;
        this._sortKeys = false;
        this._suppressableValue = null;
        this._suppressNulls = false;
    }
    
    protected MapSerializer(final MapSerializer src, final BeanProperty property, final JsonSerializer<?> keySerializer, final JsonSerializer<?> valueSerializer, final Set<String> ignoredEntries) {
        super(Map.class, false);
        this._ignoredEntries = ((ignoredEntries == null || ignoredEntries.isEmpty()) ? null : ignoredEntries);
        this._keyType = src._keyType;
        this._valueType = src._valueType;
        this._valueTypeIsStatic = src._valueTypeIsStatic;
        this._valueTypeSerializer = src._valueTypeSerializer;
        this._keySerializer = (JsonSerializer<Object>)keySerializer;
        this._valueSerializer = (JsonSerializer<Object>)valueSerializer;
        this._dynamicValueSerializers = PropertySerializerMap.emptyForProperties();
        this._property = property;
        this._filterId = src._filterId;
        this._sortKeys = src._sortKeys;
        this._suppressableValue = src._suppressableValue;
        this._suppressNulls = src._suppressNulls;
    }
    
    protected MapSerializer(final MapSerializer src, final TypeSerializer vts, final Object suppressableValue, final boolean suppressNulls) {
        super(Map.class, false);
        this._ignoredEntries = src._ignoredEntries;
        this._keyType = src._keyType;
        this._valueType = src._valueType;
        this._valueTypeIsStatic = src._valueTypeIsStatic;
        this._valueTypeSerializer = vts;
        this._keySerializer = src._keySerializer;
        this._valueSerializer = src._valueSerializer;
        this._dynamicValueSerializers = src._dynamicValueSerializers;
        this._property = src._property;
        this._filterId = src._filterId;
        this._sortKeys = src._sortKeys;
        this._suppressableValue = suppressableValue;
        this._suppressNulls = suppressNulls;
    }
    
    protected MapSerializer(final MapSerializer src, final Object filterId, final boolean sortKeys) {
        super(Map.class, false);
        this._ignoredEntries = src._ignoredEntries;
        this._keyType = src._keyType;
        this._valueType = src._valueType;
        this._valueTypeIsStatic = src._valueTypeIsStatic;
        this._valueTypeSerializer = src._valueTypeSerializer;
        this._keySerializer = src._keySerializer;
        this._valueSerializer = src._valueSerializer;
        this._dynamicValueSerializers = PropertySerializerMap.emptyForProperties();
        this._property = src._property;
        this._filterId = filterId;
        this._sortKeys = sortKeys;
        this._suppressableValue = src._suppressableValue;
        this._suppressNulls = src._suppressNulls;
    }
    
    public MapSerializer _withValueTypeSerializer(final TypeSerializer vts) {
        if (this._valueTypeSerializer == vts) {
            return this;
        }
        this._ensureOverride("_withValueTypeSerializer");
        return new MapSerializer(this, vts, this._suppressableValue, this._suppressNulls);
    }
    
    public MapSerializer withResolved(final BeanProperty property, final JsonSerializer<?> keySerializer, final JsonSerializer<?> valueSerializer, final Set<String> ignored, final boolean sortKeys) {
        this._ensureOverride("withResolved");
        MapSerializer ser = new MapSerializer(this, property, keySerializer, valueSerializer, ignored);
        if (sortKeys != ser._sortKeys) {
            ser = new MapSerializer(ser, this._filterId, sortKeys);
        }
        return ser;
    }
    
    @Override
    public MapSerializer withFilterId(final Object filterId) {
        if (this._filterId == filterId) {
            return this;
        }
        this._ensureOverride("withFilterId");
        return new MapSerializer(this, filterId, this._sortKeys);
    }
    
    public MapSerializer withContentInclusion(final Object suppressableValue, final boolean suppressNulls) {
        if (suppressableValue == this._suppressableValue && suppressNulls == this._suppressNulls) {
            return this;
        }
        this._ensureOverride("withContentInclusion");
        return new MapSerializer(this, this._valueTypeSerializer, suppressableValue, suppressNulls);
    }
    
    public static MapSerializer construct(final Set<String> ignoredEntries, final JavaType mapType, boolean staticValueType, final TypeSerializer vts, final JsonSerializer<Object> keySerializer, final JsonSerializer<Object> valueSerializer, final Object filterId) {
        JavaType keyType;
        JavaType valueType;
        if (mapType == null) {
            valueType = (keyType = MapSerializer.UNSPECIFIED_TYPE);
        }
        else {
            keyType = mapType.getKeyType();
            valueType = mapType.getContentType();
        }
        if (!staticValueType) {
            staticValueType = (valueType != null && valueType.isFinal());
        }
        else if (valueType.getRawClass() == Object.class) {
            staticValueType = false;
        }
        MapSerializer ser = new MapSerializer(ignoredEntries, keyType, valueType, staticValueType, vts, keySerializer, valueSerializer);
        if (filterId != null) {
            ser = ser.withFilterId(filterId);
        }
        return ser;
    }
    
    protected void _ensureOverride(final String method) {
        ClassUtil.verifyMustOverride(MapSerializer.class, this, method);
    }
    
    @Deprecated
    protected void _ensureOverride() {
        this._ensureOverride("N/A");
    }
    
    @Deprecated
    protected MapSerializer(final MapSerializer src, final TypeSerializer vts, final Object suppressableValue) {
        this(src, vts, suppressableValue, false);
    }
    
    @Deprecated
    public MapSerializer withContentInclusion(final Object suppressableValue) {
        return new MapSerializer(this, this._valueTypeSerializer, suppressableValue, this._suppressNulls);
    }
    
    @Deprecated
    public static MapSerializer construct(final String[] ignoredList, final JavaType mapType, final boolean staticValueType, final TypeSerializer vts, final JsonSerializer<Object> keySerializer, final JsonSerializer<Object> valueSerializer, final Object filterId) {
        final Set<String> ignoredEntries = ArrayBuilders.arrayToSet(ignoredList);
        return construct(ignoredEntries, mapType, staticValueType, vts, keySerializer, valueSerializer, filterId);
    }
    
    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider provider, final BeanProperty property) throws JsonMappingException {
        JsonSerializer<?> ser = null;
        JsonSerializer<?> keySer = null;
        final AnnotationIntrospector intr = provider.getAnnotationIntrospector();
        final AnnotatedMember propertyAcc = (property == null) ? null : property.getMember();
        if (StdSerializer._neitherNull(propertyAcc, intr)) {
            Object serDef = intr.findKeySerializer(propertyAcc);
            if (serDef != null) {
                keySer = provider.serializerInstance(propertyAcc, serDef);
            }
            serDef = intr.findContentSerializer(propertyAcc);
            if (serDef != null) {
                ser = provider.serializerInstance(propertyAcc, serDef);
            }
        }
        if (ser == null) {
            ser = this._valueSerializer;
        }
        ser = this.findContextualConvertingSerializer(provider, property, ser);
        if (ser == null && this._valueTypeIsStatic && !this._valueType.isJavaLangObject()) {
            ser = provider.findValueSerializer(this._valueType, property);
        }
        if (keySer == null) {
            keySer = this._keySerializer;
        }
        if (keySer == null) {
            keySer = provider.findKeySerializer(this._keyType, property);
        }
        else {
            keySer = provider.handleSecondaryContextualization(keySer, property);
        }
        Set<String> ignored = this._ignoredEntries;
        boolean sortKeys = false;
        if (StdSerializer._neitherNull(propertyAcc, intr)) {
            final JsonIgnoreProperties.Value ignorals = intr.findPropertyIgnorals(propertyAcc);
            if (ignorals != null) {
                final Set<String> newIgnored = ignorals.findIgnoredForSerialization();
                if (StdSerializer._nonEmpty(newIgnored)) {
                    ignored = ((ignored == null) ? new HashSet<String>() : new HashSet<String>(ignored));
                    for (final String str : newIgnored) {
                        ignored.add(str);
                    }
                }
            }
            final Boolean b = intr.findSerializationSortAlphabetically(propertyAcc);
            sortKeys = Boolean.TRUE.equals(b);
        }
        final JsonFormat.Value format = this.findFormatOverrides(provider, property, Map.class);
        if (format != null) {
            final Boolean B = format.getFeature(JsonFormat.Feature.WRITE_SORTED_MAP_ENTRIES);
            if (B != null) {
                sortKeys = B;
            }
        }
        MapSerializer mser = this.withResolved(property, keySer, ser, ignored, sortKeys);
        if (property != null) {
            final AnnotatedMember m = property.getMember();
            if (m != null) {
                final Object filterId = intr.findFilterId(m);
                if (filterId != null) {
                    mser = mser.withFilterId(filterId);
                }
            }
            final JsonInclude.Value inclV = property.findPropertyInclusion(provider.getConfig(), null);
            if (inclV != null) {
                final JsonInclude.Include incl = inclV.getContentInclusion();
                if (incl != JsonInclude.Include.USE_DEFAULTS) {
                    Object valueToSuppress = null;
                    boolean suppressNulls = false;
                    switch (incl) {
                        case NON_DEFAULT: {
                            valueToSuppress = BeanUtil.getDefaultValue(this._valueType);
                            suppressNulls = true;
                            if (valueToSuppress != null && valueToSuppress.getClass().isArray()) {
                                valueToSuppress = ArrayBuilders.getArrayComparator(valueToSuppress);
                                break;
                            }
                            break;
                        }
                        case NON_ABSENT: {
                            suppressNulls = true;
                            valueToSuppress = (this._valueType.isReferenceType() ? MapSerializer.MARKER_FOR_EMPTY : null);
                            break;
                        }
                        case NON_EMPTY: {
                            suppressNulls = true;
                            valueToSuppress = MapSerializer.MARKER_FOR_EMPTY;
                            break;
                        }
                        case CUSTOM: {
                            valueToSuppress = provider.includeFilterInstance(null, inclV.getContentFilter());
                            suppressNulls = (valueToSuppress == null || provider.includeFilterSuppressNulls(valueToSuppress));
                            break;
                        }
                        case NON_NULL: {
                            valueToSuppress = null;
                            suppressNulls = true;
                            break;
                        }
                        default: {
                            valueToSuppress = null;
                            suppressNulls = false;
                            break;
                        }
                    }
                    mser = mser.withContentInclusion(valueToSuppress, suppressNulls);
                }
            }
        }
        return mser;
    }
    
    @Override
    public JavaType getContentType() {
        return this._valueType;
    }
    
    @Override
    public JsonSerializer<?> getContentSerializer() {
        return this._valueSerializer;
    }
    
    @Override
    public boolean isEmpty(final SerializerProvider prov, final Map<?, ?> value) {
        if (value.isEmpty()) {
            return true;
        }
        final Object supp = this._suppressableValue;
        if (supp == null && !this._suppressNulls) {
            return false;
        }
        JsonSerializer<Object> valueSer = this._valueSerializer;
        final boolean checkEmpty = MapSerializer.MARKER_FOR_EMPTY == supp;
        if (valueSer != null) {
            for (final Object elemValue : value.values()) {
                if (elemValue == null) {
                    if (this._suppressNulls) {
                        continue;
                    }
                    return false;
                }
                else if (checkEmpty) {
                    if (!valueSer.isEmpty(prov, elemValue)) {
                        return false;
                    }
                    continue;
                }
                else {
                    if (supp == null || !supp.equals(value)) {
                        return false;
                    }
                    continue;
                }
            }
            return true;
        }
        for (final Object elemValue : value.values()) {
            if (elemValue == null) {
                if (this._suppressNulls) {
                    continue;
                }
                return false;
            }
            else {
                try {
                    valueSer = this._findSerializer(prov, elemValue);
                }
                catch (JsonMappingException e) {
                    return false;
                }
                if (checkEmpty) {
                    if (!valueSer.isEmpty(prov, elemValue)) {
                        return false;
                    }
                    continue;
                }
                else {
                    if (supp == null || !supp.equals(value)) {
                        return false;
                    }
                    continue;
                }
            }
        }
        return true;
    }
    
    @Override
    public boolean hasSingleElement(final Map<?, ?> value) {
        return value.size() == 1;
    }
    
    public JsonSerializer<?> getKeySerializer() {
        return this._keySerializer;
    }
    
    @Override
    public void serialize(Map<?, ?> value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        gen.writeStartObject(value);
        if (!value.isEmpty()) {
            if (this._sortKeys || provider.isEnabled(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)) {
                value = this._orderEntries(value, gen, provider);
            }
            final PropertyFilter pf;
            if (this._filterId != null && (pf = this.findPropertyFilter(provider, this._filterId, value)) != null) {
                this.serializeFilteredFields(value, gen, provider, pf, this._suppressableValue);
            }
            else if (this._suppressableValue != null || this._suppressNulls) {
                this.serializeOptionalFields(value, gen, provider, this._suppressableValue);
            }
            else if (this._valueSerializer != null) {
                this.serializeFieldsUsing(value, gen, provider, this._valueSerializer);
            }
            else {
                this.serializeFields(value, gen, provider);
            }
        }
        gen.writeEndObject();
    }
    
    @Override
    public void serializeWithType(Map<?, ?> value, final JsonGenerator gen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
        gen.setCurrentValue(value);
        final WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen, typeSer.typeId(value, JsonToken.START_OBJECT));
        if (!value.isEmpty()) {
            if (this._sortKeys || provider.isEnabled(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)) {
                value = this._orderEntries(value, gen, provider);
            }
            final PropertyFilter pf;
            if (this._filterId != null && (pf = this.findPropertyFilter(provider, this._filterId, value)) != null) {
                this.serializeFilteredFields(value, gen, provider, pf, this._suppressableValue);
            }
            else if (this._suppressableValue != null || this._suppressNulls) {
                this.serializeOptionalFields(value, gen, provider, this._suppressableValue);
            }
            else if (this._valueSerializer != null) {
                this.serializeFieldsUsing(value, gen, provider, this._valueSerializer);
            }
            else {
                this.serializeFields(value, gen, provider);
            }
        }
        typeSer.writeTypeSuffix(gen, typeIdDef);
    }
    
    public void serializeFields(final Map<?, ?> value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        if (this._valueTypeSerializer != null) {
            this.serializeTypedFields(value, gen, provider, null);
            return;
        }
        final JsonSerializer<Object> keySerializer = this._keySerializer;
        final Set<String> ignored = this._ignoredEntries;
        Object keyElem = null;
        try {
            for (final Map.Entry<?, ?> entry : value.entrySet()) {
                final Object valueElem = entry.getValue();
                keyElem = entry.getKey();
                if (keyElem == null) {
                    provider.findNullKeySerializer(this._keyType, this._property).serialize(null, gen, provider);
                }
                else {
                    if (ignored != null && ignored.contains(keyElem)) {
                        continue;
                    }
                    keySerializer.serialize(keyElem, gen, provider);
                }
                if (valueElem == null) {
                    provider.defaultSerializeNull(gen);
                }
                else {
                    JsonSerializer<Object> serializer = this._valueSerializer;
                    if (serializer == null) {
                        serializer = this._findSerializer(provider, valueElem);
                    }
                    serializer.serialize(valueElem, gen, provider);
                }
            }
        }
        catch (Exception e) {
            this.wrapAndThrow(provider, e, value, String.valueOf(keyElem));
        }
    }
    
    public void serializeOptionalFields(final Map<?, ?> value, final JsonGenerator gen, final SerializerProvider provider, final Object suppressableValue) throws IOException {
        if (this._valueTypeSerializer != null) {
            this.serializeTypedFields(value, gen, provider, suppressableValue);
            return;
        }
        final Set<String> ignored = this._ignoredEntries;
        final boolean checkEmpty = MapSerializer.MARKER_FOR_EMPTY == suppressableValue;
        for (final Map.Entry<?, ?> entry : value.entrySet()) {
            final Object keyElem = entry.getKey();
            JsonSerializer<Object> keySerializer;
            if (keyElem == null) {
                keySerializer = provider.findNullKeySerializer(this._keyType, this._property);
            }
            else {
                if (ignored != null && ignored.contains(keyElem)) {
                    continue;
                }
                keySerializer = this._keySerializer;
            }
            final Object valueElem = entry.getValue();
            JsonSerializer<Object> valueSer;
            if (valueElem == null) {
                if (this._suppressNulls) {
                    continue;
                }
                valueSer = provider.getDefaultNullValueSerializer();
            }
            else {
                valueSer = this._valueSerializer;
                if (valueSer == null) {
                    valueSer = this._findSerializer(provider, valueElem);
                }
                if (checkEmpty) {
                    if (valueSer.isEmpty(provider, valueElem)) {
                        continue;
                    }
                }
                else if (suppressableValue != null && suppressableValue.equals(valueElem)) {
                    continue;
                }
            }
            try {
                keySerializer.serialize(keyElem, gen, provider);
                valueSer.serialize(valueElem, gen, provider);
            }
            catch (Exception e) {
                this.wrapAndThrow(provider, e, value, String.valueOf(keyElem));
            }
        }
    }
    
    public void serializeFieldsUsing(final Map<?, ?> value, final JsonGenerator gen, final SerializerProvider provider, final JsonSerializer<Object> ser) throws IOException {
        final JsonSerializer<Object> keySerializer = this._keySerializer;
        final Set<String> ignored = this._ignoredEntries;
        final TypeSerializer typeSer = this._valueTypeSerializer;
        for (final Map.Entry<?, ?> entry : value.entrySet()) {
            final Object keyElem = entry.getKey();
            if (ignored != null && ignored.contains(keyElem)) {
                continue;
            }
            if (keyElem == null) {
                provider.findNullKeySerializer(this._keyType, this._property).serialize(null, gen, provider);
            }
            else {
                keySerializer.serialize(keyElem, gen, provider);
            }
            final Object valueElem = entry.getValue();
            if (valueElem == null) {
                provider.defaultSerializeNull(gen);
            }
            else {
                try {
                    if (typeSer == null) {
                        ser.serialize(valueElem, gen, provider);
                    }
                    else {
                        ser.serializeWithType(valueElem, gen, provider, typeSer);
                    }
                }
                catch (Exception e) {
                    this.wrapAndThrow(provider, e, value, String.valueOf(keyElem));
                }
            }
        }
    }
    
    public void serializeFilteredFields(final Map<?, ?> value, final JsonGenerator gen, final SerializerProvider provider, final PropertyFilter filter, final Object suppressableValue) throws IOException {
        final Set<String> ignored = this._ignoredEntries;
        final MapProperty prop = new MapProperty(this._valueTypeSerializer, this._property);
        final boolean checkEmpty = MapSerializer.MARKER_FOR_EMPTY == suppressableValue;
        for (final Map.Entry<?, ?> entry : value.entrySet()) {
            final Object keyElem = entry.getKey();
            if (ignored != null && ignored.contains(keyElem)) {
                continue;
            }
            JsonSerializer<Object> keySerializer;
            if (keyElem == null) {
                keySerializer = provider.findNullKeySerializer(this._keyType, this._property);
            }
            else {
                keySerializer = this._keySerializer;
            }
            final Object valueElem = entry.getValue();
            JsonSerializer<Object> valueSer;
            if (valueElem == null) {
                if (this._suppressNulls) {
                    continue;
                }
                valueSer = provider.getDefaultNullValueSerializer();
            }
            else {
                valueSer = this._valueSerializer;
                if (valueSer == null) {
                    valueSer = this._findSerializer(provider, valueElem);
                }
                if (checkEmpty) {
                    if (valueSer.isEmpty(provider, valueElem)) {
                        continue;
                    }
                }
                else if (suppressableValue != null && suppressableValue.equals(valueElem)) {
                    continue;
                }
            }
            prop.reset(keyElem, valueElem, keySerializer, valueSer);
            try {
                filter.serializeAsField(value, gen, provider, prop);
            }
            catch (Exception e) {
                this.wrapAndThrow(provider, e, value, String.valueOf(keyElem));
            }
        }
    }
    
    public void serializeTypedFields(final Map<?, ?> value, final JsonGenerator gen, final SerializerProvider provider, final Object suppressableValue) throws IOException {
        final Set<String> ignored = this._ignoredEntries;
        final boolean checkEmpty = MapSerializer.MARKER_FOR_EMPTY == suppressableValue;
        for (final Map.Entry<?, ?> entry : value.entrySet()) {
            final Object keyElem = entry.getKey();
            JsonSerializer<Object> keySerializer;
            if (keyElem == null) {
                keySerializer = provider.findNullKeySerializer(this._keyType, this._property);
            }
            else {
                if (ignored != null && ignored.contains(keyElem)) {
                    continue;
                }
                keySerializer = this._keySerializer;
            }
            final Object valueElem = entry.getValue();
            JsonSerializer<Object> valueSer;
            if (valueElem == null) {
                if (this._suppressNulls) {
                    continue;
                }
                valueSer = provider.getDefaultNullValueSerializer();
            }
            else {
                valueSer = this._valueSerializer;
                if (valueSer == null) {
                    valueSer = this._findSerializer(provider, valueElem);
                }
                if (checkEmpty) {
                    if (valueSer.isEmpty(provider, valueElem)) {
                        continue;
                    }
                }
                else if (suppressableValue != null && suppressableValue.equals(valueElem)) {
                    continue;
                }
            }
            keySerializer.serialize(keyElem, gen, provider);
            try {
                valueSer.serializeWithType(valueElem, gen, provider, this._valueTypeSerializer);
            }
            catch (Exception e) {
                this.wrapAndThrow(provider, e, value, String.valueOf(keyElem));
            }
        }
    }
    
    public void serializeFilteredAnyProperties(final SerializerProvider provider, final JsonGenerator gen, final Object bean, final Map<?, ?> value, final PropertyFilter filter, final Object suppressableValue) throws IOException {
        final Set<String> ignored = this._ignoredEntries;
        final MapProperty prop = new MapProperty(this._valueTypeSerializer, this._property);
        final boolean checkEmpty = MapSerializer.MARKER_FOR_EMPTY == suppressableValue;
        for (final Map.Entry<?, ?> entry : value.entrySet()) {
            final Object keyElem = entry.getKey();
            if (ignored != null && ignored.contains(keyElem)) {
                continue;
            }
            JsonSerializer<Object> keySerializer;
            if (keyElem == null) {
                keySerializer = provider.findNullKeySerializer(this._keyType, this._property);
            }
            else {
                keySerializer = this._keySerializer;
            }
            final Object valueElem = entry.getValue();
            JsonSerializer<Object> valueSer;
            if (valueElem == null) {
                if (this._suppressNulls) {
                    continue;
                }
                valueSer = provider.getDefaultNullValueSerializer();
            }
            else {
                valueSer = this._valueSerializer;
                if (valueSer == null) {
                    valueSer = this._findSerializer(provider, valueElem);
                }
                if (checkEmpty) {
                    if (valueSer.isEmpty(provider, valueElem)) {
                        continue;
                    }
                }
                else if (suppressableValue != null && suppressableValue.equals(valueElem)) {
                    continue;
                }
            }
            prop.reset(keyElem, valueElem, keySerializer, valueSer);
            try {
                filter.serializeAsField(bean, gen, provider, prop);
            }
            catch (Exception e) {
                this.wrapAndThrow(provider, e, value, String.valueOf(keyElem));
            }
        }
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
        return this.createSchemaNode("object", true);
    }
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        final JsonMapFormatVisitor v2 = visitor.expectMapFormat(typeHint);
        if (v2 != null) {
            v2.keyFormat(this._keySerializer, this._keyType);
            JsonSerializer<?> valueSer = this._valueSerializer;
            if (valueSer == null) {
                valueSer = this._findAndAddDynamic(this._dynamicValueSerializers, this._valueType, visitor.getProvider());
            }
            v2.valueFormat(valueSer, this._valueType);
        }
    }
    
    protected final JsonSerializer<Object> _findAndAddDynamic(final PropertySerializerMap map, final Class<?> type, final SerializerProvider provider) throws JsonMappingException {
        final PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSecondarySerializer(type, provider, this._property);
        if (map != result.map) {
            this._dynamicValueSerializers = result.map;
        }
        return result.serializer;
    }
    
    protected final JsonSerializer<Object> _findAndAddDynamic(final PropertySerializerMap map, final JavaType type, final SerializerProvider provider) throws JsonMappingException {
        final PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSecondarySerializer(type, provider, this._property);
        if (map != result.map) {
            this._dynamicValueSerializers = result.map;
        }
        return result.serializer;
    }
    
    protected Map<?, ?> _orderEntries(final Map<?, ?> input, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        if (input instanceof SortedMap) {
            return input;
        }
        if (this._hasNullKey(input)) {
            final TreeMap<Object, Object> result = new TreeMap<Object, Object>();
            for (final Map.Entry<?, ?> entry : input.entrySet()) {
                final Object key = entry.getKey();
                if (key == null) {
                    this._writeNullKeyedEntry(gen, provider, entry.getValue());
                }
                else {
                    result.put(key, entry.getValue());
                }
            }
            return result;
        }
        return new TreeMap<Object, Object>(input);
    }
    
    protected boolean _hasNullKey(final Map<?, ?> input) {
        return input instanceof HashMap && input.containsKey(null);
    }
    
    protected void _writeNullKeyedEntry(final JsonGenerator gen, final SerializerProvider provider, final Object value) throws IOException {
        final JsonSerializer<Object> keySerializer = provider.findNullKeySerializer(this._keyType, this._property);
        JsonSerializer<Object> valueSer;
        if (value == null) {
            if (this._suppressNulls) {
                return;
            }
            valueSer = provider.getDefaultNullValueSerializer();
        }
        else {
            valueSer = this._valueSerializer;
            if (valueSer == null) {
                valueSer = this._findSerializer(provider, value);
            }
            if (this._suppressableValue == MapSerializer.MARKER_FOR_EMPTY) {
                if (valueSer.isEmpty(provider, value)) {
                    return;
                }
            }
            else if (this._suppressableValue != null && this._suppressableValue.equals(value)) {
                return;
            }
        }
        try {
            keySerializer.serialize(null, gen, provider);
            valueSer.serialize(value, gen, provider);
        }
        catch (Exception e) {
            this.wrapAndThrow(provider, e, value, "");
        }
    }
    
    private final JsonSerializer<Object> _findSerializer(final SerializerProvider provider, final Object value) throws JsonMappingException {
        final Class<?> cc = value.getClass();
        final JsonSerializer<Object> valueSer = this._dynamicValueSerializers.serializerFor(cc);
        if (valueSer != null) {
            return valueSer;
        }
        if (this._valueType.hasGenericTypes()) {
            return this._findAndAddDynamic(this._dynamicValueSerializers, provider.constructSpecializedType(this._valueType, cc), provider);
        }
        return this._findAndAddDynamic(this._dynamicValueSerializers, cc, provider);
    }
    
    static {
        UNSPECIFIED_TYPE = TypeFactory.unknownType();
        MARKER_FOR_EMPTY = JsonInclude.Include.NON_EMPTY;
    }
}
