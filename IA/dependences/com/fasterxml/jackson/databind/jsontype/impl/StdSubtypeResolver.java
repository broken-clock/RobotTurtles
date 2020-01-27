// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.jsontype.impl;

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.HashSet;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClassResolver;
import java.util.HashMap;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import java.util.Iterator;
import java.util.Collection;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import java.util.LinkedHashSet;
import java.io.Serializable;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;

public class StdSubtypeResolver extends SubtypeResolver implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected LinkedHashSet<NamedType> _registeredSubtypes;
    
    @Override
    public void registerSubtypes(final NamedType... types) {
        if (this._registeredSubtypes == null) {
            this._registeredSubtypes = new LinkedHashSet<NamedType>();
        }
        for (final NamedType type : types) {
            this._registeredSubtypes.add(type);
        }
    }
    
    @Override
    public void registerSubtypes(final Class<?>... classes) {
        final NamedType[] types = new NamedType[classes.length];
        for (int i = 0, len = classes.length; i < len; ++i) {
            types[i] = new NamedType(classes[i]);
        }
        this.registerSubtypes(types);
    }
    
    @Override
    public void registerSubtypes(final Collection<Class<?>> subtypes) {
        final int len = subtypes.size();
        final NamedType[] types = new NamedType[len];
        int i = 0;
        for (final Class<?> subtype : subtypes) {
            types[i++] = new NamedType(subtype);
        }
        this.registerSubtypes(types);
    }
    
    @Override
    public Collection<NamedType> collectAndResolveSubtypesByClass(final MapperConfig<?> config, final AnnotatedMember property, final JavaType baseType) {
        final AnnotationIntrospector ai = config.getAnnotationIntrospector();
        final Class<?> rawBase = (baseType == null) ? property.getRawType() : baseType.getRawClass();
        final HashMap<NamedType, NamedType> collected = new HashMap<NamedType, NamedType>();
        if (this._registeredSubtypes != null) {
            for (final NamedType subtype : this._registeredSubtypes) {
                if (rawBase.isAssignableFrom(subtype.getType())) {
                    final AnnotatedClass curr = AnnotatedClassResolver.resolveWithoutSuperTypes(config, subtype.getType());
                    this._collectAndResolve(curr, subtype, config, ai, collected);
                }
            }
        }
        if (property != null) {
            final Collection<NamedType> st = ai.findSubtypes(property);
            if (st != null) {
                for (final NamedType nt : st) {
                    final AnnotatedClass ac = AnnotatedClassResolver.resolveWithoutSuperTypes(config, nt.getType());
                    this._collectAndResolve(ac, nt, config, ai, collected);
                }
            }
        }
        final NamedType rootType = new NamedType(rawBase, null);
        final AnnotatedClass ac2 = AnnotatedClassResolver.resolveWithoutSuperTypes(config, rawBase);
        this._collectAndResolve(ac2, rootType, config, ai, collected);
        return new ArrayList<NamedType>(collected.values());
    }
    
    @Override
    public Collection<NamedType> collectAndResolveSubtypesByClass(final MapperConfig<?> config, final AnnotatedClass type) {
        final AnnotationIntrospector ai = config.getAnnotationIntrospector();
        final HashMap<NamedType, NamedType> subtypes = new HashMap<NamedType, NamedType>();
        if (this._registeredSubtypes != null) {
            final Class<?> rawBase = type.getRawType();
            for (final NamedType subtype : this._registeredSubtypes) {
                if (rawBase.isAssignableFrom(subtype.getType())) {
                    final AnnotatedClass curr = AnnotatedClassResolver.resolveWithoutSuperTypes(config, subtype.getType());
                    this._collectAndResolve(curr, subtype, config, ai, subtypes);
                }
            }
        }
        final NamedType rootType = new NamedType(type.getRawType(), null);
        this._collectAndResolve(type, rootType, config, ai, subtypes);
        return new ArrayList<NamedType>(subtypes.values());
    }
    
    @Override
    public Collection<NamedType> collectAndResolveSubtypesByTypeId(final MapperConfig<?> config, final AnnotatedMember property, final JavaType baseType) {
        final AnnotationIntrospector ai = config.getAnnotationIntrospector();
        final Class<?> rawBase = baseType.getRawClass();
        final Set<Class<?>> typesHandled = new HashSet<Class<?>>();
        final Map<String, NamedType> byName = new LinkedHashMap<String, NamedType>();
        final NamedType rootType = new NamedType(rawBase, null);
        AnnotatedClass ac = AnnotatedClassResolver.resolveWithoutSuperTypes(config, rawBase);
        this._collectAndResolveByTypeId(ac, rootType, config, typesHandled, byName);
        if (property != null) {
            final Collection<NamedType> st = ai.findSubtypes(property);
            if (st != null) {
                for (final NamedType nt : st) {
                    ac = AnnotatedClassResolver.resolveWithoutSuperTypes(config, nt.getType());
                    this._collectAndResolveByTypeId(ac, nt, config, typesHandled, byName);
                }
            }
        }
        if (this._registeredSubtypes != null) {
            for (final NamedType subtype : this._registeredSubtypes) {
                if (rawBase.isAssignableFrom(subtype.getType())) {
                    final AnnotatedClass curr = AnnotatedClassResolver.resolveWithoutSuperTypes(config, subtype.getType());
                    this._collectAndResolveByTypeId(curr, subtype, config, typesHandled, byName);
                }
            }
        }
        return this._combineNamedAndUnnamed(rawBase, typesHandled, byName);
    }
    
    @Override
    public Collection<NamedType> collectAndResolveSubtypesByTypeId(final MapperConfig<?> config, final AnnotatedClass baseType) {
        final Class<?> rawBase = baseType.getRawType();
        final Set<Class<?>> typesHandled = new HashSet<Class<?>>();
        final Map<String, NamedType> byName = new LinkedHashMap<String, NamedType>();
        final NamedType rootType = new NamedType(rawBase, null);
        this._collectAndResolveByTypeId(baseType, rootType, config, typesHandled, byName);
        if (this._registeredSubtypes != null) {
            for (final NamedType subtype : this._registeredSubtypes) {
                if (rawBase.isAssignableFrom(subtype.getType())) {
                    final AnnotatedClass curr = AnnotatedClassResolver.resolveWithoutSuperTypes(config, subtype.getType());
                    this._collectAndResolveByTypeId(curr, subtype, config, typesHandled, byName);
                }
            }
        }
        return this._combineNamedAndUnnamed(rawBase, typesHandled, byName);
    }
    
    protected void _collectAndResolve(final AnnotatedClass annotatedType, NamedType namedType, final MapperConfig<?> config, final AnnotationIntrospector ai, final HashMap<NamedType, NamedType> collectedSubtypes) {
        if (!namedType.hasName()) {
            final String name = ai.findTypeName(annotatedType);
            if (name != null) {
                namedType = new NamedType(namedType.getType(), name);
            }
        }
        if (collectedSubtypes.containsKey(namedType)) {
            if (namedType.hasName()) {
                final NamedType prev = collectedSubtypes.get(namedType);
                if (!prev.hasName()) {
                    collectedSubtypes.put(namedType, namedType);
                }
            }
            return;
        }
        collectedSubtypes.put(namedType, namedType);
        final Collection<NamedType> st = ai.findSubtypes(annotatedType);
        if (st != null && !st.isEmpty()) {
            for (final NamedType subtype : st) {
                final AnnotatedClass subtypeClass = AnnotatedClassResolver.resolveWithoutSuperTypes(config, subtype.getType());
                this._collectAndResolve(subtypeClass, subtype, config, ai, collectedSubtypes);
            }
        }
    }
    
    protected void _collectAndResolveByTypeId(final AnnotatedClass annotatedType, NamedType namedType, final MapperConfig<?> config, final Set<Class<?>> typesHandled, final Map<String, NamedType> byName) {
        final AnnotationIntrospector ai = config.getAnnotationIntrospector();
        if (!namedType.hasName()) {
            final String name = ai.findTypeName(annotatedType);
            if (name != null) {
                namedType = new NamedType(namedType.getType(), name);
            }
        }
        if (namedType.hasName()) {
            byName.put(namedType.getName(), namedType);
        }
        if (typesHandled.add(namedType.getType())) {
            final Collection<NamedType> st = ai.findSubtypes(annotatedType);
            if (st != null && !st.isEmpty()) {
                for (final NamedType subtype : st) {
                    final AnnotatedClass subtypeClass = AnnotatedClassResolver.resolveWithoutSuperTypes(config, subtype.getType());
                    this._collectAndResolveByTypeId(subtypeClass, subtype, config, typesHandled, byName);
                }
            }
        }
    }
    
    protected Collection<NamedType> _combineNamedAndUnnamed(final Class<?> rawBase, final Set<Class<?>> typesHandled, final Map<String, NamedType> byName) {
        final ArrayList<NamedType> result = new ArrayList<NamedType>(byName.values());
        for (final NamedType t : byName.values()) {
            typesHandled.remove(t.getType());
        }
        for (final Class<?> cls : typesHandled) {
            if (cls == rawBase && Modifier.isAbstract(cls.getModifiers())) {
                continue;
            }
            result.add(new NamedType(cls));
        }
        return result;
    }
}
