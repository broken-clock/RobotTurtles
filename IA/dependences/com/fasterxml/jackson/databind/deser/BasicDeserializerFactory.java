// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser;

import java.util.Queue;
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.fasterxml.jackson.databind.deser.std.JdkDeserializers;
import com.fasterxml.jackson.databind.deser.std.TokenBufferDeserializer;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.fasterxml.jackson.databind.deser.std.MapEntryDeserializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;
import com.fasterxml.jackson.databind.util.EnumResolver;
import java.lang.reflect.Member;
import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializers;
import com.fasterxml.jackson.databind.ext.OptionalHandlerFactory;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.deser.std.AtomicReferenceDeserializer;
import java.util.concurrent.atomic.AtomicReference;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.JsonNodeDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.deser.std.EnumDeserializer;
import com.fasterxml.jackson.databind.type.MapLikeType;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.deser.std.MapDeserializer;
import com.fasterxml.jackson.databind.deser.std.EnumMapDeserializer;
import java.util.EnumMap;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.deser.std.CollectionDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringCollectionDeserializer;
import com.fasterxml.jackson.databind.deser.impl.JavaUtilCollectionsDeserializers;
import com.fasterxml.jackson.databind.deser.std.ArrayBlockingQueueDeserializer;
import java.util.concurrent.ArrayBlockingQueue;
import com.fasterxml.jackson.databind.deser.std.EnumSetDeserializer;
import java.util.EnumSet;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.deser.std.ObjectArrayDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringArrayDeserializer;
import com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition;
import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.annotation.JacksonInject;
import java.util.List;
import com.fasterxml.jackson.databind.introspect.POJOPropertyBuilder;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.fasterxml.jackson.databind.deser.impl.CreatorCandidate;
import java.util.LinkedList;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.util.LinkedHashMap;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.deser.impl.CreatorCollector;
import com.fasterxml.jackson.databind.util.ConstantValueInstantiator;
import java.util.Collections;
import com.fasterxml.jackson.databind.deser.std.JsonLocationInstantiator;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.util.Iterator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.AbstractTypeResolver;
import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.databind.PropertyName;
import java.io.Serializable;

public abstract class BasicDeserializerFactory extends DeserializerFactory implements Serializable
{
    private static final Class<?> CLASS_OBJECT;
    private static final Class<?> CLASS_STRING;
    private static final Class<?> CLASS_CHAR_SEQUENCE;
    private static final Class<?> CLASS_ITERABLE;
    private static final Class<?> CLASS_MAP_ENTRY;
    protected static final PropertyName UNWRAPPED_CREATOR_PARAM_NAME;
    static final HashMap<String, Class<? extends Map>> _mapFallbacks;
    static final HashMap<String, Class<? extends Collection>> _collectionFallbacks;
    protected final DeserializerFactoryConfig _factoryConfig;
    
    protected BasicDeserializerFactory(final DeserializerFactoryConfig config) {
        this._factoryConfig = config;
    }
    
    public DeserializerFactoryConfig getFactoryConfig() {
        return this._factoryConfig;
    }
    
    protected abstract DeserializerFactory withConfig(final DeserializerFactoryConfig p0);
    
    @Override
    public final DeserializerFactory withAdditionalDeserializers(final Deserializers additional) {
        return this.withConfig(this._factoryConfig.withAdditionalDeserializers(additional));
    }
    
    @Override
    public final DeserializerFactory withAdditionalKeyDeserializers(final KeyDeserializers additional) {
        return this.withConfig(this._factoryConfig.withAdditionalKeyDeserializers(additional));
    }
    
    @Override
    public final DeserializerFactory withDeserializerModifier(final BeanDeserializerModifier modifier) {
        return this.withConfig(this._factoryConfig.withDeserializerModifier(modifier));
    }
    
    @Override
    public final DeserializerFactory withAbstractTypeResolver(final AbstractTypeResolver resolver) {
        return this.withConfig(this._factoryConfig.withAbstractTypeResolver(resolver));
    }
    
    @Override
    public final DeserializerFactory withValueInstantiators(final ValueInstantiators instantiators) {
        return this.withConfig(this._factoryConfig.withValueInstantiators(instantiators));
    }
    
    @Override
    public JavaType mapAbstractType(final DeserializationConfig config, JavaType type) throws JsonMappingException {
        while (true) {
            final JavaType next = this._mapAbstractType2(config, type);
            if (next == null) {
                return type;
            }
            final Class<?> prevCls = type.getRawClass();
            final Class<?> nextCls = next.getRawClass();
            if (prevCls == nextCls || !prevCls.isAssignableFrom(nextCls)) {
                throw new IllegalArgumentException("Invalid abstract type resolution from " + type + " to " + next + ": latter is not a subtype of former");
            }
            type = next;
        }
    }
    
    private JavaType _mapAbstractType2(final DeserializationConfig config, final JavaType type) throws JsonMappingException {
        final Class<?> currClass = type.getRawClass();
        if (this._factoryConfig.hasAbstractTypeResolvers()) {
            for (final AbstractTypeResolver resolver : this._factoryConfig.abstractTypeResolvers()) {
                final JavaType concrete = resolver.findTypeMapping(config, type);
                if (concrete != null && !concrete.hasRawClass(currClass)) {
                    return concrete;
                }
            }
        }
        return null;
    }
    
