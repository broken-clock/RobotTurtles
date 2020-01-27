// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;

public class AsPropertyTypeSerializer extends AsArrayTypeSerializer
{
    protected final String _typePropertyName;
    
    public AsPropertyTypeSerializer(final TypeIdResolver idRes, final BeanProperty property, final String propName) {
        super(idRes, property);
        this._typePropertyName = propName;
    }
    
    @Override
    public AsPropertyTypeSerializer forProperty(final BeanProperty prop) {
        return (this._property == prop) ? this : new AsPropertyTypeSerializer(this._idResolver, prop, this._typePropertyName);
    }
    
    @Override
    public String getPropertyName() {
        return this._typePropertyName;
    }
    
    @Override
    public JsonTypeInfo.As getTypeInclusion() {
        return JsonTypeInfo.As.PROPERTY;
    }
}
