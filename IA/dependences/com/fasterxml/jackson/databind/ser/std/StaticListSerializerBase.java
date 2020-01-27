// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.std;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.lang.reflect.Type;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import java.util.Collection;

public abstract class StaticListSerializerBase<T extends Collection<?>> extends StdSerializer<T> implements ContextualSerializer
{
    protected final Boolean _unwrapSingle;
    
    protected StaticListSerializerBase(final Class<?> cls) {
        super(cls, false);
        this._unwrapSingle = null;
    }
    
    protected StaticListSerializerBase(final StaticListSerializerBase<?> src, final Boolean unwrapSingle) {
        super(src);
        this._unwrapSingle = unwrapSingle;
    }
    
    public abstract JsonSerializer<?> _withResolved(final BeanProperty p0, final Boolean p1);
    
    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider serializers, final BeanProperty property) throws JsonMappingException {
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
        ser = this.findContextualConvertingSerializer(serializers, property, ser);
        if (ser == null) {
            ser = serializers.findValueSerializer(String.class, property);
        }
        if (!this.isDefaultSerializer(ser)) {
            return new CollectionSerializer(serializers.constructType(String.class), true, null, (JsonSerializer<Object>)ser);
        }
        if (unwrapSingle == this._unwrapSingle) {
            return this;
        }
        return this._withResolved(property, unwrapSingle);
    }
    
    @Override
    public boolean isEmpty(final SerializerProvider provider, final T value) {
        return value == null || value.size() == 0;
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
        return this.createSchemaNode("array", true).set("items", this.contentSchema());
    }
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        final JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
        if (v2 != null) {
            this.acceptContentVisitor(v2);
        }
    }
    
    protected abstract JsonNode contentSchema();
    
    protected abstract void acceptContentVisitor(final JsonArrayFormatVisitor p0) throws JsonMappingException;
    
    @Override
    public abstract void serializeWithType(final T p0, final JsonGenerator p1, final SerializerProvider p2, final TypeSerializer p3) throws IOException;
}
