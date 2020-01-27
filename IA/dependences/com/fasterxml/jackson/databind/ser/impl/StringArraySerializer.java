// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Type;
import java.io.IOException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.ArraySerializerBase;

@JacksonStdImpl
public class StringArraySerializer extends ArraySerializerBase<String[]> implements ContextualSerializer
{
    private static final JavaType VALUE_TYPE;
    public static final StringArraySerializer instance;
    protected final JsonSerializer<Object> _elementSerializer;
    
    protected StringArraySerializer() {
        super(String[].class);
        this._elementSerializer = null;
    }
    
    public StringArraySerializer(final StringArraySerializer src, final BeanProperty prop, final JsonSerializer<?> ser, final Boolean unwrapSingle) {
        super(src, prop, unwrapSingle);
        this._elementSerializer = (JsonSerializer<Object>)ser;
    }
    
    @Override
    public JsonSerializer<?> _withResolved(final BeanProperty prop, final Boolean unwrapSingle) {
        return new StringArraySerializer(this, prop, this._elementSerializer, unwrapSingle);
    }
    
    public ContainerSerializer<?> _withValueTypeSerializer(final TypeSerializer vts) {
        return this;
    }
    
    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider provider, final BeanProperty property) throws JsonMappingException {
        JsonSerializer<?> ser = null;
        if (property != null) {
            final AnnotationIntrospector ai = provider.getAnnotationIntrospector();
            final AnnotatedMember m = property.getMember();
            if (m != null) {
                final Object serDef = ai.findContentSerializer(m);
                if (serDef != null) {
                    ser = provider.serializerInstance(m, serDef);
                }
            }
        }
        final Boolean unwrapSingle = this.findFormatFeature(provider, property, String[].class, JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
        if (ser == null) {
            ser = this._elementSerializer;
        }
        ser = this.findContextualConvertingSerializer(provider, property, ser);
        if (ser == null) {
            ser = provider.findValueSerializer(String.class, property);
        }
        if (this.isDefaultSerializer(ser)) {
            ser = null;
        }
        if (ser == this._elementSerializer && unwrapSingle == this._unwrapSingle) {
            return this;
        }
        return new StringArraySerializer(this, property, ser, unwrapSingle);
    }
    
    @Override
    public JavaType getContentType() {
        return StringArraySerializer.VALUE_TYPE;
    }
    
    @Override
    public JsonSerializer<?> getContentSerializer() {
        return this._elementSerializer;
    }
    
    @Override
    public boolean isEmpty(final SerializerProvider prov, final String[] value) {
        return value.length == 0;
    }
    
    @Override
    public boolean hasSingleElement(final String[] value) {
        return value.length == 1;
    }
    
    @Override
    public final void serialize(final String[] value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        final int len = value.length;
        if (len == 1 && ((this._unwrapSingle == null && provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)) || this._unwrapSingle == Boolean.TRUE)) {
            this.serializeContents(value, gen, provider);
            return;
        }
        gen.writeStartArray(len);
        this.serializeContents(value, gen, provider);
        gen.writeEndArray();
    }
    
    public void serializeContents(final String[] value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        final int len = value.length;
        if (len == 0) {
            return;
        }
        if (this._elementSerializer != null) {
            this.serializeContentsSlow(value, gen, provider, this._elementSerializer);
            return;
        }
        for (int i = 0; i < len; ++i) {
            final String str = value[i];
            if (str == null) {
                gen.writeNull();
            }
            else {
                gen.writeString(value[i]);
            }
        }
    }
    
    private void serializeContentsSlow(final String[] value, final JsonGenerator gen, final SerializerProvider provider, final JsonSerializer<Object> ser) throws IOException {
        for (int i = 0, len = value.length; i < len; ++i) {
            final String str = value[i];
            if (str == null) {
                provider.defaultSerializeNull(gen);
            }
            else {
                ser.serialize(value[i], gen, provider);
            }
        }
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
        return this.createSchemaNode("array", true).set("items", this.createSchemaNode("string"));
    }
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        this.visitArrayFormat(visitor, typeHint, JsonFormatTypes.STRING);
    }
    
    static {
        VALUE_TYPE = TypeFactory.defaultInstance().uncheckedSimpleType(String.class);
        instance = new StringArraySerializer();
    }
}
