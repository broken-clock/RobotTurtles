// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public abstract class ContainerSerializer<T> extends StdSerializer<T>
{
    protected ContainerSerializer(final Class<T> t) {
        super(t);
    }
    
    protected ContainerSerializer(final JavaType fullType) {
        super(fullType);
    }
    
    protected ContainerSerializer(final Class<?> t, final boolean dummy) {
        super(t, dummy);
    }
    
    protected ContainerSerializer(final ContainerSerializer<?> src) {
        super(src._handledType, false);
    }
    
    public ContainerSerializer<?> withValueTypeSerializer(final TypeSerializer vts) {
        if (vts == null) {
            return this;
        }
        return this._withValueTypeSerializer(vts);
    }
    
    public abstract JavaType getContentType();
    
    public abstract JsonSerializer<?> getContentSerializer();
    
    public abstract boolean hasSingleElement(final T p0);
    
    protected abstract ContainerSerializer<?> _withValueTypeSerializer(final TypeSerializer p0);
    
    @Deprecated
    protected boolean hasContentTypeAnnotation(final SerializerProvider provider, final BeanProperty property) {
        return false;
    }
}
