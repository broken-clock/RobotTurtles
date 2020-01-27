// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.std;

import java.io.IOException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.JsonGenerator;
import java.util.Iterator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;

@JacksonStdImpl
public class IterableSerializer extends AsArraySerializerBase<Iterable<?>>
{
    public IterableSerializer(final JavaType elemType, final boolean staticTyping, final TypeSerializer vts) {
        super(Iterable.class, elemType, staticTyping, vts, null);
    }
    
    public IterableSerializer(final IterableSerializer src, final BeanProperty property, final TypeSerializer vts, final JsonSerializer<?> valueSerializer, final Boolean unwrapSingle) {
        super(src, property, vts, valueSerializer, unwrapSingle);
    }
    
    public ContainerSerializer<?> _withValueTypeSerializer(final TypeSerializer vts) {
        return new IterableSerializer(this, this._property, vts, this._elementSerializer, this._unwrapSingle);
    }
    
    @Override
    public IterableSerializer withResolved(final BeanProperty property, final TypeSerializer vts, final JsonSerializer<?> elementSerializer, final Boolean unwrapSingle) {
        return new IterableSerializer(this, property, vts, elementSerializer, unwrapSingle);
    }
    
    @Override
    public boolean isEmpty(final SerializerProvider prov, final Iterable<?> value) {
        return !value.iterator().hasNext();
    }
    
    @Override
    public boolean hasSingleElement(final Iterable<?> value) {
        if (value != null) {
            final Iterator<?> it = value.iterator();
            if (it.hasNext()) {
                it.next();
                if (!it.hasNext()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public final void serialize(final Iterable<?> value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        if (((this._unwrapSingle == null && provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)) || this._unwrapSingle == Boolean.TRUE) && this.hasSingleElement(value)) {
            this.serializeContents(value, gen, provider);
            return;
        }
        gen.writeStartArray();
        this.serializeContents(value, gen, provider);
        gen.writeEndArray();
    }
    
    public void serializeContents(final Iterable<?> value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
        final Iterator<?> it = value.iterator();
        if (it.hasNext()) {
            final TypeSerializer typeSer = this._valueTypeSerializer;
            JsonSerializer<Object> prevSerializer = null;
            Class<?> prevClass = null;
            do {
                final Object elem = it.next();
                if (elem == null) {
                    provider.defaultSerializeNull(jgen);
                }
                else {
                    JsonSerializer<Object> currSerializer = this._elementSerializer;
                    if (currSerializer == null) {
                        final Class<?> cc = elem.getClass();
                        if (cc == prevClass) {
                            currSerializer = prevSerializer;
                        }
                        else {
                            currSerializer = (prevSerializer = provider.findValueSerializer(cc, this._property));
                            prevClass = cc;
                        }
                    }
                    if (typeSer == null) {
                        currSerializer.serialize(elem, jgen, provider);
                    }
                    else {
                        currSerializer.serializeWithType(elem, jgen, provider, typeSer);
                    }
                }
            } while (it.hasNext());
        }
    }
}
