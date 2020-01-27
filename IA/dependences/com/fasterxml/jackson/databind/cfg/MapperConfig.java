// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.core.Base64Variant;
import java.util.TimeZone;
import java.util.Locale;
import java.text.DateFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.core.type.TypeReference;
import java.lang.reflect.Type;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;

public abstract class MapperConfig<T extends MapperConfig<T>> implements ClassIntrospector.MixInResolver, Serializable
{
    private static final long serialVersionUID = 2L;
    protected static final JsonInclude.Value EMPTY_INCLUDE;
    protected static final JsonFormat.Value EMPTY_FORMAT;
    protected final int _mapperFeatures;
    protected final BaseSettings _base;
    
    protected MapperConfig(final BaseSettings base, final int mapperFeatures) {
        this._base = base;
        this._mapperFeatures = mapperFeatures;
    }
    
    protected MapperConfig(final MapperConfig<T> src, final int mapperFeatures) {
        this._base = src._base;
        this._mapperFeatures = mapperFeatures;
    }
    
    protected MapperConfig(final MapperConfig<T> src, final BaseSettings base) {
        this._base = base;
        this._mapperFeatures = src._mapperFeatures;
    }
    
    protected MapperConfig(final MapperConfig<T> src) {
        this._base = src._base;
        this._mapperFeatures = src._mapperFeatures;
    }
    
    public static <F extends Enum> int collectFeatureDefaults(final Class<F> enumClass) {
        int flags = 0;
        for (final F value : (java.lang.Enum[])enumClass.getEnumConstants()) {
            if (((ConfigFeature)value).enabledByDefault()) {
                flags |= ((ConfigFeature)value).getMask();
            }
        }
        return flags;
    }
    
    public abstract T with(final MapperFeature... p0);
    
    public abstract T without(final MapperFeature... p0);
    
    public abstract T with(final MapperFeature p0, final boolean p1);
    
    public final boolean isEnabled(final MapperFeature f) {
        return (this._mapperFeatures & f.getMask()) != 0x0;
    }
    
    public final boolean hasMapperFeatures(final int featureMask) {
        return (this._mapperFeatures & featureMask) == featureMask;
    }
    
    public final boolean isAnnotationProcessingEnabled() {
        return this.isEnabled(MapperFeature.USE_ANNOTATIONS);
    }
    
    public final boolean canOverrideAccessModifiers() {
        return this.isEnabled(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS);
    }
    
