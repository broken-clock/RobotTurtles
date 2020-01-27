// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import java.util.Iterator;
import java.io.IOException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.JavaType;
import java.util.Collection;

public class CollectionSerializer extends AsArraySerializerBase<Collection<?>>
{
    private static final long serialVersionUID = 1L;
    
    public CollectionSerializer(final JavaType elemType, final boolean staticTyping, final TypeSerializer vts, final JsonSerializer<Object> valueSerializer) {
        super(Collection.class, elemType, staticTyping, vts, valueSerializer);
    }
    
    @Deprecated
    public CollectionSerializer(final JavaType elemType, final boolean staticTyping, final TypeSerializer vts, final BeanProperty property, final JsonSerializer<Object> valueSerializer) {
        this(elemType, staticTyping, vts, valueSerializer);
    }
    
    public CollectionSerializer(final CollectionSerializer src, final BeanProperty property, final TypeSerializer vts, final JsonSerializer<?> valueSerializer, final Boolean unwrapSingle) {
        super(src, property, vts, valueSerializer, unwrapSingle);
    }
    
    public ContainerSerializer<?> _withValueTypeSerializer(final TypeSerializer vts) {
        return new CollectionSerializer(this, this._property, vts, this._elementSerializer, this._unwrapSingle);
    }
    
    @Override
    public CollectionSerializer withResolved(final BeanProperty property, final TypeSerializer vts, final JsonSerializer<?> elementSerializer, final Boolean unwrapSingle) {
        return new CollectionSerializer(this, property, vts, elementSerializer, unwrapSingle);
    }
    
    @Override
    public boolean isEmpty(final SerializerProvider prov, final Collection<?> value) {
        return value.isEmpty();
    }
    
    @Override
    public boolean hasSingleElement(final Collection<?> value) {
        return value.size() == 1;
    }
    
    @Override
    public final void serialize(final Collection<?> value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
        final int len = value.size();
        if (len == 1 && ((this._unwrapSingle == null && provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)) || this._unwrapSingle == Boolean.TRUE)) {
            this.serializeContents(value, g, provider);
            return;
        }
        g.writeStartArray(len);
        this.serializeContents(value, g, provider);
        g.writeEndArray();
    }
    
    public void serializeContents(final Collection<?> value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
        g.setCurrentValue(value);
        if (this._elementSerializer != null) {
            this.serializeContentsUsing(value, g, provider, this._elementSerializer);
            return;
        }
        final Iterator<?> it = value.iterator();
        if (!it.hasNext()) {
            return;
        }
        PropertySerializerMap serializers = this._dynamicSerializers;
        final TypeSerializer typeSer = this._valueTypeSerializer;
        int i = 0;
        try {
            do {
                final Object elem = it.next();
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
                ++i;
            } while (it.hasNext());
        }
        catch (Exception e) {
            this.wrapAndThrow(provider, e, value, i);
        }
    }
    
    public void serializeContentsUsing(final Collection<?> value, final JsonGenerator g, final SerializerProvider provider, final JsonSerializer<Object> ser) throws IOException {
        final Iterator<?> it = value.iterator();
        if (it.hasNext()) {
            final TypeSerializer typeSer = this._valueTypeSerializer;
            int i = 0;
            do {
                final Object elem = it.next();
                try {
                    if (elem == null) {
                        provider.defaultSerializeNull(g);
                    }
                    else if (typeSer == null) {
                        ser.serialize(elem, g, provider);
                    }
                    else {
                        ser.serializeWithType(elem, g, provider, typeSer);
                    }
                    ++i;
                }
                catch (Exception e) {
                    this.wrapAndThrow(provider, e, value, i);
                }
            } while (it.hasNext());
        }
    }
}
