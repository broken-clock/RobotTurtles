// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.databind.jsontype.impl.SubTypeValidator;
import com.fasterxml.jackson.databind.deser.impl.SetterlessProperty;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.deser.impl.FieldProperty;
import com.fasterxml.jackson.databind.deser.impl.MethodProperty;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.BeanProperty;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import java.util.List;
import java.util.Collection;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.Collections;
import com.fasterxml.jackson.databind.deser.std.ThrowableDeserializer;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.databind.deser.impl.PropertyBasedObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.lang.reflect.Member;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.deser.impl.ErrorThrowingDeserializer;
import com.fasterxml.jackson.databind.AbstractTypeResolver;
import java.util.Iterator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import java.io.Serializable;

public class BeanDeserializerFactory extends BasicDeserializerFactory implements Serializable
{
    private static final long serialVersionUID = 1L;
    private static final Class<?>[] INIT_CAUSE_PARAMS;
    public static final BeanDeserializerFactory instance;
    
    public BeanDeserializerFactory(final DeserializerFactoryConfig config) {
        super(config);
    }
    
    public DeserializerFactory withConfig(final DeserializerFactoryConfig config) {
        if (this._factoryConfig == config) {
            return this;
        }
        ClassUtil.verifyMustOverride(BeanDeserializerFactory.class, this, "withConfig");
        return new BeanDeserializerFactory(config);
    }
    
