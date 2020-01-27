// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;

public class AsExistingPropertyTypeSerializer extends AsPropertyTypeSerializer
{
    public AsExistingPropertyTypeSerializer(final TypeIdResolver idRes, final BeanProperty property, final String propName) {
        super(idRes, property, propName);
    }
    
    @Override
    public AsExistingPropertyTypeSerializer forProperty(final BeanProperty prop) {
        return (this._property == prop) ? this : new AsExistingPropertyTypeSerializer(this._idResolver, prop, this._typePropertyName);
    }
    
    @Override
    public JsonTypeInfo.As getTypeInclusion() {
        return JsonTypeInfo.As.EXISTING_PROPERTY;
    }
}
