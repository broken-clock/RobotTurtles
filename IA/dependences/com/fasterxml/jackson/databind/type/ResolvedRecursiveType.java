// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.databind.JavaType;

public class ResolvedRecursiveType extends TypeBase
{
    private static final long serialVersionUID = 1L;
    protected JavaType _referencedType;
    
    public ResolvedRecursiveType(final Class<?> erasedType, final TypeBindings bindings) {
        super(erasedType, bindings, null, null, 0, null, null, false);
    }
    
    public void setReference(final JavaType ref) {
        if (this._referencedType != null) {
            throw new IllegalStateException("Trying to re-set self reference; old value = " + this._referencedType + ", new = " + ref);
        }
        this._referencedType = ref;
    }
    
    @Override
    public JavaType getSuperClass() {
        if (this._referencedType != null) {
            return this._referencedType.getSuperClass();
        }
        return super.getSuperClass();
    }
    
    public JavaType getSelfReferencedType() {
        return this._referencedType;
    }
    
    @Override
    public StringBuilder getGenericSignature(final StringBuilder sb) {
        return this._referencedType.getGenericSignature(sb);
    }
    
    @Override
    public StringBuilder getErasedSignature(final StringBuilder sb) {
        return this._referencedType.getErasedSignature(sb);
    }
    
    @Override
    public JavaType withContentType(final JavaType contentType) {
        return this;
    }
    
    @Override
    public JavaType withTypeHandler(final Object h) {
        return this;
    }
    
    @Override
    public JavaType withContentTypeHandler(final Object h) {
        return this;
    }
    
    @Override
    public JavaType withValueHandler(final Object h) {
        return this;
    }
    
    @Override
    public JavaType withContentValueHandler(final Object h) {
        return this;
    }
    
    @Override
    public JavaType withStaticTyping() {
        return this;
    }
    
    @Deprecated
    @Override
    protected JavaType _narrow(final Class<?> subclass) {
        return this;
    }
    
    @Override
    public JavaType refine(final Class<?> rawType, final TypeBindings bindings, final JavaType superClass, final JavaType[] superInterfaces) {
        return null;
    }
    
    @Override
    public boolean isContainerType() {
        return false;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(40).append("[recursive type; ");
        if (this._referencedType == null) {
            sb.append("UNRESOLVED");
        }
        else {
            sb.append(this._referencedType.getRawClass().getName());
        }
        return sb.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o != null && o.getClass() == this.getClass() && false);
    }
}
