// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.TimeZone;
import java.util.Locale;
import java.text.DateFormat;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.util.Map;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.util.RootNameLookup;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import com.fasterxml.jackson.databind.introspect.SimpleMixInResolver;
import java.io.Serializable;

public abstract class MapperConfigBase<CFG extends ConfigFeature, T extends MapperConfigBase<CFG, T>> extends MapperConfig<T> implements Serializable
{
    protected static final ConfigOverride EMPTY_OVERRIDE;
    private static final int DEFAULT_MAPPER_FEATURES;
    private static final int AUTO_DETECT_MASK;
    protected final SimpleMixInResolver _mixIns;
    protected final SubtypeResolver _subtypeResolver;
    protected final PropertyName _rootName;
    protected final Class<?> _view;
    protected final ContextAttributes _attributes;
    protected final RootNameLookup _rootNames;
    protected final ConfigOverrides _configOverrides;
    
    protected MapperConfigBase(final BaseSettings base, final SubtypeResolver str, final SimpleMixInResolver mixins, final RootNameLookup rootNames, final ConfigOverrides configOverrides) {
        super(base, MapperConfigBase.DEFAULT_MAPPER_FEATURES);
        this._mixIns = mixins;
        this._subtypeResolver = str;
        this._rootNames = rootNames;
        this._rootName = null;
        this._view = null;
        this._attributes = ContextAttributes.getEmpty();
        this._configOverrides = configOverrides;
    }
    
    protected MapperConfigBase(final MapperConfigBase<CFG, T> src, final SimpleMixInResolver mixins, final RootNameLookup rootNames, final ConfigOverrides configOverrides) {
        super(src, src._base.copy());
        this._mixIns = mixins;
        this._subtypeResolver = src._subtypeResolver;
        this._rootNames = rootNames;
        this._rootName = src._rootName;
        this._view = src._view;
        this._attributes = src._attributes;
        this._configOverrides = configOverrides;
    }
    
    protected MapperConfigBase(final MapperConfigBase<CFG, T> src) {
        super(src);
        this._mixIns = src._mixIns;
        this._subtypeResolver = src._subtypeResolver;
        this._rootNames = src._rootNames;
        this._rootName = src._rootName;
        this._view = src._view;
        this._attributes = src._attributes;
        this._configOverrides = src._configOverrides;
    }
    
    protected MapperConfigBase(final MapperConfigBase<CFG, T> src, final BaseSettings base) {
        super(src, base);
        this._mixIns = src._mixIns;
        this._subtypeResolver = src._subtypeResolver;
        this._rootNames = src._rootNames;
        this._rootName = src._rootName;
        this._view = src._view;
        this._attributes = src._attributes;
        this._configOverrides = src._configOverrides;
    }
    
    protected MapperConfigBase(final MapperConfigBase<CFG, T> src, final int mapperFeatures) {
        super(src, mapperFeatures);
        this._mixIns = src._mixIns;
        this._subtypeResolver = src._subtypeResolver;
        this._rootNames = src._rootNames;
        this._rootName = src._rootName;
        this._view = src._view;
        this._attributes = src._attributes;
        this._configOverrides = src._configOverrides;
    }
    
    protected MapperConfigBase(final MapperConfigBase<CFG, T> src, final SubtypeResolver str) {
        super(src);
        this._mixIns = src._mixIns;
        this._subtypeResolver = str;
        this._rootNames = src._rootNames;
        this._rootName = src._rootName;
        this._view = src._view;
        this._attributes = src._attributes;
        this._configOverrides = src._configOverrides;
    }
    
    protected MapperConfigBase(final MapperConfigBase<CFG, T> src, final PropertyName rootName) {
        super(src);
        this._mixIns = src._mixIns;
        this._subtypeResolver = src._subtypeResolver;
        this._rootNames = src._rootNames;
        this._rootName = rootName;
        this._view = src._view;
        this._attributes = src._attributes;
        this._configOverrides = src._configOverrides;
    }
    
