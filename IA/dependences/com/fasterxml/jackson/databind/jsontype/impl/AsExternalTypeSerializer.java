// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;

public class AsExternalTypeSerializer extends TypeSerializerBase
{
    protected final String _typePropertyName;
    
    public AsExternalTypeSerializer(final TypeIdResolver idRes, final BeanProperty property, final String propName) {
        super(idRes, property);
        this._typePropertyName = propName;
    }
    
    @Override
    public AsExternalTypeSerializer forProperty(final BeanProperty prop) {
        return (this._property == prop) ? this : new AsExternalTypeSerializer(this._idResolver, prop, this._typePropertyName);
    }
    
    @Override
    public String getPropertyName() {
        return this._typePropertyName;
    }
    
    @Override
    public JsonTypeInfo.As getTypeInclusion() {
        return JsonTypeInfo.As.EXTERNAL_PROPERTY;
    }
    
    protected final void _writeScalarPrefix(final Object value, final JsonGenerator g) throws IOException {
    }
    
    protected final void _writeObjectPrefix(final Object value, final JsonGenerator g) throws IOException {
        g.writeStartObject();
    }
    
    protected final void _writeArrayPrefix(final Object value, final JsonGenerator g) throws IOException {
        g.writeStartArray();
    }
    
    protected final void _writeScalarSuffix(final Object value, final JsonGenerator g, final String typeId) throws IOException {
        if (typeId != null) {
            g.writeStringField(this._typePropertyName, typeId);
        }
    }
    
    protected final void _writeObjectSuffix(final Object value, final JsonGenerator g, final String typeId) throws IOException {
        g.writeEndObject();
        if (typeId != null) {
            g.writeStringField(this._typePropertyName, typeId);
        }
    }
    
    protected final void _writeArraySuffix(final Object value, final JsonGenerator g, final String typeId) throws IOException {
        g.writeEndArray();
        if (typeId != null) {
            g.writeStringField(this._typePropertyName, typeId);
        }
    }
}
