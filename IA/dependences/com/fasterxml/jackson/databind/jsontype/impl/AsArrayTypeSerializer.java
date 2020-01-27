// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;

public class AsArrayTypeSerializer extends TypeSerializerBase
{
    public AsArrayTypeSerializer(final TypeIdResolver idRes, final BeanProperty property) {
        super(idRes, property);
    }
    
    @Override
    public AsArrayTypeSerializer forProperty(final BeanProperty prop) {
        return (this._property == prop) ? this : new AsArrayTypeSerializer(this._idResolver, prop);
    }
    
    @Override
    public JsonTypeInfo.As getTypeInclusion() {
        return JsonTypeInfo.As.WRAPPER_ARRAY;
    }
}