    public final boolean shouldSortPropertiesAlphabetically() {
        return this.isEnabled(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
    }
    
    public abstract boolean useRootWrapping();
    
    public SerializableString compileString(final String src) {
        return new SerializedString(src);
    }
    
    public ClassIntrospector getClassIntrospector() {
        return this._base.getClassIntrospector();
    }
    
    public AnnotationIntrospector getAnnotationIntrospector() {
        if (this.isEnabled(MapperFeature.USE_ANNOTATIONS)) {
            return this._base.getAnnotationIntrospector();
        }
        return NopAnnotationIntrospector.instance;
    }
    
    public final PropertyNamingStrategy getPropertyNamingStrategy() {
        return this._base.getPropertyNamingStrategy();
    }
    
    public final HandlerInstantiator getHandlerInstantiator() {
        return this._base.getHandlerInstantiator();
    }
    
    public final TypeResolverBuilder<?> getDefaultTyper(final JavaType baseType) {
        return this._base.getTypeResolverBuilder();
    }
    
    public abstract SubtypeResolver getSubtypeResolver();
    
    public final TypeFactory getTypeFactory() {
        return this._base.getTypeFactory();
    }
    
    public final JavaType constructType(final Class<?> cls) {
        return this.getTypeFactory().constructType(cls);
    }
    
    public final JavaType constructType(final TypeReference<?> valueTypeRef) {
        return this.getTypeFactory().constructType(valueTypeRef.getType());
    }
    
    public JavaType constructSpecializedType(final JavaType baseType, final Class<?> subclass) {
        return this.getTypeFactory().constructSpecializedType(baseType, subclass);
    }
    
    public BeanDescription introspectClassAnnotations(final Class<?> cls) {
        return this.introspectClassAnnotations(this.constructType(cls));
    }
    
    public BeanDescription introspectClassAnnotations(final JavaType type) {
        return this.getClassIntrospector().forClassAnnotations(this, type, this);
    }
    
    public BeanDescription introspectDirectClassAnnotations(final Class<?> cls) {
        return this.introspectDirectClassAnnotations(this.constructType(cls));
    }
    
    public final BeanDescription introspectDirectClassAnnotations(final JavaType type) {
        return this.getClassIntrospector().forDirectClassAnnotations(this, type, this);
    }
    
    public abstract ConfigOverride findConfigOverride(final Class<?> p0);
    
    public abstract ConfigOverride getConfigOverride(final Class<?> p0);
    
    public abstract JsonInclude.Value getDefaultPropertyInclusion();
    
    public abstract JsonInclude.Value getDefaultPropertyInclusion(final Class<?> p0);
    
    public JsonInclude.Value getDefaultPropertyInclusion(final Class<?> baseType, final JsonInclude.Value defaultIncl) {
        final JsonInclude.Value v = this.getConfigOverride(baseType).getInclude();
        if (v != null) {
            return v;
        }
        return defaultIncl;
    }
    
    public abstract JsonInclude.Value getDefaultInclusion(final Class<?> p0, final Class<?> p1);
    
    public JsonInclude.Value getDefaultInclusion(final Class<?> baseType, final Class<?> propertyType, final JsonInclude.Value defaultIncl) {
        final JsonInclude.Value baseOverride = this.getConfigOverride(baseType).getInclude();
        final JsonInclude.Value propOverride = this.getConfigOverride(propertyType).getIncludeAsProperty();
        final JsonInclude.Value result = JsonInclude.Value.mergeAll(defaultIncl, baseOverride, propOverride);
        return result;
    }
    
    public abstract JsonFormat.Value getDefaultPropertyFormat(final Class<?> p0);
    
    public abstract JsonIgnoreProperties.Value getDefaultPropertyIgnorals(final Class<?> p0);
    
    public abstract JsonIgnoreProperties.Value getDefaultPropertyIgnorals(final Class<?> p0, final AnnotatedClass p1);
    
    public abstract VisibilityChecker<?> getDefaultVisibilityChecker();
    
    public abstract VisibilityChecker<?> getDefaultVisibilityChecker(final Class<?> p0, final AnnotatedClass p1);
    
    public abstract JsonSetter.Value getDefaultSetterInfo();
    
    public abstract Boolean getDefaultMergeable();
    
    public abstract Boolean getDefaultMergeable(final Class<?> p0);
    
    public final DateFormat getDateFormat() {
        return this._base.getDateFormat();
    }
    
    public final Locale getLocale() {
        return this._base.getLocale();
    }
    
    public final TimeZone getTimeZone() {
        return this._base.getTimeZone();
    }
    
    public abstract Class<?> getActiveView();
    
    public Base64Variant getBase64Variant() {
        return this._base.getBase64Variant();
    }
    
    public abstract ContextAttributes getAttributes();
    
    public abstract PropertyName findRootName(final JavaType p0);
    
    public abstract PropertyName findRootName(final Class<?> p0);
    
    public TypeResolverBuilder<?> typeResolverBuilderInstance(final Annotated annotated, final Class<? extends TypeResolverBuilder<?>> builderClass) {
        final HandlerInstantiator hi = this.getHandlerInstantiator();
        if (hi != null) {
            final TypeResolverBuilder<?> builder = hi.typeResolverBuilderInstance(this, annotated, builderClass);
            if (builder != null) {
                return builder;
            }
        }
        return ClassUtil.createInstance(builderClass, this.canOverrideAccessModifiers());
    }
    
    public TypeIdResolver typeIdResolverInstance(final Annotated annotated, final Class<? extends TypeIdResolver> resolverClass) {
        final HandlerInstantiator hi = this.getHandlerInstantiator();
        if (hi != null) {
            final TypeIdResolver builder = hi.typeIdResolverInstance(this, annotated, resolverClass);
            if (builder != null) {
                return builder;
            }
        }
        return ClassUtil.createInstance(resolverClass, this.canOverrideAccessModifiers());
    }
    
    static {
        EMPTY_INCLUDE = JsonInclude.Value.empty();
        EMPTY_FORMAT = JsonFormat.Value.empty();
    }
}
