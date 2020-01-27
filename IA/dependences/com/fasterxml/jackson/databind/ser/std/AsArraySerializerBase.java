// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.fasterxml.jackson.databind.jsonschema.SchemaAware;
import com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Type;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;

public abstract class AsArraySerializerBase<T> extends ContainerSerializer<T> implements ContextualSerializer
{
    protected final JavaType _elementType;
    protected final BeanProperty _property;
    protected final boolean _staticTyping;
    protected final Boolean _unwrapSingle;
    protected final TypeSerializer _valueTypeSerializer;
    protected final JsonSerializer<Object> _elementSerializer;
    protected PropertySerializerMap _dynamicSerializers;
    
    protected AsArraySerializerBase(final Class<?> cls, final JavaType et, final boolean staticTyping, final TypeSerializer vts, final JsonSerializer<Object> elementSerializer) {
        super(cls, false);
        this._elementType = et;
        this._staticTyping = (staticTyping || (et != null && et.isFinal()));
        this._valueTypeSerializer = vts;
        this._property = null;
        this._elementSerializer = elementSerializer;
        this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
        this._unwrapSingle = null;
    }
    
    @Deprecated
    protected AsArraySerializerBase(final Class<?> cls, final JavaType et, final boolean staticTyping, final TypeSerializer vts, final BeanProperty property, final JsonSerializer<Object> elementSerializer) {
        super(cls, false);
        this._elementType = et;
        this._staticTyping = (staticTyping || (et != null && et.isFinal()));
        this._valueTypeSerializer = vts;
        this._property = property;
        this._elementSerializer = elementSerializer;
        this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
        this._unwrapSingle = null;
    }
    
    protected AsArraySerializerBase(final AsArraySerializerBase<?> src, final BeanProperty property, final TypeSerializer vts, final JsonSerializer<?> elementSerializer, final Boolean unwrapSingle) {
        super(src);
        this._elementType = src._elementType;
        this._staticTyping = src._staticTyping;
        this._valueTypeSerializer = vts;
        this._property = property;
        this._elementSerializer = (JsonSerializer<Object>)elementSerializer;
        this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
        this._unwrapSingle = unwrapSingle;
    }
    
    @Deprecated
    protected AsArraySerializerBase(final AsArraySerializerBase<?> src, final BeanProperty property, final TypeSerializer vts, final JsonSerializer<?> elementSerializer) {
        this(src, property, vts, elementSerializer, src._unwrapSingle);
    }
    
    @Deprecated
    public final AsArraySerializerBase<T> withResolved(final BeanProperty property, final TypeSerializer vts, final JsonSerializer<?> elementSerializer) {
        return this.withResolved(property, vts, elementSerializer, this._unwrapSingle);
    }
    
    public abstract AsArraySerializerBase<T> withResolved(final BeanProperty p0, final TypeSerializer p1, final JsonSerializer<?> p2, final Boolean p3);
    
    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider serializers, final BeanProperty property) throws JsonMappingException {
        TypeSerializer typeSer = this._valueTypeSerializer;
        if (typeSer != null) {
            typeSer = typeSer.forProperty(property);
        }
        JsonSerializer<?> ser = null;
        Boolean unwrapSingle = null;
        if (property != null) {
            final AnnotationIntrospector intr = serializers.getAnnotationIntrospector();
            final AnnotatedMember m = property.getMember();
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
        if (ser != this._elementSerializer || property != this._property || this._valueTypeSerializer != typeSer || this._unwrapSingle != unwrapSingle) {
            return this.withResolved(property, typeSer, ser, unwrapSingle);
        }
        return this;
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
    public void serialize(final T value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        if (provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED) && this.hasSingleElement(value)) {
            this.serializeContents(value, gen, provider);
            return;
        }
        gen.writeStartArray();
        gen.setCurrentValue(value);
        this.serializeContents(value, gen, provider);
        gen.writeEndArray();
    }
    
    @Override
    public void serializeWithType(final T value, final JsonGenerator g, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
        g.setCurrentValue(value);
        final WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(value, JsonToken.START_ARRAY));
        this.serializeContents(value, g, provider);
        typeSer.writeTypeSuffix(g, typeIdDef);
    }
    
    protected abstract void serializeContents(final T p0, final JsonGenerator p1, final SerializerProvider p2) throws IOException;
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) throws JsonMappingException {
        final ObjectNode o = this.createSchemaNode("array", true);
        if (this._elementSerializer != null) {
            JsonNode schemaNode = null;
            if (this._elementSerializer instanceof SchemaAware) {
                schemaNode = ((SchemaAware)this._elementSerializer).getSchema(provider, null);
            }
            if (schemaNode == null) {
                schemaNode = JsonSchema.getDefaultSchemaNode();
            }
            o.set("items", schemaNode);
        }
        return o;
    }
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        JsonSerializer<?> valueSer = this._elementSerializer;
        if (valueSer == null && this._elementType != null) {
            valueSer = visitor.getProvider().findValueSerializer(this._elementType, this._property);
        }
        this.visitArrayFormat(visitor, typeHint, valueSer, this._elementType);
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