    protected MapperConfigBase(final MapperConfigBase<CFG, T> src, final Class<?> view) {
        super(src);
        this._mixIns = src._mixIns;
        this._subtypeResolver = src._subtypeResolver;
        this._rootNames = src._rootNames;
        this._rootName = src._rootName;
        this._view = view;
        this._attributes = src._attributes;
        this._configOverrides = src._configOverrides;
    }
    
    protected MapperConfigBase(final MapperConfigBase<CFG, T> src, final SimpleMixInResolver mixins) {
        super(src);
        this._mixIns = mixins;
        this._subtypeResolver = src._subtypeResolver;
        this._rootNames = src._rootNames;
        this._rootName = src._rootName;
        this._view = src._view;
        this._attributes = src._attributes;
        this._configOverrides = src._configOverrides;
    }
    
    protected MapperConfigBase(final MapperConfigBase<CFG, T> src, final ContextAttributes attr) {
        super(src);
        this._mixIns = src._mixIns;
        this._subtypeResolver = src._subtypeResolver;
        this._rootNames = src._rootNames;
        this._rootName = src._rootName;
        this._view = src._view;
        this._attributes = attr;
        this._configOverrides = src._configOverrides;
    }
    
    protected abstract T _withBase(final BaseSettings p0);
    
    protected abstract T _withMapperFeatures(final int p0);
    
    @Override
    public final T with(final MapperFeature... features) {
        int newMapperFlags = this._mapperFeatures;
        for (final MapperFeature f : features) {
            newMapperFlags |= f.getMask();
        }
        if (newMapperFlags == this._mapperFeatures) {
            return (T)this;
        }
        return this._withMapperFeatures(newMapperFlags);
    }
    
    @Override
    public final T without(final MapperFeature... features) {
        int newMapperFlags = this._mapperFeatures;
        for (final MapperFeature f : features) {
            newMapperFlags &= ~f.getMask();
        }
        if (newMapperFlags == this._mapperFeatures) {
            return (T)this;
        }
        return this._withMapperFeatures(newMapperFlags);
    }
    
    @Override
    public final T with(final MapperFeature feature, final boolean state) {
        int newMapperFlags;
        if (state) {
            newMapperFlags = (this._mapperFeatures | feature.getMask());
        }
        else {
            newMapperFlags = (this._mapperFeatures & ~feature.getMask());
        }
        if (newMapperFlags == this._mapperFeatures) {
            return (T)this;
        }
        return this._withMapperFeatures(newMapperFlags);
    }
    
    public final T with(final AnnotationIntrospector ai) {
        return this._withBase(this._base.withAnnotationIntrospector(ai));
    }
    
    public final T withAppendedAnnotationIntrospector(final AnnotationIntrospector ai) {
        return this._withBase(this._base.withAppendedAnnotationIntrospector(ai));
    }
    
    public final T withInsertedAnnotationIntrospector(final AnnotationIntrospector ai) {
        return this._withBase(this._base.withInsertedAnnotationIntrospector(ai));
    }
    
    public final T with(final ClassIntrospector ci) {
        return this._withBase(this._base.withClassIntrospector(ci));
    }
    
    public abstract T with(final ContextAttributes p0);
    
    public T withAttributes(final Map<?, ?> attributes) {
        return this.with(this.getAttributes().withSharedAttributes(attributes));
    }
    
    public T withAttribute(final Object key, final Object value) {
        return this.with(this.getAttributes().withSharedAttribute(key, value));
    }
    
    public T withoutAttribute(final Object key) {
        return this.with(this.getAttributes().withoutSharedAttribute(key));
    }
    
    public final T with(final TypeFactory tf) {
        return this._withBase(this._base.withTypeFactory(tf));
    }
    
    public final T with(final TypeResolverBuilder<?> trb) {
        return this._withBase(this._base.withTypeResolverBuilder(trb));
    }
    
    public final T with(final PropertyNamingStrategy pns) {
        return this._withBase(this._base.withPropertyNamingStrategy(pns));
    }
    
    public final T with(final HandlerInstantiator hi) {
        return this._withBase(this._base.withHandlerInstantiator(hi));
    }
    