    @Override
    public ValueInstantiator findValueInstantiator(final DeserializationContext ctxt, final BeanDescription beanDesc) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        ValueInstantiator instantiator = null;
        final AnnotatedClass ac = beanDesc.getClassInfo();
        final Object instDef = ctxt.getAnnotationIntrospector().findValueInstantiator(ac);
        if (instDef != null) {
            instantiator = this._valueInstantiatorInstance(config, ac, instDef);
        }
        if (instantiator == null) {
            instantiator = this._findStdValueInstantiator(config, beanDesc);
            if (instantiator == null) {
                instantiator = this._constructDefaultValueInstantiator(ctxt, beanDesc);
            }
        }
        if (this._factoryConfig.hasValueInstantiators()) {
            for (final ValueInstantiators insts : this._factoryConfig.valueInstantiators()) {
                instantiator = insts.findValueInstantiator(config, beanDesc, instantiator);
                if (instantiator == null) {
                    ctxt.reportBadTypeDefinition(beanDesc, "Broken registered ValueInstantiators (of type %s): returned null ValueInstantiator", insts.getClass().getName());
                }
            }
        }
        if (instantiator.getIncompleteParameter() != null) {
            final AnnotatedParameter nonAnnotatedParam = instantiator.getIncompleteParameter();
            final AnnotatedWithParams ctor = nonAnnotatedParam.getOwner();
            throw new IllegalArgumentException("Argument #" + nonAnnotatedParam.getIndex() + " of constructor " + ctor + " has no property name annotation; must have name when multiple-parameter constructor annotated as Creator");
        }
        return instantiator;
    }
    
    private ValueInstantiator _findStdValueInstantiator(final DeserializationConfig config, final BeanDescription beanDesc) throws JsonMappingException {
        final Class<?> raw = beanDesc.getBeanClass();
        if (raw == JsonLocation.class) {
            return new JsonLocationInstantiator();
        }
        if (Collection.class.isAssignableFrom(raw)) {
            if (Collections.EMPTY_SET.getClass() == raw) {
                return new ConstantValueInstantiator(Collections.EMPTY_SET);
            }
            if (Collections.EMPTY_LIST.getClass() == raw) {
                return new ConstantValueInstantiator(Collections.EMPTY_LIST);
            }
        }
        else if (Map.class.isAssignableFrom(raw) && Collections.EMPTY_MAP.getClass() == raw) {
            return new ConstantValueInstantiator(Collections.EMPTY_MAP);
        }
        return null;
    }
    
    protected ValueInstantiator _constructDefaultValueInstantiator(final DeserializationContext ctxt, final BeanDescription beanDesc) throws JsonMappingException {
        final CreatorCollector creators = new CreatorCollector(beanDesc, ctxt.getConfig());
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        final DeserializationConfig config = ctxt.getConfig();
        final VisibilityChecker<?> vchecker = config.getDefaultVisibilityChecker(beanDesc.getBeanClass(), beanDesc.getClassInfo());
        final Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorDefs = this._findCreatorsFromProperties(ctxt, beanDesc);
        this._addDeserializerFactoryMethods(ctxt, beanDesc, vchecker, intr, creators, creatorDefs);
        if (beanDesc.getType().isConcrete()) {
            this._addDeserializerConstructors(ctxt, beanDesc, vchecker, intr, creators, creatorDefs);
        }
        return creators.constructValueInstantiator(ctxt);
    }
    
    protected Map<AnnotatedWithParams, BeanPropertyDefinition[]> _findCreatorsFromProperties(final DeserializationContext ctxt, final BeanDescription beanDesc) throws JsonMappingException {
        Map<AnnotatedWithParams, BeanPropertyDefinition[]> result = Collections.emptyMap();
        for (final BeanPropertyDefinition propDef : beanDesc.findProperties()) {
            final Iterator<AnnotatedParameter> it = propDef.getConstructorParameters();
            while (it.hasNext()) {
                final AnnotatedParameter param = it.next();
                final AnnotatedWithParams owner = param.getOwner();
                BeanPropertyDefinition[] defs = result.get(owner);
                final int index = param.getIndex();
                if (defs == null) {
                    if (result.isEmpty()) {
                        result = new LinkedHashMap<AnnotatedWithParams, BeanPropertyDefinition[]>();
                    }
                    defs = new BeanPropertyDefinition[owner.getParameterCount()];
                    result.put(owner, defs);
                }
                else if (defs[index] != null) {
                    ctxt.reportBadTypeDefinition(beanDesc, "Conflict: parameter #%d of %s bound to more than one property; %s vs %s", index, owner, defs[index], propDef);
                }
                defs[index] = propDef;
            }
        }
        return result;
    }
    
    public ValueInstantiator _valueInstantiatorInstance(final DeserializationConfig config, final Annotated annotated, final Object instDef) throws JsonMappingException {
        if (instDef == null) {
            return null;
        }
        if (instDef instanceof ValueInstantiator) {
            return (ValueInstantiator)instDef;
        }
        if (!(instDef instanceof Class)) {
            throw new IllegalStateException("AnnotationIntrospector returned key deserializer definition of type " + instDef.getClass().getName() + "; expected type KeyDeserializer or Class<KeyDeserializer> instead");
        }
        final Class<?> instClass = (Class<?>)instDef;
        if (ClassUtil.isBogusClass(instClass)) {
            return null;
        }
        if (!ValueInstantiator.class.isAssignableFrom(instClass)) {
            throw new IllegalStateException("AnnotationIntrospector returned Class " + instClass.getName() + "; expected Class<ValueInstantiator>");
        }
        final HandlerInstantiator hi = config.getHandlerInstantiator();
        if (hi != null) {
            final ValueInstantiator inst = hi.valueInstantiatorInstance(config, annotated, instClass);
            if (inst != null) {
                return inst;
            }
        }
        return ClassUtil.createInstance(instClass, config.canOverrideAccessModifiers());
    }
    
    protected void _addDeserializerConstructors(final DeserializationContext ctxt, final BeanDescription beanDesc, final VisibilityChecker<?> vchecker, final AnnotationIntrospector intr, final CreatorCollector creators, final Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorParams) throws JsonMappingException {
        final boolean isNonStaticInnerClass = beanDesc.isNonStaticInnerClass();
        if (isNonStaticInnerClass) {
            return;
        }
        final AnnotatedConstructor defaultCtor = beanDesc.findDefaultConstructor();
        if (defaultCtor != null && (!creators.hasDefaultCreator() || this._hasCreatorAnnotation(ctxt, defaultCtor))) {
            creators.setDefaultCreator(defaultCtor);
        }
        final List<CreatorCandidate> nonAnnotated = new LinkedList<CreatorCandidate>();
        int explCount = 0;
        for (final AnnotatedConstructor ctor : beanDesc.getConstructors()) {
            final JsonCreator.Mode creatorMode = intr.findCreatorAnnotation(ctxt.getConfig(), ctor);
            if (JsonCreator.Mode.DISABLED == creatorMode) {
                continue;
            }
            if (creatorMode == null) {
                if (!vchecker.isCreatorVisible(ctor)) {
                    continue;
                }
                nonAnnotated.add(CreatorCandidate.construct(intr, ctor, creatorParams.get(ctor)));
            }
            else {
                switch (creatorMode) {
                    case DELEGATING: {
                        this._addExplicitDelegatingCreator(ctxt, beanDesc, creators, CreatorCandidate.construct(intr, ctor, null));
                        break;
                    }
                    case PROPERTIES: {
                        this._addExplicitPropertyCreator(ctxt, beanDesc, creators, CreatorCandidate.construct(intr, ctor, creatorParams.get(ctor)));
                        break;
                    }
                    default: {
                        this._addExplicitAnyCreator(ctxt, beanDesc, creators, CreatorCandidate.construct(intr, ctor, creatorParams.get(ctor)));
                        break;
                    }
                }
                ++explCount;
            }
        }
        if (explCount > 0) {
            return;
        }
        List<AnnotatedWithParams> implicitCtors = null;
        for (final CreatorCandidate candidate : nonAnnotated) {
            final int argCount = candidate.paramCount();
            final AnnotatedWithParams ctor2 = candidate.creator();
            if (argCount == 1) {
                final BeanPropertyDefinition propDef = candidate.propertyDef(0);
                final boolean useProps = this._checkIfCreatorPropertyBased(intr, ctor2, propDef);
                if (useProps) {
                    final SettableBeanProperty[] properties = { null };
                    final PropertyName name = candidate.paramName(0);
                    properties[0] = this.constructCreatorProperty(ctxt, beanDesc, name, 0, candidate.parameter(0), candidate.injection(0));
                    creators.addPropertyCreator(ctor2, false, properties);
                }
                else {
                    this._handleSingleArgumentCreator(creators, ctor2, false, vchecker.isCreatorVisible(ctor2));
                    if (propDef == null) {
                        continue;
                    }
                    ((POJOPropertyBuilder)propDef).removeConstructors();
                }
            }
            else {
                int nonAnnotatedParamIndex = -1;
                final SettableBeanProperty[] properties2 = new SettableBeanProperty[argCount];
                int explicitNameCount = 0;
                final int implicitWithCreatorCount = 0;
                int injectCount = 0;
                for (int i = 0; i < argCount; ++i) {
                    final AnnotatedParameter param = ctor2.getParameter(i);
                    final BeanPropertyDefinition propDef2 = candidate.propertyDef(i);
                    final JacksonInject.Value injectId = intr.findInjectableValue(param);
                    final PropertyName name2 = (propDef2 == null) ? null : propDef2.getFullName();
                    if (propDef2 != null && propDef2.isExplicitlyNamed()) {
                        ++explicitNameCount;
                        properties2[i] = this.constructCreatorProperty(ctxt, beanDesc, name2, i, param, injectId);
                    }
                    else if (injectId != null) {
                        ++injectCount;
                        properties2[i] = this.constructCreatorProperty(ctxt, beanDesc, name2, i, param, injectId);
                    }
                    else {
                        final NameTransformer unwrapper = intr.findUnwrappingNameTransformer(param);
                        if (unwrapper != null) {
                            this._reportUnwrappedCreatorProperty(ctxt, beanDesc, param);
                        }
                        else if (nonAnnotatedParamIndex < 0) {
                            nonAnnotatedParamIndex = i;
                        }
                    }
                }
                final int namedCount = explicitNameCount + implicitWithCreatorCount;
                if (explicitNameCount > 0 || injectCount > 0) {
                    if (namedCount + injectCount == argCount) {
                        creators.addPropertyCreator(ctor2, false, properties2);
                        continue;
                    }
                    if (explicitNameCount == 0 && injectCount + 1 == argCount) {
                        creators.addDelegatingCreator(ctor2, false, properties2, 0);
                        continue;
                    }
                    final PropertyName impl = candidate.findImplicitParamName(nonAnnotatedParamIndex);
                    if (impl == null || impl.isEmpty()) {
                        ctxt.reportBadTypeDefinition(beanDesc, "Argument #%d of constructor %s has no property name annotation; must have name when multiple-parameter constructor annotated as Creator", nonAnnotatedParamIndex, ctor2);
                    }
                }
                if (creators.hasDefaultCreator()) {
                    continue;
                }
                if (implicitCtors == null) {
                    implicitCtors = new LinkedList<AnnotatedWithParams>();
                }
                implicitCtors.add(ctor2);
            }
        }
        if (implicitCtors != null && !creators.hasDelegatingCreator() && !creators.hasPropertyBasedCreator()) {
            this._checkImplicitlyNamedConstructors(ctxt, beanDesc, vchecker, intr, creators, implicitCtors);
        }
    }
    
    protected void _addExplicitDelegatingCreator(final DeserializationContext ctxt, final BeanDescription beanDesc, final CreatorCollector creators, final CreatorCandidate candidate) throws JsonMappingException {
        int ix = -1;
        final int argCount = candidate.paramCount();
        final SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
        for (int i = 0; i < argCount; ++i) {
            final AnnotatedParameter param = candidate.parameter(i);
            final JacksonInject.Value injectId = candidate.injection(i);
            if (injectId != null) {
                properties[i] = this.constructCreatorProperty(ctxt, beanDesc, null, i, param, injectId);
            }
            else if (ix < 0) {
                ix = i;
            }
            else {
                ctxt.reportBadTypeDefinition(beanDesc, "More than one argument (#%d and #%d) left as delegating for Creator %s: only one allowed", ix, i, candidate);
            }
        }
        if (ix < 0) {
            ctxt.reportBadTypeDefinition(beanDesc, "No argument left as delegating for Creator %s: exactly one required", candidate);
        }
        if (argCount == 1) {
            this._handleSingleArgumentCreator(creators, candidate.creator(), true, true);
            final BeanPropertyDefinition paramDef = candidate.propertyDef(0);
            if (paramDef != null) {
                ((POJOPropertyBuilder)paramDef).removeConstructors();
            }
            return;
        }
        creators.addDelegatingCreator(candidate.creator(), true, properties, ix);
    }
    
    protected void _addExplicitPropertyCreator(final DeserializationContext ctxt, final BeanDescription beanDesc, final CreatorCollector creators, final CreatorCandidate candidate) throws JsonMappingException {
        final int paramCount = candidate.paramCount();
        final SettableBeanProperty[] properties = new SettableBeanProperty[paramCount];
        for (int i = 0; i < paramCount; ++i) {
            final JacksonInject.Value injectId = candidate.injection(i);
            final AnnotatedParameter param = candidate.parameter(i);
            PropertyName name = candidate.paramName(i);
            if (name == null) {
                final NameTransformer unwrapper = ctxt.getAnnotationIntrospector().findUnwrappingNameTransformer(param);
                if (unwrapper != null) {
                    this._reportUnwrappedCreatorProperty(ctxt, beanDesc, param);
                }
                name = candidate.findImplicitParamName(i);
                if (name == null && injectId == null) {
                    ctxt.reportBadTypeDefinition(beanDesc, "Argument #%d has no property name, is not Injectable: can not use as Creator %s", i, candidate);
                }
            }
            properties[i] = this.constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
        }
        creators.addPropertyCreator(candidate.creator(), true, properties);
    }
    
    protected void _addExplicitAnyCreator(final DeserializationContext ctxt, final BeanDescription beanDesc, final CreatorCollector creators, final CreatorCandidate candidate) throws JsonMappingException {
        if (1 != candidate.paramCount()) {
            final int oneNotInjected = candidate.findOnlyParamWithoutInjection();
            if (oneNotInjected >= 0 && candidate.paramName(oneNotInjected) == null) {
                this._addExplicitDelegatingCreator(ctxt, beanDesc, creators, candidate);
                return;
            }
            this._addExplicitPropertyCreator(ctxt, beanDesc, creators, candidate);
        }
        else {
            final AnnotatedParameter param = candidate.parameter(0);
            final JacksonInject.Value injectId = candidate.injection(0);
            PropertyName paramName = candidate.explicitParamName(0);
            final BeanPropertyDefinition paramDef = candidate.propertyDef(0);
            boolean useProps = paramName != null || injectId != null;
            if (!useProps && paramDef != null) {
                paramName = candidate.paramName(0);
                useProps = (paramName != null && paramDef.couldSerialize());
            }
            if (useProps) {
                final SettableBeanProperty[] properties = { this.constructCreatorProperty(ctxt, beanDesc, paramName, 0, param, injectId) };
                creators.addPropertyCreator(candidate.creator(), true, properties);
                return;
            }
            this._handleSingleArgumentCreator(creators, candidate.creator(), true, true);
            if (paramDef != null) {
                ((POJOPropertyBuilder)paramDef).removeConstructors();
            }
        }
    }
    
    private boolean _checkIfCreatorPropertyBased(final AnnotationIntrospector intr, final AnnotatedWithParams creator, final BeanPropertyDefinition propDef) {
        if ((propDef != null && propDef.isExplicitlyNamed()) || intr.findInjectableValue(creator.getParameter(0)) != null) {
            return true;
        }
        if (propDef != null) {
            final String implName = propDef.getName();
            if (implName != null && !implName.isEmpty() && propDef.couldSerialize()) {
                return true;
            }
        }
        return false;
    }
    
    private void _checkImplicitlyNamedConstructors(final DeserializationContext ctxt, final BeanDescription beanDesc, final VisibilityChecker<?> vchecker, final AnnotationIntrospector intr, final CreatorCollector creators, final List<AnnotatedWithParams> implicitCtors) throws JsonMappingException {
        AnnotatedWithParams found = null;
        SettableBeanProperty[] foundProps = null;
    Label_0015:
        for (final AnnotatedWithParams ctor : implicitCtors) {
            if (!vchecker.isCreatorVisible(ctor)) {
                continue;
            }
            final int argCount = ctor.getParameterCount();
            final SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
            for (int i = 0; i < argCount; ++i) {
                final AnnotatedParameter param = ctor.getParameter(i);
                final PropertyName name = this._findParamName(param, intr);
                if (name == null) {
                    continue Label_0015;
                }
                if (name.isEmpty()) {
                    continue Label_0015;
                }
                properties[i] = this.constructCreatorProperty(ctxt, beanDesc, name, param.getIndex(), param, null);
            }
            if (found != null) {
                found = null;
                break;
            }
            found = ctor;
            foundProps = properties;
        }
        if (found != null) {
            creators.addPropertyCreator(found, false, foundProps);
            final BasicBeanDescription bbd = (BasicBeanDescription)beanDesc;
            for (final SettableBeanProperty prop : foundProps) {
                final PropertyName pn = prop.getFullName();
                if (!bbd.hasProperty(pn)) {
                    final BeanPropertyDefinition newDef = SimpleBeanPropertyDefinition.construct(ctxt.getConfig(), prop.getMember(), pn);
                    bbd.addProperty(newDef);
                }
            }
        }
    }
    
    protected void _addDeserializerFactoryMethods(final DeserializationContext ctxt, final BeanDescription beanDesc, final VisibilityChecker<?> vchecker, final AnnotationIntrospector intr, final CreatorCollector creators, final Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorParams) throws JsonMappingException {
        final List<CreatorCandidate> nonAnnotated = new LinkedList<CreatorCandidate>();
        int explCount = 0;
        for (final AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
            final JsonCreator.Mode creatorMode = intr.findCreatorAnnotation(ctxt.getConfig(), factory);
            final int argCount = factory.getParameterCount();
            if (creatorMode == null) {
                if (argCount != 1 || !vchecker.isCreatorVisible(factory)) {
                    continue;
                }
                nonAnnotated.add(CreatorCandidate.construct(intr, factory, null));
            }
            else {
                if (creatorMode == JsonCreator.Mode.DISABLED) {
                    continue;
                }
                if (argCount == 0) {
                    creators.setDefaultCreator(factory);
                }
                else {
                    switch (creatorMode) {
                        case DELEGATING: {
                            this._addExplicitDelegatingCreator(ctxt, beanDesc, creators, CreatorCandidate.construct(intr, factory, null));
                            break;
                        }
                        case PROPERTIES: {
                            this._addExplicitPropertyCreator(ctxt, beanDesc, creators, CreatorCandidate.construct(intr, factory, creatorParams.get(factory)));
                            break;
                        }
                        default: {
                            this._addExplicitAnyCreator(ctxt, beanDesc, creators, CreatorCandidate.construct(intr, factory, creatorParams.get(factory)));
                            break;
                        }
                    }
                    ++explCount;
                }
            }
        }
        if (explCount > 0) {
            return;
        }
        for (final CreatorCandidate candidate : nonAnnotated) {
            final int argCount2 = candidate.paramCount();
            final AnnotatedWithParams factory2 = candidate.creator();
            final BeanPropertyDefinition[] propDefs = creatorParams.get(factory2);
            if (argCount2 != 1) {
                continue;
            }
            final BeanPropertyDefinition argDef = candidate.propertyDef(0);
            final boolean useProps = this._checkIfCreatorPropertyBased(intr, factory2, argDef);
            if (!useProps) {
                this._handleSingleArgumentCreator(creators, factory2, false, vchecker.isCreatorVisible(factory2));
                if (argDef == null) {
                    continue;
                }
                ((POJOPropertyBuilder)argDef).removeConstructors();
            }
            else {
                AnnotatedParameter nonAnnotatedParam = null;
                final SettableBeanProperty[] properties = new SettableBeanProperty[argCount2];
                final int implicitNameCount = 0;
                int explicitNameCount = 0;
                int injectCount = 0;
                for (int i = 0; i < argCount2; ++i) {
                    final AnnotatedParameter param = factory2.getParameter(i);
                    final BeanPropertyDefinition propDef = (propDefs == null) ? null : propDefs[i];
                    final JacksonInject.Value injectable = intr.findInjectableValue(param);
                    final PropertyName name = (propDef == null) ? null : propDef.getFullName();
                    if (propDef != null && propDef.isExplicitlyNamed()) {
                        ++explicitNameCount;
                        properties[i] = this.constructCreatorProperty(ctxt, beanDesc, name, i, param, injectable);
                    }
                    else if (injectable != null) {
                        ++injectCount;
                        properties[i] = this.constructCreatorProperty(ctxt, beanDesc, name, i, param, injectable);
                    }
                    else {
                        final NameTransformer unwrapper = intr.findUnwrappingNameTransformer(param);
                        if (unwrapper != null) {
                            this._reportUnwrappedCreatorProperty(ctxt, beanDesc, param);
                        }
                        else if (nonAnnotatedParam == null) {
                            nonAnnotatedParam = param;
                        }
                    }
                }
                final int namedCount = explicitNameCount + implicitNameCount;
                if (explicitNameCount <= 0 && injectCount <= 0) {
                    continue;
                }
                if (namedCount + injectCount == argCount2) {
                    creators.addPropertyCreator(factory2, false, properties);
                }
                else if (explicitNameCount == 0 && injectCount + 1 == argCount2) {
                    creators.addDelegatingCreator(factory2, false, properties, 0);
                }
                else {
                    ctxt.reportBadTypeDefinition(beanDesc, "Argument #%d of factory method %s has no property name annotation; must have name when multiple-parameter constructor annotated as Creator", nonAnnotatedParam.getIndex(), factory2);
                }
            }
        }
    }
    
    protected boolean _handleSingleArgumentCreator(final CreatorCollector creators, final AnnotatedWithParams ctor, final boolean isCreator, final boolean isVisible) {
        final Class<?> type = ctor.getRawParameterType(0);
        if (type == String.class || type == BasicDeserializerFactory.CLASS_CHAR_SEQUENCE) {
            if (isCreator || isVisible) {
                creators.addStringCreator(ctor, isCreator);
            }
            return true;
        }
        if (type == Integer.TYPE || type == Integer.class) {
            if (isCreator || isVisible) {
                creators.addIntCreator(ctor, isCreator);
            }
            return true;
        }
        if (type == Long.TYPE || type == Long.class) {
            if (isCreator || isVisible) {
                creators.addLongCreator(ctor, isCreator);
            }
            return true;
        }
        if (type == Double.TYPE || type == Double.class) {
            if (isCreator || isVisible) {
                creators.addDoubleCreator(ctor, isCreator);
            }
            return true;
        }
        if (type == Boolean.TYPE || type == Boolean.class) {
            if (isCreator || isVisible) {
                creators.addBooleanCreator(ctor, isCreator);
            }
            return true;
        }
        if (isCreator) {
            creators.addDelegatingCreator(ctor, isCreator, null, 0);
            return true;
        }
        return false;
    }
    
    protected void _reportUnwrappedCreatorProperty(final DeserializationContext ctxt, final BeanDescription beanDesc, final AnnotatedParameter param) throws JsonMappingException {
        ctxt.reportBadDefinition(beanDesc.getType(), String.format("Cannot define Creator parameter %d as `@JsonUnwrapped`: combination not yet supported", param.getIndex()));
    }
    
    protected SettableBeanProperty constructCreatorProperty(final DeserializationContext ctxt, final BeanDescription beanDesc, final PropertyName name, final int index, final AnnotatedParameter param, final JacksonInject.Value injectable) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        PropertyMetadata metadata;
        if (intr == null) {
            metadata = PropertyMetadata.STD_REQUIRED_OR_OPTIONAL;
        }
        else {
            final Boolean b = intr.hasRequiredMarker(param);
            final String desc = intr.findPropertyDescription(param);
            final Integer idx = intr.findPropertyIndex(param);
            final String def = intr.findPropertyDefaultValue(param);
            metadata = PropertyMetadata.construct(b, desc, idx, def);
        }
        final JavaType type = this.resolveMemberAndTypeAnnotations(ctxt, param, param.getType());
        final BeanProperty.Std property = new BeanProperty.Std(name, type, intr.findWrapperName(param), param, metadata);
        TypeDeserializer typeDeser = type.getTypeHandler();
        if (typeDeser == null) {
            typeDeser = this.findTypeDeserializer(config, type);
        }
        final Object injectableValueId = (injectable == null) ? null : injectable.getId();
        SettableBeanProperty prop = new CreatorProperty(name, type, property.getWrapperName(), typeDeser, beanDesc.getClassAnnotations(), param, index, injectableValueId, metadata);
        JsonDeserializer<?> deser = this.findDeserializerFromAnnotation(ctxt, param);
        if (deser == null) {
            deser = type.getValueHandler();
        }
        if (deser != null) {
            deser = ctxt.handlePrimaryContextualization(deser, prop, type);
            prop = prop.withValueDeserializer(deser);
        }
        return prop;
    }
    
    private PropertyName _findParamName(final AnnotatedParameter param, final AnnotationIntrospector intr) {
        if (param != null && intr != null) {
            final PropertyName name = intr.findNameForDeserialization(param);
            if (name != null) {
                return name;
            }
            final String str = intr.findImplicitPropertyName(param);
            if (str != null && !str.isEmpty()) {
                return PropertyName.construct(str);
            }
        }
        return null;
    }
    
    @Override
    public JsonDeserializer<?> createArrayDeserializer(final DeserializationContext ctxt, final ArrayType type, final BeanDescription beanDesc) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        final JavaType elemType = type.getContentType();
        final JsonDeserializer<Object> contentDeser = elemType.getValueHandler();
        TypeDeserializer elemTypeDeser = elemType.getTypeHandler();
        if (elemTypeDeser == null) {
            elemTypeDeser = this.findTypeDeserializer(config, elemType);
        }
        JsonDeserializer<?> deser = this._findCustomArrayDeserializer(type, config, beanDesc, elemTypeDeser, contentDeser);
        if (deser == null) {
            if (contentDeser == null) {
                final Class<?> raw = elemType.getRawClass();
                if (elemType.isPrimitive()) {
                    return PrimitiveArrayDeserializers.forType(raw);
                }
                if (raw == String.class) {
                    return StringArrayDeserializer.instance;
                }
            }
            deser = new ObjectArrayDeserializer(type, contentDeser, elemTypeDeser);
        }
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyArrayDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }
    
    @Override
    public JsonDeserializer<?> createCollectionDeserializer(final DeserializationContext ctxt, CollectionType type, BeanDescription beanDesc) throws JsonMappingException {
        final JavaType contentType = type.getContentType();
        final JsonDeserializer<Object> contentDeser = contentType.getValueHandler();
        final DeserializationConfig config = ctxt.getConfig();
        TypeDeserializer contentTypeDeser = contentType.getTypeHandler();
        if (contentTypeDeser == null) {
            contentTypeDeser = this.findTypeDeserializer(config, contentType);
        }
        JsonDeserializer<?> deser = this._findCustomCollectionDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
        if (deser == null) {
            final Class<?> collectionClass = type.getRawClass();
            if (contentDeser == null && EnumSet.class.isAssignableFrom(collectionClass)) {
                deser = new EnumSetDeserializer(contentType, null);
            }
        }
        if (deser == null) {
            if (type.isInterface() || type.isAbstract()) {
                final CollectionType implType = this._mapAbstractCollectionType(type, config);
                if (implType == null) {
                    if (type.getTypeHandler() == null) {
                        throw new IllegalArgumentException("Cannot find a deserializer for non-concrete Collection type " + type);
                    }
                    deser = AbstractDeserializer.constructForNonPOJO(beanDesc);
                }
                else {
                    type = implType;
                    beanDesc = config.introspectForCreation(type);
                }
            }
            if (deser == null) {
                final ValueInstantiator inst = this.findValueInstantiator(ctxt, beanDesc);
                if (!inst.canCreateUsingDefault()) {
                    if (type.hasRawClass(ArrayBlockingQueue.class)) {
                        return new ArrayBlockingQueueDeserializer(type, contentDeser, contentTypeDeser, inst);
                    }
                    deser = JavaUtilCollectionsDeserializers.findForCollection(ctxt, type);
                    if (deser != null) {
                        return deser;
                    }
                }
                if (contentType.hasRawClass(String.class)) {
                    deser = new StringCollectionDeserializer(type, contentDeser, inst);
                }
                else {
                    deser = new CollectionDeserializer(type, contentDeser, contentTypeDeser, inst);
                }
            }
        }
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyCollectionDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }
    
    protected CollectionType _mapAbstractCollectionType(final JavaType type, final DeserializationConfig config) {
        Class<?> collectionClass = type.getRawClass();
        collectionClass = BasicDeserializerFactory._collectionFallbacks.get(collectionClass.getName());
        if (collectionClass == null) {
            return null;
        }
        return (CollectionType)config.constructSpecializedType(type, collectionClass);
    }
    
    @Override
    public JsonDeserializer<?> createCollectionLikeDeserializer(final DeserializationContext ctxt, final CollectionLikeType type, final BeanDescription beanDesc) throws JsonMappingException {
        final JavaType contentType = type.getContentType();
        final JsonDeserializer<Object> contentDeser = contentType.getValueHandler();
        final DeserializationConfig config = ctxt.getConfig();
        TypeDeserializer contentTypeDeser = contentType.getTypeHandler();
        if (contentTypeDeser == null) {
            contentTypeDeser = this.findTypeDeserializer(config, contentType);
        }
        JsonDeserializer<?> deser = this._findCustomCollectionLikeDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
        if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyCollectionLikeDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }
    
    @Override
    public JsonDeserializer<?> createMapDeserializer(final DeserializationContext ctxt, MapType type, BeanDescription beanDesc) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        final JavaType keyType = type.getKeyType();
        final JavaType contentType = type.getContentType();
        final JsonDeserializer<Object> contentDeser = contentType.getValueHandler();
        final KeyDeserializer keyDes = keyType.getValueHandler();
        TypeDeserializer contentTypeDeser = contentType.getTypeHandler();
        if (contentTypeDeser == null) {
            contentTypeDeser = this.findTypeDeserializer(config, contentType);
        }
        JsonDeserializer<?> deser = this._findCustomMapDeserializer(type, config, beanDesc, keyDes, contentTypeDeser, contentDeser);
        if (deser == null) {
            Class<?> mapClass = type.getRawClass();
            if (EnumMap.class.isAssignableFrom(mapClass)) {
                ValueInstantiator inst;
                if (mapClass == EnumMap.class) {
                    inst = null;
                }
                else {
                    inst = this.findValueInstantiator(ctxt, beanDesc);
                }
                final Class<?> kt = keyType.getRawClass();
                if (kt == null || !kt.isEnum()) {
                    throw new IllegalArgumentException("Cannot construct EnumMap; generic (key) type not available");
                }
                deser = new EnumMapDeserializer(type, inst, null, contentDeser, contentTypeDeser, null);
            }
            if (deser == null) {
                if (type.isInterface() || type.isAbstract()) {
                    final Class<? extends Map> fallback = BasicDeserializerFactory._mapFallbacks.get(mapClass.getName());
                    if (fallback != null) {
                        mapClass = fallback;
                        type = (MapType)config.constructSpecializedType(type, mapClass);
                        beanDesc = config.introspectForCreation(type);
                    }
                    else {
                        if (type.getTypeHandler() == null) {
                            throw new IllegalArgumentException("Cannot find a deserializer for non-concrete Map type " + type);
                        }
                        deser = AbstractDeserializer.constructForNonPOJO(beanDesc);
                    }
                }
                else {
                    deser = JavaUtilCollectionsDeserializers.findForMap(ctxt, type);
                    if (deser != null) {
                        return deser;
                    }
                }
                if (deser == null) {
                    final ValueInstantiator inst = this.findValueInstantiator(ctxt, beanDesc);
                    final MapDeserializer md = new MapDeserializer(type, inst, keyDes, contentDeser, contentTypeDeser);
                    final JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(Map.class, beanDesc.getClassInfo());
                    final Set<String> ignored = (ignorals == null) ? null : ignorals.findIgnoredForDeserialization();
                    md.setIgnorableProperties(ignored);
                    deser = md;
                }
            }
        }
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyMapDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }
    
    @Override
    public JsonDeserializer<?> createMapLikeDeserializer(final DeserializationContext ctxt, final MapLikeType type, final BeanDescription beanDesc) throws JsonMappingException {
        final JavaType keyType = type.getKeyType();
        final JavaType contentType = type.getContentType();
        final DeserializationConfig config = ctxt.getConfig();
        final JsonDeserializer<Object> contentDeser = contentType.getValueHandler();
        final KeyDeserializer keyDes = keyType.getValueHandler();
        TypeDeserializer contentTypeDeser = contentType.getTypeHandler();
        if (contentTypeDeser == null) {
            contentTypeDeser = this.findTypeDeserializer(config, contentType);
        }
        JsonDeserializer<?> deser = this._findCustomMapLikeDeserializer(type, config, beanDesc, keyDes, contentTypeDeser, contentDeser);
        if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyMapLikeDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }
    
    @Override
    public JsonDeserializer<?> createEnumDeserializer(final DeserializationContext ctxt, final JavaType type, final BeanDescription beanDesc) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        final Class<?> enumClass = type.getRawClass();
        JsonDeserializer<?> deser = this._findCustomEnumDeserializer(enumClass, config, beanDesc);
        if (deser == null) {
            final ValueInstantiator valueInstantiator = this._constructDefaultValueInstantiator(ctxt, beanDesc);
            final SettableBeanProperty[] creatorProps = (SettableBeanProperty[])((valueInstantiator == null) ? null : valueInstantiator.getFromObjectArguments(ctxt.getConfig()));
            for (final AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
                if (this._hasCreatorAnnotation(ctxt, factory)) {
                    if (factory.getParameterCount() == 0) {
                        deser = EnumDeserializer.deserializerForNoArgsCreator(config, enumClass, factory);
                        break;
                    }
                    final Class<?> returnType = factory.getRawReturnType();
                    if (returnType.isAssignableFrom(enumClass)) {
                        deser = EnumDeserializer.deserializerForCreator(config, enumClass, factory, valueInstantiator, creatorProps);
                        break;
                    }
                    continue;
                }
            }
            if (deser == null) {
                deser = new EnumDeserializer(this.constructEnumResolver(enumClass, config, beanDesc.findJsonValueAccessor()), config.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS));
            }
        }
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyEnumDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }
    
    @Override
    public JsonDeserializer<?> createTreeDeserializer(final DeserializationConfig config, final JavaType nodeType, final BeanDescription beanDesc) throws JsonMappingException {
        final Class<? extends JsonNode> nodeClass = (Class<? extends JsonNode>)nodeType.getRawClass();
        final JsonDeserializer<?> custom = this._findCustomTreeNodeDeserializer(nodeClass, config, beanDesc);
        if (custom != null) {
            return custom;
        }
        return JsonNodeDeserializer.getDeserializer(nodeClass);
    }
    
    @Override
    public JsonDeserializer<?> createReferenceDeserializer(final DeserializationContext ctxt, final ReferenceType type, final BeanDescription beanDesc) throws JsonMappingException {
        final JavaType contentType = type.getContentType();
        final JsonDeserializer<Object> contentDeser = contentType.getValueHandler();
        final DeserializationConfig config = ctxt.getConfig();
        TypeDeserializer contentTypeDeser = contentType.getTypeHandler();
        if (contentTypeDeser == null) {
            contentTypeDeser = this.findTypeDeserializer(config, contentType);
        }
        JsonDeserializer<?> deser = this._findCustomReferenceDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
        if (deser == null && type.isTypeOrSubTypeOf(AtomicReference.class)) {
            final Class<?> rawType = type.getRawClass();
            ValueInstantiator inst;
            if (rawType == AtomicReference.class) {
                inst = null;
            }
            else {
                inst = this.findValueInstantiator(ctxt, beanDesc);
            }
            return new AtomicReferenceDeserializer(type, inst, contentTypeDeser, contentDeser);
        }
        if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyReferenceDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }
    
    @Override
    public TypeDeserializer findTypeDeserializer(final DeserializationConfig config, final JavaType baseType) throws JsonMappingException {
        final BeanDescription bean = config.introspectClassAnnotations(baseType.getRawClass());
        final AnnotatedClass ac = bean.getClassInfo();
        final AnnotationIntrospector ai = config.getAnnotationIntrospector();
        TypeResolverBuilder<?> b = ai.findTypeResolver(config, ac, baseType);
        Collection<NamedType> subtypes = null;
        if (b == null) {
            b = config.getDefaultTyper(baseType);
            if (b == null) {
                return null;
            }
        }
        else {
            subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, ac);
        }
        if (b.getDefaultImpl() == null && baseType.isAbstract()) {
            final JavaType defaultType = this.mapAbstractType(config, baseType);
            if (defaultType != null && !defaultType.hasRawClass(baseType.getRawClass())) {
                b = (TypeResolverBuilder<?>)b.defaultImpl(defaultType.getRawClass());
            }
        }
        try {
            return b.buildTypeDeserializer(config, baseType, subtypes);
        }
        catch (IllegalArgumentException e2) {
            final InvalidDefinitionException e = InvalidDefinitionException.from((JsonParser)null, ClassUtil.exceptionMessage(e2), baseType);
            e.initCause(e2);
            throw e;
        }
    }
    
    protected JsonDeserializer<?> findOptionalStdDeserializer(final DeserializationContext ctxt, final JavaType type, final BeanDescription beanDesc) throws JsonMappingException {
        return OptionalHandlerFactory.instance.findDeserializer(type, ctxt.getConfig(), beanDesc);
    }
    
    @Override
    public KeyDeserializer createKeyDeserializer(final DeserializationContext ctxt, final JavaType type) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        KeyDeserializer deser = null;
        if (this._factoryConfig.hasKeyDeserializers()) {
            final BeanDescription beanDesc = config.introspectClassAnnotations(type.getRawClass());
            for (final KeyDeserializers d : this._factoryConfig.keyDeserializers()) {
                deser = d.findKeyDeserializer(type, config, beanDesc);
                if (deser != null) {
                    break;
                }
            }
        }
        if (deser == null) {
            if (type.isEnumType()) {
                deser = this._createEnumKeyDeserializer(ctxt, type);
            }
            else {
                deser = StdKeyDeserializers.findStringBasedKeyDeserializer(config, type);
            }
        }
        if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyKeyDeserializer(config, type, deser);
            }
        }
        return deser;
    }
    
    private KeyDeserializer _createEnumKeyDeserializer(final DeserializationContext ctxt, final JavaType type) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        final Class<?> enumClass = type.getRawClass();
        final BeanDescription beanDesc = config.introspect(type);
        final KeyDeserializer des = this.findKeyDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
        if (des != null) {
            return des;
        }
        final JsonDeserializer<?> custom = this._findCustomEnumDeserializer(enumClass, config, beanDesc);
        if (custom != null) {
            return StdKeyDeserializers.constructDelegatingKeyDeserializer(config, type, custom);
        }
        final JsonDeserializer<?> valueDesForKey = this.findDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
        if (valueDesForKey != null) {
            return StdKeyDeserializers.constructDelegatingKeyDeserializer(config, type, valueDesForKey);
        }
        final EnumResolver enumRes = this.constructEnumResolver(enumClass, config, beanDesc.findJsonValueAccessor());
        for (final AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
            if (this._hasCreatorAnnotation(ctxt, factory)) {
                final int argCount = factory.getParameterCount();
                if (argCount == 1) {
                    final Class<?> returnType = factory.getRawReturnType();
                    if (returnType.isAssignableFrom(enumClass)) {
                        if (factory.getRawParameterType(0) != String.class) {
                            throw new IllegalArgumentException("Parameter #0 type for factory method (" + factory + ") not suitable, must be java.lang.String");
                        }
                        if (config.canOverrideAccessModifiers()) {
                            ClassUtil.checkAndFixAccess(factory.getMember(), ctxt.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
                        }
                        return StdKeyDeserializers.constructEnumKeyDeserializer(enumRes, factory);
                    }
                }
                throw new IllegalArgumentException("Unsuitable method (" + factory + ") decorated with @JsonCreator (for Enum type " + enumClass.getName() + ")");
            }
        }
        return StdKeyDeserializers.constructEnumKeyDeserializer(enumRes);
    }
    
    public TypeDeserializer findPropertyTypeDeserializer(final DeserializationConfig config, final JavaType baseType, final AnnotatedMember annotated) throws JsonMappingException {
        final AnnotationIntrospector ai = config.getAnnotationIntrospector();
        final TypeResolverBuilder<?> b = ai.findPropertyTypeResolver(config, annotated, baseType);
        if (b == null) {
            return this.findTypeDeserializer(config, baseType);
        }
        final Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, annotated, baseType);
        return b.buildTypeDeserializer(config, baseType, subtypes);
    }
    
    public TypeDeserializer findPropertyContentTypeDeserializer(final DeserializationConfig config, final JavaType containerType, final AnnotatedMember propertyEntity) throws JsonMappingException {
        final AnnotationIntrospector ai = config.getAnnotationIntrospector();
        final TypeResolverBuilder<?> b = ai.findPropertyContentTypeResolver(config, propertyEntity, containerType);
        final JavaType contentType = containerType.getContentType();
        if (b == null) {
            return this.findTypeDeserializer(config, contentType);
        }
        final Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, propertyEntity, contentType);
        return b.buildTypeDeserializer(config, contentType, subtypes);
    }
    
    public JsonDeserializer<?> findDefaultDeserializer(final DeserializationContext ctxt, final JavaType type, final BeanDescription beanDesc) throws JsonMappingException {
        final Class<?> rawType = type.getRawClass();
        if (rawType == BasicDeserializerFactory.CLASS_OBJECT) {
            final DeserializationConfig config = ctxt.getConfig();
            JavaType lt;
            JavaType mt;
            if (this._factoryConfig.hasAbstractTypeResolvers()) {
                lt = this._findRemappedType(config, List.class);
                mt = this._findRemappedType(config, Map.class);
            }
            else {
                mt = (lt = null);
            }
            return new UntypedObjectDeserializer(lt, mt);
        }
        if (rawType == BasicDeserializerFactory.CLASS_STRING || rawType == BasicDeserializerFactory.CLASS_CHAR_SEQUENCE) {
            return StringDeserializer.instance;
        }
        if (rawType == BasicDeserializerFactory.CLASS_ITERABLE) {
            final TypeFactory tf = ctxt.getTypeFactory();
            final JavaType[] tps = tf.findTypeParameters(type, BasicDeserializerFactory.CLASS_ITERABLE);
            final JavaType elemType = (tps == null || tps.length != 1) ? TypeFactory.unknownType() : tps[0];
            final CollectionType ct = tf.constructCollectionType(Collection.class, elemType);
            return this.createCollectionDeserializer(ctxt, ct, beanDesc);
        }
        if (rawType == BasicDeserializerFactory.CLASS_MAP_ENTRY) {
            final JavaType kt = type.containedTypeOrUnknown(0);
            final JavaType vt = type.containedTypeOrUnknown(1);
            TypeDeserializer vts = vt.getTypeHandler();
            if (vts == null) {
                vts = this.findTypeDeserializer(ctxt.getConfig(), vt);
            }
            final JsonDeserializer<Object> valueDeser = vt.getValueHandler();
            final KeyDeserializer keyDes = kt.getValueHandler();
            return new MapEntryDeserializer(type, keyDes, valueDeser, vts);
        }
        final String clsName = rawType.getName();
        if (rawType.isPrimitive() || clsName.startsWith("java.")) {
            JsonDeserializer<?> deser = NumberDeserializers.find(rawType, clsName);
            if (deser == null) {
                deser = DateDeserializers.find(rawType, clsName);
            }
            if (deser != null) {
                return deser;
            }
        }
        if (rawType == TokenBuffer.class) {
            return new TokenBufferDeserializer();
        }
        JsonDeserializer<?> deser = this.findOptionalStdDeserializer(ctxt, type, beanDesc);
        if (deser != null) {
            return deser;
        }
        return JdkDeserializers.find(rawType, clsName);
    }
    
    protected JavaType _findRemappedType(final DeserializationConfig config, final Class<?> rawType) throws JsonMappingException {
        final JavaType type = this.mapAbstractType(config, config.constructType(rawType));
        return (type == null || type.hasRawClass(rawType)) ? null : type;
    }
    
    protected JsonDeserializer<?> _findCustomTreeNodeDeserializer(final Class<? extends JsonNode> type, final DeserializationConfig config, final BeanDescription beanDesc) throws JsonMappingException {
        for (final Deserializers d : this._factoryConfig.deserializers()) {
            final JsonDeserializer<?> deser = d.findTreeNodeDeserializer(type, config, beanDesc);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }
    
    protected JsonDeserializer<?> _findCustomReferenceDeserializer(final ReferenceType type, final DeserializationConfig config, final BeanDescription beanDesc, final TypeDeserializer contentTypeDeserializer, final JsonDeserializer<?> contentDeserializer) throws JsonMappingException {
        for (final Deserializers d : this._factoryConfig.deserializers()) {
            final JsonDeserializer<?> deser = d.findReferenceDeserializer(type, config, beanDesc, contentTypeDeserializer, contentDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }
    
    protected JsonDeserializer<Object> _findCustomBeanDeserializer(final JavaType type, final DeserializationConfig config, final BeanDescription beanDesc) throws JsonMappingException {
        for (final Deserializers d : this._factoryConfig.deserializers()) {
            final JsonDeserializer<?> deser = d.findBeanDeserializer(type, config, beanDesc);
            if (deser != null) {
                return (JsonDeserializer<Object>)deser;
            }
        }
        return null;
    }
    
    protected JsonDeserializer<?> _findCustomArrayDeserializer(final ArrayType type, final DeserializationConfig config, final BeanDescription beanDesc, final TypeDeserializer elementTypeDeserializer, final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (final Deserializers d : this._factoryConfig.deserializers()) {
            final JsonDeserializer<?> deser = d.findArrayDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }
    
    protected JsonDeserializer<?> _findCustomCollectionDeserializer(final CollectionType type, final DeserializationConfig config, final BeanDescription beanDesc, final TypeDeserializer elementTypeDeserializer, final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (final Deserializers d : this._factoryConfig.deserializers()) {
            final JsonDeserializer<?> deser = d.findCollectionDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }
    
    protected JsonDeserializer<?> _findCustomCollectionLikeDeserializer(final CollectionLikeType type, final DeserializationConfig config, final BeanDescription beanDesc, final TypeDeserializer elementTypeDeserializer, final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (final Deserializers d : this._factoryConfig.deserializers()) {
            final JsonDeserializer<?> deser = d.findCollectionLikeDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }
    
    protected JsonDeserializer<?> _findCustomEnumDeserializer(final Class<?> type, final DeserializationConfig config, final BeanDescription beanDesc) throws JsonMappingException {
        for (final Deserializers d : this._factoryConfig.deserializers()) {
            final JsonDeserializer<?> deser = d.findEnumDeserializer(type, config, beanDesc);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }
    
    protected JsonDeserializer<?> _findCustomMapDeserializer(final MapType type, final DeserializationConfig config, final BeanDescription beanDesc, final KeyDeserializer keyDeserializer, final TypeDeserializer elementTypeDeserializer, final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (final Deserializers d : this._factoryConfig.deserializers()) {
            final JsonDeserializer<?> deser = d.findMapDeserializer(type, config, beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }
    
    protected JsonDeserializer<?> _findCustomMapLikeDeserializer(final MapLikeType type, final DeserializationConfig config, final BeanDescription beanDesc, final KeyDeserializer keyDeserializer, final TypeDeserializer elementTypeDeserializer, final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (final Deserializers d : this._factoryConfig.deserializers()) {
            final JsonDeserializer<?> deser = d.findMapLikeDeserializer(type, config, beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }
    
    protected JsonDeserializer<Object> findDeserializerFromAnnotation(final DeserializationContext ctxt, final Annotated ann) throws JsonMappingException {
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr != null) {
            final Object deserDef = intr.findDeserializer(ann);
            if (deserDef != null) {
                return ctxt.deserializerInstance(ann, deserDef);
            }
        }
        return null;
    }
    
    protected KeyDeserializer findKeyDeserializerFromAnnotation(final DeserializationContext ctxt, final Annotated ann) throws JsonMappingException {
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr != null) {
            final Object deserDef = intr.findKeyDeserializer(ann);
            if (deserDef != null) {
                return ctxt.keyDeserializerInstance(ann, deserDef);
            }
        }
        return null;
    }
    
    protected JsonDeserializer<Object> findContentDeserializerFromAnnotation(final DeserializationContext ctxt, final Annotated ann) throws JsonMappingException {
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr != null) {
            final Object deserDef = intr.findContentDeserializer(ann);
            if (deserDef != null) {
                return ctxt.deserializerInstance(ann, deserDef);
            }
        }
        return null;
    }
    
    protected JavaType resolveMemberAndTypeAnnotations(final DeserializationContext ctxt, final AnnotatedMember member, JavaType type) throws JsonMappingException {
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr == null) {
            return type;
        }
        if (type.isMapLikeType()) {
            JavaType keyType = type.getKeyType();
            if (keyType != null) {
                final Object kdDef = intr.findKeyDeserializer(member);
                final KeyDeserializer kd = ctxt.keyDeserializerInstance(member, kdDef);
                if (kd != null) {
                    type = ((MapLikeType)type).withKeyValueHandler(kd);
                    keyType = type.getKeyType();
                }
            }
        }
        if (type.hasContentType()) {
            final Object cdDef = intr.findContentDeserializer(member);
            final JsonDeserializer<?> cd = ctxt.deserializerInstance(member, cdDef);
            if (cd != null) {
                type = type.withContentValueHandler(cd);
            }
            final TypeDeserializer contentTypeDeser = this.findPropertyContentTypeDeserializer(ctxt.getConfig(), type, member);
            if (contentTypeDeser != null) {
                type = type.withContentTypeHandler(contentTypeDeser);
            }
        }
        final TypeDeserializer valueTypeDeser = this.findPropertyTypeDeserializer(ctxt.getConfig(), type, member);
        if (valueTypeDeser != null) {
            type = type.withTypeHandler(valueTypeDeser);
        }
        type = intr.refineDeserializationType(ctxt.getConfig(), member, type);
        return type;
    }
    
    protected EnumResolver constructEnumResolver(final Class<?> enumClass, final DeserializationConfig config, final AnnotatedMember jsonValueAccessor) {
        if (jsonValueAccessor != null) {
            if (config.canOverrideAccessModifiers()) {
                ClassUtil.checkAndFixAccess(jsonValueAccessor.getMember(), config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
            }
            return EnumResolver.constructUnsafeUsingMethod(enumClass, jsonValueAccessor, config.getAnnotationIntrospector());
        }
        return EnumResolver.constructUnsafe(enumClass, config.getAnnotationIntrospector());
    }
    
    protected boolean _hasCreatorAnnotation(final DeserializationContext ctxt, final Annotated ann) {
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr != null) {
            final JsonCreator.Mode mode = intr.findCreatorAnnotation(ctxt.getConfig(), ann);
            return mode != null && mode != JsonCreator.Mode.DISABLED;
        }
        return false;
    }
    
    @Deprecated
    protected JavaType modifyTypeByAnnotation(final DeserializationContext ctxt, final Annotated a, final JavaType type) throws JsonMappingException {
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr == null) {
            return type;
        }
        return intr.refineDeserializationType(ctxt.getConfig(), a, type);
    }
    
    @Deprecated
    protected JavaType resolveType(final DeserializationContext ctxt, final BeanDescription beanDesc, final JavaType type, final AnnotatedMember member) throws JsonMappingException {
        return this.resolveMemberAndTypeAnnotations(ctxt, member, type);
    }
    
    @Deprecated
    protected AnnotatedMethod _findJsonValueFor(final DeserializationConfig config, final JavaType enumType) {
        if (enumType == null) {
            return null;
        }
        final BeanDescription beanDesc = config.introspect(enumType);
        return beanDesc.findJsonValueMethod();
    }
    
    static {
        CLASS_OBJECT = Object.class;
        CLASS_STRING = String.class;
        CLASS_CHAR_SEQUENCE = CharSequence.class;
        CLASS_ITERABLE = Iterable.class;
        CLASS_MAP_ENTRY = Map.Entry.class;
        UNWRAPPED_CREATOR_PARAM_NAME = new PropertyName("@JsonUnwrapped");
        (_mapFallbacks = new HashMap<String, Class<? extends Map>>()).put(Map.class.getName(), LinkedHashMap.class);
        BasicDeserializerFactory._mapFallbacks.put(ConcurrentMap.class.getName(), ConcurrentHashMap.class);
        BasicDeserializerFactory._mapFallbacks.put(SortedMap.class.getName(), TreeMap.class);
        BasicDeserializerFactory._mapFallbacks.put(NavigableMap.class.getName(), TreeMap.class);
        BasicDeserializerFactory._mapFallbacks.put(ConcurrentNavigableMap.class.getName(), ConcurrentSkipListMap.class);
        (_collectionFallbacks = new HashMap<String, Class<? extends Collection>>()).put(Collection.class.getName(), ArrayList.class);
        BasicDeserializerFactory._collectionFallbacks.put(List.class.getName(), ArrayList.class);
        BasicDeserializerFactory._collectionFallbacks.put(Set.class.getName(), HashSet.class);
        BasicDeserializerFactory._collectionFallbacks.put(SortedSet.class.getName(), TreeSet.class);
        BasicDeserializerFactory._collectionFallbacks.put(Queue.class.getName(), LinkedList.class);
        BasicDeserializerFactory._collectionFallbacks.put("java.util.Deque", LinkedList.class);
        BasicDeserializerFactory._collectionFallbacks.put("java.util.NavigableSet", TreeSet.class);
    }
}
