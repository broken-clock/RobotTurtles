// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.type;

import java.util.Collection;
import java.util.Map;
import com.fasterxml.jackson.databind.JavaType;

public class SimpleType extends TypeBase
{
    private static final long serialVersionUID = 1L;
    
    protected SimpleType(final Class<?> cls) {
        this(cls, TypeBindings.emptyBindings(), null, null);
    }
    
    protected SimpleType(final Class<?> cls, final TypeBindings bindings, final JavaType superClass, final JavaType[] superInts) {
        this(cls, bindings, superClass, superInts, null, null, false);
    }
    
    protected SimpleType(final TypeBase base) {
        super(base);
    }
    
    protected SimpleType(final Class<?> cls, final TypeBindings bindings, final JavaType superClass, final JavaType[] superInts, final Object valueHandler, final Object typeHandler, final boolean asStatic) {
        super(cls, bindings, superClass, superInts, 0, valueHandler, typeHandler, asStatic);
    }
    
    protected SimpleType(final Class<?> cls, final TypeBindings bindings, final JavaType superClass, final JavaType[] superInts, final int extraHash, final Object valueHandler, final Object typeHandler, final boolean asStatic) {
        super(cls, bindings, superClass, superInts, extraHash, valueHandler, typeHandler, asStatic);
    }
    
    public static SimpleType constructUnsafe(final Class<?> raw) {
        return new SimpleType(raw, null, null, null, null, null, false);
    }
    
    @Deprecated
    public static SimpleType construct(final Class<?> cls) {
        if (Map.class.isAssignableFrom(cls)) {
            throw new IllegalArgumentException("Cannot construct SimpleType for a Map (class: " + cls.getName() + ")");
        }
        if (Collection.class.isAssignableFrom(cls)) {
            throw new IllegalArgumentException("Cannot construct SimpleType for a Collection (class: " + cls.getName() + ")");
        }
        if (cls.isArray()) {
            throw new IllegalArgumentException("Cannot construct SimpleType for an array (class: " + cls.getName() + ")");
        }
        final TypeBindings b = TypeBindings.emptyBindings();
        return new SimpleType(cls, b, _buildSuperClass(cls.getSuperclass(), b), null, null, null, false);
    }
    
    @Deprecated
    @Override
    protected JavaType _narrow(final Class<?> subclass) {
        if (this._class == subclass) {
            return this;
        }
        if (!this._class.isAssignableFrom(subclass)) {
            return new SimpleType(subclass, this._bindings, this, this._superInterfaces, this._valueHandler, this._typeHandler, this._asStatic);
        }
        final Class<?> next = subclass.getSuperclass();
        if (next == this._class) {
            return new SimpleType(subclass, this._bindings, this, this._superInterfaces, this._valueHandler, this._typeHandler, this._asStatic);
        }
        if (next != null && this._class.isAssignableFrom(next)) {
            final JavaType superb = this._narrow(next);
            return new SimpleType(subclass, this._bindings, superb, null, this._valueHandler, this._typeHandler, this._asStatic);
        }
        final Class<?>[] interfaces;
        final Class<?>[] nextI = interfaces = subclass.getInterfaces();
        for (final Class<?> iface : interfaces) {
            if (iface == this._class) {
                return new SimpleType(subclass, this._bindings, null, new JavaType[] { this }, this._valueHandler, this._typeHandler, this._asStatic);
            }
            if (this._class.isAssignableFrom(iface)) {
                final JavaType superb2 = this._narrow(iface);
                return new SimpleType(subclass, this._bindings, null, new JavaType[] { superb2 }, this._valueHandler, this._typeHandler, this._asStatic);
            }
        }
        throw new IllegalArgumentException("Internal error: Cannot resolve sub-type for Class " + subclass.getName() + " to " + this._class.getName());
    }
    
    @Override
    public JavaType withContentType(final JavaType contentType) {
        throw new IllegalArgumentException("Simple types have no content types; cannot call withContentType()");
    }
    
    @Override
    public SimpleType withTypeHandler(final Object h) {
        if (this._typeHandler == h) {
            return this;
        }
        return new SimpleType(this._class, this._bindings, this._superClass, this._superInterfaces, this._valueHandler, h, this._asStatic);
    }
    
    @Override
    public JavaType withContentTypeHandler(final Object h) {
        throw new IllegalArgumentException("Simple types have no content types; cannot call withContenTypeHandler()");
    }
    
    @Override
    public SimpleType withValueHandler(final Object h) {
        if (h == this._valueHandler) {
            return this;
        }
        return new SimpleType(this._class, this._bindings, this._superClass, this._superInterfaces, h, this._typeHandler, this._asStatic);
    }
    
    @Override
    public SimpleType withContentValueHandler(final Object h) {
        throw new IllegalArgumentException("Simple types have no content types; cannot call withContenValueHandler()");
    }
    
    @Override
    public SimpleType withStaticTyping() {
        return this._asStatic ? this : new SimpleType(this._class, this._bindings, this._superClass, this._superInterfaces, this._valueHandler, this._typeHandler, true);
    }
    
    @Override
    public JavaType refine(final Class<?> rawType, final TypeBindings bindings, final JavaType superClass, final JavaType[] superInterfaces) {
        return null;
    }
    
    @Override
    protected String buildCanonicalName() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this._class.getName());
        final int count = this._bindings.size();
        if (count > 0) {
            sb.append('<');
            for (int i = 0; i < count; ++i) {
                final JavaType t = this.containedType(i);
                if (i > 0) {
                    sb.append(',');
                }
                sb.append(t.toCanonical());
            }
            sb.append('>');
        }
        return sb.toString();
    }
    
    @Override
    public boolean isContainerType() {
        return false;
    }
    
    @Override
    public boolean hasContentType() {
        return false;
    }
    
    @Override
    public StringBuilder getErasedSignature(final StringBuilder sb) {
        return TypeBase._classSignature(this._class, sb, true);
    }
    
    @Override
    public StringBuilder getGenericSignature(StringBuilder sb) {
        TypeBase._classSignature(this._class, sb, false);
        final int count = this._bindings.size();
        if (count > 0) {
            sb.append('<');
            for (int i = 0; i < count; ++i) {
                sb = this.containedType(i).getGenericSignature(sb);
            }
            sb.append('>');
        }
        sb.append(';');
        return sb;
    }
    
    private static JavaType _buildSuperClass(final Class<?> superClass, final TypeBindings b) {
        if (superClass == null) {
            return null;
        }
        if (superClass == Object.class) {
            return TypeFactory.unknownType();
        }
        final JavaType superSuper = _buildSuperClass(superClass.getSuperclass(), b);
        return new SimpleType(superClass, b, superSuper, null, null, null, false);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(40);
        sb.append("[simple type, class ").append(this.buildCanonicalName()).append(']');
        return sb.toString();
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
        final SimpleType other = (SimpleType)o;
        if (other._class != this._class) {
            return false;
        }
        final TypeBindings b1 = this._bindings;
        final TypeBindings b2 = other._bindings;
        return b1.equals(b2);
    }
}
