// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import java.io.IOException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

@JacksonStdImpl
public class ObjectArraySerializer extends ArraySerializerBase<Object[]> implements ContextualSerializer
{
    protected final boolean _staticTyping;
    protected final JavaType _elementType;
    protected final TypeSerializer _valueTypeSerializer;
    protected JsonSerializer<Object> _elementSerializer;
    protected PropertySerializerMap _dynamicSerializers;
    
    public ObjectArraySerializer(final JavaType elemType, final boolean staticTyping, final TypeSerializer vts, final JsonSerializer<Object> elementSerializer) {
        super(Object[].class);
        this._elementType = elemType;
        this._staticTyping = staticTyping;
        this._valueTypeSerializer = vts;
        this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
        this._elementSerializer = elementSerializer;
    }
    
    public ObjectArraySerializer(final ObjectArraySerializer src, final TypeSerializer vts) {
        super(src);
        this._elementType = src._elementType;
        this._valueTypeSerializer = vts;
        this._staticTyping = src._staticTyping;
        this._dynamicSerializers = src._dynamicSerializers;
        this._elementSerializer = src._elementSerializer;
    }
    
    public ObjectArraySerializer(final ObjectArraySerializer src, final BeanProperty property, final TypeSerializer vts, final JsonSerializer<?> elementSerializer, final Boolean unwrapSingle) {
        super(src, property, unwrapSingle);
        this._elementType = src._elementType;
        this._valueTypeSerializer = vts;
        this._staticTyping = src._staticTyping;
        this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
        this._elementSerializer = (JsonSerializer<Object>)elementSerializer;
    }
    
    @Override
    public JsonSerializer<?> _withResolved(final BeanProperty prop, final Boolean unwrapSingle) {
        return new ObjectArraySerializer(this, prop, this._valueTypeSerializer, this._elementSerializer, unwrapSingle);
    }
    
    public ContainerSerializer<?> _withValueTypeSerializer(final TypeSerializer vts) {
        return new ObjectArraySerializer(this._elementType, this._staticTyping, vts, this._elementSerializer);
    }
    
