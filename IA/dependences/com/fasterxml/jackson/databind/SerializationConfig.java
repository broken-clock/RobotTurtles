// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.util.Instantiatable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.FormatFeature;
import com.fasterxml.jackson.core.JsonGenerator;
import java.text.DateFormat;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.cfg.ConfigOverrides;
import com.fasterxml.jackson.databind.util.RootNameLookup;
import com.fasterxml.jackson.databind.introspect.SimpleMixInResolver;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import com.fasterxml.jackson.databind.cfg.BaseSettings;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.core.PrettyPrinter;
import java.io.Serializable;
import com.fasterxml.jackson.databind.cfg.MapperConfigBase;

public final class SerializationConfig extends MapperConfigBase<SerializationFeature, SerializationConfig> implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected static final PrettyPrinter DEFAULT_PRETTY_PRINTER;
    protected final FilterProvider _filterProvider;
    protected final PrettyPrinter _defaultPrettyPrinter;
    protected final int _serFeatures;
    protected final int _generatorFeatures;
    protected final int _generatorFeaturesToChange;
    protected final int _formatWriteFeatures;
    protected final int _formatWriteFeaturesToChange;
    
    public SerializationConfig(final BaseSettings base, final SubtypeResolver str, final SimpleMixInResolver mixins, final RootNameLookup rootNames, final ConfigOverrides configOverrides) {
        super(base, str, mixins, rootNames, configOverrides);
        this._serFeatures = MapperConfig.collectFeatureDefaults(SerializationFeature.class);
        this._filterProvider = null;
        this._defaultPrettyPrinter = SerializationConfig.DEFAULT_PRETTY_PRINTER;
        this._generatorFeatures = 0;
        this._generatorFeaturesToChange = 0;
        this._formatWriteFeatures = 0;
        this._formatWriteFeaturesToChange = 0;
    }
    
    protected SerializationConfig(final SerializationConfig src, final SimpleMixInResolver mixins, final RootNameLookup rootNames, final ConfigOverrides configOverrides) {
        super(src, mixins, rootNames, configOverrides);
        this._serFeatures = src._serFeatures;
        this._filterProvider = src._filterProvider;
        this._defaultPrettyPrinter = src._defaultPrettyPrinter;
        this._generatorFeatures = src._generatorFeatures;
        this._generatorFeaturesToChange = src._generatorFeaturesToChange;
        this._formatWriteFeatures = src._formatWriteFeatures;
        this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
    }
    
    private SerializationConfig(final SerializationConfig src, final SubtypeResolver str) {
        super(src, str);
        this._serFeatures = src._serFeatures;
        this._filterProvider = src._filterProvider;
        this._defaultPrettyPrinter = src._defaultPrettyPrinter;
        this._generatorFeatures = src._generatorFeatures;
        this._generatorFeaturesToChange = src._generatorFeaturesToChange;
        this._formatWriteFeatures = src._formatWriteFeatures;
        this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
    }
    
    private SerializationConfig(final SerializationConfig src, final int mapperFeatures, final int serFeatures, final int generatorFeatures, final int generatorFeatureMask, final int formatFeatures, final int formatFeaturesMask) {
        super(src, mapperFeatures);
        this._serFeatures = serFeatures;
        this._filterProvider = src._filterProvider;
        this._defaultPrettyPrinter = src._defaultPrettyPrinter;
        this._generatorFeatures = generatorFeatures;
        this._generatorFeaturesToChange = generatorFeatureMask;
        this._formatWriteFeatures = formatFeatures;
        this._formatWriteFeaturesToChange = formatFeaturesMask;
    }
    
    private SerializationConfig(final SerializationConfig src, final BaseSettings base) {
        super(src, base);
        this._serFeatures = src._serFeatures;
        this._filterProvider = src._filterProvider;
        this._defaultPrettyPrinter = src._defaultPrettyPrinter;
        this._generatorFeatures = src._generatorFeatures;
        this._generatorFeaturesToChange = src._generatorFeaturesToChange;
        this._formatWriteFeatures = src._formatWriteFeatures;
        this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
    }
    
    private SerializationConfig(final SerializationConfig src, final FilterProvider filters) {
        super(src);
        this._serFeatures = src._serFeatures;
        this._filterProvider = filters;
        this._defaultPrettyPrinter = src._defaultPrettyPrinter;
        this._generatorFeatures = src._generatorFeatures;
        this._generatorFeaturesToChange = src._generatorFeaturesToChange;
        this._formatWriteFeatures = src._formatWriteFeatures;
        this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
    }
    
    private SerializationConfig(final SerializationConfig src, final Class<?> view) {
        super(src, view);
        this._serFeatures = src._serFeatures;
        this._filterProvider = src._filterProvider;
        this._defaultPrettyPrinter = src._defaultPrettyPrinter;
        this._generatorFeatures = src._generatorFeatures;
        this._generatorFeaturesToChange = src._generatorFeaturesToChange;
        this._formatWriteFeatures = src._formatWriteFeatures;
        this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
    }
    
    private SerializationConfig(final SerializationConfig src, final PropertyName rootName) {
        super(src, rootName);
        this._serFeatures = src._serFeatures;
        this._filterProvider = src._filterProvider;
        this._defaultPrettyPrinter = src._defaultPrettyPrinter;
        this._generatorFeatures = src._generatorFeatures;
        this._generatorFeaturesToChange = src._generatorFeaturesToChange;
        this._formatWriteFeatures = src._formatWriteFeatures;
        this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
    }
    
    protected SerializationConfig(final SerializationConfig src, final ContextAttributes attrs) {
        super(src, attrs);
        this._serFeatures = src._serFeatures;
        this._filterProvider = src._filterProvider;
        this._defaultPrettyPrinter = src._defaultPrettyPrinter;
        this._generatorFeatures = src._generatorFeatures;
        this._generatorFeaturesToChange = src._generatorFeaturesToChange;
        this._formatWriteFeatures = src._formatWriteFeatures;
        this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
    }
    
    protected SerializationConfig(final SerializationConfig src, final SimpleMixInResolver mixins) {
        super(src, mixins);
        this._serFeatures = src._serFeatures;
        this._filterProvider = src._filterProvider;
        this._defaultPrettyPrinter = src._defaultPrettyPrinter;
        this._generatorFeatures = src._generatorFeatures;
        this._generatorFeaturesToChange = src._generatorFeaturesToChange;
        this._formatWriteFeatures = src._formatWriteFeatures;
        this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
    }
    
    protected SerializationConfig(final SerializationConfig src, final PrettyPrinter defaultPP) {
        super(src);
        this._serFeatures = src._serFeatures;
        this._filterProvider = src._filterProvider;
        this._defaultPrettyPrinter = defaultPP;
        this._generatorFeatures = src._generatorFeatures;
        this._generatorFeaturesToChange = src._generatorFeaturesToChange;
        this._formatWriteFeatures = src._formatWriteFeatures;
        this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
    }
    
    @Override
    protected final SerializationConfig _withBase(final BaseSettings newBase) {
        return (this._base == newBase) ? this : new SerializationConfig(this, newBase);
    }
    
    @Override
    protected final SerializationConfig _withMapperFeatures(final int mapperFeatures) {
        return new SerializationConfig(this, mapperFeatures, this._serFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
    }
    
    @Override
    public SerializationConfig withRootName(final PropertyName rootName) {
        if (rootName == null) {
            if (this._rootName == null) {
                return this;
            }
        }
        else if (rootName.equals(this._rootName)) {
            return this;
        }
        return new SerializationConfig(this, rootName);
    }
    
    @Override
    public SerializationConfig with(final SubtypeResolver str) {
        return (str == this._subtypeResolver) ? this : new SerializationConfig(this, str);
    }
    
    @Override
    public SerializationConfig withView(final Class<?> view) {
        return (this._view == view) ? this : new SerializationConfig(this, view);
    }
    
    @Override
    public SerializationConfig with(final ContextAttributes attrs) {
        return (attrs == this._attributes) ? this : new SerializationConfig(this, attrs);
    }
    
    @Override
    public SerializationConfig with(final DateFormat df) {
        final SerializationConfig cfg = super.with(df);
        if (df == null) {
            return cfg.with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        }
        return cfg.without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    
    public SerializationConfig with(final SerializationFeature feature) {
        final int newSerFeatures = this._serFeatures | feature.getMask();
        return (newSerFeatures == this._serFeatures) ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
    }
    
    public SerializationConfig with(final SerializationFeature first, final SerializationFeature... features) {
        int newSerFeatures = this._serFeatures | first.getMask();
        for (final SerializationFeature f : features) {
            newSerFeatures |= f.getMask();
        }
        return (newSerFeatures == this._serFeatures) ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
    }
    
    public SerializationConfig withFeatures(final SerializationFeature... features) {
        int newSerFeatures = this._serFeatures;
        for (final SerializationFeature f : features) {
            newSerFeatures |= f.getMask();
        }
        return (newSerFeatures == this._serFeatures) ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
    }
    
    public SerializationConfig without(final SerializationFeature feature) {
        final int newSerFeatures = this._serFeatures & ~feature.getMask();
        return (newSerFeatures == this._serFeatures) ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
    }
    
    public SerializationConfig without(final SerializationFeature first, final SerializationFeature... features) {
        int newSerFeatures = this._serFeatures & ~first.getMask();
        for (final SerializationFeature f : features) {
            newSerFeatures &= ~f.getMask();
        }
        return (newSerFeatures == this._serFeatures) ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
    }
    
    public SerializationConfig withoutFeatures(final SerializationFeature... features) {
        int newSerFeatures = this._serFeatures;
        for (final SerializationFeature f : features) {
            newSerFeatures &= ~f.getMask();
        }
        return (newSerFeatures == this._serFeatures) ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
    }
    
    public SerializationConfig with(final JsonGenerator.Feature feature) {
        final int newSet = this._generatorFeatures | feature.getMask();
        final int newMask = this._generatorFeaturesToChange | feature.getMask();
        return (this._generatorFeatures == newSet && this._generatorFeaturesToChange == newMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, newSet, newMask, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
    }
    
    public SerializationConfig withFeatures(final JsonGenerator.Feature... features) {
        int newSet = this._generatorFeatures;
        int newMask = this._generatorFeaturesToChange;
        for (final JsonGenerator.Feature f : features) {
            final int mask = f.getMask();
            newSet |= mask;
            newMask |= mask;
        }
        return (this._generatorFeatures == newSet && this._generatorFeaturesToChange == newMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, newSet, newMask, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
    }
    
    public SerializationConfig without(final JsonGenerator.Feature feature) {
        final int newSet = this._generatorFeatures & ~feature.getMask();
        final int newMask = this._generatorFeaturesToChange | feature.getMask();
        return (this._generatorFeatures == newSet && this._generatorFeaturesToChange == newMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, newSet, newMask, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
    }
    
    public SerializationConfig withoutFeatures(final JsonGenerator.Feature... features) {
        int newSet = this._generatorFeatures;
        int newMask = this._generatorFeaturesToChange;
        for (final JsonGenerator.Feature f : features) {
            final int mask = f.getMask();
            newSet &= ~mask;
            newMask |= mask;
        }
        return (this._generatorFeatures == newSet && this._generatorFeaturesToChange == newMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, newSet, newMask, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
    }
    
    public SerializationConfig with(final FormatFeature feature) {
        final int newSet = this._formatWriteFeatures | feature.getMask();
        final int newMask = this._formatWriteFeaturesToChange | feature.getMask();
        return (this._formatWriteFeatures == newSet && this._formatWriteFeaturesToChange == newMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, this._generatorFeatures, this._generatorFeaturesToChange, newSet, newMask);
    }
    
    public SerializationConfig withFeatures(final FormatFeature... features) {
        int newSet = this._formatWriteFeatures;
        int newMask = this._formatWriteFeaturesToChange;
        for (final FormatFeature f : features) {
            final int mask = f.getMask();
            newSet |= mask;
            newMask |= mask;
        }
        return (this._formatWriteFeatures == newSet && this._formatWriteFeaturesToChange == newMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, this._generatorFeatures, this._generatorFeaturesToChange, newSet, newMask);
    }
    
    public SerializationConfig without(final FormatFeature feature) {
        final int newSet = this._formatWriteFeatures & ~feature.getMask();
        final int newMask = this._formatWriteFeaturesToChange | feature.getMask();
        return (this._formatWriteFeatures == newSet && this._formatWriteFeaturesToChange == newMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, this._generatorFeatures, this._generatorFeaturesToChange, newSet, newMask);
    }
    
    public SerializationConfig withoutFeatures(final FormatFeature... features) {
        int newSet = this._formatWriteFeatures;
        int newMask = this._formatWriteFeaturesToChange;
        for (final FormatFeature f : features) {
            final int mask = f.getMask();
            newSet &= ~mask;
            newMask |= mask;
        }
        return (this._formatWriteFeatures == newSet && this._formatWriteFeaturesToChange == newMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, this._generatorFeatures, this._generatorFeaturesToChange, newSet, newMask);
    }
    
    public SerializationConfig withFilters(final FilterProvider filterProvider) {
        return (filterProvider == this._filterProvider) ? this : new SerializationConfig(this, filterProvider);
    }
    
    @Deprecated
    public SerializationConfig withPropertyInclusion(final JsonInclude.Value incl) {
        this._configOverrides.setDefaultInclusion(incl);
        return this;
    }
    
    public SerializationConfig withDefaultPrettyPrinter(final PrettyPrinter pp) {
        return (this._defaultPrettyPrinter == pp) ? this : new SerializationConfig(this, pp);
    }
    
    public PrettyPrinter constructDefaultPrettyPrinter() {
        PrettyPrinter pp = this._defaultPrettyPrinter;
        if (pp instanceof Instantiatable) {
            pp = ((Instantiatable)pp).createInstance();
        }
        return pp;
    }
    
    public void initialize(final JsonGenerator g) {
        if (SerializationFeature.INDENT_OUTPUT.enabledIn(this._serFeatures) && g.getPrettyPrinter() == null) {
            final PrettyPrinter pp = this.constructDefaultPrettyPrinter();
            if (pp != null) {
                g.setPrettyPrinter(pp);
            }
        }
        final boolean useBigDec = SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN.enabledIn(this._serFeatures);
        int mask = this._generatorFeaturesToChange;
        if (mask != 0 || useBigDec) {
            int newFlags = this._generatorFeatures;
            if (useBigDec) {
                final int f = JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN.getMask();
                newFlags |= f;
                mask |= f;
            }
            g.overrideStdFeatures(newFlags, mask);
        }
        if (this._formatWriteFeaturesToChange != 0) {
            g.overrideFormatFeatures(this._formatWriteFeatures, this._formatWriteFeaturesToChange);
        }
    }
    
    @Deprecated
    public JsonInclude.Include getSerializationInclusion() {
        final JsonInclude.Include incl = this.getDefaultPropertyInclusion().getValueInclusion();
        return (incl == JsonInclude.Include.USE_DEFAULTS) ? JsonInclude.Include.ALWAYS : incl;
    }
    
    @Override
    public boolean useRootWrapping() {
        if (this._rootName != null) {
            return !this._rootName.isEmpty();
        }
        return this.isEnabled(SerializationFeature.WRAP_ROOT_VALUE);
    }
    
    public final boolean isEnabled(final SerializationFeature f) {
        return (this._serFeatures & f.getMask()) != 0x0;
    }
    
    public final boolean isEnabled(final JsonGenerator.Feature f, final JsonFactory factory) {
        final int mask = f.getMask();
        if ((this._generatorFeaturesToChange & mask) != 0x0) {
            return (this._generatorFeatures & f.getMask()) != 0x0;
        }
        return factory.isEnabled(f);
    }
    
    public final boolean hasSerializationFeatures(final int featureMask) {
        return (this._serFeatures & featureMask) == featureMask;
    }
    
    public final int getSerializationFeatures() {
        return this._serFeatures;
    }
    
    public FilterProvider getFilterProvider() {
        return this._filterProvider;
    }
    
    public PrettyPrinter getDefaultPrettyPrinter() {
        return this._defaultPrettyPrinter;
    }
    
    public <T extends BeanDescription> T introspect(final JavaType type) {
        return (T)this.getClassIntrospector().forSerialization(this, type, this);
    }
    
    static {
        DEFAULT_PRETTY_PRINTER = new DefaultPrettyPrinter();
    }
}
