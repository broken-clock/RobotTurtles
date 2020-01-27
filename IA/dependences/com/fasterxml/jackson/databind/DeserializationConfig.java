// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import java.util.Collection;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.FormatFeature;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.cfg.ConfigOverrides;
import com.fasterxml.jackson.databind.util.RootNameLookup;
import com.fasterxml.jackson.databind.introspect.SimpleMixInResolver;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import com.fasterxml.jackson.databind.cfg.BaseSettings;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.util.LinkedNode;
import java.io.Serializable;
import com.fasterxml.jackson.databind.cfg.MapperConfigBase;

public final class DeserializationConfig extends MapperConfigBase<DeserializationFeature, DeserializationConfig> implements Serializable
{
    private static final long serialVersionUID = 2L;
    protected final LinkedNode<DeserializationProblemHandler> _problemHandlers;
    protected final JsonNodeFactory _nodeFactory;
    protected final int _deserFeatures;
    protected final int _parserFeatures;
    protected final int _parserFeaturesToChange;
    protected final int _formatReadFeatures;
    protected final int _formatReadFeaturesToChange;
    
    public DeserializationConfig(final BaseSettings base, final SubtypeResolver str, final SimpleMixInResolver mixins, final RootNameLookup rootNames, final ConfigOverrides configOverrides) {
        super(base, str, mixins, rootNames, configOverrides);
        this._deserFeatures = MapperConfig.collectFeatureDefaults(DeserializationFeature.class);
        this._nodeFactory = JsonNodeFactory.instance;
        this._problemHandlers = null;
        this._parserFeatures = 0;
        this._parserFeaturesToChange = 0;
        this._formatReadFeatures = 0;
        this._formatReadFeaturesToChange = 0;
    }
    
    protected DeserializationConfig(final DeserializationConfig src, final SimpleMixInResolver mixins, final RootNameLookup rootNames, final ConfigOverrides configOverrides) {
        super(src, mixins, rootNames, configOverrides);
        this._deserFeatures = src._deserFeatures;
        this._problemHandlers = src._problemHandlers;
        this._nodeFactory = src._nodeFactory;
        this._parserFeatures = src._parserFeatures;
        this._parserFeaturesToChange = src._parserFeaturesToChange;
        this._formatReadFeatures = src._formatReadFeatures;
        this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
    }
    
    private DeserializationConfig(final DeserializationConfig src, final int mapperFeatures, final int deserFeatures, final int parserFeatures, final int parserFeatureMask, final int formatFeatures, final int formatFeatureMask) {
        super(src, mapperFeatures);
        this._deserFeatures = deserFeatures;
        this._nodeFactory = src._nodeFactory;
        this._problemHandlers = src._problemHandlers;
        this._parserFeatures = parserFeatures;
        this._parserFeaturesToChange = parserFeatureMask;
        this._formatReadFeatures = formatFeatures;
        this._formatReadFeaturesToChange = formatFeatureMask;
    }
    
    private DeserializationConfig(final DeserializationConfig src, final SubtypeResolver str) {
        super(src, str);
        this._deserFeatures = src._deserFeatures;
        this._nodeFactory = src._nodeFactory;
        this._problemHandlers = src._problemHandlers;
        this._parserFeatures = src._parserFeatures;
        this._parserFeaturesToChange = src._parserFeaturesToChange;
        this._formatReadFeatures = src._formatReadFeatures;
        this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
    }
    
    private DeserializationConfig(final DeserializationConfig src, final BaseSettings base) {
        super(src, base);
        this._deserFeatures = src._deserFeatures;
        this._nodeFactory = src._nodeFactory;
        this._problemHandlers = src._problemHandlers;
        this._parserFeatures = src._parserFeatures;
        this._parserFeaturesToChange = src._parserFeaturesToChange;
        this._formatReadFeatures = src._formatReadFeatures;
        this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
    }
    
    private DeserializationConfig(final DeserializationConfig src, final JsonNodeFactory f) {
        super(src);
        this._deserFeatures = src._deserFeatures;
        this._problemHandlers = src._problemHandlers;
        this._nodeFactory = f;
        this._parserFeatures = src._parserFeatures;
        this._parserFeaturesToChange = src._parserFeaturesToChange;
        this._formatReadFeatures = src._formatReadFeatures;
        this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
    }
    
    private DeserializationConfig(final DeserializationConfig src, final LinkedNode<DeserializationProblemHandler> problemHandlers) {
        super(src);
        this._deserFeatures = src._deserFeatures;
        this._problemHandlers = problemHandlers;
        this._nodeFactory = src._nodeFactory;
        this._parserFeatures = src._parserFeatures;
        this._parserFeaturesToChange = src._parserFeaturesToChange;
        this._formatReadFeatures = src._formatReadFeatures;
        this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
    }
    
