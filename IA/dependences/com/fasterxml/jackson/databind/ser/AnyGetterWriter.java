// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.databind.JsonMappingException;
import java.util.Map;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.std.MapSerializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.BeanProperty;

public class AnyGetterWriter
{
    protected final BeanProperty _property;
    protected final AnnotatedMember _accessor;
    protected JsonSerializer<Object> _serializer;
    protected MapSerializer _mapSerializer;
    
    public AnyGetterWriter(final BeanProperty property, final AnnotatedMember accessor, final JsonSerializer<?> serializer) {
        this._accessor = accessor;
        this._property = property;
        this._serializer = (JsonSerializer<Object>)serializer;
        if (serializer instanceof MapSerializer) {
            this._mapSerializer = (MapSerializer)serializer;
        }
    }
    
    public void fixAccess(final SerializationConfig config) {
        this._accessor.fixAccess(config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
    }
    
    public void getAndSerialize(final Object bean, final JsonGenerator gen, final SerializerProvider provider) throws Exception {
        final Object value = this._accessor.getValue(bean);
        if (value == null) {
            return;
        }
        if (!(value instanceof Map)) {
            provider.reportBadDefinition(this._property.getType(), String.format("Value returned by 'any-getter' %s() not java.util.Map but %s", this._accessor.getName(), value.getClass().getName()));
        }
        if (this._mapSerializer != null) {
            this._mapSerializer.serializeFields((Map<?, ?>)value, gen, provider);
            return;
        }
        this._serializer.serialize(value, gen, provider);
    }
    
    public void getAndFilter(final Object bean, final JsonGenerator gen, final SerializerProvider provider, final PropertyFilter filter) throws Exception {
        final Object value = this._accessor.getValue(bean);
        if (value == null) {
            return;
        }
        if (!(value instanceof Map)) {
            provider.reportBadDefinition(this._property.getType(), String.format("Value returned by 'any-getter' (%s()) not java.util.Map but %s", this._accessor.getName(), value.getClass().getName()));
        }
        if (this._mapSerializer != null) {
            this._mapSerializer.serializeFilteredAnyProperties(provider, gen, bean, (Map<?, ?>)value, filter, null);
            return;
        }
        this._serializer.serialize(value, gen, provider);
    }
    
    public void resolve(final SerializerProvider provider) throws JsonMappingException {
        if (this._serializer instanceof ContextualSerializer) {
            final JsonSerializer<?> ser = provider.handlePrimaryContextualization(this._serializer, this._property);
            this._serializer = (JsonSerializer<Object>)ser;
            if (ser instanceof MapSerializer) {
                this._mapSerializer = (MapSerializer)ser;
            }
        }
    }
}
