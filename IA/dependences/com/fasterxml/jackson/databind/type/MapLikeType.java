// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.core.type.ResolvedType;
import java.util.Map;
import java.lang.reflect.TypeVariable;
import com.fasterxml.jackson.databind.JavaType;

public class MapLikeType extends TypeBase
{
    private static final long serialVersionUID = 1L;
    protected final JavaType _keyType;
    protected final JavaType _valueType;
    
    protected MapLikeType(final Class<?> mapType, final TypeBindings bindings, final JavaType superClass, final JavaType[] superInts, final JavaType keyT, final JavaType valueT, final Object valueHandler, final Object typeHandler, final boolean asStatic) {
        super(mapType, bindings, superClass, superInts, keyT.hashCode() ^ valueT.hashCode(), valueHandler, typeHandler, asStatic);
        this._keyType = keyT;
        this._valueType = valueT;
    }
    
    protected MapLikeType(final TypeBase base, final JavaType keyT, final JavaType valueT) {
        super(base);
        this._keyType = keyT;
        this._valueType = valueT;
    }
    
    public static MapLikeType upgradeFrom(final JavaType baseType, final JavaType keyT, final JavaType valueT) {
        if (baseType instanceof TypeBase) {
            return new MapLikeType((TypeBase)baseType, keyT, valueT);
        }
        throw new IllegalArgumentException("Cannot upgrade from an instance of " + baseType.getClass());
    }
    
    @Deprecated
    public static MapLikeType construct(final Class<?> rawType, final JavaType keyT, final JavaType valueT) {
        final TypeVariable<?>[] vars = rawType.getTypeParameters();
        TypeBindings bindings;
        if (vars == null || vars.length != 2) {
            bindings = TypeBindings.emptyBindings();
        }
        else {
            bindings = TypeBindings.create(rawType, keyT, valueT);
        }
        return new MapLikeType(rawType, bindings, TypeBase._bogusSuperClass(rawType), null, keyT, valueT, null, null, false);
    }
    
    @Deprecated
    @Override
    protected JavaType _narrow(final Class<?> subclass) {
        return new MapLikeType(subclass, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    public MapLikeType withKeyType(final JavaType keyType) {
        if (keyType == this._keyType) {
            return this;
        }
        return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, keyType, this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public JavaType withContentType(final JavaType contentType) {
        if (this._valueType == contentType) {
            return this;
        }
        return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, contentType, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public MapLikeType withTypeHandler(final Object h) {
        return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType, this._valueHandler, h, this._asStatic);
    }
    
    @Override
    public MapLikeType withContentTypeHandler(final Object h) {
        return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType.withTypeHandler(h), this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public MapLikeType withValueHandler(final Object h) {
        return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType, h, this._typeHandler, this._asStatic);
    }
    
    @Override
    public MapLikeType withContentValueHandler(final Object h) {
        return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType.withValueHandler(h), this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public JavaType withHandlersFrom(final JavaType src) {
        JavaType type = super.withHandlersFrom(src);
        final JavaType srcKeyType = src.getKeyType();
        if (type instanceof MapLikeType && srcKeyType != null) {
            final JavaType ct = this._keyType.withHandlersFrom(srcKeyType);
            if (ct != this._keyType) {
                type = ((MapLikeType)type).withKeyType(ct);
            }
        }
        final JavaType srcCt = src.getContentType();
        if (srcCt != null) {
            final JavaType ct2 = this._valueType.withHandlersFrom(srcCt);
            if (ct2 != this._valueType) {
                type = type.withContentType(ct2);
            }
        }
        return type;
    }
    
    @Override
    public MapLikeType withStaticTyping() {
        if (this._asStatic) {
            return this;
        }
        return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType.withStaticTyping(), this._valueHandler, this._typeHandler, true);
    }
    
    @Override
    public JavaType refine(final Class<?> rawType, final TypeBindings bindings, final JavaType superClass, final JavaType[] superInterfaces) {
        return new MapLikeType(rawType, bindings, superClass, superInterfaces, this._keyType, this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    protected String buildCanonicalName() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this._class.getName());
        if (this._keyType != null) {
            sb.append('<');
            sb.append(this._keyType.toCanonical());
            sb.append(',');
            sb.append(this._valueType.toCanonical());
            sb.append('>');
        }
        return sb.toString();
    }
    
    @Override
    public boolean isContainerType() {
        return true;
    }
    
    @Override
    public boolean isMapLikeType() {
        return true;
    }
    
    @Override
    public JavaType getKeyType() {
        return this._keyType;
    }
    
    @Override
    public JavaType getContentType() {
        return this._valueType;
    }
    
    @Override
    public Object getContentValueHandler() {
        return this._valueType.getValueHandler();
    }
    
    @Override
    public Object getContentTypeHandler() {
        return this._valueType.getTypeHandler();
    }
    
    @Override
    public boolean hasHandlers() {
        return super.hasHandlers() || this._valueType.hasHandlers() || this._keyType.hasHandlers();
    }
    
    @Override
    public StringBuilder getErasedSignature(final StringBuilder sb) {
        return TypeBase._classSignature(this._class, sb, true);
    }
    
    @Override
    public StringBuilder getGenericSignature(final StringBuilder sb) {
        TypeBase._classSignature(this._class, sb, false);
        sb.append('<');
        this._keyType.getGenericSignature(sb);
        this._valueType.getGenericSignature(sb);
        sb.append(">;");
        return sb;
    }
    
    public MapLikeType withKeyTypeHandler(final Object h) {
        return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType.withTypeHandler(h), this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    public MapLikeType withKeyValueHandler(final Object h) {
        return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType.withValueHandler(h), this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    public boolean isTrueMapType() {
        return Map.class.isAssignableFrom(this._class);
    }
    
    @Override
    public String toString() {
        return String.format("[map-like type; class %s, %s -> %s]", this._class.getName(), this._keyType, this._valueType);
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
        final MapLikeType other = (MapLikeType)o;
        return this._class == other._class && this._keyType.equals(other._keyType) && this._valueType.equals(other._valueType);
    }
}