    public ObjectArraySerializer withResolved(final BeanProperty prop, final TypeSerializer vts, final JsonSerializer<?> ser, final Boolean unwrapSingle) {
        if (this._property == prop && ser == this._elementSerializer && this._valueTypeSerializer == vts && this._unwrapSingle == unwrapSingle) {
            return this;
        }
        return new ObjectArraySerializer(this, prop, vts, ser, unwrapSingle);
    }
    
    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider serializers, final BeanProperty property) throws JsonMappingException {
        TypeSerializer vts = this._valueTypeSerializer;
        if (vts != null) {
            vts = vts.forProperty(property);
        }
        JsonSerializer<?> ser = null;
        Boolean unwrapSingle = null;
        if (property != null) {
            final AnnotatedMember m = property.getMember();
            final AnnotationIntrospector intr = serializers.getAnnotationIntrospector();
            if (m != null) {
                final Object serDef = intr.findContentSerializer(m);
                if (serDef != null) {
                    ser = serializers.serializerInstance(m, serDef);
                }
            }
        }
        final JsonFormat.Value format = this.findFormatOverrides(serializers, property, this.handledType());
        if (format != null) {
            unwrapSingle = format.getFeature(JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
        }
        if (ser == null) {
            ser = this._elementSerializer;
        }
        ser = this.findContextualConvertingSerializer(serializers, property, ser);
        if (ser == null && this._elementType != null && this._staticTyping && !this._elementType.isJavaLangObject()) {
            ser = serializers.findValueSerializer(this._elementType, property);
        }
        return this.withResolved(property, vts, ser, unwrapSingle);
    }
    
    @Override
    public JavaType getContentType() {
        return this._elementType;
    }
    
    @Override
    public JsonSerializer<?> getContentSerializer() {
        return this._elementSerializer;
    }
    
    @Override
    public boolean isEmpty(final SerializerProvider prov, final Object[] value) {
        return value.length == 0;
    }
    
    @Override
    public boolean hasSingleElement(final Object[] value) {
        return value.length == 1;
    }
    
    @Override
    public final void serialize(final Object[] value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        final int len = value.length;
        if (len == 1 && ((this._unwrapSingle == null && provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)) || this._unwrapSingle == Boolean.TRUE)) {
            this.serializeContents(value, gen, provider);
            return;
        }
        gen.writeStartArray(len);
        this.serializeContents(value, gen, provider);
        gen.writeEndArray();
    }
    
    public void serializeContents(final Object[] value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        final int len = value.length;
        if (len == 0) {
            return;
        }
        if (this._elementSerializer != null) {
            this.serializeContentsUsing(value, gen, provider, this._elementSerializer);
            return;
        }
        if (this._valueTypeSerializer != null) {
            this.serializeTypedContents(value, gen, provider);
            return;
        }
        int i = 0;
        Object elem = null;
        try {
            final PropertySerializerMap serializers = this._dynamicSerializers;
            while (i < len) {
                elem = value[i];
                if (elem == null) {
                    provider.defaultSerializeNull(gen);
                }
                else {
                    final Class<?> cc = elem.getClass();
                    JsonSerializer<Object> serializer = serializers.serializerFor(cc);
                    if (serializer == null) {
                        if (this._elementType.hasGenericTypes()) {
                            serializer = this._findAndAddDynamic(serializers, provider.constructSpecializedType(this._elementType, cc), provider);
                        }
                        else {
                            serializer = this._findAndAddDynamic(serializers, cc, provider);
                        }
                    }
                    serializer.serialize(elem, gen, provider);
                }
                ++i;
            }
        }
        catch (Exception e) {
            this.wrapAndThrow(provider, e, elem, i);
        }
    }
    
    public void serializeContentsUsing(final Object[] value, final JsonGenerator jgen, final SerializerProvider provider, final JsonSerializer<Object> ser) throws IOException {
        final int len = value.length;
        final TypeSerializer typeSer = this._valueTypeSerializer;
        int i = 0;
        Object elem = null;
        try {
            while (i < len) {
                elem = value[i];
                if (elem == null) {
                    provider.defaultSerializeNull(jgen);
                }
                else if (typeSer == null) {
                    ser.serialize(elem, jgen, provider);
                }
                else {
                    ser.serializeWithType(elem, jgen, provider, typeSer);
                }
                ++i;
            }
        }
        catch (Exception e) {
            this.wrapAndThrow(provider, e, elem, i);
        }
    }
    
    public void serializeTypedContents(final Object[] value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
        final int len = value.length;
        final TypeSerializer typeSer = this._valueTypeSerializer;
        int i = 0;
        Object elem = null;
        try {
            final PropertySerializerMap serializers = this._dynamicSerializers;
            while (i < len) {
                elem = value[i];
                if (elem == null) {
                    provider.defaultSerializeNull(jgen);
                }
                else {
                    final Class<?> cc = elem.getClass();
                    JsonSerializer<Object> serializer = serializers.serializerFor(cc);
                    if (serializer == null) {
                        serializer = this._findAndAddDynamic(serializers, cc, provider);
                    }
                    serializer.serializeWithType(elem, jgen, provider, typeSer);
                }
                ++i;
            }
        }
        catch (Exception e) {
            this.wrapAndThrow(provider, e, elem, i);
        }
    }
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        final JsonArrayFormatVisitor arrayVisitor = visitor.expectArrayFormat(typeHint);
        if (arrayVisitor != null) {
            final JavaType contentType = this._elementType;
            JsonSerializer<?> valueSer = this._elementSerializer;
            if (valueSer == null) {
                valueSer = visitor.getProvider().findValueSerializer(contentType, this._property);
            }
            arrayVisitor.itemsFormat(valueSer, contentType);
        }
    }
    
    protected final JsonSerializer<Object> _findAndAddDynamic(final PropertySerializerMap map, final Class<?> type, final SerializerProvider provider) throws JsonMappingException {
        final PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSecondarySerializer(type, provider, this._property);
        if (map != result.map) {
            this._dynamicSerializers = result.map;
        }
        return result.serializer;
    }
    
    protected final JsonSerializer<Object> _findAndAddDynamic(final PropertySerializerMap map, final JavaType type, final SerializerProvider provider) throws JsonMappingException {
        final PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSecondarySerializer(type, provider, this._property);
        if (map != result.map) {
            this._dynamicSerializers = result.map;
        }
        return result.serializer;
    }
}
