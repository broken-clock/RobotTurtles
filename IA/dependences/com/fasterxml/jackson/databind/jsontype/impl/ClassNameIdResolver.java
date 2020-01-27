// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.jsontype.impl;

import java.util.Map;
import java.util.EnumMap;
import java.util.Collection;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.util.EnumSet;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.JavaType;

public class ClassNameIdResolver extends TypeIdResolverBase
{
    private static final String JAVA_UTIL_PKG = "java.util.";
    
    public ClassNameIdResolver(final JavaType baseType, final TypeFactory typeFactory) {
        super(baseType, typeFactory);
    }
    
    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CLASS;
    }
    
    public void registerSubtype(final Class<?> type, final String name) {
    }
    
    @Override
    public String idFromValue(final Object value) {
        return this._idFrom(value, value.getClass(), this._typeFactory);
    }
    
    @Override
    public String idFromValueAndType(final Object value, final Class<?> type) {
        return this._idFrom(value, type, this._typeFactory);
    }
    
    @Override
    public JavaType typeFromId(final DatabindContext context, final String id) throws IOException {
        return this._typeFromId(id, context);
    }
    
    protected JavaType _typeFromId(final String id, final DatabindContext ctxt) throws IOException {
        final JavaType t = ctxt.resolveSubType(this._baseType, id);
        if (t == null && ctxt instanceof DeserializationContext) {
            return ((DeserializationContext)ctxt).handleUnknownTypeId(this._baseType, id, this, "no such class found");
        }
        return t;
    }
    
    protected String _idFrom(final Object value, Class<?> cls, final TypeFactory typeFactory) {
        if (Enum.class.isAssignableFrom(cls) && !cls.isEnum()) {
            cls = cls.getSuperclass();
        }
        String str = cls.getName();
        if (str.startsWith("java.util.")) {
            if (value instanceof EnumSet) {
                final Class<?> enumClass = ClassUtil.findEnumType((EnumSet<?>)value);
                str = typeFactory.constructCollectionType(EnumSet.class, enumClass).toCanonical();
            }
            else if (value instanceof EnumMap) {
                final Class<?> enumClass = ClassUtil.findEnumType((EnumMap<?, ?>)value);
                final Class<?> valueClass = Object.class;
                str = typeFactory.constructMapType(EnumMap.class, enumClass, valueClass).toCanonical();
            }
        }
        else if (str.indexOf(36) >= 0) {
            final Class<?> outer = ClassUtil.getOuterClass(cls);
            if (outer != null) {
                final Class<?> staticType = this._baseType.getRawClass();
                if (ClassUtil.getOuterClass(staticType) == null) {
                    cls = this._baseType.getRawClass();
                    str = cls.getName();
                }
            }
        }
        return str;
    }
    
    @Override
    public String getDescForKnownTypeIds() {
        return "class name used as type id";
    }
}
