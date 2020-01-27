// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;

public class AsWrapperTypeSerializer extends TypeSerializerBase
{
    public AsWrapperTypeSerializer(final TypeIdResolver idRes, final BeanProperty property) {
        super(idRes, property);
    }
    
    @Override
    public AsWrapperTypeSerializer forProperty(final BeanProperty prop) {
        return (this._property == prop) ? this : new AsWrapperTypeSerializer(this._idResolver, prop);
    }
    
    @Override
    public JsonTypeInfo.As getTypeInclusion() {
        return JsonTypeInfo.As.WRAPPER_OBJECT;
    }
    
    protected String _validTypeId(final String typeId) {
        return ClassUtil.nonNullString(typeId);
    }
    
    protected final void _writeTypeId(final JsonGenerator g, final String typeId) throws IOException {
        if (typeId != null) {
            g.writeTypeId(typeId);
        }
    }
}
