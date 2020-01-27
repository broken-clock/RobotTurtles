// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import java.util.Iterator;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;

public abstract class JsonSerializer<T> implements JsonFormatVisitable
{
    public JsonSerializer<T> unwrappingSerializer(final NameTransformer unwrapper) {
        return this;
    }
    
    public JsonSerializer<T> replaceDelegatee(final JsonSerializer<?> delegatee) {
        throw new UnsupportedOperationException();
    }
    
    public JsonSerializer<?> withFilterId(final Object filterId) {
        return this;
    }
    
    public abstract void serialize(final T p0, final JsonGenerator p1, final SerializerProvider p2) throws IOException;
    
    public void serializeWithType(final T value, final JsonGenerator gen, final SerializerProvider serializers, final TypeSerializer typeSer) throws IOException {
        Class<?> clz = this.handledType();
        if (clz == null) {
            clz = value.getClass();
        }
        serializers.reportBadDefinition(clz, String.format("Type id handling not implemented for type %s (by serializer of type %s)", clz.getName(), this.getClass().getName()));
    }
    
    public Class<T> handledType() {
        return null;
    }
    
    @Deprecated
    public boolean isEmpty(final T value) {
        return this.isEmpty(null, value);
    }
    
    public boolean isEmpty(final SerializerProvider provider, final T value) {
        return value == null;
    }
    
    public boolean usesObjectId() {
        return false;
    }
    
    public boolean isUnwrappingSerializer() {
        return false;
    }
    
    public JsonSerializer<?> getDelegatee() {
        return null;
    }
    
    public Iterator<PropertyWriter> properties() {
        return ClassUtil.emptyIterator();
    }
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType type) throws JsonMappingException {
        visitor.expectAnyFormat(type);
    }
    
    public abstract static class None extends JsonSerializer<Object>
    {
    }
}
