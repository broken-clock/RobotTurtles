// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.core.type.ResolvedType;
import com.fasterxml.jackson.databind.JavaType;

public class ReferenceType extends SimpleType
{
    private static final long serialVersionUID = 1L;
    protected final JavaType _referencedType;
    protected final JavaType _anchorType;
    
    protected ReferenceType(final Class<?> cls, final TypeBindings bindings, final JavaType superClass, final JavaType[] superInts, final JavaType refType, final JavaType anchorType, final Object valueHandler, final Object typeHandler, final boolean asStatic) {
        super(cls, bindings, superClass, superInts, refType.hashCode(), valueHandler, typeHandler, asStatic);
        this._referencedType = refType;
        this._anchorType = ((anchorType == null) ? this : anchorType);
    }
    
    protected ReferenceType(final TypeBase base, final JavaType refType) {
        super(base);
        this._referencedType = refType;
        this._anchorType = this;
    }
    
    public static ReferenceType upgradeFrom(final JavaType baseType, final JavaType refdType) {
        if (refdType == null) {
            throw new IllegalArgumentException("Missing referencedType");
        }
        if (baseType instanceof TypeBase) {
            return new ReferenceType((TypeBase)baseType, refdType);
        }
        throw new IllegalArgumentException("Cannot upgrade from an instance of " + baseType.getClass());
    }
    
    public static ReferenceType construct(final Class<?> cls, final TypeBindings bindings, final JavaType superClass, final JavaType[] superInts, final JavaType refType) {
        return new ReferenceType(cls, bindings, superClass, superInts, refType, null, null, null, false);
    }
    
    @Deprecated
    public static ReferenceType construct(final Class<?> cls, final JavaType refType) {
        return new ReferenceType(cls, TypeBindings.emptyBindings(), null, null, null, refType, null, null, false);
    }
    
    @Override
    public JavaType withContentType(final JavaType contentType) {
        if (this._referencedType == contentType) {
            return this;
        }
        return new ReferenceType(this._class, this._bindings, this._superClass, this._superInterfaces, contentType, this._anchorType, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public ReferenceType withTypeHandler(final Object h) {
        if (h == this._typeHandler) {
            return this;
        }
        return new ReferenceType(this._class, this._bindings, this._superClass, this._superInterfaces, this._referencedType, this._anchorType, this._valueHandler, h, this._asStatic);
    }
    
    @Override
    public ReferenceType withContentTypeHandler(final Object h) {
        if (h == this._referencedType.getTypeHandler()) {
            return this;
        }
        return new ReferenceType(this._class, this._bindings, this._superClass, this._superInterfaces, this._referencedType.withTypeHandler(h), this._anchorType, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public ReferenceType withValueHandler(final Object h) {
        if (h == this._valueHandler) {
            return this;
        }
        return new ReferenceType(this._class, this._bindings, this._superClass, this._superInterfaces, this._referencedType, this._anchorType, h, this._typeHandler, this._asStatic);
    }
    
    @Override
    public ReferenceType withContentValueHandler(final Object h) {
        if (h == this._referencedType.getValueHandler()) {
            return this;
        }
        final JavaType refdType = this._referencedType.withValueHandler(h);
        return new ReferenceType(this._class, this._bindings, this._superClass, this._superInterfaces, refdType, this._anchorType, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public ReferenceType withStaticTyping() {
        if (this._asStatic) {
            return this;
        }
        return new ReferenceType(this._class, this._bindings, this._superClass, this._superInterfaces, this._referencedType.withStaticTyping(), this._anchorType, this._valueHandler, this._typeHandler, true);
    }
    
    @Override
    public JavaType refine(final Class<?> rawType, final TypeBindings bindings, final JavaType superClass, final JavaType[] superInterfaces) {
        return new ReferenceType(rawType, this._bindings, superClass, superInterfaces, this._referencedType, this._anchorType, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    protected String buildCanonicalName() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this._class.getName());
        sb.append('<');
        sb.append(this._referencedType.toCanonical());
        sb.append('>');
        return sb.toString();
    }
    
    @Deprecated
    @Override
    protected JavaType _narrow(final Class<?> subclass) {
        return new ReferenceType(subclass, this._bindings, this._superClass, this._superInterfaces, this._referencedType, this._anchorType, this._valueHandler, this._typeHandler, this._asStatic);
    }
    
    @Override
    public JavaType getContentType() {
        return this._referencedType;
    }
    
    @Override
    public JavaType getReferencedType() {
        return this._referencedType;
    }
    
    @Override
    public boolean hasContentType() {
        return true;
    }
    
    @Override
    public boolean isReferenceType() {
        return true;
    }
    
    @Override
    public StringBuilder getErasedSignature(final StringBuilder sb) {
        return TypeBase._classSignature(this._class, sb, true);
    }
    
    @Override
    public StringBuilder getGenericSignature(StringBuilder sb) {
        TypeBase._classSignature(this._class, sb, false);
        sb.append('<');
        sb = this._referencedType.getGenericSignature(sb);
        sb.append(">;");
        return sb;
    }
    
    public JavaType getAnchorType() {
        return this._anchorType;
    }
    
    public boolean isAnchorType() {
        return this._anchorType == this;
    }
    
    @Override
    public String toString() {
        return new StringBuilder(40).append("[reference type, class ").append(this.buildCanonicalName()).append('<').append(this._referencedType).append('>').append(']').toString();
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
        final ReferenceType other = (ReferenceType)o;
        return other._class == this._class && this._referencedType.equals(other._referencedType);
    }
}
