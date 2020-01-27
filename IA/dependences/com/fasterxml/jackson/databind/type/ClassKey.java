// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.type;

import java.io.Serializable;

public final class ClassKey implements Comparable<ClassKey>, Serializable
{
    private static final long serialVersionUID = 1L;
    private String _className;
    private Class<?> _class;
    private int _hashCode;
    
    public ClassKey() {
        this._class = null;
        this._className = null;
        this._hashCode = 0;
    }
    
    public ClassKey(final Class<?> clz) {
        this._class = clz;
        this._className = clz.getName();
        this._hashCode = this._className.hashCode();
    }
    
    public void reset(final Class<?> clz) {
        this._class = clz;
        this._className = clz.getName();
        this._hashCode = this._className.hashCode();
    }
    
    @Override
    public int compareTo(final ClassKey other) {
        return this._className.compareTo(other._className);
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
        final ClassKey other = (ClassKey)o;
        return other._class == this._class;
    }
    
    @Override
    public int hashCode() {
        return this._hashCode;
    }
    
    @Override
    public String toString() {
        return this._className;
    }
}
