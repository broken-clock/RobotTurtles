// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.core.type.ResolvedType;
import java.lang.reflect.Array;
import com.fasterxml.jackson.databind.JavaType;

public final class ArrayType extends TypeBase
{
    private static final long serialVersionUID = 1L;
    protected final JavaType _componentType;
    protected final Object _emptyArray;
    
    protected ArrayType(final JavaType componentType, final TypeBindings bindings, final Object emptyInstance, final Object valueHandler, final Object typeHandler, final boolean asStatic) {
        super(emptyInstance.getClass(), bindings, null, null, componentType.hashCode(), valueHandler, typeHandler, asStatic);
        this._componentType = componentType;
        this._emptyArray = emptyInstance;
    }
    
    public static ArrayType construct(final JavaType componentType, final TypeBindings bindings) {
        return construct(componentType, bindings, null, null);
    }
    
    public static ArrayType construct(final JavaType componentType, final TypeBindings bindings, final Object valueHandler, final Object typeHandler) {
        final Object emptyInstance = Array.newInstance(componentType.getRawClass(), 0);
        return new ArrayType(componentType, bindings, emptyInstance, valueHandler, typeHandler, false);
    }
    
    @Override
    public JavaType withContentType(final JavaType contentType) {
        final Object emptyInstance = Array.newInstance(contentType.getRawClass(), 0);
        return new ArrayType(contentType, this._bindings, emptyInstance, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public ArrayType withTypeHandler(final Object h) {
        if (h == this._typeHandler) {
            return this;
        }
        return new ArrayType(this._componentType, this._bindings, this._emptyArray, this._valueHandler, h, this._asStatic);
    }
    
    @Override
    public ArrayType withContentTypeHandler(final Object h) {
        if (h == this._componentType.getTypeHandler()) {
            return this;
        }
        return new ArrayType(this._componentType.withTypeHandler(h), this._bindings, this._emptyArray, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public ArrayType withValueHandler(final Object h) {
        if (h == this._valueHandler) {
            return this;
        }
        return new ArrayType(this._componentType, this._bindings, this._emptyArray, h, this._typeHandler, this._asStatic);
    }
    
    @Override
    public ArrayType withContentValueHandler(final Object h) {
        if (h == this._componentType.getValueHandler()) {
            return this;
        }
        return new ArrayType(this._componentType.withValueHandler(h), this._bindings, this._emptyArray, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public ArrayType withStaticTyping() {
        if (this._asStatic) {
            return this;
        }
        return new ArrayType(this._componentType.withStaticTyping(), this._bindings, this._emptyArray, this._valueHandler, this._typeHandler, true);
    }
    
    @Deprecated
    @Override
    protected JavaType _narrow(final Class<?> subclass) {
        return this._reportUnsupported();
    }
    
    @Override
    public JavaType refine(final Class<?> contentClass, final TypeBindings bindings, final JavaType superClass, final JavaType[] superInterfaces) {
        return null;
    }
    
    private JavaType _reportUnsupported() {
        throw new UnsupportedOperationException("Cannot narrow or widen array types");
    }
    
    @Override
    public boolean isArrayType() {
        return true;
    }
    
    @Override
    public boolean isAbstract() {
        return false;
    }
    
    @Override
    public boolean isConcrete() {
        return true;
    }
    
    @Override
    public boolean hasGenericTypes() {
        return this._componentType.hasGenericTypes();
    }
    
    @Override
    public boolean isContainerType() {
        return true;
    }
    
    @Override
    public JavaType getContentType() {
        return this._componentType;
    }
    
    @Override
    public Object getContentValueHandler() {
        return this._componentType.getValueHandler();
    }
    
    @Override
    public Object getContentTypeHandler() {
        return this._componentType.getTypeHandler();
    }
    
    @Override
    public boolean hasHandlers() {
        return super.hasHandlers() || this._componentType.hasHandlers();
    }
    
    @Override
    public StringBuilder getGenericSignature(final StringBuilder sb) {
        sb.append('[');
        return this._componentType.getGenericSignature(sb);
    }
    
    @Override
    public StringBuilder getErasedSignature(final StringBuilder sb) {
        sb.append('[');
        return this._componentType.getErasedSignature(sb);
    }
    
    @Override
    public String toString() {
        return "[array type, component type: " + this._componentType + "]";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        final ArrayType other = (ArrayType)o;
        return this._componentType.equals(other._componentType);
    }
}
