// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.introspect;

import java.lang.reflect.Modifier;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.List;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class AnnotatedFieldCollector extends CollectorBase
{
    private final TypeFactory _typeFactory;
    private final ClassIntrospector.MixInResolver _mixInResolver;
    
    AnnotatedFieldCollector(final AnnotationIntrospector intr, final TypeFactory types, final ClassIntrospector.MixInResolver mixins) {
        super(intr);
        this._typeFactory = types;
        this._mixInResolver = ((intr == null) ? null : mixins);
    }
    
    public static List<AnnotatedField> collectFields(final AnnotationIntrospector intr, final TypeResolutionContext tc, final ClassIntrospector.MixInResolver mixins, final TypeFactory types, final JavaType type) {
        return new AnnotatedFieldCollector(intr, types, mixins).collect(tc, type);
    }
    
    List<AnnotatedField> collect(final TypeResolutionContext tc, final JavaType type) {
        final Map<String, FieldBuilder> foundFields = this._findFields(tc, type, null);
        if (foundFields == null) {
            return Collections.emptyList();
        }
        final List<AnnotatedField> result = new ArrayList<AnnotatedField>(foundFields.size());
        for (final FieldBuilder b : foundFields.values()) {
            result.add(b.build());
        }
        return result;
    }
    
    private Map<String, FieldBuilder> _findFields(final TypeResolutionContext tc, final JavaType type, Map<String, FieldBuilder> fields) {
        final JavaType parent = type.getSuperClass();
        if (parent == null) {
            return fields;
        }
        final Class<?> cls = type.getRawClass();
        fields = this._findFields(new TypeResolutionContext.Basic(this._typeFactory, parent.getBindings()), parent, fields);
        for (final Field f : ClassUtil.getDeclaredFields(cls)) {
            if (this._isIncludableField(f)) {
                if (fields == null) {
                    fields = new LinkedHashMap<String, FieldBuilder>();
                }
                final FieldBuilder b = new FieldBuilder(tc, f);
                if (this._intr != null) {
                    b.annotations = this.collectAnnotations(b.annotations, f.getDeclaredAnnotations());
                }
                fields.put(f.getName(), b);
            }
        }
        if (this._mixInResolver != null) {
            final Class<?> mixin = this._mixInResolver.findMixInClassFor(cls);
            if (mixin != null) {
                this._addFieldMixIns(mixin, cls, fields);
            }
        }
        return fields;
    }
    
    private void _addFieldMixIns(final Class<?> mixInCls, final Class<?> targetClass, final Map<String, FieldBuilder> fields) {
        final List<Class<?>> parents = ClassUtil.findSuperClasses(mixInCls, targetClass, true);
        for (final Class<?> mixin : parents) {
            for (final Field mixinField : ClassUtil.getDeclaredFields(mixin)) {
                if (this._isIncludableField(mixinField)) {
                    final String name = mixinField.getName();
                    final FieldBuilder b = fields.get(name);
                    if (b != null) {
                        b.annotations = this.collectAnnotations(b.annotations, mixinField.getDeclaredAnnotations());
                    }
                }
            }
        }
    }
    
    private boolean _isIncludableField(final Field f) {
        if (f.isSynthetic()) {
            return false;
        }
        final int mods = f.getModifiers();
        return !Modifier.isStatic(mods);
    }
    
    private static final class FieldBuilder
    {
        public final TypeResolutionContext typeContext;
        public final Field field;
        public AnnotationCollector annotations;
        
        public FieldBuilder(final TypeResolutionContext tc, final Field f) {
            this.typeContext = tc;
            this.field = f;
            this.annotations = AnnotationCollector.emptyCollector();
        }
        
        public AnnotatedField build() {
            return new AnnotatedField(this.typeContext, this.field, this.annotations.asAnnotationMap());
        }
    }
}
