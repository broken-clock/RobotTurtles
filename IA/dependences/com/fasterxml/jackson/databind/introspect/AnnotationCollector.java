// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.introspect;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.lang.annotation.Annotation;
import com.fasterxml.jackson.databind.util.Annotations;

public abstract class AnnotationCollector
{
    protected static final Annotations NO_ANNOTATIONS;
    protected final Object _data;
    
    protected AnnotationCollector(final Object d) {
        this._data = d;
    }
    
    public static Annotations emptyAnnotations() {
        return AnnotationCollector.NO_ANNOTATIONS;
    }
    
    public static AnnotationCollector emptyCollector() {
        return EmptyCollector.instance;
    }
    
    public static AnnotationCollector emptyCollector(final Object data) {
        return new EmptyCollector(data);
    }
    
    public abstract Annotations asAnnotations();
    
    public abstract AnnotationMap asAnnotationMap();
    
    public Object getData() {
        return this._data;
    }
    
    public abstract boolean isPresent(final Annotation p0);
    
    public abstract AnnotationCollector addOrOverride(final Annotation p0);
    
    static {
        NO_ANNOTATIONS = new NoAnnotations();
    }
    
    static class EmptyCollector extends AnnotationCollector
    {
        public static final EmptyCollector instance;
        
        EmptyCollector(final Object data) {
            super(data);
        }
        
        @Override
        public Annotations asAnnotations() {
            return EmptyCollector.NO_ANNOTATIONS;
        }
        
        @Override
        public AnnotationMap asAnnotationMap() {
            return new AnnotationMap();
        }
        
        @Override
        public boolean isPresent(final Annotation ann) {
            return false;
        }
        
        @Override
        public AnnotationCollector addOrOverride(final Annotation ann) {
            return new OneCollector(this._data, ann.annotationType(), ann);
        }
        
        static {
            instance = new EmptyCollector(null);
        }
    }
    
    static class OneCollector extends AnnotationCollector
    {
        private Class<?> _type;
        private Annotation _value;
        
        public OneCollector(final Object data, final Class<?> type, final Annotation value) {
            super(data);
            this._type = type;
            this._value = value;
        }
        
        @Override
        public Annotations asAnnotations() {
            return new OneAnnotation(this._type, this._value);
        }
        
        @Override
        public AnnotationMap asAnnotationMap() {
            return AnnotationMap.of(this._type, this._value);
        }
        
        @Override
        public boolean isPresent(final Annotation ann) {
            return ann.annotationType() == this._type;
        }
        
        @Override
        public AnnotationCollector addOrOverride(final Annotation ann) {
            final Class<?> type = ann.annotationType();
            if (this._type == type) {
                this._value = ann;
                return this;
            }
            return new NCollector(this._data, this._type, this._value, type, ann);
        }
    }
    
    static class NCollector extends AnnotationCollector
    {
        protected final HashMap<Class<?>, Annotation> _annotations;
        
        public NCollector(final Object data, final Class<?> type1, final Annotation value1, final Class<?> type2, final Annotation value2) {
            super(data);
            (this._annotations = new HashMap<Class<?>, Annotation>()).put(type1, value1);
            this._annotations.put(type2, value2);
        }
        
        @Override
        public Annotations asAnnotations() {
            if (this._annotations.size() == 2) {
                final Iterator<Map.Entry<Class<?>, Annotation>> it = this._annotations.entrySet().iterator();
                final Map.Entry<Class<?>, Annotation> en1 = it.next();
                final Map.Entry<Class<?>, Annotation> en2 = it.next();
                return new TwoAnnotations(en1.getKey(), en1.getValue(), en2.getKey(), en2.getValue());
            }
            return new AnnotationMap(this._annotations);
        }
        
        @Override
        public AnnotationMap asAnnotationMap() {
            final AnnotationMap result = new AnnotationMap();
            for (final Annotation ann : this._annotations.values()) {
                result.add(ann);
            }
            return result;
        }
        
        @Override
        public boolean isPresent(final Annotation ann) {
            return this._annotations.containsKey(ann.annotationType());
        }
        
        @Override
        public AnnotationCollector addOrOverride(final Annotation ann) {
            this._annotations.put(ann.annotationType(), ann);
            return this;
        }
    }
    
    public static class NoAnnotations implements Annotations, Serializable
    {
        private static final long serialVersionUID = 1L;
        
        NoAnnotations() {
        }
        
        @Override
        public <A extends Annotation> A get(final Class<A> cls) {
            return null;
        }
        
        @Override
        public boolean has(final Class<?> cls) {
            return false;
        }
        
        @Override
        public boolean hasOneOf(final Class<? extends Annotation>[] annoClasses) {
            return false;
        }
        
        @Override
        public int size() {
            return 0;
        }
    }
    
    public static class OneAnnotation implements Annotations, Serializable
    {
        private static final long serialVersionUID = 1L;
        private final Class<?> _type;
        private final Annotation _value;
        
        public OneAnnotation(final Class<?> type, final Annotation value) {
            this._type = type;
            this._value = value;
        }
        
        @Override
        public <A extends Annotation> A get(final Class<A> cls) {
            if (this._type == cls) {
                return (A)this._value;
            }
            return null;
        }
        
        @Override
        public boolean has(final Class<?> cls) {
            return this._type == cls;
        }
        
        @Override
        public boolean hasOneOf(final Class<? extends Annotation>[] annoClasses) {
            for (final Class<?> cls : annoClasses) {
                if (cls == this._type) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public int size() {
            return 1;
        }
    }
    
    public static class TwoAnnotations implements Annotations, Serializable
    {
        private static final long serialVersionUID = 1L;
        private final Class<?> _type1;
        private final Class<?> _type2;
        private final Annotation _value1;
        private final Annotation _value2;
        
        public TwoAnnotations(final Class<?> type1, final Annotation value1, final Class<?> type2, final Annotation value2) {
            this._type1 = type1;
            this._value1 = value1;
            this._type2 = type2;
            this._value2 = value2;
        }
        
        @Override
        public <A extends Annotation> A get(final Class<A> cls) {
            if (this._type1 == cls) {
                return (A)this._value1;
            }
            if (this._type2 == cls) {
                return (A)this._value2;
            }
            return null;
        }
        
        @Override
        public boolean has(final Class<?> cls) {
            return this._type1 == cls || this._type2 == cls;
        }
        
        @Override
        public boolean hasOneOf(final Class<? extends Annotation>[] annoClasses) {
            for (final Class<?> cls : annoClasses) {
                if (cls == this._type1 || cls == this._type2) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public int size() {
            return 2;
        }
    }
}
