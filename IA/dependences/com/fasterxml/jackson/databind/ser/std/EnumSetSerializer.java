// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import java.util.Iterator;
import java.io.IOException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.JavaType;
import java.util.EnumSet;

public class EnumSetSerializer extends AsArraySerializerBase<EnumSet<? extends Enum<?>>>
{
    public EnumSetSerializer(final JavaType elemType) {
        super(EnumSet.class, elemType, true, null, null);
    }
    
    public EnumSetSerializer(final EnumSetSerializer src, final BeanProperty property, final TypeSerializer vts, final JsonSerializer<?> valueSerializer, final Boolean unwrapSingle) {
        super(src, property, vts, valueSerializer, unwrapSingle);
    }
    
    public EnumSetSerializer _withValueTypeSerializer(final TypeSerializer vts) {
        return this;
    }
    
    @Override
    public EnumSetSerializer withResolved(final BeanProperty property, final TypeSerializer vts, final JsonSerializer<?> elementSerializer, final Boolean unwrapSingle) {
        return new EnumSetSerializer(this, property, vts, elementSerializer, unwrapSingle);
    }
    
    @Override
    public boolean isEmpty(final SerializerProvider prov, final EnumSet<? extends Enum<?>> value) {
        return value.isEmpty();
    }
    
    @Override
    public boolean hasSingleElement(final EnumSet<? extends Enum<?>> value) {
        return value.size() == 1;
    }
    
    @Override
    public final void serialize(final EnumSet<? extends Enum<?>> value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        final int len = value.size();
        if (len == 1 && ((this._unwrapSingle == null && provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)) || this._unwrapSingle == Boolean.TRUE)) {
            this.serializeContents(value, gen, provider);
            return;
        }
        gen.writeStartArray(len);
        this.serializeContents(value, gen, provider);
        gen.writeEndArray();
    }
    
    public void serializeContents(final EnumSet<? extends Enum<?>> value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        JsonSerializer<Object> enumSer = this._elementSerializer;
        for (final Enum<?> en : value) {
            if (enumSer == null) {
                enumSer = provider.findValueSerializer(en.getDeclaringClass(), this._property);
            }
            enumSer.serialize(en, gen, provider);
        }
    }
}
