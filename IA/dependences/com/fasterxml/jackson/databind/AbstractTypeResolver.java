// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind;

public abstract class AbstractTypeResolver
{
    public JavaType findTypeMapping(final DeserializationConfig config, final JavaType type) {
        return null;
    }
    
    @Deprecated
    public JavaType resolveAbstractType(final DeserializationConfig config, final JavaType type) {
        return null;
    }
    
    public JavaType resolveAbstractType(final DeserializationConfig config, final BeanDescription typeDesc) {
        return null;
    }
}
