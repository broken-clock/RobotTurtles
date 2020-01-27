// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.databind.JavaType;

public class TypeKey
{
    protected int _hashCode;
    protected Class<?> _class;
    protected JavaType _type;
    protected boolean _isTyped;
    
    public TypeKey() {
    }
    
    public TypeKey(final TypeKey src) {
        this._hashCode = src._hashCode;
        this._class = src._class;
        this._type = src._type;
        this._isTyped = src._isTyped;
    }
    
    public TypeKey(final Class<?> key, final boolean typed) {
        this._class = key;
        this._type = null;
        this._isTyped = typed;
        this._hashCode = (typed ? typedHash(key) : untypedHash(key));
    }
    
    public TypeKey(final JavaType key, final boolean typed) {
        this._type = key;
        this._class = null;
        this._isTyped = typed;
        this._hashCode = (typed ? typedHash(key) : untypedHash(key));
    }
    
    public static final int untypedHash(final Class<?> cls) {
        return cls.getName().hashCode();
    }
    
    public static final int typedHash(final Class<?> cls) {
        return cls.getName().hashCode() + 1;
    }
    
    public static final int untypedHash(final JavaType type) {
        return type.hashCode() - 1;
    }
    
    public static final int typedHash(final JavaType type) {
        return type.hashCode() - 2;
    }
    
    public final void resetTyped(final Class<?> cls) {
        this._type = null;
        this._class = cls;
        this._isTyped = true;
        this._hashCode = typedHash(cls);
    }
    
    public final void resetUntyped(final Class<?> cls) {
        this._type = null;
        this._class = cls;
        this._isTyped = false;
        this._hashCode = untypedHash(cls);
    }
    
    public final void resetTyped(final JavaType type) {
        this._type = type;
        this._class = null;
        this._isTyped = true;
        this._hashCode = typedHash(type);
    }
    
    public final void resetUntyped(final JavaType type) {
        this._type = type;
        this._class = null;
        this._isTyped = false;
        this._hashCode = untypedHash(type);
    }
    
    public boolean isTyped() {
        return this._isTyped;
    }
    
    public Class<?> getRawType() {
        return this._class;
    }
    
    public JavaType getType() {
        return this._type;
    }
    
    @Override
    public final int hashCode() {
        return this._hashCode;
    }
    
    @Override
    public final String toString() {
        if (this._class != null) {
            return "{class: " + this._class.getName() + ", typed? " + this._isTyped + "}";
        }
        return "{type: " + this._type + ", typed? " + this._isTyped + "}";
    }
    
    @Override
    public final boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        final TypeKey other = (TypeKey)o;
        if (other._isTyped != this._isTyped) {
            return false;
        }
        if (this._class != null) {
            return other._class == this._class;
        }
        return this._type.equals(other._type);
    }
}
