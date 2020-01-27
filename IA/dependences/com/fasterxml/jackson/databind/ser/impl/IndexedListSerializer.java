// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.impl;

import java.io.IOException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import java.util.List;
import com.fasterxml.jackson.databind.ser.std.AsArraySerializerBase;

@JacksonStdImpl
public final class IndexedListSerializer extends AsArraySerializerBase<List<?>>
{
    private static final long serialVersionUID = 1L;
    
    public IndexedListSerializer(final JavaType elemType, final boolean staticTyping, final TypeSerializer vts, final JsonSerializer<Object> valueSerializer) {
        super(List.class, elemType, staticTyping, vts, valueSerializer);
    }
    
    public IndexedListSerializer(final IndexedListSerializer src, final BeanProperty property, final TypeSerializer vts, final JsonSerializer<?> valueSerializer, final Boolean unwrapSingle) {
        super(src, property, vts, valueSerializer, unwrapSingle);
    }
    
    @Override
    public IndexedListSerializer withResolved(final BeanProperty property, final TypeSerializer vts, final JsonSerializer<?> elementSerializer, final Boolean unwrapSingle) {
        return new IndexedListSerializer(this, property, vts, elementSerializer, unwrapSingle);
    }
    
    @Override
    public boolean isEmpty(final SerializerProvider prov, final List<?> value) {
        return value.isEmpty();
    }
    
    @Override
    public boolean hasSingleElement(final List<?> value) {
        return value.size() == 1;
    }
    
    public ContainerSerializer<?> _withValueTypeSerializer(final TypeSerializer vts) {
        return new IndexedListSerializer(this, this._property, vts, this._elementSerializer, this._unwrapSingle);
    }
    
    @Override
    public final void serialize(final List<?> value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        final int len = value.size();
        if (len == 1 && ((this._unwrapSingle == null && provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)) || this._unwrapSingle == Boolean.TRUE)) {
            this.serializeContents(value, gen, provider);
            return;
        }
        gen.writeStartArray(len);
        this.serializeContents(value, gen, provider);
        gen.writeEndArray();
    }
    
    public void serializeContents(final List<?> value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
        if (this._elementSerializer != null) {
            this.serializeContentsUsing(value, g, provider, this._elementSerializer);
            return;
        }
        if (this._valueTypeSerializer != null) {
            this.serializeTypedContents(value, g, provider);
            return;
        }
        final int len = value.size();
        if (len == 0) {
            return;
        }
        int i = 0;
        try {
            PropertySerializerMap serializers = this._dynamicSerializers;
            while (i < len) {
                final Object elem = value.get(i);
                if (elem == null) {
                    provider.defaultSerializeNull(g);
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
                        serializers = this._dynamicSerializers;
                    }
                    serializer.serialize(elem, g, provider);
                }
                ++i;
            }
        }
        catch (Exception e) {
            this.wrapAndThrow(provider, e, value, i);
        }
    }
    
    public void serializeContentsUsing(final List<?> value, final JsonGenerator jgen, final SerializerProvider provider, final JsonSerializer<Object> ser) throws IOException {
        final int len = value.size();
        if (len == 0) {
            return;
        }
        final TypeSerializer typeSer = this._valueTypeSerializer;
        for (int i = 0; i < len; ++i) {
            final Object elem = value.get(i);
            try {
                if (elem == null) {
                    provider.defaultSerializeNull(jgen);
                }
                else if (typeSer == null) {
                    ser.serialize(elem, jgen, provider);
                }
                else {
                    ser.serializeWithType(elem, jgen, provider, typeSer);
                }
            }
            catch (Exception e) {
                this.wrapAndThrow(provider, e, value, i);
            }
        }
    }
    
    public void serializeTypedContents(final List<?> value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
        final int len = value.size();
        if (len == 0) {
            return;
        }
        int i = 0;
        try {
            final TypeSerializer typeSer = this._valueTypeSerializer;
            PropertySerializerMap serializers = this._dynamicSerializers;
            while (i < len) {
                final Object elem = value.get(i);
                if (elem == null) {
                    provider.defaultSerializeNull(jgen);
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
                        serializers = this._dynamicSerializers;
                    }
                    serializer.serializeWithType(elem, jgen, provider, typeSer);
                }
                ++i;
            }
        }
        catch (Exception e) {
            this.wrapAndThrow(provider, e, value, i);
        }
    }
}
