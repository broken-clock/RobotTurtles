// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.core.type.ResolvedType;
import java.util.Collection;
import java.lang.reflect.TypeVariable;
import com.fasterxml.jackson.databind.JavaType;

public class CollectionLikeType extends TypeBase
{
    private static final long serialVersionUID = 1L;
    protected final JavaType _elementType;
    
    protected CollectionLikeType(final Class<?> collT, final TypeBindings bindings, final JavaType superClass, final JavaType[] superInts, final JavaType elemT, final Object valueHandler, final Object typeHandler, final boolean asStatic) {
        super(collT, bindings, superClass, superInts, elemT.hashCode(), valueHandler, typeHandler, asStatic);
        this._elementType = elemT;
    }
    
    protected CollectionLikeType(final TypeBase base, final JavaType elemT) {
        super(base);
        this._elementType = elemT;
    }
    
    public static CollectionLikeType construct(final Class<?> rawType, final TypeBindings bindings, final JavaType superClass, final JavaType[] superInts, final JavaType elemT) {
        return new CollectionLikeType(rawType, bindings, superClass, superInts, elemT, null, null, false);
    }
    
    @Deprecated
    public static CollectionLikeType construct(final Class<?> rawType, final JavaType elemT) {
        final TypeVariable<?>[] vars = rawType.getTypeParameters();
        TypeBindings bindings;
        if (vars == null || vars.length != 1) {
            bindings = TypeBindings.emptyBindings();
        }
        else {
            bindings = TypeBindings.create(rawType, elemT);
        }
        return new CollectionLikeType(rawType, bindings, TypeBase._bogusSuperClass(rawType), null, elemT, null, null, false);
    }
    
    public static CollectionLikeType upgradeFrom(final JavaType baseType, final JavaType elementType) {
        if (baseType instanceof TypeBase) {
            return new CollectionLikeType((TypeBase)baseType, elementType);
        }
        throw new IllegalArgumentException("Cannot upgrade from an instance of " + baseType.getClass());
    }
    
    @Deprecated
    @Override
    protected JavaType _narrow(final Class<?> subclass) {
        return new CollectionLikeType(subclass, this._bindings, this._superClass, this._superInterfaces, this._elementType, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public JavaType withContentType(final JavaType contentType) {
        if (this._elementType == contentType) {
            return this;
        }
        return new CollectionLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, contentType, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public CollectionLikeType withTypeHandler(final Object h) {
        return new CollectionLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._elementType, this._valueHandler, h, this._asStatic);
    }
    
    @Override
    public CollectionLikeType withContentTypeHandler(final Object h) {
        return new CollectionLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._elementType.withTypeHandler(h), this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public CollectionLikeType withValueHandler(final Object h) {
        return new CollectionLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._elementType, h, this._typeHandler, this._asStatic);
    }
    
    @Override
    public CollectionLikeType withContentValueHandler(final Object h) {
        return new CollectionLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._elementType.withValueHandler(h), this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public JavaType withHandlersFrom(final JavaType src) {
        JavaType type = super.withHandlersFrom(src);
        final JavaType srcCt = src.getContentType();
        if (srcCt != null) {
            final JavaType ct = this._elementType.withHandlersFrom(srcCt);
            if (ct != this._elementType) {
                type = type.withContentType(ct);
            }
        }
        return type;
    }
    
    @Override
    public CollectionLikeType withStaticTyping() {
        if (this._asStatic) {
            return this;
        }
        return new CollectionLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._elementType.withStaticTyping(), this._valueHandler, this._typeHandler, true);
    }
    
    @Override
    public JavaType refine(final Class<?> rawType, final TypeBindings bindings, final JavaType superClass, final JavaType[] superInterfaces) {
        return new CollectionLikeType(rawType, bindings, superClass, superInterfaces, this._elementType, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public boolean isContainerType() {
        return true;
    }
    
    @Override
    public boolean isCollectionLikeType() {
        return true;
    }
    
    @Override
    public JavaType getContentType() {
        return this._elementType;
    }
    
    @Override
    public Object getContentValueHandler() {
        return this._elementType.getValueHandler();
    }
    
    @Override
    public Object getContentTypeHandler() {
        return this._elementType.getTypeHandler();
    }
    
    @Override
    public boolean hasHandlers() {
        return super.hasHandlers() || this._elementType.hasHandlers();
    }
    
    @Override
    public StringBuilder getErasedSignature(final StringBuilder sb) {
        return TypeBase._classSignature(this._class, sb, true);
    }
    
    @Override
    public StringBuilder getGenericSignature(final StringBuilder sb) {
        TypeBase._classSignature(this._class, sb, false);
        sb.append('<');
        this._elementType.getGenericSignature(sb);
        sb.append(">;");
        return sb;
    }
    
    @Override
    protected String buildCanonicalName() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this._class.getName());
        if (this._elementType != null) {
            sb.append('<');
            sb.append(this._elementType.toCanonical());
            sb.append('>');
        }
        return sb.toString();
    }
    
    public boolean isTrueCollectionType() {
        return Collection.class.isAssignableFrom(this._class);
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
        final CollectionLikeType other = (CollectionLikeType)o;
        return this._class == other._class && this._elementType.equals(other._elementType);
    }
    
    @Override
    public String toString() {
        return "[collection-like type; class " + this._class.getName() + ", contains " + this._elementType + "]";
    }
}
