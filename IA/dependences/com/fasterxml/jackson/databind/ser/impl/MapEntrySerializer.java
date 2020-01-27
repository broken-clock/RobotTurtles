// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import java.util.Map;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;

@JacksonStdImpl
public class MapEntrySerializer extends ContainerSerializer<Map.Entry<?, ?>> implements ContextualSerializer
{
    public static final Object MARKER_FOR_EMPTY;
    protected final BeanProperty _property;
    protected final boolean _valueTypeIsStatic;
    protected final JavaType _entryType;
    protected final JavaType _keyType;
    protected final JavaType _valueType;
    protected JsonSerializer<Object> _keySerializer;
    protected JsonSerializer<Object> _valueSerializer;
    protected final TypeSerializer _valueTypeSerializer;
    protected PropertySerializerMap _dynamicValueSerializers;
    protected final Object _suppressableValue;
    protected final boolean _suppressNulls;
    
    public MapEntrySerializer(final JavaType type, final JavaType keyType, final JavaType valueType, final boolean staticTyping, final TypeSerializer vts, final BeanProperty property) {
        super(type);
        this._entryType = type;
        this._keyType = keyType;
        this._valueType = valueType;
        this._valueTypeIsStatic = staticTyping;
        this._valueTypeSerializer = vts;
        this._property = property;
        this._dynamicValueSerializers = PropertySerializerMap.emptyForProperties();
        this._suppressableValue = null;
        this._suppressNulls = false;
    }
    
    @Deprecated
    protected MapEntrySerializer(final MapEntrySerializer src, final BeanProperty property, final TypeSerializer vts, final JsonSerializer<?> keySer, final JsonSerializer<?> valueSer) {
        this(src, property, vts, keySer, valueSer, src._suppressableValue, src._suppressNulls);
    }
    
    protected MapEntrySerializer(final MapEntrySerializer src, final BeanProperty property, final TypeSerializer vts, final JsonSerializer<?> keySer, final JsonSerializer<?> valueSer, final Object suppressableValue, final boolean suppressNulls) {
        super(Map.class, false);
        this._entryType = src._entryType;
        this._keyType = src._keyType;
        this._valueType = src._valueType;
        this._valueTypeIsStatic = src._valueTypeIsStatic;
        this._valueTypeSerializer = src._valueTypeSerializer;
        this._keySerializer = (JsonSerializer<Object>)keySer;
        this._valueSerializer = (JsonSerializer<Object>)valueSer;
        this._dynamicValueSerializers = PropertySerializerMap.emptyForProperties();
        this._property = src._property;
        this._suppressableValue = suppressableValue;
        this._suppressNulls = suppressNulls;
    }
    
    public ContainerSerializer<?> _withValueTypeSerializer(final TypeSerializer vts) {
        return new MapEntrySerializer(this, this._property, vts, this._keySerializer, this._valueSerializer, this._suppressableValue, this._suppressNulls);
    }
    
    public MapEntrySerializer withResolved(final BeanProperty property, final JsonSerializer<?> keySerializer, final JsonSerializer<?> valueSerializer, final Object suppressableValue, final boolean suppressNulls) {
        return new MapEntrySerializer(this, property, this._valueTypeSerializer, keySerializer, valueSerializer, suppressableValue, suppressNulls);
    }
    