    private DeserializationConfig(final DeserializationConfig src, final PropertyName rootName) {
        super(src, rootName);
        this._deserFeatures = src._deserFeatures;
        this._problemHandlers = src._problemHandlers;
        this._nodeFactory = src._nodeFactory;
        this._parserFeatures = src._parserFeatures;
        this._parserFeaturesToChange = src._parserFeaturesToChange;
        this._formatReadFeatures = src._formatReadFeatures;
        this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
    }
    
    private DeserializationConfig(final DeserializationConfig src, final Class<?> view) {
        super(src, view);
        this._deserFeatures = src._deserFeatures;
        this._problemHandlers = src._problemHandlers;
        this._nodeFactory = src._nodeFactory;
        this._parserFeatures = src._parserFeatures;
        this._parserFeaturesToChange = src._parserFeaturesToChange;
        this._formatReadFeatures = src._formatReadFeatures;
        this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
    }
    
    protected DeserializationConfig(final DeserializationConfig src, final ContextAttributes attrs) {
        super(src, attrs);
        this._deserFeatures = src._deserFeatures;
        this._problemHandlers = src._problemHandlers;
        this._nodeFactory = src._nodeFactory;
        this._parserFeatures = src._parserFeatures;
        this._parserFeaturesToChange = src._parserFeaturesToChange;
        this._formatReadFeatures = src._formatReadFeatures;
        this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
    }
    
    protected DeserializationConfig(final DeserializationConfig src, final SimpleMixInResolver mixins) {
        super(src, mixins);
        this._deserFeatures = src._deserFeatures;
        this._problemHandlers = src._problemHandlers;
        this._nodeFactory = src._nodeFactory;
        this._parserFeatures = src._parserFeatures;
        this._parserFeaturesToChange = src._parserFeaturesToChange;
        this._formatReadFeatures = src._formatReadFeatures;
        this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
    }
    
    protected BaseSettings getBaseSettings() {
        return this._base;
    }
    
    @Override
    protected final DeserializationConfig _withBase(final BaseSettings newBase) {
        return (this._base == newBase) ? this : new DeserializationConfig(this, newBase);
    }
    
    @Override
    protected final DeserializationConfig _withMapperFeatures(final int mapperFeatures) {
        return new DeserializationConfig(this, mapperFeatures, this._deserFeatures, this._parserFeatures, this._parserFeaturesToChange, this._formatReadFeatures, this._formatReadFeaturesToChange);
    }
    
    @Override
    public DeserializationConfig with(final SubtypeResolver str) {
        return (this._subtypeResolver == str) ? this : new DeserializationConfig(this, str);
    }
    
    @Override
    public DeserializationConfig withRootName(final PropertyName rootName) {
        if (rootName == null) {
            if (this._rootName == null) {
                return this;
            }
        }
        else if (rootName.equals(this._rootName)) {
            return this;
        }
        return new DeserializationConfig(this, rootName);
    }
    
    @Override
    public DeserializationConfig withView(final Class<?> view) {
        return (this._view == view) ? this : new DeserializationConfig(this, view);
    }
    
    @Override
    public DeserializationConfig with(final ContextAttributes attrs) {
        return (attrs == this._attributes) ? this : new DeserializationConfig(this, attrs);
    }
    
    public DeserializationConfig with(final DeserializationFeature feature) {
        final int newDeserFeatures = this._deserFeatures | feature.getMask();
        return (newDeserFeatures == this._deserFeatures) ? this : new DeserializationConfig(this, this._mapperFeatures, newDeserFeatures, this._parserFeatures, this._parserFeaturesToChange, this._formatReadFeatures, this._formatReadFeaturesToChange);
    }
    
    public DeserializationConfig with(final DeserializationFeature first, final DeserializationFeature... features) {
        int newDeserFeatures = this._deserFeatures | first.getMask();
        for (final DeserializationFeature f : features) {
            newDeserFeatures |= f.getMask();
        }
        return (newDeserFeatures == this._deserFeatures) ? this : new DeserializationConfig(this, this._mapperFeatures, newDeserFeatures, this._parserFeatures, this._parserFeaturesToChange, this._formatReadFeatures, this._formatReadFeaturesToChange);
    }
    
