// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.type.SimpleType;
import com.fasterxml.jackson.databind.BeanDescription;
import java.util.Map;
import java.util.Collection;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.util.LRUMap;
import java.io.Serializable;

public class BasicClassIntrospector extends ClassIntrospector implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected static final BasicBeanDescription STRING_DESC;
    protected static final BasicBeanDescription BOOLEAN_DESC;
    protected static final BasicBeanDescription INT_DESC;
    protected static final BasicBeanDescription LONG_DESC;
    protected final LRUMap<JavaType, BasicBeanDescription> _cachedFCA;
    
    public BasicClassIntrospector() {
        this._cachedFCA = new LRUMap<JavaType, BasicBeanDescription>(16, 64);
    }
    
    @Override
    public ClassIntrospector copy() {
        return new BasicClassIntrospector();
    }
    
    @Override
    public BasicBeanDescription forSerialization(final SerializationConfig cfg, final JavaType type, final MixInResolver r) {
        BasicBeanDescription desc = this._findStdTypeDesc(type);
        if (desc == null) {
            desc = this._findStdJdkCollectionDesc(cfg, type);
            if (desc == null) {
                desc = BasicBeanDescription.forSerialization(this.collectProperties(cfg, type, r, true, "set"));
            }
            this._cachedFCA.putIfAbsent(type, desc);
        }
        return desc;
    }
    
    @Override
    public BasicBeanDescription forDeserialization(final DeserializationConfig cfg, final JavaType type, final MixInResolver r) {
        BasicBeanDescription desc = this._findStdTypeDesc(type);
        if (desc == null) {
            desc = this._findStdJdkCollectionDesc(cfg, type);
            if (desc == null) {
                desc = BasicBeanDescription.forDeserialization(this.collectProperties(cfg, type, r, false, "set"));
            }
            this._cachedFCA.putIfAbsent(type, desc);
        }
        return desc;
    }
    
    @Override
    public BasicBeanDescription forDeserializationWithBuilder(final DeserializationConfig cfg, final JavaType type, final MixInResolver r) {
        final BasicBeanDescription desc = BasicBeanDescription.forDeserialization(this.collectPropertiesWithBuilder(cfg, type, r, false));
        this._cachedFCA.putIfAbsent(type, desc);
        return desc;
    }
    
    @Override
    public BasicBeanDescription forCreation(final DeserializationConfig cfg, final JavaType type, final MixInResolver r) {
        BasicBeanDescription desc = this._findStdTypeDesc(type);
        if (desc == null) {
            desc = this._findStdJdkCollectionDesc(cfg, type);
            if (desc == null) {
                desc = BasicBeanDescription.forDeserialization(this.collectProperties(cfg, type, r, false, "set"));
            }
        }
        return desc;
    }
    
    @Override
    public BasicBeanDescription forClassAnnotations(final MapperConfig<?> config, final JavaType type, final MixInResolver r) {
        BasicBeanDescription desc = this._findStdTypeDesc(type);
        if (desc == null) {
            desc = this._cachedFCA.get(type);
            if (desc == null) {
                desc = BasicBeanDescription.forOtherUse(config, type, this._resolveAnnotatedClass(config, type, r));
                this._cachedFCA.put(type, desc);
            }
        }
        return desc;
    }
    
    @Override
    public BasicBeanDescription forDirectClassAnnotations(final MapperConfig<?> config, final JavaType type, final MixInResolver r) {
        BasicBeanDescription desc = this._findStdTypeDesc(type);
        if (desc == null) {
            desc = BasicBeanDescription.forOtherUse(config, type, this._resolveAnnotatedWithoutSuperTypes(config, type, r));
        }
        return desc;
    }
    
    protected POJOPropertiesCollector collectProperties(final MapperConfig<?> config, final JavaType type, final MixInResolver r, final boolean forSerialization, final String mutatorPrefix) {
        return this.constructPropertyCollector(config, this._resolveAnnotatedClass(config, type, r), type, forSerialization, mutatorPrefix);
    }
    
    protected POJOPropertiesCollector collectPropertiesWithBuilder(final MapperConfig<?> config, final JavaType type, final MixInResolver r, final boolean forSerialization) {
        final AnnotatedClass ac = this._resolveAnnotatedClass(config, type, r);
        final AnnotationIntrospector ai = config.isAnnotationProcessingEnabled() ? config.getAnnotationIntrospector() : null;
        final JsonPOJOBuilder.Value builderConfig = (ai == null) ? null : ai.findPOJOBuilderConfig(ac);
        final String mutatorPrefix = (builderConfig == null) ? "with" : builderConfig.withPrefix;
        return this.constructPropertyCollector(config, ac, type, forSerialization, mutatorPrefix);
    }
    
    protected POJOPropertiesCollector constructPropertyCollector(final MapperConfig<?> config, final AnnotatedClass ac, final JavaType type, final boolean forSerialization, final String mutatorPrefix) {
        return new POJOPropertiesCollector(config, forSerialization, type, ac, mutatorPrefix);
    }
    
    protected BasicBeanDescription _findStdTypeDesc(final JavaType type) {
        final Class<?> cls = type.getRawClass();
        if (cls.isPrimitive()) {
            if (cls == Boolean.TYPE) {
                return BasicClassIntrospector.BOOLEAN_DESC;
            }
            if (cls == Integer.TYPE) {
                return BasicClassIntrospector.INT_DESC;
            }
            if (cls == Long.TYPE) {
                return BasicClassIntrospector.LONG_DESC;
            }
        }
        else if (cls == String.class) {
            return BasicClassIntrospector.STRING_DESC;
        }
        return null;
    }
    
    protected boolean _isStdJDKCollection(final JavaType type) {
        if (!type.isContainerType() || type.isArrayType()) {
            return false;
        }
        final Class<?> raw = type.getRawClass();
        final String pkgName = ClassUtil.getPackageName(raw);
        return pkgName != null && (pkgName.startsWith("java.lang") || pkgName.startsWith("java.util")) && (Collection.class.isAssignableFrom(raw) || Map.class.isAssignableFrom(raw));
    }
    
    protected BasicBeanDescription _findStdJdkCollectionDesc(final MapperConfig<?> cfg, final JavaType type) {
        if (this._isStdJDKCollection(type)) {
            return BasicBeanDescription.forOtherUse(cfg, type, this._resolveAnnotatedClass(cfg, type, cfg));
        }
        return null;
    }
    
    protected AnnotatedClass _resolveAnnotatedClass(final MapperConfig<?> config, final JavaType type, final MixInResolver r) {
        return AnnotatedClassResolver.resolve(config, type, r);
    }
    
    protected AnnotatedClass _resolveAnnotatedWithoutSuperTypes(final MapperConfig<?> config, final JavaType type, final MixInResolver r) {
        return AnnotatedClassResolver.resolveWithoutSuperTypes(config, type, r);
    }
    
    static {
        STRING_DESC = BasicBeanDescription.forOtherUse(null, SimpleType.constructUnsafe(String.class), AnnotatedClassResolver.createPrimordial(String.class));
        BOOLEAN_DESC = BasicBeanDescription.forOtherUse(null, SimpleType.constructUnsafe(Boolean.TYPE), AnnotatedClassResolver.createPrimordial(Boolean.TYPE));
        INT_DESC = BasicBeanDescription.forOtherUse(null, SimpleType.constructUnsafe(Integer.TYPE), AnnotatedClassResolver.createPrimordial(Integer.TYPE));
        LONG_DESC = BasicBeanDescription.forOtherUse(null, SimpleType.constructUnsafe(Long.TYPE), AnnotatedClassResolver.createPrimordial(Long.TYPE));
    }
}