    public MapEntrySerializer withContentInclusion(final Object suppressableValue, final boolean suppressNulls) {
        if (this._suppressableValue == suppressableValue && this._suppressNulls == suppressNulls) {
            return this;
        }
        return new MapEntrySerializer(this, this._property, this._valueTypeSerializer, this._keySerializer, this._valueSerializer, suppressableValue, suppressNulls);
    }
    
    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider provider, final BeanProperty property) throws JsonMappingException {
        JsonSerializer<?> ser = null;
        JsonSerializer<?> keySer = null;
        final AnnotationIntrospector intr = provider.getAnnotationIntrospector();
        final AnnotatedMember propertyAcc = (property == null) ? null : property.getMember();
        if (propertyAcc != null && intr != null) {
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
        Object valueToSuppress = this._suppressableValue;
        boolean suppressNulls = this._suppressNulls;
        if (property != null) {
            final JsonInclude.Value inclV = property.findPropertyInclusion(provider.getConfig(), null);
            if (inclV != null) {
                final JsonInclude.Include incl = inclV.getContentInclusion();
                if (incl != JsonInclude.Include.USE_DEFAULTS) {
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
                            valueToSuppress = (this._valueType.isReferenceType() ? MapEntrySerializer.MARKER_FOR_EMPTY : null);
                            break;
                        }
                        case NON_EMPTY: {
                            suppressNulls = true;
                            valueToSuppress = MapEntrySerializer.MARKER_FOR_EMPTY;
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
                }
            }
        }
        final MapEntrySerializer mser = this.withResolved(property, keySer, ser, valueToSuppress, suppressNulls);
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
    public boolean hasSingleElement(final Map.Entry<?, ?> value) {
        return true;
    }
    
    @Override
    public boolean isEmpty(final SerializerProvider prov, final Map.Entry<?, ?> entry) {
        final Object value = entry.getValue();
        if (value == null) {
            return this._suppressNulls;
        }
        if (this._suppressableValue == null) {
            return false;
        }
        JsonSerializer<Object> valueSer = this._valueSerializer;
        if (valueSer == null) {
            final Class<?> cc = value.getClass();
            valueSer = this._dynamicValueSerializers.serializerFor(cc);
            if (valueSer == null) {
                try {
                    valueSer = this._findAndAddDynamic(this._dynamicValueSerializers, cc, prov);
                }
                catch (JsonMappingException e) {
                    return false;
                }
            }
        }
        if (this._suppressableValue == MapEntrySerializer.MARKER_FOR_EMPTY) {
            return valueSer.isEmpty(prov, value);
        }
        return this._suppressableValue.equals(value);
    }
    
    @Override
    public void serialize(final Map.Entry<?, ?> value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        gen.writeStartObject(value);
        this.serializeDynamic(value, gen, provider);
        gen.writeEndObject();
    }
    
    @Override
    public void serializeWithType(final Map.Entry<?, ?> value, final JsonGenerator g, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
        g.setCurrentValue(value);
        final WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(value, JsonToken.START_OBJECT));
        this.serializeDynamic(value, g, provider);
        typeSer.writeTypeSuffix(g, typeIdDef);
    }
    
    protected void serializeDynamic(final Map.Entry<?, ?> value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        final TypeSerializer vts = this._valueTypeSerializer;
        final Object keyElem = value.getKey();
        JsonSerializer<Object> keySerializer;
        if (keyElem == null) {
            keySerializer = provider.findNullKeySerializer(this._keyType, this._property);
        }
        else {
            keySerializer = this._keySerializer;
        }
        final Object valueElem = value.getValue();
        JsonSerializer<Object> valueSer;
        if (valueElem == null) {
            if (this._suppressNulls) {
                return;
            }
            valueSer = provider.getDefaultNullValueSerializer();
        }
        else {
            valueSer = this._valueSerializer;
            if (valueSer == null) {
                final Class<?> cc = valueElem.getClass();
                valueSer = this._dynamicValueSerializers.serializerFor(cc);
                if (valueSer == null) {
                    if (this._valueType.hasGenericTypes()) {
                        valueSer = this._findAndAddDynamic(this._dynamicValueSerializers, provider.constructSpecializedType(this._valueType, cc), provider);
                    }
                    else {
                        valueSer = this._findAndAddDynamic(this._dynamicValueSerializers, cc, provider);
                    }
                }
            }
            if (this._suppressableValue != null) {
                if (this._suppressableValue == MapEntrySerializer.MARKER_FOR_EMPTY && valueSer.isEmpty(provider, valueElem)) {
                    return;
                }
                if (this._suppressableValue.equals(valueElem)) {
                    return;
                }
            }
        }
        keySerializer.serialize(keyElem, gen, provider);
        try {
            if (vts == null) {
                valueSer.serialize(valueElem, gen, provider);
            }
            else {
                valueSer.serializeWithType(valueElem, gen, provider, vts);
            }
        }
        catch (Exception e) {
            final String keyDesc = "" + keyElem;
            this.wrapAndThrow(provider, e, value, keyDesc);
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
    
    static {
        MARKER_FOR_EMPTY = JsonInclude.Include.NON_EMPTY;
    }
}