    public final T with(final Base64Variant base64) {
        return this._withBase(this._base.with(base64));
    }
    
    public T with(final DateFormat df) {
        return this._withBase(this._base.withDateFormat(df));
    }
    
    public final T with(final Locale l) {
        return this._withBase(this._base.with(l));
    }
    
    public final T with(final TimeZone tz) {
        return this._withBase(this._base.with(tz));
    }
    
    public abstract T withRootName(final PropertyName p0);
    
    public T withRootName(final String rootName) {
        if (rootName == null) {
            return this.withRootName((PropertyName)null);
        }
        return this.withRootName(PropertyName.construct(rootName));
    }
    
    public abstract T with(final SubtypeResolver p0);
    
    public abstract T withView(final Class<?> p0);
    
    @Override
    public final SubtypeResolver getSubtypeResolver() {
        return this._subtypeResolver;
    }
    
    @Deprecated
    public final String getRootName() {
        return (this._rootName == null) ? null : this._rootName.getSimpleName();
    }
    
    public final PropertyName getFullRootName() {
        return this._rootName;
    }
    
    @Override
    public final Class<?> getActiveView() {
        return this._view;
    }
    
    @Override
    public final ContextAttributes getAttributes() {
        return this._attributes;
    }
    
    @Override
    public final ConfigOverride getConfigOverride(final Class<?> type) {
        final ConfigOverride override = this._configOverrides.findOverride(type);
        return (override == null) ? MapperConfigBase.EMPTY_OVERRIDE : override;
    }
    
    @Override
    public final ConfigOverride findConfigOverride(final Class<?> type) {
        return this._configOverrides.findOverride(type);
    }
    
    @Override
    public final JsonInclude.Value getDefaultPropertyInclusion() {
        return this._configOverrides.getDefaultInclusion();
    }
    
    @Override
    public final JsonInclude.Value getDefaultPropertyInclusion(final Class<?> baseType) {
        final JsonInclude.Value v = this.getConfigOverride(baseType).getInclude();
        final JsonInclude.Value def = this.getDefaultPropertyInclusion();
        if (def == null) {
            return v;
        }
        return def.withOverrides(v);
    }
    
    @Override
    public final JsonInclude.Value getDefaultInclusion(final Class<?> baseType, final Class<?> propertyType) {
        final JsonInclude.Value v = this.getConfigOverride(propertyType).getIncludeAsProperty();
        final JsonInclude.Value def = this.getDefaultPropertyInclusion(baseType);
        if (def == null) {
            return v;
        }
        return def.withOverrides(v);
    }
    
    @Override
    public final JsonFormat.Value getDefaultPropertyFormat(final Class<?> type) {
        final ConfigOverride overrides = this._configOverrides.findOverride(type);
        if (overrides != null) {
            final JsonFormat.Value v = overrides.getFormat();
            if (v != null) {
                return v;
            }
        }
        return MapperConfigBase.EMPTY_FORMAT;
    }
    
    @Override
    public final JsonIgnoreProperties.Value getDefaultPropertyIgnorals(final Class<?> type) {
        final ConfigOverride overrides = this._configOverrides.findOverride(type);
        if (overrides != null) {
            final JsonIgnoreProperties.Value v = overrides.getIgnorals();
            if (v != null) {
                return v;
            }
        }
        return null;
    }
    
    @Override
    public final JsonIgnoreProperties.Value getDefaultPropertyIgnorals(final Class<?> baseType, final AnnotatedClass actualClass) {
        final AnnotationIntrospector intr = this.getAnnotationIntrospector();
        final JsonIgnoreProperties.Value base = (intr == null) ? null : intr.findPropertyIgnorals(actualClass);
        final JsonIgnoreProperties.Value overrides = this.getDefaultPropertyIgnorals(baseType);
        return JsonIgnoreProperties.Value.merge(base, overrides);
    }
    
