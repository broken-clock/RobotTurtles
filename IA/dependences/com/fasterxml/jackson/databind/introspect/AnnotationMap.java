// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.introspect;

import java.util.Iterator;
import java.util.Collections;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import com.fasterxml.jackson.databind.util.Annotations;

public final class AnnotationMap implements Annotations
{
    protected HashMap<Class<?>, Annotation> _annotations;
    
    public AnnotationMap() {
    }
    
    public static AnnotationMap of(final Class<?> type, final Annotation value) {
        final HashMap<Class<?>, Annotation> ann = new HashMap<Class<?>, Annotation>(4);
        ann.put(type, value);
        return new AnnotationMap(ann);
    }
    
    AnnotationMap(final HashMap<Class<?>, Annotation> a) {
        this._annotations = a;
    }
    
    @Override
    public <A extends Annotation> A get(final Class<A> cls) {
        if (this._annotations == null) {
            return null;
        }
        return (A)this._annotations.get(cls);
    }
    
    @Override
    public boolean has(final Class<?> cls) {
        return this._annotations != null && this._annotations.containsKey(cls);
    }
    
    @Override
    public boolean hasOneOf(final Class<? extends Annotation>[] annoClasses) {
        if (this._annotations != null) {
            for (int i = 0, end = annoClasses.length; i < end; ++i) {
                if (this._annotations.containsKey(annoClasses[i])) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public Iterable<Annotation> annotations() {
        if (this._annotations == null || this._annotations.size() == 0) {
            return (Iterable<Annotation>)Collections.emptyList();
        }
        return this._annotations.values();
    }
    
    public static AnnotationMap merge(final AnnotationMap primary, final AnnotationMap secondary) {
        if (primary == null || primary._annotations == null || primary._annotations.isEmpty()) {
            return secondary;
        }
        if (secondary == null || secondary._annotations == null || secondary._annotations.isEmpty()) {
            return primary;
        }
        final HashMap<Class<?>, Annotation> annotations = new HashMap<Class<?>, Annotation>();
        for (final Annotation ann : secondary._annotations.values()) {
            annotations.put(ann.annotationType(), ann);
        }
        for (final Annotation ann : primary._annotations.values()) {
            annotations.put(ann.annotationType(), ann);
        }
        return new AnnotationMap(annotations);
    }
    
    @Override
    public int size() {
        return (this._annotations == null) ? 0 : this._annotations.size();
    }
    
    public boolean addIfNotPresent(final Annotation ann) {
        if (this._annotations == null || !this._annotations.containsKey(ann.annotationType())) {
            this._add(ann);
            return true;
        }
        return false;
    }
    
    public boolean add(final Annotation ann) {
        return this._add(ann);
    }
    
    @Override
    public String toString() {
        if (this._annotations == null) {
            return "[null]";
        }
        return this._annotations.toString();
    }
    
    protected final boolean _add(final Annotation ann) {
        if (this._annotations == null) {
            this._annotations = new HashMap<Class<?>, Annotation>();
        }
        final Annotation previous = this._annotations.put(ann.annotationType(), ann);
        return previous == null || !previous.equals(ann);
    }
}
