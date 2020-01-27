// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.jsontype;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationConfig;
import java.util.Collection;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializationConfig;

public interface TypeResolverBuilder<T extends TypeResolverBuilder<T>>
{
    Class<?> getDefaultImpl();
    
    TypeSerializer buildTypeSerializer(final SerializationConfig p0, final JavaType p1, final Collection<NamedType> p2);
    
    TypeDeserializer buildTypeDeserializer(final DeserializationConfig p0, final JavaType p1, final Collection<NamedType> p2);
    
    T init(final JsonTypeInfo.Id p0, final TypeIdResolver p1);
    
    T inclusion(final JsonTypeInfo.As p0);
    
    T typeProperty(final String p0);
    
    T defaultImpl(final Class<?> p0);
    
    T typeIdVisibility(final boolean p0);
}
