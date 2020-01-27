// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.introspect;

import java.util.Collections;
import java.util.Iterator;
import java.lang.reflect.Method;
import java.util.Map;

public final class AnnotatedMethodMap implements Iterable<AnnotatedMethod>
{
    protected Map<MemberKey, AnnotatedMethod> _methods;
    
    public AnnotatedMethodMap() {
    }
    
    public AnnotatedMethodMap(final Map<MemberKey, AnnotatedMethod> m) {
        this._methods = m;
    }
    
    public int size() {
        return (this._methods == null) ? 0 : this._methods.size();
    }
    
    public AnnotatedMethod find(final String name, final Class<?>[] paramTypes) {
        if (this._methods == null) {
            return null;
        }
        return this._methods.get(new MemberKey(name, paramTypes));
    }
    
    public AnnotatedMethod find(final Method m) {
        if (this._methods == null) {
            return null;
        }
        return this._methods.get(new MemberKey(m));
    }
    
    @Override
    public Iterator<AnnotatedMethod> iterator() {
        if (this._methods == null) {
            return Collections.emptyIterator();
        }
        return this._methods.values().iterator();
    }
}
