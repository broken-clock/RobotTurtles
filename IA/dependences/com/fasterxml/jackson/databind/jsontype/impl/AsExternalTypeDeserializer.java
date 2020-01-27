// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.JavaType;

public class AsExternalTypeDeserializer extends AsArrayTypeDeserializer
{
    private static final long serialVersionUID = 1L;
    
    public AsExternalTypeDeserializer(final JavaType bt, final TypeIdResolver idRes, final String typePropertyName, final boolean typeIdVisible, final JavaType defaultImpl) {
        super(bt, idRes, typePropertyName, typeIdVisible, defaultImpl);
    }
    
    public AsExternalTypeDeserializer(final AsExternalTypeDeserializer src, final BeanProperty property) {
        super(src, property);
    }
    
    @Override
    public TypeDeserializer forProperty(final BeanProperty prop) {
        if (prop == this._property) {
            return this;
        }
        return new AsExternalTypeDeserializer(this, prop);
    }
    
    @Override
    public JsonTypeInfo.As getTypeInclusion() {
        return JsonTypeInfo.As.EXTERNAL_PROPERTY;
    }
    
    @Override
    protected boolean _usesExternalId() {
        return true;
    }
}
