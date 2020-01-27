// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.impl;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import java.util.Iterator;
import com.fasterxml.jackson.databind.ser.std.AsArraySerializerBase;

@JacksonStdImpl
public class IteratorSerializer extends AsArraySerializerBase<Iterator<?>>
{
    public IteratorSerializer(final JavaType elemType, final boolean staticTyping, final TypeSerializer vts) {
        super(Iterator.class, elemType, staticTyping, vts, null);
    }
    
    public IteratorSerializer(final IteratorSerializer src, final BeanProperty property, final TypeSerializer vts, final JsonSerializer<?> valueSerializer, final Boolean unwrapSingle) {
        super(src, property, vts, valueSerializer, unwrapSingle);
    }
    
    @Override
    public boolean isEmpty(final SerializerProvider prov, final Iterator<?> value) {
        return !value.hasNext();
    }
    
    @Override
    public boolean hasSingleElement(final Iterator<?> value) {
        return false;
    }
    
    public ContainerSerializer<?> _withValueTypeSerializer(final TypeSerializer vts) {
        return new IteratorSerializer(this, this._property, vts, this._elementSerializer, this._unwrapSingle);
    }
    
    @Override
    public IteratorSerializer withResolved(final BeanProperty property, final TypeSerializer vts, final JsonSerializer<?> elementSerializer, final Boolean unwrapSingle) {
        return new IteratorSerializer(this, property, vts, elementSerializer, unwrapSingle);
    }
    
    @Override
    public final void serialize(final Iterator<?> value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        gen.writeStartArray();
        this.serializeContents(value, gen, provider);
        gen.writeEndArray();
    }
    
    public void serializeContents(final Iterator<?> value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
        if (!value.hasNext()) {
            return;
        }
        final JsonSerializer<Object> serializer = this._elementSerializer;
        if (serializer == null) {
            this._serializeDynamicContents(value, g, provider);
            return;
        }
        final TypeSerializer typeSer = this._valueTypeSerializer;
        do {
            final Object elem = value.next();
            if (elem == null) {
                provider.defaultSerializeNull(g);
            }
            else if (typeSer == null) {
                serializer.serialize(elem, g, provider);
            }
            else {
                serializer.serializeWithType(elem, g, provider, typeSer);
            }
        } while (value.hasNext());
    }
    
    protected void _serializeDynamicContents(final Iterator<?> value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
        final TypeSerializer typeSer = this._valueTypeSerializer;
        PropertySerializerMap serializers = this._dynamicSerializers;
        do {
            final Object elem = value.next();
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
                if (typeSer == null) {
                    serializer.serialize(elem, g, provider);
                }
                else {
                    serializer.serializeWithType(elem, g, provider, typeSer);
                }
            }
        } while (value.hasNext());
    }
}
