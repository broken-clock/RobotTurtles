// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.type;

import java.lang.reflect.TypeVariable;
import com.fasterxml.jackson.databind.JavaType;

public final class MapType extends MapLikeType
{
    private static final long serialVersionUID = 1L;
    
    private MapType(final Class<?> mapType, final TypeBindings bindings, final JavaType superClass, final JavaType[] superInts, final JavaType keyT, final JavaType valueT, final Object valueHandler, final Object typeHandler, final boolean asStatic) {
        super(mapType, bindings, superClass, superInts, keyT, valueT, valueHandler, typeHandler, asStatic);
    }
    
    protected MapType(final TypeBase base, final JavaType keyT, final JavaType valueT) {
        super(base, keyT, valueT);
    }
    
    public static MapType construct(final Class<?> rawType, final TypeBindings bindings, final JavaType superClass, final JavaType[] superInts, final JavaType keyT, final JavaType valueT) {
        return new MapType(rawType, bindings, superClass, superInts, keyT, valueT, null, null, false);
    }
    
    @Deprecated
    public static MapType construct(final Class<?> rawType, final JavaType keyT, final JavaType valueT) {
        final TypeVariable<?>[] vars = rawType.getTypeParameters();
        TypeBindings bindings;
        if (vars == null || vars.length != 2) {
            bindings = TypeBindings.emptyBindings();
        }
        else {
            bindings = TypeBindings.create(rawType, keyT, valueT);
        }
        return new MapType(rawType, bindings, TypeBase._bogusSuperClass(rawType), null, keyT, valueT, null, null, false);
    }
    
    @Deprecated
    @Override
    protected JavaType _narrow(final Class<?> subclass) {
        return new MapType(subclass, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public MapType withTypeHandler(final Object h) {
        return new MapType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType, this._valueHandler, h, this._asStatic);
    }
    
    @Override
    public MapType withContentTypeHandler(final Object h) {
        return new MapType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType.withTypeHandler(h), this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public MapType withValueHandler(final Object h) {
        return new MapType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType, h, this._typeHandler, this._asStatic);
    }
    
    @Override
    public MapType withContentValueHandler(final Object h) {
        return new MapType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType.withValueHandler(h), this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public MapType withStaticTyping() {
        if (this._asStatic) {
            return this;
        }
        return new MapType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType.withStaticTyping(), this._valueType.withStaticTyping(), this._valueHandler, this._typeHandler, true);
    }
    
    @Override
    public JavaType withContentType(final JavaType contentType) {
        if (this._valueType == contentType) {
            return this;
        }
        return new MapType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, contentType, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public MapType withKeyType(final JavaType keyType) {
        if (keyType == this._keyType) {
            return this;
        }
        return new MapType(this._class, this._bindings, this._superClass, this._superInterfaces, keyType, this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public JavaType refine(final Class<?> rawType, final TypeBindings bindings, final JavaType superClass, final JavaType[] superInterfaces) {
        return new MapType(rawType, bindings, superClass, superInterfaces, this._keyType, this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public MapType withKeyTypeHandler(final Object h) {
        return new MapType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType.withTypeHandler(h), this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public MapType withKeyValueHandler(final Object h) {
        return new MapType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType.withValueHandler(h), this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public String toString() {
        return "[map type; class " + this._class.getName() + ", " + this._keyType + " -> " + this._valueType + "]";
    }
}
