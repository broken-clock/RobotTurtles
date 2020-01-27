// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.jsontype.impl;

import java.io.IOException;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;

public abstract class TypeIdResolverBase implements TypeIdResolver
{
    protected final TypeFactory _typeFactory;
    protected final JavaType _baseType;
    
    protected TypeIdResolverBase() {
        this(null, null);
    }
    
    protected TypeIdResolverBase(final JavaType baseType, final TypeFactory typeFactory) {
        this._baseType = baseType;
        this._typeFactory = typeFactory;
    }
    
    @Override
    public void init(final JavaType bt) {
    }
    
    @Override
    public String idFromBaseType() {
        return this.idFromValueAndType(null, this._baseType.getRawClass());
    }
    
    @Override
    public JavaType typeFromId(final DatabindContext context, final String id) throws IOException {
        throw new IllegalStateException("Sub-class " + this.getClass().getName() + " MUST implement `typeFromId(DatabindContext,String)");
    }
    
    @Override
    public String getDescForKnownTypeIds() {
        return null;
    }
}
