// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.jsontype.impl;

import java.io.IOException;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

public abstract class TypeSerializerBase extends TypeSerializer
{
    protected final TypeIdResolver _idResolver;
    protected final BeanProperty _property;
    
    protected TypeSerializerBase(final TypeIdResolver idRes, final BeanProperty property) {
        this._idResolver = idRes;
        this._property = property;
    }
    
    @Override
    public abstract JsonTypeInfo.As getTypeInclusion();
    
    @Override
    public String getPropertyName() {
        return null;
    }
    
    @Override
    public TypeIdResolver getTypeIdResolver() {
        return this._idResolver;
    }
    
    @Override
    public WritableTypeId writeTypePrefix(final JsonGenerator g, final WritableTypeId idMetadata) throws IOException {
        this._generateTypeId(idMetadata);
        return g.writeTypePrefix(idMetadata);
    }
    
    @Override
    public WritableTypeId writeTypeSuffix(final JsonGenerator g, final WritableTypeId idMetadata) throws IOException {
        return g.writeTypeSuffix(idMetadata);
    }
    
    protected void _generateTypeId(final WritableTypeId idMetadata) {
        Object id = idMetadata.id;
        if (id == null) {
            final Object value = idMetadata.forValue;
            final Class<?> typeForId = idMetadata.forValueType;
            if (typeForId == null) {
                id = this.idFromValue(value);
            }
            else {
                id = this.idFromValueAndType(value, typeForId);
            }
            idMetadata.id = id;
        }
    }
    
    protected String idFromValue(final Object value) {
        final String id = this._idResolver.idFromValue(value);
        if (id == null) {
            this.handleMissingId(value);
        }
        return id;
    }
    
    protected String idFromValueAndType(final Object value, final Class<?> type) {
        final String id = this._idResolver.idFromValueAndType(value, type);
        if (id == null) {
            this.handleMissingId(value);
        }
        return id;
    }
    
    protected void handleMissingId(final Object value) {
    }
}