    @Override
    public JsonDeserializer<Object> createBeanDeserializer(final DeserializationContext ctxt, final JavaType type, BeanDescription beanDesc) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        final JsonDeserializer<Object> custom = this._findCustomBeanDeserializer(type, config, beanDesc);
        if (custom != null) {
            return custom;
        }
        if (type.isThrowable()) {
            return this.buildThrowableDeserializer(ctxt, type, beanDesc);
        }
        if (type.isAbstract() && !type.isPrimitive() && !type.isEnumType()) {
            final JavaType concreteType = this.materializeAbstractType(ctxt, type, beanDesc);
            if (concreteType != null) {
                beanDesc = config.introspect(concreteType);
                return this.buildBeanDeserializer(ctxt, concreteType, beanDesc);
            }
        }
        final JsonDeserializer<Object> deser = (JsonDeserializer<Object>)this.findStdDeserializer(ctxt, type, beanDesc);
        if (deser != null) {
            return deser;
        }
        if (!this.isPotentialBeanType(type.getRawClass())) {
            return null;
        }
        this._validateSubType(ctxt, type, beanDesc);
        return this.buildBeanDeserializer(ctxt, type, beanDesc);
    }
    
    @Override
    public JsonDeserializer<Object> createBuilderBasedDeserializer(final DeserializationContext ctxt, final JavaType valueType, final BeanDescription beanDesc, final Class<?> builderClass) throws JsonMappingException {
        final JavaType builderType = ctxt.constructType(builderClass);
        final BeanDescription builderDesc = ctxt.getConfig().introspectForBuilder(builderType);
        return this.buildBuilderBasedDeserializer(ctxt, valueType, builderDesc);
    }
    
    protected JsonDeserializer<?> findStdDeserializer(final DeserializationContext ctxt, final JavaType type, final BeanDescription beanDesc) throws JsonMappingException {
        JsonDeserializer<?> deser = this.findDefaultDeserializer(ctxt, type, beanDesc);
        if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyDeserializer(ctxt.getConfig(), beanDesc, deser);
            }
        }
        return deser;
    }
    
    protected JavaType materializeAbstractType(final DeserializationContext ctxt, final JavaType type, final BeanDescription beanDesc) throws JsonMappingException {
        for (final AbstractTypeResolver r : this._factoryConfig.abstractTypeResolvers()) {
            final JavaType concrete = r.resolveAbstractType(ctxt.getConfig(), beanDesc);
            if (concrete != null) {
                return concrete;
            }
        }
        return null;
    }
    
    public JsonDeserializer<Object> buildBeanDeserializer(final DeserializationContext ctxt, final JavaType type, final BeanDescription beanDesc) throws JsonMappingException {
        ValueInstantiator valueInstantiator;
        try {
            valueInstantiator = this.findValueInstantiator(ctxt, beanDesc);
        }
        catch (NoClassDefFoundError error) {
            return new ErrorThrowingDeserializer(error);
        }
        catch (IllegalArgumentException e) {
            throw InvalidDefinitionException.from(ctxt.getParser(), ClassUtil.exceptionMessage(e), beanDesc, null);
        }
        BeanDeserializerBuilder builder = this.constructBeanDeserializerBuilder(ctxt, beanDesc);
        builder.setValueInstantiator(valueInstantiator);
        this.addBeanProps(ctxt, beanDesc, builder);
        this.addObjectIdReader(ctxt, beanDesc, builder);
        this.addBackReferenceProperties(ctxt, beanDesc, builder);
        this.addInjectables(ctxt, beanDesc, builder);
        final DeserializationConfig config = ctxt.getConfig();
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                builder = mod.updateBuilder(config, beanDesc, builder);
            }
        }
        JsonDeserializer<?> deserializer;
        if (type.isAbstract() && !valueInstantiator.canInstantiate()) {
            deserializer = builder.buildAbstract();
        }
        else {
            deserializer = builder.build();
        }
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod2 : this._factoryConfig.deserializerModifiers()) {
                deserializer = mod2.modifyDeserializer(config, beanDesc, deserializer);
            }
        }
        return (JsonDeserializer<Object>)deserializer;
    }
    
    protected JsonDeserializer<Object> buildBuilderBasedDeserializer(final DeserializationContext ctxt, final JavaType valueType, final BeanDescription builderDesc) throws JsonMappingException {
        ValueInstantiator valueInstantiator;
        try {
            valueInstantiator = this.findValueInstantiator(ctxt, builderDesc);
        }
        catch (NoClassDefFoundError error) {
            return new ErrorThrowingDeserializer(error);
        }
        catch (IllegalArgumentException e) {
            throw InvalidDefinitionException.from(ctxt.getParser(), ClassUtil.exceptionMessage(e), builderDesc, null);
        }
        final DeserializationConfig config = ctxt.getConfig();
        BeanDeserializerBuilder builder = this.constructBeanDeserializerBuilder(ctxt, builderDesc);
        builder.setValueInstantiator(valueInstantiator);
        this.addBeanProps(ctxt, builderDesc, builder);
        this.addObjectIdReader(ctxt, builderDesc, builder);
        this.addBackReferenceProperties(ctxt, builderDesc, builder);
        this.addInjectables(ctxt, builderDesc, builder);
        final JsonPOJOBuilder.Value builderConfig = builderDesc.findPOJOBuilderConfig();
        final String buildMethodName = (builderConfig == null) ? "build" : builderConfig.buildMethodName;
        final AnnotatedMethod buildMethod = builderDesc.findMethod(buildMethodName, null);
        if (buildMethod != null && config.canOverrideAccessModifiers()) {
            ClassUtil.checkAndFixAccess(buildMethod.getMember(), config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
        }
        builder.setPOJOBuilder(buildMethod, builderConfig);
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                builder = mod.updateBuilder(config, builderDesc, builder);
            }
        }
        JsonDeserializer<?> deserializer = builder.buildBuilderBased(valueType, buildMethodName);
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod2 : this._factoryConfig.deserializerModifiers()) {
                deserializer = mod2.modifyDeserializer(config, builderDesc, deserializer);
            }
        }
        return (JsonDeserializer<Object>)deserializer;
    }
    
    protected void addObjectIdReader(final DeserializationContext ctxt, final BeanDescription beanDesc, final BeanDeserializerBuilder builder) throws JsonMappingException {
        final ObjectIdInfo objectIdInfo = beanDesc.getObjectIdInfo();
        if (objectIdInfo == null) {
            return;
        }
        final Class<?> implClass = objectIdInfo.getGeneratorType();
        final ObjectIdResolver resolver = ctxt.objectIdResolverInstance(beanDesc.getClassInfo(), objectIdInfo);
        SettableBeanProperty idProp;
        JavaType idType;
        ObjectIdGenerator<?> gen;
        if (implClass == ObjectIdGenerators.PropertyGenerator.class) {
            final PropertyName propName = objectIdInfo.getPropertyName();
            idProp = builder.findProperty(propName);
            if (idProp == null) {
                throw new IllegalArgumentException("Invalid Object Id definition for " + beanDesc.getBeanClass().getName() + ": cannot find property with name '" + propName + "'");
            }
            idType = idProp.getType();
            gen = new PropertyBasedObjectIdGenerator(objectIdInfo.getScope());
        }
        else {
            final JavaType type = ctxt.constructType(implClass);
            idType = ctxt.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
            idProp = null;
            gen = ctxt.objectIdGeneratorInstance(beanDesc.getClassInfo(), objectIdInfo);
        }
        final JsonDeserializer<?> deser = ctxt.findRootValueDeserializer(idType);
        builder.setObjectIdReader(ObjectIdReader.construct(idType, objectIdInfo.getPropertyName(), gen, deser, idProp, resolver));
    }
    
    public JsonDeserializer<Object> buildThrowableDeserializer(final DeserializationContext ctxt, final JavaType type, final BeanDescription beanDesc) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        BeanDeserializerBuilder builder = this.constructBeanDeserializerBuilder(ctxt, beanDesc);
        builder.setValueInstantiator(this.findValueInstantiator(ctxt, beanDesc));
        this.addBeanProps(ctxt, beanDesc, builder);
        final AnnotatedMethod am = beanDesc.findMethod("initCause", BeanDeserializerFactory.INIT_CAUSE_PARAMS);
        if (am != null) {
            final SimpleBeanPropertyDefinition propDef = SimpleBeanPropertyDefinition.construct(ctxt.getConfig(), am, new PropertyName("cause"));
            final SettableBeanProperty prop = this.constructSettableProperty(ctxt, beanDesc, propDef, am.getParameterType(0));
            if (prop != null) {
                builder.addOrReplaceProperty(prop, true);
            }
        }
        builder.addIgnorable("localizedMessage");
        builder.addIgnorable("suppressed");
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                builder = mod.updateBuilder(config, beanDesc, builder);
            }
        }
        JsonDeserializer<?> deserializer = builder.build();
        if (deserializer instanceof BeanDeserializer) {
            deserializer = new ThrowableDeserializer((BeanDeserializer)deserializer);
        }
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod2 : this._factoryConfig.deserializerModifiers()) {
                deserializer = mod2.modifyDeserializer(config, beanDesc, deserializer);
            }
        }
        return (JsonDeserializer<Object>)deserializer;
    }
    
    protected BeanDeserializerBuilder constructBeanDeserializerBuilder(final DeserializationContext ctxt, final BeanDescription beanDesc) {
        return new BeanDeserializerBuilder(beanDesc, ctxt);
    }
    
    protected void addBeanProps(final DeserializationContext ctxt, final BeanDescription beanDesc, final BeanDeserializerBuilder builder) throws JsonMappingException {
        final boolean isConcrete = !beanDesc.getType().isAbstract();
        final SettableBeanProperty[] creatorProps = (SettableBeanProperty[])(isConcrete ? builder.getValueInstantiator().getFromObjectArguments(ctxt.getConfig()) : null);
        final boolean hasCreatorProps = creatorProps != null;
        final JsonIgnoreProperties.Value ignorals = ctxt.getConfig().getDefaultPropertyIgnorals(beanDesc.getBeanClass(), beanDesc.getClassInfo());
        Set<String> ignored;
        if (ignorals != null) {
            final boolean ignoreAny = ignorals.getIgnoreUnknown();
            builder.setIgnoreUnknownProperties(ignoreAny);
            ignored = ignorals.findIgnoredForDeserialization();
            for (final String propName : ignored) {
                builder.addIgnorable(propName);
            }
        }
        else {
            ignored = Collections.emptySet();
        }
        final AnnotatedMember anySetter = beanDesc.findAnySetterAccessor();
        if (anySetter != null) {
            builder.setAnySetter(this.constructAnySetter(ctxt, beanDesc, anySetter));
        }
        else {
            final Collection<String> ignored2 = beanDesc.getIgnoredPropertyNames();
            if (ignored2 != null) {
                for (final String propName2 : ignored2) {
                    builder.addIgnorable(propName2);
                }
            }
        }
        final boolean useGettersAsSetters = ctxt.isEnabled(MapperFeature.USE_GETTERS_AS_SETTERS) && ctxt.isEnabled(MapperFeature.AUTO_DETECT_GETTERS);
        List<BeanPropertyDefinition> propDefs = this.filterBeanProps(ctxt, beanDesc, builder, beanDesc.findProperties(), ignored);
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                propDefs = mod.updateProperties(ctxt.getConfig(), beanDesc, propDefs);
            }
        }
        for (final BeanPropertyDefinition propDef : propDefs) {
            SettableBeanProperty prop = null;
            if (propDef.hasSetter()) {
                final AnnotatedMethod setter = propDef.getSetter();
                final JavaType propertyType = setter.getParameterType(0);
                prop = this.constructSettableProperty(ctxt, beanDesc, propDef, propertyType);
            }
            else if (propDef.hasField()) {
                final AnnotatedField field = propDef.getField();
                final JavaType propertyType = field.getType();
                prop = this.constructSettableProperty(ctxt, beanDesc, propDef, propertyType);
            }
            else {
                final AnnotatedMethod getter = propDef.getGetter();
                if (getter != null) {
                    if (useGettersAsSetters && this._isSetterlessType(getter.getRawType())) {
                        if (!builder.hasIgnorable(propDef.getName())) {
                            prop = this.constructSetterlessProperty(ctxt, beanDesc, propDef);
                        }
                    }
                    else if (!propDef.hasConstructorParameter()) {
                        final PropertyMetadata md = propDef.getMetadata();
                        if (md.getMergeInfo() != null) {
                            prop = this.constructSetterlessProperty(ctxt, beanDesc, propDef);
                        }
                    }
                }
            }
            if (hasCreatorProps && propDef.hasConstructorParameter()) {
                final String name = propDef.getName();
                CreatorProperty cprop = null;
                if (creatorProps != null) {
                    for (final SettableBeanProperty cp : creatorProps) {
                        if (name.equals(cp.getName()) && cp instanceof CreatorProperty) {
                            cprop = (CreatorProperty)cp;
                            break;
                        }
                    }
                }
                if (cprop == null) {
                    final List<String> n = new ArrayList<String>();
                    for (final SettableBeanProperty cp2 : creatorProps) {
                        n.add(cp2.getName());
                    }
                    ctxt.reportBadPropertyDefinition(beanDesc, propDef, "Could not find creator property with name '%s' (known Creator properties: %s)", name, n);
                }
                else {
                    if (prop != null) {
                        cprop.setFallbackSetter(prop);
                    }
                    Class<?>[] views = propDef.findViews();
                    if (views == null) {
                        views = beanDesc.findDefaultViews();
                    }
                    cprop.setViews(views);
                    builder.addCreatorProperty(cprop);
                }
            }
            else {
                if (prop == null) {
                    continue;
                }
                Class<?>[] views2 = propDef.findViews();
                if (views2 == null) {
                    views2 = beanDesc.findDefaultViews();
                }
                prop.setViews(views2);
                builder.addProperty(prop);
            }
        }
    }
    
    private boolean _isSetterlessType(final Class<?> rawType) {
        return Collection.class.isAssignableFrom(rawType) || Map.class.isAssignableFrom(rawType);
    }
    
    protected List<BeanPropertyDefinition> filterBeanProps(final DeserializationContext ctxt, final BeanDescription beanDesc, final BeanDeserializerBuilder builder, final List<BeanPropertyDefinition> propDefsIn, final Set<String> ignored) throws JsonMappingException {
        final ArrayList<BeanPropertyDefinition> result = new ArrayList<BeanPropertyDefinition>(Math.max(4, propDefsIn.size()));
        final HashMap<Class<?>, Boolean> ignoredTypes = new HashMap<Class<?>, Boolean>();
        for (final BeanPropertyDefinition property : propDefsIn) {
            final String name = property.getName();
            if (ignored.contains(name)) {
                continue;
            }
            if (!property.hasConstructorParameter()) {
                final Class<?> rawPropertyType = property.getRawPrimaryType();
                if (rawPropertyType != null && this.isIgnorableType(ctxt.getConfig(), property, rawPropertyType, ignoredTypes)) {
                    builder.addIgnorable(name);
                    continue;
                }
            }
            result.add(property);
        }
        return result;
    }
    
    protected void addBackReferenceProperties(final DeserializationContext ctxt, final BeanDescription beanDesc, final BeanDeserializerBuilder builder) throws JsonMappingException {
        final List<BeanPropertyDefinition> refProps = beanDesc.findBackReferences();
        if (refProps != null) {
            for (final BeanPropertyDefinition refProp : refProps) {
                final String refName = refProp.findReferenceName();
                builder.addBackReferenceProperty(refName, this.constructSettableProperty(ctxt, beanDesc, refProp, refProp.getPrimaryType()));
            }
        }
    }
    
    @Deprecated
    protected void addReferenceProperties(final DeserializationContext ctxt, final BeanDescription beanDesc, final BeanDeserializerBuilder builder) throws JsonMappingException {
        this.addBackReferenceProperties(ctxt, beanDesc, builder);
    }
    
    protected void addInjectables(final DeserializationContext ctxt, final BeanDescription beanDesc, final BeanDeserializerBuilder builder) throws JsonMappingException {
        final Map<Object, AnnotatedMember> raw = beanDesc.findInjectables();
        if (raw != null) {
            for (final Map.Entry<Object, AnnotatedMember> entry : raw.entrySet()) {
                final AnnotatedMember m = entry.getValue();
                builder.addInjectable(PropertyName.construct(m.getName()), m.getType(), beanDesc.getClassAnnotations(), m, entry.getKey());
            }
        }
    }
    
    protected SettableAnyProperty constructAnySetter(final DeserializationContext ctxt, final BeanDescription beanDesc, final AnnotatedMember mutator) throws JsonMappingException {
        JavaType keyType;
        JavaType valueType;
        BeanProperty prop;
        if (mutator instanceof AnnotatedMethod) {
            final AnnotatedMethod am = (AnnotatedMethod)mutator;
            keyType = am.getParameterType(0);
            valueType = am.getParameterType(1);
            valueType = this.resolveMemberAndTypeAnnotations(ctxt, mutator, valueType);
            prop = new BeanProperty.Std(PropertyName.construct(mutator.getName()), valueType, null, mutator, PropertyMetadata.STD_OPTIONAL);
        }
        else {
            if (!(mutator instanceof AnnotatedField)) {
                return ctxt.reportBadDefinition(beanDesc.getType(), String.format("Unrecognized mutator type for any setter: %s", mutator.getClass()));
            }
            final AnnotatedField af = (AnnotatedField)mutator;
            JavaType mapType = af.getType();
            mapType = this.resolveMemberAndTypeAnnotations(ctxt, mutator, mapType);
            keyType = mapType.getKeyType();
            valueType = mapType.getContentType();
            prop = new BeanProperty.Std(PropertyName.construct(mutator.getName()), mapType, null, mutator, PropertyMetadata.STD_OPTIONAL);
        }
        KeyDeserializer keyDeser = this.findKeyDeserializerFromAnnotation(ctxt, mutator);
        if (keyDeser == null) {
            keyDeser = keyType.getValueHandler();
        }
        if (keyDeser == null) {
            keyDeser = ctxt.findKeyDeserializer(keyType, prop);
        }
        else if (keyDeser instanceof ContextualKeyDeserializer) {
            keyDeser = ((ContextualKeyDeserializer)keyDeser).createContextual(ctxt, prop);
        }
        JsonDeserializer<Object> deser = this.findContentDeserializerFromAnnotation(ctxt, mutator);
        if (deser == null) {
            deser = valueType.getValueHandler();
        }
        if (deser != null) {
            deser = (JsonDeserializer<Object>)ctxt.handlePrimaryContextualization(deser, prop, valueType);
        }
        final TypeDeserializer typeDeser = valueType.getTypeHandler();
        return new SettableAnyProperty(prop, mutator, valueType, keyDeser, deser, typeDeser);
    }
    
    protected SettableBeanProperty constructSettableProperty(final DeserializationContext ctxt, final BeanDescription beanDesc, final BeanPropertyDefinition propDef, final JavaType propType0) throws JsonMappingException {
        final AnnotatedMember mutator = propDef.getNonConstructorMutator();
        if (mutator == null) {
            ctxt.reportBadPropertyDefinition(beanDesc, propDef, "No non-constructor mutator available", new Object[0]);
        }
        final JavaType type = this.resolveMemberAndTypeAnnotations(ctxt, mutator, propType0);
        final TypeDeserializer typeDeser = type.getTypeHandler();
        SettableBeanProperty prop;
        if (mutator instanceof AnnotatedMethod) {
            prop = new MethodProperty(propDef, type, typeDeser, beanDesc.getClassAnnotations(), (AnnotatedMethod)mutator);
        }
        else {
            prop = new FieldProperty(propDef, type, typeDeser, beanDesc.getClassAnnotations(), (AnnotatedField)mutator);
        }
        JsonDeserializer<?> deser = this.findDeserializerFromAnnotation(ctxt, mutator);
        if (deser == null) {
            deser = type.getValueHandler();
        }
        if (deser != null) {
            deser = ctxt.handlePrimaryContextualization(deser, prop, type);
            prop = prop.withValueDeserializer(deser);
        }
        final AnnotationIntrospector.ReferenceProperty ref = propDef.findReferenceType();
        if (ref != null && ref.isManagedReference()) {
            prop.setManagedReferenceName(ref.getName());
        }
        final ObjectIdInfo objectIdInfo = propDef.findObjectIdInfo();
        if (objectIdInfo != null) {
            prop.setObjectIdInfo(objectIdInfo);
        }
        return prop;
    }
    
    protected SettableBeanProperty constructSetterlessProperty(final DeserializationContext ctxt, final BeanDescription beanDesc, final BeanPropertyDefinition propDef) throws JsonMappingException {
        final AnnotatedMethod getter = propDef.getGetter();
        final JavaType type = this.resolveMemberAndTypeAnnotations(ctxt, getter, getter.getType());
        final TypeDeserializer typeDeser = type.getTypeHandler();
        SettableBeanProperty prop = new SetterlessProperty(propDef, type, typeDeser, beanDesc.getClassAnnotations(), getter);
        JsonDeserializer<?> deser = this.findDeserializerFromAnnotation(ctxt, getter);
        if (deser == null) {
            deser = type.getValueHandler();
        }
        if (deser != null) {
            deser = ctxt.handlePrimaryContextualization(deser, prop, type);
            prop = prop.withValueDeserializer(deser);
        }
        return prop;
    }
    
    protected boolean isPotentialBeanType(final Class<?> type) {
        String typeStr = ClassUtil.canBeABeanType(type);
        if (typeStr != null) {
            throw new IllegalArgumentException("Cannot deserialize Class " + type.getName() + " (of type " + typeStr + ") as a Bean");
        }
        if (ClassUtil.isProxyType(type)) {
            throw new IllegalArgumentException("Cannot deserialize Proxy class " + type.getName() + " as a Bean");
        }
        typeStr = ClassUtil.isLocalType(type, true);
        if (typeStr != null) {
            throw new IllegalArgumentException("Cannot deserialize Class " + type.getName() + " (of type " + typeStr + ") as a Bean");
        }
        return true;
    }
    
    protected boolean isIgnorableType(final DeserializationConfig config, final BeanPropertyDefinition propDef, final Class<?> type, final Map<Class<?>, Boolean> ignoredTypes) {
        Boolean status = ignoredTypes.get(type);
        if (status != null) {
            return status;
        }
        if (type == String.class || type.isPrimitive()) {
            status = Boolean.FALSE;
        }
        else {
            status = config.getConfigOverride(type).getIsIgnoredType();
            if (status == null) {
                final BeanDescription desc = config.introspectClassAnnotations(type);
                status = config.getAnnotationIntrospector().isIgnorableType(desc.getClassInfo());
                if (status == null) {
                    status = Boolean.FALSE;
                }
            }
        }
        ignoredTypes.put(type, status);
        return status;
    }
    
    protected void _validateSubType(final DeserializationContext ctxt, final JavaType type, final BeanDescription beanDesc) throws JsonMappingException {
        SubTypeValidator.instance().validateSubType(ctxt, type, beanDesc);
    }
    
    static {
        INIT_CAUSE_PARAMS = new Class[] { Throwable.class };
        instance = new BeanDeserializerFactory(new DeserializerFactoryConfig());
    }
}
