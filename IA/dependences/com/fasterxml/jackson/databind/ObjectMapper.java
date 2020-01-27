// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.databind.cfg.MapperConfigBase;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.core.io.SegmentedStringWriter;
import java.io.Writer;
import java.io.DataOutput;
import java.io.OutputStream;
import com.fasterxml.jackson.core.JsonEncoding;
import java.io.DataInput;
import java.util.concurrent.atomic.AtomicReference;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import com.fasterxml.jackson.databind.node.POJONode;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.core.JsonGenerationException;
import java.io.Closeable;
import java.net.URL;
import java.io.File;
import java.io.Reader;
import java.io.InputStream;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.ResolvedType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonParseException;
import java.io.IOException;
import java.util.TimeZone;
import java.util.Locale;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import java.text.DateFormat;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.lang.reflect.Type;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import java.util.Map;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ServiceLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Iterator;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.deser.ValueInstantiators;
import com.fasterxml.jackson.databind.type.TypeModifier;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.KeyDeserializers;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.cfg.MutableConfigOverride;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.cfg.PackageVersion;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.databind.introspect.BasicClassIntrospector;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.deser.DeserializerFactory;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.fasterxml.jackson.databind.jsontype.impl.StdSubtypeResolver;
import java.util.Collection;
import java.util.LinkedHashSet;
import com.fasterxml.jackson.databind.util.RootNameLookup;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.databind.introspect.SimpleMixInResolver;
import com.fasterxml.jackson.databind.cfg.ConfigOverrides;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.cfg.BaseSettings;
import java.io.Serializable;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.core.ObjectCodec;

public class ObjectMapper extends ObjectCodec implements Versioned, Serializable
{
    private static final long serialVersionUID = 2L;
    private static final JavaType JSON_NODE_TYPE;
    protected static final AnnotationIntrospector DEFAULT_ANNOTATION_INTROSPECTOR;
    protected static final BaseSettings DEFAULT_BASE;
    protected final JsonFactory _jsonFactory;
    protected TypeFactory _typeFactory;
    protected InjectableValues _injectableValues;
    protected SubtypeResolver _subtypeResolver;
    protected final ConfigOverrides _configOverrides;
    protected SimpleMixInResolver _mixIns;
    protected SerializationConfig _serializationConfig;
    protected DefaultSerializerProvider _serializerProvider;
    protected SerializerFactory _serializerFactory;
    protected DeserializationConfig _deserializationConfig;
    protected DefaultDeserializationContext _deserializationContext;
    protected Set<Object> _registeredModuleTypes;
    protected final ConcurrentHashMap<JavaType, JsonDeserializer<Object>> _rootDeserializers;
    
    public ObjectMapper() {
        this(null, null, null);
    }
    
    public ObjectMapper(final JsonFactory jf) {
        this(jf, null, null);
    }
    
    protected ObjectMapper(final ObjectMapper src) {
        this._rootDeserializers = new ConcurrentHashMap<JavaType, JsonDeserializer<Object>>(64, 0.6f, 2);
        (this._jsonFactory = src._jsonFactory.copy()).setCodec(this);
        this._subtypeResolver = src._subtypeResolver;
        this._typeFactory = src._typeFactory;
        this._injectableValues = src._injectableValues;
        this._configOverrides = src._configOverrides.copy();
        this._mixIns = src._mixIns.copy();
        final RootNameLookup rootNames = new RootNameLookup();
        this._serializationConfig = new SerializationConfig(src._serializationConfig, this._mixIns, rootNames, this._configOverrides);
        this._deserializationConfig = new DeserializationConfig(src._deserializationConfig, this._mixIns, rootNames, this._configOverrides);
        this._serializerProvider = src._serializerProvider.copy();
        this._deserializationContext = src._deserializationContext.copy();
        this._serializerFactory = src._serializerFactory;
        final Set<Object> reg = src._registeredModuleTypes;
        if (reg == null) {
            this._registeredModuleTypes = null;
        }
        else {
            this._registeredModuleTypes = new LinkedHashSet<Object>(reg);
        }
    }
    
