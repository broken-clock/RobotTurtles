// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.jsontype;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.IOException;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;

public interface TypeIdResolver
{
    void init(final JavaType p0);
    
    String idFromValue(final Object p0);
    
    String idFromValueAndType(final Object p0, final Class<?> p1);
    
    String idFromBaseType();
    
    JavaType typeFromId(final DatabindContext p0, final String p1) throws IOException;
    
    String getDescForKnownTypeIds();
    
    JsonTypeInfo.Id getMechanism();
}
