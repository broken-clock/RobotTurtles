// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.introspect;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import java.lang.annotation.Annotation;

class CollectorBase
{
    protected static final AnnotationMap[] NO_ANNOTATION_MAPS;
    protected static final Annotation[] NO_ANNOTATIONS;
    protected final AnnotationIntrospector _intr;
    
    protected CollectorBase(final AnnotationIntrospector intr) {
        this._intr = intr;
    }
    
    protected final AnnotationCollector collectAnnotations(final Annotation[] anns) {
        AnnotationCollector c = AnnotationCollector.emptyCollector();
        for (int i = 0, end = anns.length; i < end; ++i) {
            final Annotation ann = anns[i];
            c = c.addOrOverride(ann);
            if (this._intr.isAnnotationBundle(ann)) {
                c = this.collectFromBundle(c, ann);
            }
        }
        return c;
    }
    
    protected final AnnotationCollector collectAnnotations(AnnotationCollector c, final Annotation[] anns) {
        for (int i = 0, end = anns.length; i < end; ++i) {
            final Annotation ann = anns[i];
            c = c.addOrOverride(ann);
            if (this._intr.isAnnotationBundle(ann)) {
                c = this.collectFromBundle(c, ann);
            }
        }
        return c;
    }
    
    protected final AnnotationCollector collectFromBundle(AnnotationCollector c, final Annotation bundle) {
        final Annotation[] anns = ClassUtil.findClassAnnotations(bundle.annotationType());
        for (int i = 0, end = anns.length; i < end; ++i) {
            final Annotation ann = anns[i];
            if (!_ignorableAnnotation(ann)) {
                if (this._intr.isAnnotationBundle(ann)) {
                    if (!c.isPresent(ann)) {
                        c = c.addOrOverride(ann);
                        c = this.collectFromBundle(c, ann);
                    }
                }
                else {
                    c = c.addOrOverride(ann);
                }
            }
        }
        return c;
    }
    
    protected final AnnotationCollector collectDefaultAnnotations(AnnotationCollector c, final Annotation[] anns) {
        for (int i = 0, end = anns.length; i < end; ++i) {
            final Annotation ann = anns[i];
            if (!c.isPresent(ann)) {
                c = c.addOrOverride(ann);
                if (this._intr.isAnnotationBundle(ann)) {
                    c = this.collectDefaultFromBundle(c, ann);
                }
            }
        }
        return c;
    }
    
    protected final AnnotationCollector collectDefaultFromBundle(AnnotationCollector c, final Annotation bundle) {
        final Annotation[] anns = ClassUtil.findClassAnnotations(bundle.annotationType());
        for (int i = 0, end = anns.length; i < end; ++i) {
            final Annotation ann = anns[i];
            if (!_ignorableAnnotation(ann)) {
                if (!c.isPresent(ann)) {
                    c = c.addOrOverride(ann);
                    if (this._intr.isAnnotationBundle(ann)) {
                        c = this.collectFromBundle(c, ann);
                    }
                }
            }
        }
        return c;
    }
    
    protected static final boolean _ignorableAnnotation(final Annotation a) {
        return a instanceof Target || a instanceof Retention;
    }
    
    static AnnotationMap _emptyAnnotationMap() {
        return new AnnotationMap();
    }
    
    static AnnotationMap[] _emptyAnnotationMaps(final int count) {
        if (count == 0) {
            return CollectorBase.NO_ANNOTATION_MAPS;
        }
        final AnnotationMap[] maps = new AnnotationMap[count];
        for (int i = 0; i < count; ++i) {
            maps[i] = _emptyAnnotationMap();
        }
        return maps;
    }
    
    static {
        NO_ANNOTATION_MAPS = new AnnotationMap[0];
        NO_ANNOTATIONS = new Annotation[0];
    }
}