    public ObjectMapper(final JsonFactory jf, final DefaultSerializerProvider sp, final DefaultDeserializationContext dc) {
        this._rootDeserializers = new ConcurrentHashMap<JavaType, JsonDeserializer<Object>>(64, 0.6f, 2);
        if (jf == null) {
            this._jsonFactory = new MappingJsonFactory(this);
        }
        else {
            this._jsonFactory = jf;
            if (jf.getCodec() == null) {
                this._jsonFactory.setCodec(this);
            }
        }
        this._subtypeResolver = new StdSubtypeResolver();
        final RootNameLookup rootNames = new RootNameLookup();
        this._typeFactory = TypeFactory.defaultInstance();
        final SimpleMixInResolver mixins = new SimpleMixInResolver(null);
        this._mixIns = mixins;
        final BaseSettings base = ObjectMapper.DEFAULT_BASE.withClassIntrospector(this.defaultClassIntrospector());
        this._configOverrides = new ConfigOverrides();
        this._serializationConfig = new SerializationConfig(base, this._subtypeResolver, mixins, rootNames, this._configOverrides);
        this._deserializationConfig = new DeserializationConfig(base, this._subtypeResolver, mixins, rootNames, this._configOverrides);
        final boolean needOrder = this._jsonFactory.requiresPropertyOrdering();
        if (needOrder ^ this._serializationConfig.isEnabled(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)) {
            this.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, needOrder);
        }
        this._serializerProvider = ((sp == null) ? new DefaultSerializerProvider.Impl() : sp);
        this._deserializationContext = ((dc == null) ? new DefaultDeserializationContext.Impl(BeanDeserializerFactory.instance) : dc);
        this._serializerFactory = BeanSerializerFactory.instance;
    }
    
    protected ClassIntrospector defaultClassIntrospector() {
        return new BasicClassIntrospector();
    }
    
    public ObjectMapper copy() {
        this._checkInvalidCopy(ObjectMapper.class);
        return new ObjectMapper(this);
    }
    
    protected void _checkInvalidCopy(final Class<?> exp) {
        if (this.getClass() != exp) {
            throw new IllegalStateException("Failed copy(): " + this.getClass().getName() + " (version: " + this.version() + ") does not override copy(); it has to");
        }
    }
    
    protected ObjectReader _newReader(final DeserializationConfig config) {
        return new ObjectReader(this, config);
    }
    
    protected ObjectReader _newReader(final DeserializationConfig config, final JavaType valueType, final Object valueToUpdate, final FormatSchema schema, final InjectableValues injectableValues) {
        return new ObjectReader(this, config, valueType, valueToUpdate, schema, injectableValues);
    }
    
    protected ObjectWriter _newWriter(final SerializationConfig config) {
        return new ObjectWriter(this, config);
    }
    
    protected ObjectWriter _newWriter(final SerializationConfig config, final FormatSchema schema) {
        return new ObjectWriter(this, config, schema);
    }
    
    protected ObjectWriter _newWriter(final SerializationConfig config, final JavaType rootType, final PrettyPrinter pp) {
        return new ObjectWriter(this, config, rootType, pp);
    }
    
    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }
    
    public ObjectMapper registerModule(final Module module) {
        if (this.isEnabled(MapperFeature.IGNORE_DUPLICATE_MODULE_REGISTRATIONS)) {
            final Object typeId = module.getTypeId();
            if (typeId != null) {
                if (this._registeredModuleTypes == null) {
                    this._registeredModuleTypes = new LinkedHashSet<Object>();
                }
                if (!this._registeredModuleTypes.add(typeId)) {
                    return this;
                }
            }
        }
        final String name = module.getModuleName();
        if (name == null) {
            throw new IllegalArgumentException("Module without defined name");
        }
        final Version version = module.version();
        if (version == null) {
            throw new IllegalArgumentException("Module without defined version");
        }
        module.setupModule(new Module.SetupContext() {
            @Override
            public Version getMapperVersion() {
                return ObjectMapper.this.version();
            }
            
            @Override
            public <C extends ObjectCodec> C getOwner() {
                return (C)ObjectMapper.this;
            }
            
            @Override
            public TypeFactory getTypeFactory() {
                return ObjectMapper.this._typeFactory;
            }
            
            @Override
            public boolean isEnabled(final MapperFeature f) {
                return ObjectMapper.this.isEnabled(f);
            }
            
            @Override
            public boolean isEnabled(final DeserializationFeature f) {
                return ObjectMapper.this.isEnabled(f);
            }
            
            @Override
            public boolean isEnabled(final SerializationFeature f) {
                return ObjectMapper.this.isEnabled(f);
            }
            
            @Override
            public boolean isEnabled(final JsonFactory.Feature f) {
                return ObjectMapper.this.isEnabled(f);
            }
            
            @Override
            public boolean isEnabled(final JsonParser.Feature f) {
                return ObjectMapper.this.isEnabled(f);
            }
            
            @Override
            public boolean isEnabled(final JsonGenerator.Feature f) {
                return ObjectMapper.this.isEnabled(f);
            }
            
            @Override
            public MutableConfigOverride configOverride(final Class<?> type) {
                return ObjectMapper.this.configOverride(type);
            }
            
            @Override
            public void addDeserializers(final Deserializers d) {
                final DeserializerFactory df = ObjectMapper.this._deserializationContext._factory.withAdditionalDeserializers(d);
                ObjectMapper.this._deserializationContext = ObjectMapper.this._deserializationContext.with(df);
            }
            
            @Override
            public void addKeyDeserializers(final KeyDeserializers d) {
                final DeserializerFactory df = ObjectMapper.this._deserializationContext._factory.withAdditionalKeyDeserializers(d);
                ObjectMapper.this._deserializationContext = ObjectMapper.this._deserializationContext.with(df);
            }
            
            @Override
            public void addBeanDeserializerModifier(final BeanDeserializerModifier modifier) {
                final DeserializerFactory df = ObjectMapper.this._deserializationContext._factory.withDeserializerModifier(modifier);
                ObjectMapper.this._deserializationContext = ObjectMapper.this._deserializationContext.with(df);
            }
            
            @Override
            public void addSerializers(final Serializers s) {
                ObjectMapper.this._serializerFactory = ObjectMapper.this._serializerFactory.withAdditionalSerializers(s);
            }
            
            @Override
            public void addKeySerializers(final Serializers s) {
                ObjectMapper.this._serializerFactory = ObjectMapper.this._serializerFactory.withAdditionalKeySerializers(s);
            }
            
            @Override
            public void addBeanSerializerModifier(final BeanSerializerModifier modifier) {
                ObjectMapper.this._serializerFactory = ObjectMapper.this._serializerFactory.withSerializerModifier(modifier);
            }
            
            @Override
            public void addAbstractTypeResolver(final AbstractTypeResolver resolver) {
                final DeserializerFactory df = ObjectMapper.this._deserializationContext._factory.withAbstractTypeResolver(resolver);
                ObjectMapper.this._deserializationContext = ObjectMapper.this._deserializationContext.with(df);
            }
            
            @Override
            public void addTypeModifier(final TypeModifier modifier) {
                TypeFactory f = ObjectMapper.this._typeFactory;
                f = f.withModifier(modifier);
                ObjectMapper.this.setTypeFactory(f);
            }
            
            @Override
            public void addValueInstantiators(final ValueInstantiators instantiators) {
                final DeserializerFactory df = ObjectMapper.this._deserializationContext._factory.withValueInstantiators(instantiators);
                ObjectMapper.this._deserializationContext = ObjectMapper.this._deserializationContext.with(df);
            }
            
            @Override
            public void setClassIntrospector(final ClassIntrospector ci) {
                ObjectMapper.this._deserializationConfig = ((MapperConfigBase<CFG, DeserializationConfig>)ObjectMapper.this._deserializationConfig).with(ci);
                ObjectMapper.this._serializationConfig = ((MapperConfigBase<CFG, SerializationConfig>)ObjectMapper.this._serializationConfig).with(ci);
            }
            
            @Override
            public void insertAnnotationIntrospector(final AnnotationIntrospector ai) {
                ObjectMapper.this._deserializationConfig = ((MapperConfigBase<CFG, DeserializationConfig>)ObjectMapper.this._deserializationConfig).withInsertedAnnotationIntrospector(ai);
                ObjectMapper.this._serializationConfig = ((MapperConfigBase<CFG, SerializationConfig>)ObjectMapper.this._serializationConfig).withInsertedAnnotationIntrospector(ai);
            }
            
            @Override
            public void appendAnnotationIntrospector(final AnnotationIntrospector ai) {
                ObjectMapper.this._deserializationConfig = ((MapperConfigBase<CFG, DeserializationConfig>)ObjectMapper.this._deserializationConfig).withAppendedAnnotationIntrospector(ai);
                ObjectMapper.this._serializationConfig = ((MapperConfigBase<CFG, SerializationConfig>)ObjectMapper.this._serializationConfig).withAppendedAnnotationIntrospector(ai);
            }
            
            @Override
            public void registerSubtypes(final Class<?>... subtypes) {
                ObjectMapper.this.registerSubtypes(subtypes);
            }
            
            @Override
            public void registerSubtypes(final NamedType... subtypes) {
                ObjectMapper.this.registerSubtypes(subtypes);
            }
            
            @Override
            public void registerSubtypes(final Collection<Class<?>> subtypes) {
                ObjectMapper.this.registerSubtypes(subtypes);
            }
            
            @Override
            public void setMixInAnnotations(final Class<?> target, final Class<?> mixinSource) {
                ObjectMapper.this.addMixIn(target, mixinSource);
            }
            
            @Override
            public void addDeserializationProblemHandler(final DeserializationProblemHandler handler) {
                ObjectMapper.this.addHandler(handler);
            }
            
            @Override
            public void setNamingStrategy(final PropertyNamingStrategy naming) {
                ObjectMapper.this.setPropertyNamingStrategy(naming);
            }
        });
        return this;
    }
    
    public ObjectMapper registerModules(final Module... modules) {
        for (final Module module : modules) {
            this.registerModule(module);
        }
        return this;
    }
    
    public ObjectMapper registerModules(final Iterable<? extends Module> modules) {
        for (final Module module : modules) {
            this.registerModule(module);
        }
        return this;
    }
    
    public Set<Object> getRegisteredModuleIds() {
        return Collections.unmodifiableSet((Set<?>)this._registeredModuleTypes);
    }
    
    public static List<Module> findModules() {
        return findModules(null);
    }
    
    public static List<Module> findModules(final ClassLoader classLoader) {
        final ArrayList<Module> modules = new ArrayList<Module>();
        final ServiceLoader<Module> loader = secureGetServiceLoader(Module.class, classLoader);
        for (final Module module : loader) {
            modules.add(module);
        }
        return modules;
    }
    
    private static <T> ServiceLoader<T> secureGetServiceLoader(final Class<T> clazz, final ClassLoader classLoader) {
        final SecurityManager sm = System.getSecurityManager();
        if (sm == null) {
            return (classLoader == null) ? ServiceLoader.load(clazz) : ServiceLoader.load(clazz, classLoader);
        }
        return AccessController.doPrivileged((PrivilegedAction<ServiceLoader<T>>)new PrivilegedAction<ServiceLoader<T>>() {
            @Override
            public ServiceLoader<T> run() {
                return (classLoader == null) ? ServiceLoader.load(clazz) : ServiceLoader.load(clazz, classLoader);
            }
        });
    }
    
    public ObjectMapper findAndRegisterModules() {
        return this.registerModules(findModules());
    }
    
    public SerializationConfig getSerializationConfig() {
        return this._serializationConfig;
    }
    
    public DeserializationConfig getDeserializationConfig() {
        return this._deserializationConfig;
    }
    
    public DeserializationContext getDeserializationContext() {
        return this._deserializationContext;
    }
    
    public ObjectMapper setSerializerFactory(final SerializerFactory f) {
        this._serializerFactory = f;
        return this;
    }
    
    public SerializerFactory getSerializerFactory() {
        return this._serializerFactory;
    }
    
    public ObjectMapper setSerializerProvider(final DefaultSerializerProvider p) {
        this._serializerProvider = p;
        return this;
    }
    
    public SerializerProvider getSerializerProvider() {
        return this._serializerProvider;
    }
    
    public SerializerProvider getSerializerProviderInstance() {
        return this._serializerProvider(this._serializationConfig);
    }
    
    public ObjectMapper setMixIns(final Map<Class<?>, Class<?>> sourceMixins) {
        this._mixIns.setLocalDefinitions(sourceMixins);
        return this;
    }
    
    public ObjectMapper addMixIn(final Class<?> target, final Class<?> mixinSource) {
        this._mixIns.addLocalDefinition(target, mixinSource);
        return this;
    }
    
    public ObjectMapper setMixInResolver(final ClassIntrospector.MixInResolver resolver) {
        final SimpleMixInResolver r = this._mixIns.withOverrides(resolver);
        if (r != this._mixIns) {
            this._mixIns = r;
            this._deserializationConfig = new DeserializationConfig(this._deserializationConfig, r);
            this._serializationConfig = new SerializationConfig(this._serializationConfig, r);
        }
        return this;
    }
    
    public Class<?> findMixInClassFor(final Class<?> cls) {
        return this._mixIns.findMixInClassFor(cls);
    }
    
    public int mixInCount() {
        return this._mixIns.localSize();
    }
    
    @Deprecated
    public void setMixInAnnotations(final Map<Class<?>, Class<?>> sourceMixins) {
        this.setMixIns(sourceMixins);
    }
    
    @Deprecated
    public final void addMixInAnnotations(final Class<?> target, final Class<?> mixinSource) {
        this.addMixIn(target, mixinSource);
    }
    
    public VisibilityChecker<?> getVisibilityChecker() {
        return this._serializationConfig.getDefaultVisibilityChecker();
    }
    
    public ObjectMapper setVisibility(final VisibilityChecker<?> vc) {
        this._configOverrides.setDefaultVisibility(vc);
        return this;
    }
    
    public ObjectMapper setVisibility(final PropertyAccessor forMethod, final JsonAutoDetect.Visibility visibility) {
        VisibilityChecker<?> vc = this._configOverrides.getDefaultVisibility();
        vc = (VisibilityChecker<?>)vc.withVisibility(forMethod, visibility);
        this._configOverrides.setDefaultVisibility(vc);
        return this;
    }
    
    public SubtypeResolver getSubtypeResolver() {
        return this._subtypeResolver;
    }
    
    public ObjectMapper setSubtypeResolver(final SubtypeResolver str) {
        this._subtypeResolver = str;
        this._deserializationConfig = this._deserializationConfig.with(str);
        this._serializationConfig = this._serializationConfig.with(str);
        return this;
    }
    
    public ObjectMapper setAnnotationIntrospector(final AnnotationIntrospector ai) {
        this._serializationConfig = ((MapperConfigBase<CFG, SerializationConfig>)this._serializationConfig).with(ai);
        this._deserializationConfig = ((MapperConfigBase<CFG, DeserializationConfig>)this._deserializationConfig).with(ai);
        return this;
    }
    
    public ObjectMapper setAnnotationIntrospectors(final AnnotationIntrospector serializerAI, final AnnotationIntrospector deserializerAI) {
        this._serializationConfig = ((MapperConfigBase<CFG, SerializationConfig>)this._serializationConfig).with(serializerAI);
        this._deserializationConfig = ((MapperConfigBase<CFG, DeserializationConfig>)this._deserializationConfig).with(deserializerAI);
        return this;
    }
    
    public ObjectMapper setPropertyNamingStrategy(final PropertyNamingStrategy s) {
        this._serializationConfig = ((MapperConfigBase<CFG, SerializationConfig>)this._serializationConfig).with(s);
        this._deserializationConfig = ((MapperConfigBase<CFG, DeserializationConfig>)this._deserializationConfig).with(s);
        return this;
    }
    
    public PropertyNamingStrategy getPropertyNamingStrategy() {
        return this._serializationConfig.getPropertyNamingStrategy();
    }
    
    public ObjectMapper setDefaultPrettyPrinter(final PrettyPrinter pp) {
        this._serializationConfig = this._serializationConfig.withDefaultPrettyPrinter(pp);
        return this;
    }
    
    @Deprecated
    public void setVisibilityChecker(final VisibilityChecker<?> vc) {
        this.setVisibility(vc);
    }
    
    public ObjectMapper setSerializationInclusion(final JsonInclude.Include incl) {
        this.setPropertyInclusion(JsonInclude.Value.construct(incl, incl));
        return this;
    }
    
    @Deprecated
    public ObjectMapper setPropertyInclusion(final JsonInclude.Value incl) {
        return this.setDefaultPropertyInclusion(incl);
    }
    
    public ObjectMapper setDefaultPropertyInclusion(final JsonInclude.Value incl) {
        this._configOverrides.setDefaultInclusion(incl);
        return this;
    }
    
    public ObjectMapper setDefaultPropertyInclusion(final JsonInclude.Include incl) {
        this._configOverrides.setDefaultInclusion(JsonInclude.Value.construct(incl, incl));
        return this;
    }
    
    public ObjectMapper setDefaultSetterInfo(final JsonSetter.Value v) {
        this._configOverrides.setDefaultSetterInfo(v);
        return this;
    }
    
    public ObjectMapper setDefaultVisibility(final JsonAutoDetect.Value vis) {
        this._configOverrides.setDefaultVisibility(VisibilityChecker.Std.construct(vis));
        return this;
    }
    
    public ObjectMapper setDefaultMergeable(final Boolean b) {
        this._configOverrides.setDefaultMergeable(b);
        return this;
    }
    
    public ObjectMapper enableDefaultTyping() {
        return this.enableDefaultTyping(DefaultTyping.OBJECT_AND_NON_CONCRETE);
    }
    
    public ObjectMapper enableDefaultTyping(final DefaultTyping dti) {
        return this.enableDefaultTyping(dti, JsonTypeInfo.As.WRAPPER_ARRAY);
    }
    
    public ObjectMapper enableDefaultTyping(final DefaultTyping applicability, final JsonTypeInfo.As includeAs) {
        if (includeAs == JsonTypeInfo.As.EXTERNAL_PROPERTY) {
            throw new IllegalArgumentException("Cannot use includeAs of " + includeAs);
        }
        TypeResolverBuilder<?> typer = new DefaultTypeResolverBuilder(applicability);
        typer = (TypeResolverBuilder<?>)typer.init(JsonTypeInfo.Id.CLASS, null);
        typer = (TypeResolverBuilder<?>)typer.inclusion(includeAs);
        return this.setDefaultTyping(typer);
    }
    
    public ObjectMapper enableDefaultTypingAsProperty(final DefaultTyping applicability, final String propertyName) {
        TypeResolverBuilder<?> typer = new DefaultTypeResolverBuilder(applicability);
        typer = (TypeResolverBuilder<?>)typer.init(JsonTypeInfo.Id.CLASS, null);
        typer = (TypeResolverBuilder<?>)typer.inclusion(JsonTypeInfo.As.PROPERTY);
        typer = (TypeResolverBuilder<?>)typer.typeProperty(propertyName);
        return this.setDefaultTyping(typer);
    }
    
    public ObjectMapper disableDefaultTyping() {
        return this.setDefaultTyping(null);
    }
    
    public ObjectMapper setDefaultTyping(final TypeResolverBuilder<?> typer) {
        this._deserializationConfig = ((MapperConfigBase<CFG, DeserializationConfig>)this._deserializationConfig).with(typer);
        this._serializationConfig = ((MapperConfigBase<CFG, SerializationConfig>)this._serializationConfig).with(typer);
        return this;
    }
    
    public void registerSubtypes(final Class<?>... classes) {
        this.getSubtypeResolver().registerSubtypes(classes);
    }
    
    public void registerSubtypes(final NamedType... types) {
        this.getSubtypeResolver().registerSubtypes(types);
    }
    
    public void registerSubtypes(final Collection<Class<?>> subtypes) {
        this.getSubtypeResolver().registerSubtypes(subtypes);
    }
    
    public MutableConfigOverride configOverride(final Class<?> type) {
        return this._configOverrides.findOrCreateOverride(type);
    }
    
    public TypeFactory getTypeFactory() {
        return this._typeFactory;
    }
    
    public ObjectMapper setTypeFactory(final TypeFactory f) {
        this._typeFactory = f;
        this._deserializationConfig = ((MapperConfigBase<CFG, DeserializationConfig>)this._deserializationConfig).with(f);
        this._serializationConfig = ((MapperConfigBase<CFG, SerializationConfig>)this._serializationConfig).with(f);
        return this;
    }
    
    public JavaType constructType(final Type t) {
        return this._typeFactory.constructType(t);
    }
    
    public JsonNodeFactory getNodeFactory() {
        return this._deserializationConfig.getNodeFactory();
    }
    
    public ObjectMapper setNodeFactory(final JsonNodeFactory f) {
        this._deserializationConfig = this._deserializationConfig.with(f);
        return this;
    }
    
    public ObjectMapper addHandler(final DeserializationProblemHandler h) {
        this._deserializationConfig = this._deserializationConfig.withHandler(h);
        return this;
    }
    
    public ObjectMapper clearProblemHandlers() {
        this._deserializationConfig = this._deserializationConfig.withNoProblemHandlers();
        return this;
    }
    
    public ObjectMapper setConfig(final DeserializationConfig config) {
        this._deserializationConfig = config;
        return this;
    }
    
    @Deprecated
    public void setFilters(final FilterProvider filterProvider) {
        this._serializationConfig = this._serializationConfig.withFilters(filterProvider);
    }
    
    public ObjectMapper setFilterProvider(final FilterProvider filterProvider) {
        this._serializationConfig = this._serializationConfig.withFilters(filterProvider);
        return this;
    }
    
    public ObjectMapper setBase64Variant(final Base64Variant v) {
        this._serializationConfig = ((MapperConfigBase<CFG, SerializationConfig>)this._serializationConfig).with(v);
        this._deserializationConfig = ((MapperConfigBase<CFG, DeserializationConfig>)this._deserializationConfig).with(v);
        return this;
    }
    
    public ObjectMapper setConfig(final SerializationConfig config) {
        this._serializationConfig = config;
        return this;
    }
    
    @Override
    public JsonFactory getFactory() {
        return this._jsonFactory;
    }
    
    @Deprecated
    @Override
    public JsonFactory getJsonFactory() {
        return this.getFactory();
    }
    
    public ObjectMapper setDateFormat(final DateFormat dateFormat) {
        this._deserializationConfig = ((MapperConfigBase<CFG, DeserializationConfig>)this._deserializationConfig).with(dateFormat);
        this._serializationConfig = this._serializationConfig.with(dateFormat);
        return this;
    }
    
    public DateFormat getDateFormat() {
        return this._serializationConfig.getDateFormat();
    }
    
    public Object setHandlerInstantiator(final HandlerInstantiator hi) {
        this._deserializationConfig = ((MapperConfigBase<CFG, DeserializationConfig>)this._deserializationConfig).with(hi);
        this._serializationConfig = ((MapperConfigBase<CFG, SerializationConfig>)this._serializationConfig).with(hi);
        return this;
    }
    
    public ObjectMapper setInjectableValues(final InjectableValues injectableValues) {
        this._injectableValues = injectableValues;
        return this;
    }
    
    public InjectableValues getInjectableValues() {
        return this._injectableValues;
    }
    
    public ObjectMapper setLocale(final Locale l) {
        this._deserializationConfig = ((MapperConfigBase<CFG, DeserializationConfig>)this._deserializationConfig).with(l);
        this._serializationConfig = ((MapperConfigBase<CFG, SerializationConfig>)this._serializationConfig).with(l);
        return this;
    }
    
    public ObjectMapper setTimeZone(final TimeZone tz) {
        this._deserializationConfig = ((MapperConfigBase<CFG, DeserializationConfig>)this._deserializationConfig).with(tz);
        this._serializationConfig = ((MapperConfigBase<CFG, SerializationConfig>)this._serializationConfig).with(tz);
        return this;
    }
    
    public boolean isEnabled(final MapperFeature f) {
        return this._serializationConfig.isEnabled(f);
    }
    
    public ObjectMapper configure(final MapperFeature f, final boolean state) {
        this._serializationConfig = (state ? ((MapperConfigBase<CFG, SerializationConfig>)this._serializationConfig).with(f) : ((MapperConfigBase<CFG, SerializationConfig>)this._serializationConfig).without(f));
        this._deserializationConfig = (state ? ((MapperConfigBase<CFG, DeserializationConfig>)this._deserializationConfig).with(f) : ((MapperConfigBase<CFG, DeserializationConfig>)this._deserializationConfig).without(f));
        return this;
    }
    
    public ObjectMapper enable(final MapperFeature... f) {
        this._deserializationConfig = ((MapperConfigBase<CFG, DeserializationConfig>)this._deserializationConfig).with(f);
        this._serializationConfig = ((MapperConfigBase<CFG, SerializationConfig>)this._serializationConfig).with(f);
        return this;
    }
    
    public ObjectMapper disable(final MapperFeature... f) {
        this._deserializationConfig = ((MapperConfigBase<CFG, DeserializationConfig>)this._deserializationConfig).without(f);
        this._serializationConfig = ((MapperConfigBase<CFG, SerializationConfig>)this._serializationConfig).without(f);
        return this;
    }
    
    public boolean isEnabled(final SerializationFeature f) {
        return this._serializationConfig.isEnabled(f);
    }
    
    public ObjectMapper configure(final SerializationFeature f, final boolean state) {
        this._serializationConfig = (state ? this._serializationConfig.with(f) : this._serializationConfig.without(f));
        return this;
    }
    
    public ObjectMapper enable(final SerializationFeature f) {
        this._serializationConfig = this._serializationConfig.with(f);
        return this;
    }
    
    public ObjectMapper enable(final SerializationFeature first, final SerializationFeature... f) {
        this._serializationConfig = this._serializationConfig.with(first, f);
        return this;
    }
    
    public ObjectMapper disable(final SerializationFeature f) {
        this._serializationConfig = this._serializationConfig.without(f);
        return this;
    }
    
    public ObjectMapper disable(final SerializationFeature first, final SerializationFeature... f) {
        this._serializationConfig = this._serializationConfig.without(first, f);
        return this;
    }
    
    public boolean isEnabled(final DeserializationFeature f) {
        return this._deserializationConfig.isEnabled(f);
    }
    
    public ObjectMapper configure(final DeserializationFeature f, final boolean state) {
        this._deserializationConfig = (state ? this._deserializationConfig.with(f) : this._deserializationConfig.without(f));
        return this;
    }
    
    public ObjectMapper enable(final DeserializationFeature feature) {
        this._deserializationConfig = this._deserializationConfig.with(feature);
        return this;
    }
    
    public ObjectMapper enable(final DeserializationFeature first, final DeserializationFeature... f) {
        this._deserializationConfig = this._deserializationConfig.with(first, f);
        return this;
    }
    
    public ObjectMapper disable(final DeserializationFeature feature) {
        this._deserializationConfig = this._deserializationConfig.without(feature);
        return this;
    }
    
    public ObjectMapper disable(final DeserializationFeature first, final DeserializationFeature... f) {
        this._deserializationConfig = this._deserializationConfig.without(first, f);
        return this;
    }
    
    public boolean isEnabled(final JsonParser.Feature f) {
        return this._deserializationConfig.isEnabled(f, this._jsonFactory);
    }
    
    public ObjectMapper configure(final JsonParser.Feature f, final boolean state) {
        this._jsonFactory.configure(f, state);
        return this;
    }
    
    public ObjectMapper enable(final JsonParser.Feature... features) {
        for (final JsonParser.Feature f : features) {
            this._jsonFactory.enable(f);
        }
        return this;
    }
    
    public ObjectMapper disable(final JsonParser.Feature... features) {
        for (final JsonParser.Feature f : features) {
            this._jsonFactory.disable(f);
        }
        return this;
    }
    
    public boolean isEnabled(final JsonGenerator.Feature f) {
        return this._serializationConfig.isEnabled(f, this._jsonFactory);
    }
    
    public ObjectMapper configure(final JsonGenerator.Feature f, final boolean state) {
        this._jsonFactory.configure(f, state);
        return this;
    }
    
    public ObjectMapper enable(final JsonGenerator.Feature... features) {
        for (final JsonGenerator.Feature f : features) {
            this._jsonFactory.enable(f);
        }
        return this;
    }
    
    public ObjectMapper disable(final JsonGenerator.Feature... features) {
        for (final JsonGenerator.Feature f : features) {
            this._jsonFactory.disable(f);
        }
        return this;
    }
    
    public boolean isEnabled(final JsonFactory.Feature f) {
        return this._jsonFactory.isEnabled(f);
    }
    
    @Override
    public <T> T readValue(final JsonParser p, final Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readValue(this.getDeserializationConfig(), p, this._typeFactory.constructType(valueType));
    }
    
    @Override
    public <T> T readValue(final JsonParser p, final TypeReference<?> valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readValue(this.getDeserializationConfig(), p, this._typeFactory.constructType(valueTypeRef));
    }
    
    @Override
    public final <T> T readValue(final JsonParser p, final ResolvedType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readValue(this.getDeserializationConfig(), p, (JavaType)valueType);
    }
    
    public <T> T readValue(final JsonParser p, final JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readValue(this.getDeserializationConfig(), p, valueType);
    }
    
    @Override
    public <T extends TreeNode> T readTree(final JsonParser p) throws IOException, JsonProcessingException {
        final DeserializationConfig cfg = this.getDeserializationConfig();
        JsonToken t = p.getCurrentToken();
        if (t == null) {
            t = p.nextToken();
            if (t == null) {
                return null;
            }
        }
        JsonNode n = (JsonNode)this._readValue(cfg, p, ObjectMapper.JSON_NODE_TYPE);
        if (n == null) {
            n = this.getNodeFactory().nullNode();
        }
        final T result = (T)n;
        return result;
    }
    
    @Override
    public <T> MappingIterator<T> readValues(final JsonParser p, final ResolvedType valueType) throws IOException, JsonProcessingException {
        return this.readValues(p, (JavaType)valueType);
    }
    
    public <T> MappingIterator<T> readValues(final JsonParser p, final JavaType valueType) throws IOException, JsonProcessingException {
        final DeserializationConfig config = this.getDeserializationConfig();
        final DeserializationContext ctxt = this.createDeserializationContext(p, config);
        final JsonDeserializer<?> deser = this._findRootDeserializer(ctxt, valueType);
        return new MappingIterator<T>(valueType, p, ctxt, deser, false, null);
    }
    
    @Override
    public <T> MappingIterator<T> readValues(final JsonParser p, final Class<T> valueType) throws IOException, JsonProcessingException {
        return this.readValues(p, this._typeFactory.constructType(valueType));
    }
    
    @Override
    public <T> MappingIterator<T> readValues(final JsonParser p, final TypeReference<?> valueTypeRef) throws IOException, JsonProcessingException {
        return this.readValues(p, this._typeFactory.constructType(valueTypeRef));
    }
    
    public JsonNode readTree(final InputStream in) throws IOException {
        return this._readTreeAndClose(this._jsonFactory.createParser(in));
    }
    
    public JsonNode readTree(final Reader r) throws IOException {
        return this._readTreeAndClose(this._jsonFactory.createParser(r));
    }
    
    public JsonNode readTree(final String content) throws IOException {
        return this._readTreeAndClose(this._jsonFactory.createParser(content));
    }
    
    public JsonNode readTree(final byte[] content) throws IOException {
        return this._readTreeAndClose(this._jsonFactory.createParser(content));
    }
    
    public JsonNode readTree(final File file) throws IOException, JsonProcessingException {
        return this._readTreeAndClose(this._jsonFactory.createParser(file));
    }
    
    public JsonNode readTree(final URL source) throws IOException {
        return this._readTreeAndClose(this._jsonFactory.createParser(source));
    }
    
    @Override
    public void writeValue(final JsonGenerator g, final Object value) throws IOException, JsonGenerationException, JsonMappingException {
        final SerializationConfig config = this.getSerializationConfig();
        if (config.isEnabled(SerializationFeature.INDENT_OUTPUT) && g.getPrettyPrinter() == null) {
            g.setPrettyPrinter(config.constructDefaultPrettyPrinter());
        }
        if (config.isEnabled(SerializationFeature.CLOSE_CLOSEABLE) && value instanceof Closeable) {
            this._writeCloseableValue(g, value, config);
        }
        else {
            this._serializerProvider(config).serializeValue(g, value);
            if (config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
                g.flush();
            }
        }
    }
    
    @Override
    public void writeTree(final JsonGenerator jgen, final TreeNode rootNode) throws IOException, JsonProcessingException {
        final SerializationConfig config = this.getSerializationConfig();
        this._serializerProvider(config).serializeValue(jgen, rootNode);
        if (config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
            jgen.flush();
        }
    }
    
    public void writeTree(final JsonGenerator jgen, final JsonNode rootNode) throws IOException, JsonProcessingException {
        final SerializationConfig config = this.getSerializationConfig();
        this._serializerProvider(config).serializeValue(jgen, rootNode);
        if (config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
            jgen.flush();
        }
    }
    
    @Override
    public ObjectNode createObjectNode() {
        return this._deserializationConfig.getNodeFactory().objectNode();
    }
    
    @Override
    public ArrayNode createArrayNode() {
        return this._deserializationConfig.getNodeFactory().arrayNode();
    }
    
    @Override
    public JsonParser treeAsTokens(final TreeNode n) {
        return new TreeTraversingParser((JsonNode)n, this);
    }
    
    @Override
    public <T> T treeToValue(final TreeNode n, final Class<T> valueType) throws JsonProcessingException {
        try {
            if (valueType != Object.class && valueType.isAssignableFrom(n.getClass())) {
                return (T)n;
            }
            if (n.asToken() == JsonToken.VALUE_EMBEDDED_OBJECT && n instanceof POJONode) {
                final Object ob = ((POJONode)n).getPojo();
                if (ob == null || valueType.isInstance(ob)) {
                    return (T)ob;
                }
            }
            return this.readValue(this.treeAsTokens(n), valueType);
        }
        catch (JsonProcessingException e) {
            throw e;
        }
        catch (IOException e2) {
            throw new IllegalArgumentException(e2.getMessage(), e2);
        }
    }
    
    public <T extends JsonNode> T valueToTree(final Object fromValue) throws IllegalArgumentException {
        if (fromValue == null) {
            return null;
        }
        TokenBuffer buf = new TokenBuffer(this, false);
        if (this.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
            buf = buf.forceUseOfBigDecimal(true);
        }
        JsonNode result;
        try {
            this.writeValue(buf, fromValue);
            final JsonParser p = buf.asParser();
            result = this.readTree(p);
            p.close();
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        return (T)result;
    }
    
    public boolean canSerialize(final Class<?> type) {
        return this._serializerProvider(this.getSerializationConfig()).hasSerializerFor(type, null);
    }
    
    public boolean canSerialize(final Class<?> type, final AtomicReference<Throwable> cause) {
        return this._serializerProvider(this.getSerializationConfig()).hasSerializerFor(type, cause);
    }
    
    public boolean canDeserialize(final JavaType type) {
        return this.createDeserializationContext(null, this.getDeserializationConfig()).hasValueDeserializerFor(type, null);
    }
    
    public boolean canDeserialize(final JavaType type, final AtomicReference<Throwable> cause) {
        return this.createDeserializationContext(null, this.getDeserializationConfig()).hasValueDeserializerFor(type, cause);
    }
    
    public <T> T readValue(final File src, final Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
    }
    
    public <T> T readValue(final File src, final TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
    }
    
    public <T> T readValue(final File src, final JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src), valueType);
    }
    
    public <T> T readValue(final URL src, final Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
    }
    
    public <T> T readValue(final URL src, final TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
    }
    
    public <T> T readValue(final URL src, final JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src), valueType);
    }
    
    public <T> T readValue(final String content, final Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(content), this._typeFactory.constructType(valueType));
    }
    
    public <T> T readValue(final String content, final TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(content), this._typeFactory.constructType(valueTypeRef));
    }
    
    public <T> T readValue(final String content, final JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(content), valueType);
    }
    
    public <T> T readValue(final Reader src, final Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
    }
    
    public <T> T readValue(final Reader src, final TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
    }
    
    public <T> T readValue(final Reader src, final JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src), valueType);
    }
    
    public <T> T readValue(final InputStream src, final Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
    }
    
    public <T> T readValue(final InputStream src, final TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
    }
    
    public <T> T readValue(final InputStream src, final JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src), valueType);
    }
    
    public <T> T readValue(final byte[] src, final Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
    }
    
    public <T> T readValue(final byte[] src, final int offset, final int len, final Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src, offset, len), this._typeFactory.constructType(valueType));
    }
    
    public <T> T readValue(final byte[] src, final TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
    }
    
    public <T> T readValue(final byte[] src, final int offset, final int len, final TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src, offset, len), this._typeFactory.constructType(valueTypeRef));
    }
    
    public <T> T readValue(final byte[] src, final JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src), valueType);
    }
    
    public <T> T readValue(final byte[] src, final int offset, final int len, final JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src, offset, len), valueType);
    }
    
    public <T> T readValue(final DataInput src, final Class<T> valueType) throws IOException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
    }
    
    public <T> T readValue(final DataInput src, final JavaType valueType) throws IOException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src), valueType);
    }
    
    public void writeValue(final File resultFile, final Object value) throws IOException, JsonGenerationException, JsonMappingException {
        this._configAndWriteValue(this._jsonFactory.createGenerator(resultFile, JsonEncoding.UTF8), value);
    }
    
    public void writeValue(final OutputStream out, final Object value) throws IOException, JsonGenerationException, JsonMappingException {
        this._configAndWriteValue(this._jsonFactory.createGenerator(out, JsonEncoding.UTF8), value);
    }
    
    public void writeValue(final DataOutput out, final Object value) throws IOException {
        this._configAndWriteValue(this._jsonFactory.createGenerator(out, JsonEncoding.UTF8), value);
    }
    
    public void writeValue(final Writer w, final Object value) throws IOException, JsonGenerationException, JsonMappingException {
        this._configAndWriteValue(this._jsonFactory.createGenerator(w), value);
    }
    
    public String writeValueAsString(final Object value) throws JsonProcessingException {
        final SegmentedStringWriter sw = new SegmentedStringWriter(this._jsonFactory._getBufferRecycler());
        try {
            this._configAndWriteValue(this._jsonFactory.createGenerator(sw), value);
        }
        catch (JsonProcessingException e) {
            throw e;
        }
        catch (IOException e2) {
            throw JsonMappingException.fromUnexpectedIOE(e2);
        }
        return sw.getAndClear();
    }
    
    public byte[] writeValueAsBytes(final Object value) throws JsonProcessingException {
        final ByteArrayBuilder bb = new ByteArrayBuilder(this._jsonFactory._getBufferRecycler());
        try {
            this._configAndWriteValue(this._jsonFactory.createGenerator(bb, JsonEncoding.UTF8), value);
        }
        catch (JsonProcessingException e) {
            throw e;
        }
        catch (IOException e2) {
            throw JsonMappingException.fromUnexpectedIOE(e2);
        }
        final byte[] result = bb.toByteArray();
        bb.release();
        return result;
    }
    
    public ObjectWriter writer() {
        return this._newWriter(this.getSerializationConfig());
    }
    
    public ObjectWriter writer(final SerializationFeature feature) {
        return this._newWriter(this.getSerializationConfig().with(feature));
    }
    
    public ObjectWriter writer(final SerializationFeature first, final SerializationFeature... other) {
        return this._newWriter(this.getSerializationConfig().with(first, other));
    }
    
    public ObjectWriter writer(final DateFormat df) {
        return this._newWriter(this.getSerializationConfig().with(df));
    }
    
    public ObjectWriter writerWithView(final Class<?> serializationView) {
        return this._newWriter(this.getSerializationConfig().withView(serializationView));
    }
    
    public ObjectWriter writerFor(final Class<?> rootType) {
        return this._newWriter(this.getSerializationConfig(), (rootType == null) ? null : this._typeFactory.constructType(rootType), null);
    }
    
    public ObjectWriter writerFor(final TypeReference<?> rootType) {
        return this._newWriter(this.getSerializationConfig(), (rootType == null) ? null : this._typeFactory.constructType(rootType), null);
    }
    
    public ObjectWriter writerFor(final JavaType rootType) {
        return this._newWriter(this.getSerializationConfig(), rootType, null);
    }
    
    public ObjectWriter writer(PrettyPrinter pp) {
        if (pp == null) {
            pp = ObjectWriter.NULL_PRETTY_PRINTER;
        }
        return this._newWriter(this.getSerializationConfig(), null, pp);
    }
    
    public ObjectWriter writerWithDefaultPrettyPrinter() {
        final SerializationConfig config = this.getSerializationConfig();
        return this._newWriter(config, null, config.getDefaultPrettyPrinter());
    }
    
    public ObjectWriter writer(final FilterProvider filterProvider) {
        return this._newWriter(this.getSerializationConfig().withFilters(filterProvider));
    }
    
    public ObjectWriter writer(final FormatSchema schema) {
        this._verifySchemaType(schema);
        return this._newWriter(this.getSerializationConfig(), schema);
    }
    
    public ObjectWriter writer(final Base64Variant defaultBase64) {
        return this._newWriter(((MapperConfigBase<CFG, SerializationConfig>)this.getSerializationConfig()).with(defaultBase64));
    }
    
    public ObjectWriter writer(final CharacterEscapes escapes) {
        return this._newWriter(this.getSerializationConfig()).with(escapes);
    }
    
    public ObjectWriter writer(final ContextAttributes attrs) {
        return this._newWriter(this.getSerializationConfig().with(attrs));
    }
    
    @Deprecated
    public ObjectWriter writerWithType(final Class<?> rootType) {
        return this._newWriter(this.getSerializationConfig(), (rootType == null) ? null : this._typeFactory.constructType(rootType), null);
    }
    
    @Deprecated
    public ObjectWriter writerWithType(final TypeReference<?> rootType) {
        return this._newWriter(this.getSerializationConfig(), (rootType == null) ? null : this._typeFactory.constructType(rootType), null);
    }
    
    @Deprecated
    public ObjectWriter writerWithType(final JavaType rootType) {
        return this._newWriter(this.getSerializationConfig(), rootType, null);
    }
    
    public ObjectReader reader() {
        return this._newReader(this.getDeserializationConfig()).with(this._injectableValues);
    }
    
    public ObjectReader reader(final DeserializationFeature feature) {
        return this._newReader(this.getDeserializationConfig().with(feature));
    }
    
    public ObjectReader reader(final DeserializationFeature first, final DeserializationFeature... other) {
        return this._newReader(this.getDeserializationConfig().with(first, other));
    }
    
    public ObjectReader readerForUpdating(final Object valueToUpdate) {
        final JavaType t = this._typeFactory.constructType(valueToUpdate.getClass());
        return this._newReader(this.getDeserializationConfig(), t, valueToUpdate, null, this._injectableValues);
    }
    
    public ObjectReader readerFor(final JavaType type) {
        return this._newReader(this.getDeserializationConfig(), type, null, null, this._injectableValues);
    }
    
    public ObjectReader readerFor(final Class<?> type) {
        return this._newReader(this.getDeserializationConfig(), this._typeFactory.constructType(type), null, null, this._injectableValues);
    }
    
    public ObjectReader readerFor(final TypeReference<?> type) {
        return this._newReader(this.getDeserializationConfig(), this._typeFactory.constructType(type), null, null, this._injectableValues);
    }
    
    public ObjectReader reader(final JsonNodeFactory f) {
        return this._newReader(this.getDeserializationConfig()).with(f);
    }
    
    public ObjectReader reader(final FormatSchema schema) {
        this._verifySchemaType(schema);
        return this._newReader(this.getDeserializationConfig(), null, null, schema, this._injectableValues);
    }
    
    public ObjectReader reader(final InjectableValues injectableValues) {
        return this._newReader(this.getDeserializationConfig(), null, null, null, injectableValues);
    }
    
    public ObjectReader readerWithView(final Class<?> view) {
        return this._newReader(this.getDeserializationConfig().withView(view));
    }
    
    public ObjectReader reader(final Base64Variant defaultBase64) {
        return this._newReader(((MapperConfigBase<CFG, DeserializationConfig>)this.getDeserializationConfig()).with(defaultBase64));
    }
    
    public ObjectReader reader(final ContextAttributes attrs) {
        return this._newReader(this.getDeserializationConfig().with(attrs));
    }
    
    @Deprecated
    public ObjectReader reader(final JavaType type) {
        return this._newReader(this.getDeserializationConfig(), type, null, null, this._injectableValues);
    }
    
    @Deprecated
    public ObjectReader reader(final Class<?> type) {
        return this._newReader(this.getDeserializationConfig(), this._typeFactory.constructType(type), null, null, this._injectableValues);
    }
    
    @Deprecated
    public ObjectReader reader(final TypeReference<?> type) {
        return this._newReader(this.getDeserializationConfig(), this._typeFactory.constructType(type), null, null, this._injectableValues);
    }
    
    public <T> T convertValue(final Object fromValue, final Class<T> toValueType) throws IllegalArgumentException {
        return (T)this._convert(fromValue, this._typeFactory.constructType(toValueType));
    }
    
    public <T> T convertValue(final Object fromValue, final TypeReference<?> toValueTypeRef) throws IllegalArgumentException {
        return (T)this._convert(fromValue, this._typeFactory.constructType(toValueTypeRef));
    }
    
    public <T> T convertValue(final Object fromValue, final JavaType toValueType) throws IllegalArgumentException {
        return (T)this._convert(fromValue, toValueType);
    }
    
    protected Object _convert(final Object fromValue, final JavaType toValueType) throws IllegalArgumentException {
        if (fromValue != null) {
            final Class<?> targetType = toValueType.getRawClass();
            if (targetType != Object.class && !toValueType.hasGenericTypes() && targetType.isAssignableFrom(fromValue.getClass())) {
                return fromValue;
            }
        }
        TokenBuffer buf = new TokenBuffer(this, false);
        if (this.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
            buf = buf.forceUseOfBigDecimal(true);
        }
        try {
            final SerializationConfig config = this.getSerializationConfig().without(SerializationFeature.WRAP_ROOT_VALUE);
            this._serializerProvider(config).serializeValue(buf, fromValue);
            final JsonParser p = buf.asParser();
            final DeserializationConfig deserConfig = this.getDeserializationConfig();
            final JsonToken t = this._initForReading(p, toValueType);
            Object result;
            if (t == JsonToken.VALUE_NULL) {
                final DeserializationContext ctxt = this.createDeserializationContext(p, deserConfig);
                result = this._findRootDeserializer(ctxt, toValueType).getNullValue(ctxt);
            }
            else if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
                result = null;
            }
            else {
                final DeserializationContext ctxt = this.createDeserializationContext(p, deserConfig);
                final JsonDeserializer<Object> deser = this._findRootDeserializer(ctxt, toValueType);
                result = deser.deserialize(p, ctxt);
            }
            p.close();
            return result;
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
    
    public <T> T updateValue(final T valueToUpdate, final Object overrides) throws JsonMappingException {
        T result = valueToUpdate;
        if (valueToUpdate != null && overrides != null) {
            TokenBuffer buf = new TokenBuffer(this, false);
            if (this.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                buf = buf.forceUseOfBigDecimal(true);
            }
            try {
                final SerializationConfig config = this.getSerializationConfig().without(SerializationFeature.WRAP_ROOT_VALUE);
                this._serializerProvider(config).serializeValue(buf, overrides);
                final JsonParser p = buf.asParser();
                result = this.readerForUpdating(valueToUpdate).readValue(p);
                p.close();
            }
            catch (IOException e) {
                if (e instanceof JsonMappingException) {
                    throw (JsonMappingException)e;
                }
                throw JsonMappingException.fromUnexpectedIOE(e);
            }
        }
        return result;
    }
    
    @Deprecated
    public JsonSchema generateJsonSchema(final Class<?> t) throws JsonMappingException {
        return this._serializerProvider(this.getSerializationConfig()).generateJsonSchema(t);
    }
    
    public void acceptJsonFormatVisitor(final Class<?> type, final JsonFormatVisitorWrapper visitor) throws JsonMappingException {
        this.acceptJsonFormatVisitor(this._typeFactory.constructType(type), visitor);
    }
    
    public void acceptJsonFormatVisitor(final JavaType type, final JsonFormatVisitorWrapper visitor) throws JsonMappingException {
        if (type == null) {
            throw new IllegalArgumentException("type must be provided");
        }
        this._serializerProvider(this.getSerializationConfig()).acceptJsonFormatVisitor(type, visitor);
    }
    
    protected DefaultSerializerProvider _serializerProvider(final SerializationConfig config) {
        return this._serializerProvider.createInstance(config, this._serializerFactory);
    }
    
    protected final void _configAndWriteValue(final JsonGenerator g, final Object value) throws IOException {
        final SerializationConfig cfg = this.getSerializationConfig();
        cfg.initialize(g);
        if (cfg.isEnabled(SerializationFeature.CLOSE_CLOSEABLE) && value instanceof Closeable) {
            this._configAndWriteCloseable(g, value, cfg);
            return;
        }
        try {
            this._serializerProvider(cfg).serializeValue(g, value);
        }
        catch (Exception e) {
            ClassUtil.closeOnFailAndThrowAsIOE(g, e);
            return;
        }
        g.close();
    }
    
    private final void _configAndWriteCloseable(final JsonGenerator g, final Object value, final SerializationConfig cfg) throws IOException {
        Closeable toClose = (Closeable)value;
        try {
            this._serializerProvider(cfg).serializeValue(g, value);
            final Closeable tmpToClose = toClose;
            toClose = null;
            tmpToClose.close();
        }
        catch (Exception e) {
            ClassUtil.closeOnFailAndThrowAsIOE(g, toClose, e);
            return;
        }
        g.close();
    }
    
    private final void _writeCloseableValue(final JsonGenerator g, final Object value, final SerializationConfig cfg) throws IOException {
        final Closeable toClose = (Closeable)value;
        try {
            this._serializerProvider(cfg).serializeValue(g, value);
            if (cfg.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
                g.flush();
            }
        }
        catch (Exception e) {
            ClassUtil.closeOnFailAndThrowAsIOE(null, toClose, e);
            return;
        }
        toClose.close();
    }
    
    protected Object _readValue(final DeserializationConfig cfg, final JsonParser p, final JavaType valueType) throws IOException {
        final JsonToken t = this._initForReading(p, valueType);
        final DeserializationContext ctxt = this.createDeserializationContext(p, cfg);
        Object result;
        if (t == JsonToken.VALUE_NULL) {
            result = this._findRootDeserializer(ctxt, valueType).getNullValue(ctxt);
        }
        else if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
            result = null;
        }
        else {
            final JsonDeserializer<Object> deser = this._findRootDeserializer(ctxt, valueType);
            if (cfg.useRootWrapping()) {
                result = this._unwrapAndDeserialize(p, ctxt, cfg, valueType, deser);
            }
            else {
                result = deser.deserialize(p, ctxt);
            }
        }
        p.clearCurrentToken();
        if (cfg.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
            this._verifyNoTrailingTokens(p, ctxt, valueType);
        }
        return result;
    }
    
    protected Object _readMapAndClose(final JsonParser p0, final JavaType valueType) throws IOException {
        try (final JsonParser p = p0) {
            final JsonToken t = this._initForReading(p, valueType);
            final DeserializationConfig cfg = this.getDeserializationConfig();
            final DeserializationContext ctxt = this.createDeserializationContext(p, cfg);
            Object result;
            if (t == JsonToken.VALUE_NULL) {
                result = this._findRootDeserializer(ctxt, valueType).getNullValue(ctxt);
            }
            else if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
                result = null;
            }
            else {
                final JsonDeserializer<Object> deser = this._findRootDeserializer(ctxt, valueType);
                if (cfg.useRootWrapping()) {
                    result = this._unwrapAndDeserialize(p, ctxt, cfg, valueType, deser);
                }
                else {
                    result = deser.deserialize(p, ctxt);
                }
                ctxt.checkUnresolvedObjectId();
            }
            if (cfg.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
                this._verifyNoTrailingTokens(p, ctxt, valueType);
            }
            return result;
        }
    }
    
    protected JsonNode _readTreeAndClose(final JsonParser p0) throws IOException {
        try (final JsonParser p = p0) {
            final JavaType valueType = ObjectMapper.JSON_NODE_TYPE;
            final DeserializationConfig cfg = this.getDeserializationConfig();
            cfg.initialize(p);
            JsonToken t = p.getCurrentToken();
            if (t == null) {
                t = p.nextToken();
                if (t == null) {
                    return null;
                }
            }
            if (t == JsonToken.VALUE_NULL) {
                return cfg.getNodeFactory().nullNode();
            }
            final DeserializationContext ctxt = this.createDeserializationContext(p, cfg);
            final JsonDeserializer<Object> deser = this._findRootDeserializer(ctxt, valueType);
            Object result;
            if (cfg.useRootWrapping()) {
                result = this._unwrapAndDeserialize(p, ctxt, cfg, valueType, deser);
            }
            else {
                result = deser.deserialize(p, ctxt);
                if (cfg.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
                    this._verifyNoTrailingTokens(p, ctxt, valueType);
                }
            }
            return (JsonNode)result;
        }
    }
    
    protected Object _unwrapAndDeserialize(final JsonParser p, final DeserializationContext ctxt, final DeserializationConfig config, final JavaType rootType, final JsonDeserializer<Object> deser) throws IOException {
        final PropertyName expRootName = config.findRootName(rootType);
        final String expSimpleName = expRootName.getSimpleName();
        if (p.getCurrentToken() != JsonToken.START_OBJECT) {
            ctxt.reportWrongTokenException(rootType, JsonToken.START_OBJECT, "Current token not START_OBJECT (needed to unwrap root name '%s'), but %s", expSimpleName, p.getCurrentToken());
        }
        if (p.nextToken() != JsonToken.FIELD_NAME) {
            ctxt.reportWrongTokenException(rootType, JsonToken.FIELD_NAME, "Current token not FIELD_NAME (to contain expected root name '%s'), but %s", expSimpleName, p.getCurrentToken());
        }
        final String actualName = p.getCurrentName();
        if (!expSimpleName.equals(actualName)) {
            ctxt.reportInputMismatch(rootType, "Root name '%s' does not match expected ('%s') for type %s", actualName, expSimpleName, rootType);
        }
        p.nextToken();
        final Object result = deser.deserialize(p, ctxt);
        if (p.nextToken() != JsonToken.END_OBJECT) {
            ctxt.reportWrongTokenException(rootType, JsonToken.END_OBJECT, "Current token not END_OBJECT (to match wrapper object with root name '%s'), but %s", expSimpleName, p.getCurrentToken());
        }
        if (config.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
            this._verifyNoTrailingTokens(p, ctxt, rootType);
        }
        return result;
    }
    
    protected DefaultDeserializationContext createDeserializationContext(final JsonParser p, final DeserializationConfig cfg) {
        return this._deserializationContext.createInstance(cfg, p, this._injectableValues);
    }
    
    protected JsonToken _initForReading(final JsonParser p, final JavaType targetType) throws IOException {
        this._deserializationConfig.initialize(p);
        JsonToken t = p.getCurrentToken();
        if (t == null) {
            t = p.nextToken();
            if (t == null) {
                throw MismatchedInputException.from(p, targetType, "No content to map due to end-of-input");
            }
        }
        return t;
    }
    
    @Deprecated
    protected JsonToken _initForReading(final JsonParser p) throws IOException {
        return this._initForReading(p, null);
    }
    
    protected final void _verifyNoTrailingTokens(final JsonParser p, final DeserializationContext ctxt, final JavaType bindType) throws IOException {
        final JsonToken t = p.nextToken();
        if (t != null) {
            final Class<?> bt = ClassUtil.rawClass(bindType);
            ctxt.reportTrailingTokens(bt, p, t);
        }
    }
    
    protected JsonDeserializer<Object> _findRootDeserializer(final DeserializationContext ctxt, final JavaType valueType) throws JsonMappingException {
        JsonDeserializer<Object> deser = this._rootDeserializers.get(valueType);
        if (deser != null) {
            return deser;
        }
        deser = ctxt.findRootValueDeserializer(valueType);
        if (deser == null) {
            return ctxt.reportBadDefinition(valueType, "Cannot find a deserializer for type " + valueType);
        }
        this._rootDeserializers.put(valueType, deser);
        return deser;
    }
    
    protected void _verifySchemaType(final FormatSchema schema) {
        if (schema != null && !this._jsonFactory.canUseSchema(schema)) {
            throw new IllegalArgumentException("Cannot use FormatSchema of type " + schema.getClass().getName() + " for format " + this._jsonFactory.getFormatName());
        }
    }
    
    static {
        JSON_NODE_TYPE = SimpleType.constructUnsafe(JsonNode.class);
        DEFAULT_ANNOTATION_INTROSPECTOR = new JacksonAnnotationIntrospector();
        DEFAULT_BASE = new BaseSettings(null, ObjectMapper.DEFAULT_ANNOTATION_INTROSPECTOR, null, TypeFactory.defaultInstance(), null, StdDateFormat.instance, null, Locale.getDefault(), null, Base64Variants.getDefaultVariant());
    }
    
    public enum DefaultTyping
    {
        JAVA_LANG_OBJECT, 
        OBJECT_AND_NON_CONCRETE, 
        NON_CONCRETE_AND_ARRAYS, 
        NON_FINAL;
    }
    
    public static class DefaultTypeResolverBuilder extends StdTypeResolverBuilder implements Serializable
    {
        private static final long serialVersionUID = 1L;
        protected final DefaultTyping _appliesFor;
        
        public DefaultTypeResolverBuilder(final DefaultTyping t) {
            this._appliesFor = t;
        }
        
        @Override
        public TypeDeserializer buildTypeDeserializer(final DeserializationConfig config, final JavaType baseType, final Collection<NamedType> subtypes) {
            return this.useForType(baseType) ? super.buildTypeDeserializer(config, baseType, subtypes) : null;
        }
        
        @Override
        public TypeSerializer buildTypeSerializer(final SerializationConfig config, final JavaType baseType, final Collection<NamedType> subtypes) {
            return this.useForType(baseType) ? super.buildTypeSerializer(config, baseType, subtypes) : null;
        }
        
        public boolean useForType(JavaType t) {
            if (t.isPrimitive()) {
                return false;
            }
            switch (this._appliesFor) {
                case NON_CONCRETE_AND_ARRAYS: {
                    while (t.isArrayType()) {
                        t = t.getContentType();
                    }
                }
                case OBJECT_AND_NON_CONCRETE: {
                    while (t.isReferenceType()) {
                        t = t.getReferencedType();
                    }
                    return t.isJavaLangObject() || (!t.isConcrete() && !TreeNode.class.isAssignableFrom(t.getRawClass()));
                }
                case NON_FINAL: {
                    while (t.isArrayType()) {
                        t = t.getContentType();
                    }
                    while (t.isReferenceType()) {
                        t = t.getReferencedType();
                    }
                    return !t.isFinal() && !TreeNode.class.isAssignableFrom(t.getRawClass());
                }
                default: {
                    return t.isJavaLangObject();
                }
            }
        }
    }
}
