// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.type;

import java.util.Iterator;
import com.fasterxml.jackson.databind.JavaType;
import java.util.ArrayList;

public final class ClassStack
{
    protected final ClassStack _parent;
    protected final Class<?> _current;
    private ArrayList<ResolvedRecursiveType> _selfRefs;
    
    public ClassStack(final Class<?> rootType) {
        this(null, rootType);
    }
    
    private ClassStack(final ClassStack parent, final Class<?> curr) {
        this._parent = parent;
        this._current = curr;
    }
    
    public ClassStack child(final Class<?> cls) {
        return new ClassStack(this, cls);
    }
    
    public void addSelfReference(final ResolvedRecursiveType ref) {
        if (this._selfRefs == null) {
            this._selfRefs = new ArrayList<ResolvedRecursiveType>();
        }
        this._selfRefs.add(ref);
    }
    
    public void resolveSelfReferences(final JavaType resolved) {
        if (this._selfRefs != null) {
            for (final ResolvedRecursiveType ref : this._selfRefs) {
                ref.setReference(resolved);
            }
        }
    }
    
    public ClassStack find(final Class<?> cls) {
        if (this._current == cls) {
            return this;
        }
        for (ClassStack curr = this._parent; curr != null; curr = curr._parent) {
            if (curr._current == cls) {
                return curr;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[ClassStack (self-refs: ").append((this._selfRefs == null) ? "0" : String.valueOf(this._selfRefs.size())).append(')');
        for (ClassStack curr = this; curr != null; curr = curr._parent) {
            sb.append(' ').append(curr._current.getName());
        }
        sb.append(']');
        return sb.toString();
    }
}