    public DeserializationConfig withFeatures(final DeserializationFeature... features) {
        int newDeserFeatures = this._deserFeatures;
        for (final DeserializationFeature f : features) {
            newDeserFeatures |= f.getMask();
        }
        return (newDeserFeatures == this._deserFeatures) ? this : new DeserializationConfig(this, this._mapperFeatures, newDeserFeatures, this._parserFeatures, this._parserFeaturesToChange, this._formatReadFeatures, this._formatReadFeaturesToChange);
    }
    
    public DeserializationConfig without(final DeserializationFeature feature) {
        final int newDeserFeatures = this._deserFeatures & ~feature.getMask();
        return (newDeserFeatures == this._deserFeatures) ? this : new DeserializationConfig(this, this._mapperFeatures, newDeserFeatures, this._parserFeatures, this._parserFeaturesToChange, this._formatReadFeatures, this._formatReadFeaturesToChange);
    }
    
    public DeserializationConfig without(final DeserializationFeature first, final DeserializationFeature... features) {
        int newDeserFeatures = this._deserFeatures & ~first.getMask();
        for (final DeserializationFeature f : features) {
            newDeserFeatures &= ~f.getMask();
        }
        return (newDeserFeatures == this._deserFeatures) ? this : new DeserializationConfig(this, this._mapperFeatures, newDeserFeatures, this._parserFeatures, this._parserFeaturesToChange, this._formatReadFeatures, this._formatReadFeaturesToChange);
    }
    
    public DeserializationConfig withoutFeatures(final DeserializationFeature... features) {
        int newDeserFeatures = this._deserFeatures;
        for (final DeserializationFeature f : features) {
            newDeserFeatures &= ~f.getMask();
        }
        return (newDeserFeatures == this._deserFeatures) ? this : new DeserializationConfig(this, this._mapperFeatures, newDeserFeatures, this._parserFeatures, this._parserFeaturesToChange, this._formatReadFeatures, this._formatReadFeaturesToChange);
    }
    
    public DeserializationConfig with(final JsonParser.Feature feature) {
        final int newSet = this._parserFeatures | feature.getMask();
        final int newMask = this._parserFeaturesToChange | feature.getMask();
        return (this._parserFeatures == newSet && this._parserFeaturesToChange == newMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, newSet, newMask, this._formatReadFeatures, this._formatReadFeaturesToChange);
    }
    
    public DeserializationConfig withFeatures(final JsonParser.Feature... features) {
        int newSet = this._parserFeatures;
        int newMask = this._parserFeaturesToChange;
        for (final JsonParser.Feature f : features) {
            final int mask = f.getMask();
            newSet |= mask;
            newMask |= mask;
        }
        return (this._parserFeatures == newSet && this._parserFeaturesToChange == newMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, newSet, newMask, this._formatReadFeatures, this._formatReadFeaturesToChange);
    }
    
    public DeserializationConfig without(final JsonParser.Feature feature) {
        final int newSet = this._parserFeatures & ~feature.getMask();
        final int newMask = this._parserFeaturesToChange | feature.getMask();
        return (this._parserFeatures == newSet && this._parserFeaturesToChange == newMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, newSet, newMask, this._formatReadFeatures, this._formatReadFeaturesToChange);
    }
    
    public DeserializationConfig withoutFeatures(final JsonParser.Feature... features) {
        int newSet = this._parserFeatures;
        int newMask = this._parserFeaturesToChange;
        for (final JsonParser.Feature f : features) {
            final int mask = f.getMask();
            newSet &= ~mask;
            newMask |= mask;
        }
        return (this._parserFeatures == newSet && this._parserFeaturesToChange == newMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, newSet, newMask, this._formatReadFeatures, this._formatReadFeaturesToChange);
    }
    
    public DeserializationConfig with(final FormatFeature feature) {
        final int newSet = this._formatReadFeatures | feature.getMask();
        final int newMask = this._formatReadFeaturesToChange | feature.getMask();
        return (this._formatReadFeatures == newSet && this._formatReadFeaturesToChange == newMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, this._parserFeatures, this._parserFeaturesToChange, newSet, newMask);
    }
    
    public DeserializationConfig withFeatures(final FormatFeature... features) {
        int newSet = this._formatReadFeatures;
        int newMask = this._formatReadFeaturesToChange;
        for (final FormatFeature f : features) {
            final int mask = f.getMask();
            newSet |= mask;
            newMask |= mask;
        }
        return (this._formatReadFeatures == newSet && this._formatReadFeaturesToChange == newMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, this._parserFeatures, this._parserFeaturesToChange, newSet, newMask);
    }
    
