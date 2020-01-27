// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.JavaType;
import java.lang.reflect.Type;

public interface TypeResolutionContext
{
    JavaType resolveType(final Type p0);
    
    public static class Basic implements TypeResolutionContext
    {
        private final TypeFactory _typeFactory;
        private final TypeBindings _bindings;
        
        public Basic(final TypeFactory tf, final TypeBindings b) {
            this._typeFactory = tf;
            this._bindings = b;
        }
        
        @Override
        public JavaType resolveType(final Type type) {
            return this._typeFactory.constructType(type, this._bindings);
        }
    }
}
