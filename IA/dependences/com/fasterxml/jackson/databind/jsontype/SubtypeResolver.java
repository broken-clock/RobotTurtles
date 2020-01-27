// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.jsontype;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import java.util.Collection;

public abstract class SubtypeResolver
{
    public abstract void registerSubtypes(final NamedType... p0);
    
    public abstract void registerSubtypes(final Class<?>... p0);
    
    public abstract void registerSubtypes(final Collection<Class<?>> p0);
    
    public Collection<NamedType> collectAndResolveSubtypesByClass(final MapperConfig<?> config, final AnnotatedMember property, final JavaType baseType) {
        return this.collectAndResolveSubtypes(property, config, config.getAnnotationIntrospector(), baseType);
    }
    
    public Collection<NamedType> collectAndResolveSubtypesByClass(final MapperConfig<?> config, final AnnotatedClass baseType) {
        return this.collectAndResolveSubtypes(baseType, config, config.getAnnotationIntrospector());
    }
    
    public Collection<NamedType> collectAndResolveSubtypesByTypeId(final MapperConfig<?> config, final AnnotatedMember property, final JavaType baseType) {
        return this.collectAndResolveSubtypes(property, config, config.getAnnotationIntrospector(), baseType);
    }
    
    public Collection<NamedType> collectAndResolveSubtypesByTypeId(final MapperConfig<?> config, final AnnotatedClass baseType) {
        return this.collectAndResolveSubtypes(baseType, config, config.getAnnotationIntrospector());
    }
    
    @Deprecated
    public Collection<NamedType> collectAndResolveSubtypes(final AnnotatedMember property, final MapperConfig<?> config, final AnnotationIntrospector ai, final JavaType baseType) {
        return this.collectAndResolveSubtypesByClass(config, property, baseType);
    }
    
    @Deprecated
    public Collection<NamedType> collectAndResolveSubtypes(final AnnotatedClass baseType, final MapperConfig<?> config, final AnnotationIntrospector ai) {
        return this.collectAndResolveSubtypesByClass(config, baseType);
    }
}