    public DeserializationConfig without(final FormatFeature feature) {
        final int newSet = this._formatReadFeatures & ~feature.getMask();
        final int newMask = this._formatReadFeaturesToChange | feature.getMask();
        return (this._formatReadFeatures == newSet && this._formatReadFeaturesToChange == newMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, this._parserFeatures, this._parserFeaturesToChange, newSet, newMask);
    }
    
    public DeserializationConfig withoutFeatures(final FormatFeature... features) {
        int newSet = this._formatReadFeatures;
        int newMask = this._formatReadFeaturesToChange;
        for (final FormatFeature f : features) {
            final int mask = f.getMask();
            newSet &= ~mask;
            newMask |= mask;
        }
        return (this._formatReadFeatures == newSet && this._formatReadFeaturesToChange == newMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, this._parserFeatures, this._parserFeaturesToChange, newSet, newMask);
    }
    
    public DeserializationConfig with(final JsonNodeFactory f) {
        if (this._nodeFactory == f) {
            return this;
        }
        return new DeserializationConfig(this, f);
    }
    
    public DeserializationConfig withHandler(final DeserializationProblemHandler h) {
        if (LinkedNode.contains(this._problemHandlers, h)) {
            return this;
        }
        return new DeserializationConfig(this, new LinkedNode<DeserializationProblemHandler>(h, this._problemHandlers));
    }
    
    public DeserializationConfig withNoProblemHandlers() {
        if (this._problemHandlers == null) {
            return this;
        }
        return new DeserializationConfig(this, (LinkedNode<DeserializationProblemHandler>)null);
    }
    
    public void initialize(final JsonParser p) {
        if (this._parserFeaturesToChange != 0) {
            p.overrideStdFeatures(this._parserFeatures, this._parserFeaturesToChange);
        }
        if (this._formatReadFeaturesToChange != 0) {
            p.overrideFormatFeatures(this._formatReadFeatures, this._formatReadFeaturesToChange);
        }
    }
    
    @Override
    public boolean useRootWrapping() {
        if (this._rootName != null) {
            return !this._rootName.isEmpty();
        }
        return this.isEnabled(DeserializationFeature.UNWRAP_ROOT_VALUE);
    }
    
    public final boolean isEnabled(final DeserializationFeature f) {
        return (this._deserFeatures & f.getMask()) != 0x0;
    }
    
    public final boolean isEnabled(final JsonParser.Feature f, final JsonFactory factory) {
        final int mask = f.getMask();
        if ((this._parserFeaturesToChange & mask) != 0x0) {
            return (this._parserFeatures & f.getMask()) != 0x0;
        }
        return factory.isEnabled(f);
    }
    
    public final boolean hasDeserializationFeatures(final int featureMask) {
        return (this._deserFeatures & featureMask) == featureMask;
    }
    
    public final boolean hasSomeOfFeatures(final int featureMask) {
        return (this._deserFeatures & featureMask) != 0x0;
    }
    
    public final int getDeserializationFeatures() {
        return this._deserFeatures;
    }
    
    public final boolean requiresFullValue() {
        return DeserializationFeature.FAIL_ON_TRAILING_TOKENS.enabledIn(this._deserFeatures);
    }
    
    public LinkedNode<DeserializationProblemHandler> getProblemHandlers() {
        return this._problemHandlers;
    }
    
    public final JsonNodeFactory getNodeFactory() {
        return this._nodeFactory;
    }
    
    public <T extends BeanDescription> T introspect(final JavaType type) {
        return (T)this.getClassIntrospector().forDeserialization(this, type, this);
    }
    
    public <T extends BeanDescription> T introspectForCreation(final JavaType type) {
        return (T)this.getClassIntrospector().forCreation(this, type, this);
    }
    
    public <T extends BeanDescription> T introspectForBuilder(final JavaType type) {
        return (T)this.getClassIntrospector().forDeserializationWithBuilder(this, type, this);
    }
    
    public TypeDeserializer findTypeDeserializer(final JavaType baseType) throws JsonMappingException {
        final BeanDescription bean = this.introspectClassAnnotations(baseType.getRawClass());
        final AnnotatedClass ac = bean.getClassInfo();
        TypeResolverBuilder<?> b = this.getAnnotationIntrospector().findTypeResolver(this, ac, baseType);
        Collection<NamedType> subtypes = null;
        if (b == null) {
            b = this.getDefaultTyper(baseType);
            if (b == null) {
                return null;
            }
        }
        else {
            subtypes = this.getSubtypeResolver().collectAndResolveSubtypesByTypeId(this, ac);
        }
        return b.buildTypeDeserializer(this, baseType, subtypes);
    }
}