    @Override
    public final VisibilityChecker<?> getDefaultVisibilityChecker() {
        VisibilityChecker<?> vchecker = this._configOverrides.getDefaultVisibility();
        if ((this._mapperFeatures & MapperConfigBase.AUTO_DETECT_MASK) != MapperConfigBase.AUTO_DETECT_MASK) {
            if (!this.isEnabled(MapperFeature.AUTO_DETECT_FIELDS)) {
                vchecker = (VisibilityChecker<?>)vchecker.withFieldVisibility(JsonAutoDetect.Visibility.NONE);
            }
            if (!this.isEnabled(MapperFeature.AUTO_DETECT_GETTERS)) {
                vchecker = (VisibilityChecker<?>)vchecker.withGetterVisibility(JsonAutoDetect.Visibility.NONE);
            }
            if (!this.isEnabled(MapperFeature.AUTO_DETECT_IS_GETTERS)) {
                vchecker = (VisibilityChecker<?>)vchecker.withIsGetterVisibility(JsonAutoDetect.Visibility.NONE);
            }
            if (!this.isEnabled(MapperFeature.AUTO_DETECT_SETTERS)) {
                vchecker = (VisibilityChecker<?>)vchecker.withSetterVisibility(JsonAutoDetect.Visibility.NONE);
            }
            if (!this.isEnabled(MapperFeature.AUTO_DETECT_CREATORS)) {
                vchecker = (VisibilityChecker<?>)vchecker.withCreatorVisibility(JsonAutoDetect.Visibility.NONE);
            }
        }
        return vchecker;
    }
    
    @Override
    public final VisibilityChecker<?> getDefaultVisibilityChecker(final Class<?> baseType, final AnnotatedClass actualClass) {
        VisibilityChecker<?> vc = this.getDefaultVisibilityChecker();
        final AnnotationIntrospector intr = this.getAnnotationIntrospector();
        if (intr != null) {
            vc = intr.findAutoDetectVisibility(actualClass, vc);
        }
        final ConfigOverride overrides = this._configOverrides.findOverride(baseType);
        if (overrides != null) {
            vc = (VisibilityChecker<?>)vc.withOverrides(overrides.getVisibility());
        }
        return vc;
    }
    
    @Override
    public final JsonSetter.Value getDefaultSetterInfo() {
        return this._configOverrides.getDefaultSetterInfo();
    }
    
    @Override
    public Boolean getDefaultMergeable() {
        return this._configOverrides.getDefaultMergeable();
    }
    
    @Override
    public Boolean getDefaultMergeable(final Class<?> baseType) {
        final ConfigOverride cfg = this._configOverrides.findOverride(baseType);
        if (cfg != null) {
            final Boolean b = cfg.getMergeable();
            if (b != null) {
                return b;
            }
        }
        return this._configOverrides.getDefaultMergeable();
    }
    
    @Override
    public PropertyName findRootName(final JavaType rootType) {
        if (this._rootName != null) {
            return this._rootName;
        }
        return this._rootNames.findRootName(rootType, this);
    }
    
    @Override
    public PropertyName findRootName(final Class<?> rawRootType) {
        if (this._rootName != null) {
            return this._rootName;
        }
        return this._rootNames.findRootName(rawRootType, this);
    }
    
    @Override
    public final Class<?> findMixInClassFor(final Class<?> cls) {
        return this._mixIns.findMixInClassFor(cls);
    }
    
    @Override
    public ClassIntrospector.MixInResolver copy() {
        throw new UnsupportedOperationException();
    }
    
    public final int mixInCount() {
        return this._mixIns.localSize();
    }
    
    static {
        EMPTY_OVERRIDE = ConfigOverride.empty();
        DEFAULT_MAPPER_FEATURES = MapperConfig.collectFeatureDefaults(MapperFeature.class);
        AUTO_DETECT_MASK = (MapperFeature.AUTO_DETECT_FIELDS.getMask() | MapperFeature.AUTO_DETECT_GETTERS.getMask() | MapperFeature.AUTO_DETECT_IS_GETTERS.getMask() | MapperFeature.AUTO_DETECT_SETTERS.getMask() | MapperFeature.AUTO_DETECT_CREATORS.getMask());
    }
}
