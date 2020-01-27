// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.lang.reflect.InvocationTargetException;
import com.fasterxml.jackson.databind.exc.IgnoredPropertyException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonParser;
import java.util.Collections;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.DeserializationConfig;
import java.io.IOException;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.deser.impl.MergingSettableBeanProperty;
import com.fasterxml.jackson.databind.deser.impl.SetterlessProperty;
import java.lang.reflect.Constructor;
import com.fasterxml.jackson.databind.deser.impl.InnerClassProperty;
import java.lang.reflect.Member;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReferenceProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import java.util.HashSet;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.databind.deser.impl.PropertyBasedObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.deser.impl.TypeWrappedDeserializer;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.util.Iterator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.deser.impl.ManagedReferenceProperty;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.util.Collection;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdValueProperty;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.util.List;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler;
import com.fasterxml.jackson.databind.deser.impl.UnwrappedPropertyHandler;
import com.fasterxml.jackson.databind.type.ClassKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.fasterxml.jackson.databind.deser.impl.ValueInjector;
import com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
import com.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.PropertyName;
import java.io.Serializable;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public abstract class BeanDeserializerBase extends StdDeserializer<Object> implements ContextualDeserializer, ResolvableDeserializer, ValueInstantiator.Gettable, Serializable
{
    private static final long serialVersionUID = 1L;
    protected static final PropertyName TEMP_PROPERTY_NAME;
    protected final JavaType _beanType;
    protected final JsonFormat.Shape _serializationShape;
    protected final ValueInstantiator _valueInstantiator;
    protected JsonDeserializer<Object> _delegateDeserializer;
    protected JsonDeserializer<Object> _arrayDelegateDeserializer;
    protected PropertyBasedCreator _propertyBasedCreator;
    protected boolean _nonStandardCreation;
    protected boolean _vanillaProcessing;
    protected final BeanPropertyMap _beanProperties;
    protected final ValueInjector[] _injectables;
    protected SettableAnyProperty _anySetter;
    protected final Set<String> _ignorableProps;
    protected final boolean _ignoreAllUnknown;
    protected final boolean _needViewProcesing;
    protected final Map<String, SettableBeanProperty> _backRefs;
    protected transient HashMap<ClassKey, JsonDeserializer<Object>> _subDeserializers;
    protected UnwrappedPropertyHandler _unwrappedPropertyHandler;
    protected ExternalTypeHandler _externalTypeIdHandler;
    protected final ObjectIdReader _objectIdReader;
    
    protected BeanDeserializerBase(final BeanDeserializerBuilder builder, final BeanDescription beanDesc, final BeanPropertyMap properties, final Map<String, SettableBeanProperty> backRefs, final Set<String> ignorableProps, final boolean ignoreAllUnknown, final boolean hasViews) {
        super(beanDesc.getType());
        this._beanType = beanDesc.getType();
        this._valueInstantiator = builder.getValueInstantiator();
        this._beanProperties = properties;
        this._backRefs = backRefs;
        this._ignorableProps = ignorableProps;
        this._ignoreAllUnknown = ignoreAllUnknown;
        this._anySetter = builder.getAnySetter();
        final List<ValueInjector> injectables = builder.getInjectables();
        this._injectables = (ValueInjector[])((injectables == null || injectables.isEmpty()) ? null : ((ValueInjector[])injectables.toArray(new ValueInjector[injectables.size()])));
        this._objectIdReader = builder.getObjectIdReader();
        this._nonStandardCreation = (this._unwrappedPropertyHandler != null || this._valueInstantiator.canCreateUsingDelegate() || this._valueInstantiator.canCreateUsingArrayDelegate() || this._valueInstantiator.canCreateFromObjectWith() || !this._valueInstantiator.canCreateUsingDefault());
        final JsonFormat.Value format = beanDesc.findExpectedFormat(null);
        this._serializationShape = ((format == null) ? null : format.getShape());
        this._needViewProcesing = hasViews;
        this._vanillaProcessing = (!this._nonStandardCreation && this._injectables == null && !this._needViewProcesing && this._objectIdReader == null);
    }
    
    protected BeanDeserializerBase(final BeanDeserializerBase src) {
        this(src, src._ignoreAllUnknown);
    }
    
    protected BeanDeserializerBase(final BeanDeserializerBase src, final boolean ignoreAllUnknown) {
        super(src._beanType);
        this._beanType = src._beanType;
        this._valueInstantiator = src._valueInstantiator;
        this._delegateDeserializer = src._delegateDeserializer;
        this._propertyBasedCreator = src._propertyBasedCreator;
        this._beanProperties = src._beanProperties;
        this._backRefs = src._backRefs;
        this._ignorableProps = src._ignorableProps;
        this._ignoreAllUnknown = ignoreAllUnknown;
        this._anySetter = src._anySetter;
        this._injectables = src._injectables;
        this._objectIdReader = src._objectIdReader;
        this._nonStandardCreation = src._nonStandardCreation;
        this._unwrappedPropertyHandler = src._unwrappedPropertyHandler;
        this._needViewProcesing = src._needViewProcesing;
        this._serializationShape = src._serializationShape;
        this._vanillaProcessing = src._vanillaProcessing;
    }
    
    protected BeanDeserializerBase(final BeanDeserializerBase src, final NameTransformer unwrapper) {
        super(src._beanType);
        this._beanType = src._beanType;
        this._valueInstantiator = src._valueInstantiator;
        this._delegateDeserializer = src._delegateDeserializer;
        this._propertyBasedCreator = src._propertyBasedCreator;
        this._backRefs = src._backRefs;
        this._ignorableProps = src._ignorableProps;
        this._ignoreAllUnknown = (unwrapper != null || src._ignoreAllUnknown);
        this._anySetter = src._anySetter;
        this._injectables = src._injectables;
        this._objectIdReader = src._objectIdReader;
        this._nonStandardCreation = src._nonStandardCreation;
        UnwrappedPropertyHandler uph = src._unwrappedPropertyHandler;
        if (unwrapper != null) {
            if (uph != null) {
                uph = uph.renameAll(unwrapper);
            }
            this._beanProperties = src._beanProperties.renameAll(unwrapper);
        }
        else {
            this._beanProperties = src._beanProperties;
        }
        this._unwrappedPropertyHandler = uph;
        this._needViewProcesing = src._needViewProcesing;
        this._serializationShape = src._serializationShape;
        this._vanillaProcessing = false;
    }
    
    public BeanDeserializerBase(final BeanDeserializerBase src, final ObjectIdReader oir) {
        super(src._beanType);
        this._beanType = src._beanType;
        this._valueInstantiator = src._valueInstantiator;
        this._delegateDeserializer = src._delegateDeserializer;
        this._propertyBasedCreator = src._propertyBasedCreator;
        this._backRefs = src._backRefs;
        this._ignorableProps = src._ignorableProps;
        this._ignoreAllUnknown = src._ignoreAllUnknown;
        this._anySetter = src._anySetter;
        this._injectables = src._injectables;
        this._nonStandardCreation = src._nonStandardCreation;
        this._unwrappedPropertyHandler = src._unwrappedPropertyHandler;
        this._needViewProcesing = src._needViewProcesing;
        this._serializationShape = src._serializationShape;
        this._objectIdReader = oir;
        if (oir == null) {
            this._beanProperties = src._beanProperties;
            this._vanillaProcessing = src._vanillaProcessing;
        }
        else {
            final ObjectIdValueProperty idProp = new ObjectIdValueProperty(oir, PropertyMetadata.STD_REQUIRED);
            this._beanProperties = src._beanProperties.withProperty(idProp);
            this._vanillaProcessing = false;
        }
    }
    
    public BeanDeserializerBase(final BeanDeserializerBase src, final Set<String> ignorableProps) {
        super(src._beanType);
        this._beanType = src._beanType;
        this._valueInstantiator = src._valueInstantiator;
        this._delegateDeserializer = src._delegateDeserializer;
        this._propertyBasedCreator = src._propertyBasedCreator;
        this._backRefs = src._backRefs;
        this._ignorableProps = ignorableProps;
        this._ignoreAllUnknown = src._ignoreAllUnknown;
        this._anySetter = src._anySetter;
        this._injectables = src._injectables;
        this._nonStandardCreation = src._nonStandardCreation;
        this._unwrappedPropertyHandler = src._unwrappedPropertyHandler;
        this._needViewProcesing = src._needViewProcesing;
        this._serializationShape = src._serializationShape;
        this._vanillaProcessing = src._vanillaProcessing;
        this._objectIdReader = src._objectIdReader;
        this._beanProperties = src._beanProperties.withoutProperties(ignorableProps);
    }
    
    protected BeanDeserializerBase(final BeanDeserializerBase src, final BeanPropertyMap beanProps) {
        super(src._beanType);
        this._beanType = src._beanType;
        this._valueInstantiator = src._valueInstantiator;
        this._delegateDeserializer = src._delegateDeserializer;
        this._propertyBasedCreator = src._propertyBasedCreator;
        this._beanProperties = beanProps;
        this._backRefs = src._backRefs;
        this._ignorableProps = src._ignorableProps;
        this._ignoreAllUnknown = src._ignoreAllUnknown;
        this._anySetter = src._anySetter;
        this._injectables = src._injectables;
        this._objectIdReader = src._objectIdReader;
        this._nonStandardCreation = src._nonStandardCreation;
        this._unwrappedPropertyHandler = src._unwrappedPropertyHandler;
        this._needViewProcesing = src._needViewProcesing;
        this._serializationShape = src._serializationShape;
        this._vanillaProcessing = src._vanillaProcessing;
    }
    
    @Override
    public abstract JsonDeserializer<Object> unwrappingDeserializer(final NameTransformer p0);
    
    public abstract BeanDeserializerBase withObjectIdReader(final ObjectIdReader p0);
    
    public abstract BeanDeserializerBase withIgnorableProperties(final Set<String> p0);
    
    public BeanDeserializerBase withBeanProperties(final BeanPropertyMap props) {
        throw new UnsupportedOperationException("Class " + this.getClass().getName() + " does not override `withBeanProperties()`, needs to");
    }
    
    protected abstract BeanDeserializerBase asArrayDeserializer();
    
    @Override
    public void resolve(final DeserializationContext ctxt) throws JsonMappingException {
        ExternalTypeHandler.Builder extTypes = null;
        SettableBeanProperty[] creatorProps;
        if (this._valueInstantiator.canCreateFromObjectWith()) {
            creatorProps = this._valueInstantiator.getFromObjectArguments(ctxt.getConfig());
            if (this._ignorableProps != null) {
                for (int i = 0, end = creatorProps.length; i < end; ++i) {
                    final SettableBeanProperty prop = creatorProps[i];
                    if (this._ignorableProps.contains(prop.getName())) {
                        creatorProps[i].markAsIgnorable();
                    }
                }
            }
        }
        else {
            creatorProps = null;
        }
        UnwrappedPropertyHandler unwrapped = null;
        final Iterator<SettableBeanProperty> iterator = this._beanProperties.iterator();
        while (iterator.hasNext()) {
            final SettableBeanProperty prop = iterator.next();
            if (!prop.hasValueDeserializer()) {
                JsonDeserializer<?> deser = this.findConvertingDeserializer(ctxt, prop);
                if (deser == null) {
                    deser = ctxt.findNonContextualValueDeserializer(prop.getType());
                }
                final SettableBeanProperty newProp = prop.withValueDeserializer(deser);
                this._replaceProperty(this._beanProperties, creatorProps, prop, newProp);
            }
        }
        for (SettableBeanProperty prop2 : this._beanProperties) {
            final SettableBeanProperty origProp = prop2;
            JsonDeserializer<?> deser2 = prop2.getValueDeserializer();
            deser2 = ctxt.handlePrimaryContextualization(deser2, prop2, prop2.getType());
            prop2 = prop2.withValueDeserializer(deser2);
            prop2 = this._resolveManagedReferenceProperty(ctxt, prop2);
            if (!(prop2 instanceof ManagedReferenceProperty)) {
                prop2 = this._resolvedObjectIdProperty(ctxt, prop2);
            }
            final NameTransformer xform = this._findPropertyUnwrapper(ctxt, prop2);
            if (xform != null) {
                final JsonDeserializer<Object> orig = prop2.getValueDeserializer();
                final JsonDeserializer<Object> unwrapping = orig.unwrappingDeserializer(xform);
                if (unwrapping != orig && unwrapping != null) {
                    prop2 = prop2.withValueDeserializer(unwrapping);
                    if (unwrapped == null) {
                        unwrapped = new UnwrappedPropertyHandler();
                    }
                    unwrapped.addProperty(prop2);
                    this._beanProperties.remove(prop2);
                    continue;
                }
            }
            final PropertyMetadata md = prop2.getMetadata();
            prop2 = this._resolveMergeAndNullSettings(ctxt, prop2, md);
            prop2 = this._resolveInnerClassValuedProperty(ctxt, prop2);
            if (prop2 != origProp) {
                this._replaceProperty(this._beanProperties, creatorProps, origProp, prop2);
            }
            if (prop2.hasValueTypeDeserializer()) {
                final TypeDeserializer typeDeser = prop2.getValueTypeDeserializer();
                if (typeDeser.getTypeInclusion() != JsonTypeInfo.As.EXTERNAL_PROPERTY) {
                    continue;
                }
                if (extTypes == null) {
                    extTypes = ExternalTypeHandler.builder(this._beanType);
                }
                extTypes.addExternal(prop2, typeDeser);
                this._beanProperties.remove(prop2);
            }
        }
        if (this._anySetter != null && !this._anySetter.hasValueDeserializer()) {
            this._anySetter = this._anySetter.withValueDeserializer(this.findDeserializer(ctxt, this._anySetter.getType(), this._anySetter.getProperty()));
        }
        if (this._valueInstantiator.canCreateUsingDelegate()) {
            final JavaType delegateType = this._valueInstantiator.getDelegateType(ctxt.getConfig());
            if (delegateType == null) {
                ctxt.reportBadDefinition(this._beanType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingDelegate()', but null for 'getDelegateType()'", this._beanType, this._valueInstantiator.getClass().getName()));
            }
            this._delegateDeserializer = this._findDelegateDeserializer(ctxt, delegateType, this._valueInstantiator.getDelegateCreator());
        }
        if (this._valueInstantiator.canCreateUsingArrayDelegate()) {
            final JavaType delegateType = this._valueInstantiator.getArrayDelegateType(ctxt.getConfig());
            if (delegateType == null) {
                ctxt.reportBadDefinition(this._beanType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingArrayDelegate()', but null for 'getArrayDelegateType()'", this._beanType, this._valueInstantiator.getClass().getName()));
            }
            this._arrayDelegateDeserializer = this._findDelegateDeserializer(ctxt, delegateType, this._valueInstantiator.getArrayDelegateCreator());
        }
        if (creatorProps != null) {
            this._propertyBasedCreator = PropertyBasedCreator.construct(ctxt, this._valueInstantiator, creatorProps, this._beanProperties);
        }
        if (extTypes != null) {
            this._externalTypeIdHandler = extTypes.build(this._beanProperties);
            this._nonStandardCreation = true;
        }
        if ((this._unwrappedPropertyHandler = unwrapped) != null) {
            this._nonStandardCreation = true;
        }
        this._vanillaProcessing = (this._vanillaProcessing && !this._nonStandardCreation);
    }
    
    protected void _replaceProperty(final BeanPropertyMap props, final SettableBeanProperty[] creatorProps, final SettableBeanProperty origProp, final SettableBeanProperty newProp) {
        props.replace(origProp, newProp);
        if (creatorProps != null) {
            for (int i = 0, len = creatorProps.length; i < len; ++i) {
                if (creatorProps[i] == origProp) {
                    creatorProps[i] = newProp;
                    return;
                }
            }
        }
    }
    
    private JsonDeserializer<Object> _findDelegateDeserializer(final DeserializationContext ctxt, final JavaType delegateType, final AnnotatedWithParams delegateCreator) throws JsonMappingException {
        final BeanProperty.Std property = new BeanProperty.Std(BeanDeserializerBase.TEMP_PROPERTY_NAME, delegateType, null, delegateCreator, PropertyMetadata.STD_OPTIONAL);
        TypeDeserializer td = delegateType.getTypeHandler();
        if (td == null) {
            td = ctxt.getConfig().findTypeDeserializer(delegateType);
        }
        JsonDeserializer<Object> dd = delegateType.getValueHandler();
        if (dd == null) {
            dd = this.findDeserializer(ctxt, delegateType, property);
        }
        else {
            dd = (JsonDeserializer<Object>)ctxt.handleSecondaryContextualization(dd, property, delegateType);
        }
        if (td != null) {
            td = td.forProperty(property);
            return new TypeWrappedDeserializer(td, dd);
        }
        return dd;
    }
    
    protected JsonDeserializer<Object> findConvertingDeserializer(final DeserializationContext ctxt, final SettableBeanProperty prop) throws JsonMappingException {
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr != null) {
            final Object convDef = intr.findDeserializationConverter(prop.getMember());
            if (convDef != null) {
                final Converter<Object, Object> conv = ctxt.converterInstance(prop.getMember(), convDef);
                final JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
                final JsonDeserializer<?> deser = ctxt.findNonContextualValueDeserializer(delegateType);
                return new StdDelegatingDeserializer<Object>(conv, delegateType, deser);
            }
        }
        return null;
    }
    
    @Override
    public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
        ObjectIdReader oir = this._objectIdReader;
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        final AnnotatedMember accessor = StdDeserializer._neitherNull(property, intr) ? property.getMember() : null;
        if (accessor != null) {
            ObjectIdInfo objectIdInfo = intr.findObjectIdInfo(accessor);
            if (objectIdInfo != null) {
                objectIdInfo = intr.findObjectReferenceInfo(accessor, objectIdInfo);
                final Class<?> implClass = objectIdInfo.getGeneratorType();
                final ObjectIdResolver resolver = ctxt.objectIdResolverInstance(accessor, objectIdInfo);
                SettableBeanProperty idProp;
                JavaType idType;
                ObjectIdGenerator<?> idGen;
                if (implClass == ObjectIdGenerators.PropertyGenerator.class) {
                    final PropertyName propName = objectIdInfo.getPropertyName();
                    idProp = this.findProperty(propName);
                    if (idProp == null) {
                        ctxt.reportBadDefinition(this._beanType, String.format("Invalid Object Id definition for %s: cannot find property with name '%s'", this.handledType().getName(), propName));
                    }
                    idType = idProp.getType();
                    idGen = new PropertyBasedObjectIdGenerator(objectIdInfo.getScope());
                }
                else {
                    final JavaType type = ctxt.constructType(implClass);
                    idType = ctxt.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
                    idProp = null;
                    idGen = ctxt.objectIdGeneratorInstance(accessor, objectIdInfo);
                }
                final JsonDeserializer<?> deser = ctxt.findRootValueDeserializer(idType);
                oir = ObjectIdReader.construct(idType, objectIdInfo.getPropertyName(), idGen, deser, idProp, resolver);
            }
        }
        BeanDeserializerBase contextual = this;
        if (oir != null && oir != this._objectIdReader) {
            contextual = contextual.withObjectIdReader(oir);
        }
        if (accessor != null) {
            final JsonIgnoreProperties.Value ignorals = intr.findPropertyIgnorals(accessor);
            if (ignorals != null) {
                Set<String> ignored = ignorals.findIgnoredForDeserialization();
                if (!ignored.isEmpty()) {
                    final Set<String> prev = contextual._ignorableProps;
                    if (prev != null && !prev.isEmpty()) {
                        ignored = new HashSet<String>(ignored);
                        ignored.addAll(prev);
                    }
                    contextual = contextual.withIgnorableProperties(ignored);
                }
            }
        }
        final JsonFormat.Value format = this.findFormatOverrides(ctxt, property, this.handledType());
        JsonFormat.Shape shape = null;
        if (format != null) {
            if (format.hasShape()) {
                shape = format.getShape();
            }
            final Boolean B = format.getFeature(JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
            if (B != null) {
                final BeanPropertyMap propsOrig = this._beanProperties;
                final BeanPropertyMap props = propsOrig.withCaseInsensitivity(B);
                if (props != propsOrig) {
                    contextual = contextual.withBeanProperties(props);
                }
            }
        }
        if (shape == null) {
            shape = this._serializationShape;
        }
        if (shape == JsonFormat.Shape.ARRAY) {
            contextual = contextual.asArrayDeserializer();
        }
        return contextual;
    }
    
    protected SettableBeanProperty _resolveManagedReferenceProperty(final DeserializationContext ctxt, final SettableBeanProperty prop) throws JsonMappingException {
        final String refName = prop.getManagedReferenceName();
        if (refName == null) {
            return prop;
        }
        final JsonDeserializer<?> valueDeser = prop.getValueDeserializer();
        final SettableBeanProperty backProp = valueDeser.findBackReference(refName);
        if (backProp == null) {
            ctxt.reportBadDefinition(this._beanType, String.format("Cannot handle managed/back reference '%s': no back reference property found from type %s", refName, prop.getType()));
        }
        final JavaType referredType = this._beanType;
        final JavaType backRefType = backProp.getType();
        final boolean isContainer = prop.getType().isContainerType();
        if (!backRefType.getRawClass().isAssignableFrom(referredType.getRawClass())) {
            ctxt.reportBadDefinition(this._beanType, String.format("Cannot handle managed/back reference '%s': back reference type (%s) not compatible with managed type (%s)", refName, backRefType.getRawClass().getName(), referredType.getRawClass().getName()));
        }
        return new ManagedReferenceProperty(prop, refName, backProp, isContainer);
    }
    
    protected SettableBeanProperty _resolvedObjectIdProperty(final DeserializationContext ctxt, final SettableBeanProperty prop) throws JsonMappingException {
        final ObjectIdInfo objectIdInfo = prop.getObjectIdInfo();
        final JsonDeserializer<Object> valueDeser = prop.getValueDeserializer();
        final ObjectIdReader objectIdReader = (valueDeser == null) ? null : valueDeser.getObjectIdReader();
        if (objectIdInfo == null && objectIdReader == null) {
            return prop;
        }
        return new ObjectIdReferenceProperty(prop, objectIdInfo);
    }
    
    protected NameTransformer _findPropertyUnwrapper(final DeserializationContext ctxt, final SettableBeanProperty prop) throws JsonMappingException {
        final AnnotatedMember am = prop.getMember();
        if (am != null) {
            final NameTransformer unwrapper = ctxt.getAnnotationIntrospector().findUnwrappingNameTransformer(am);
            if (unwrapper != null) {
                if (prop instanceof CreatorProperty) {
                    ctxt.reportBadDefinition(this.getValueType(), String.format("Cannot define Creator property \"%s\" as `@JsonUnwrapped`: combination not yet supported", prop.getName()));
                }
                return unwrapper;
            }
        }
        return null;
    }
    
    protected SettableBeanProperty _resolveInnerClassValuedProperty(final DeserializationContext ctxt, final SettableBeanProperty prop) {
        final JsonDeserializer<Object> deser = prop.getValueDeserializer();
        if (deser instanceof BeanDeserializerBase) {
            final BeanDeserializerBase bd = (BeanDeserializerBase)deser;
            final ValueInstantiator vi = bd.getValueInstantiator();
            if (!vi.canCreateUsingDefault()) {
                final Class<?> valueClass = prop.getType().getRawClass();
                final Class<?> enclosing = ClassUtil.getOuterClass(valueClass);
                if (enclosing != null && enclosing == this._beanType.getRawClass()) {
                    for (final Constructor<?> ctor : valueClass.getConstructors()) {
                        final Class<?>[] paramTypes = ctor.getParameterTypes();
                        if (paramTypes.length == 1 && enclosing.equals(paramTypes[0])) {
                            if (ctxt.canOverrideAccessModifiers()) {
                                ClassUtil.checkAndFixAccess(ctor, ctxt.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
                            }
                            return new InnerClassProperty(prop, ctor);
                        }
                    }
                }
            }
        }
        return prop;
    }
    
    protected SettableBeanProperty _resolveMergeAndNullSettings(final DeserializationContext ctxt, SettableBeanProperty prop, final PropertyMetadata propMetadata) throws JsonMappingException {
        final PropertyMetadata.MergeInfo merge = propMetadata.getMergeInfo();
        if (merge != null) {
            final JsonDeserializer<?> valueDeser = prop.getValueDeserializer();
            final Boolean mayMerge = valueDeser.supportsUpdate(ctxt.getConfig());
            if (mayMerge == null) {
                if (merge.fromDefaults) {
                    return prop;
                }
            }
            else if (!mayMerge) {
                if (!merge.fromDefaults) {
                    ctxt.reportBadMerge(valueDeser);
                }
                return prop;
            }
            final AnnotatedMember accessor = merge.getter;
            accessor.fixAccess(ctxt.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
            if (!(prop instanceof SetterlessProperty)) {
                prop = MergingSettableBeanProperty.construct(prop, accessor);
            }
        }
        final NullValueProvider nuller = this.findValueNullProvider(ctxt, prop, propMetadata);
        if (nuller != null) {
            prop = prop.withNullProvider(nuller);
        }
        return prop;
    }
    
    @Override
    public AccessPattern getNullAccessPattern() {
        return AccessPattern.ALWAYS_NULL;
    }
    
    @Override
    public AccessPattern getEmptyAccessPattern() {
        return AccessPattern.DYNAMIC;
    }
    
    @Override
    public Object getEmptyValue(final DeserializationContext ctxt) throws JsonMappingException {
        try {
            return this._valueInstantiator.createUsingDefault(ctxt);
        }
        catch (IOException e) {
            return ClassUtil.throwAsMappingException(ctxt, e);
        }
    }
    
    @Override
    public boolean isCachable() {
        return true;
    }
    
    @Override
    public Boolean supportsUpdate(final DeserializationConfig config) {
        return Boolean.TRUE;
    }
    
    @Override
    public Class<?> handledType() {
        return this._beanType.getRawClass();
    }
    
    @Override
    public ObjectIdReader getObjectIdReader() {
        return this._objectIdReader;
    }
    
    public boolean hasProperty(final String propertyName) {
        return this._beanProperties.find(propertyName) != null;
    }
    
    public boolean hasViews() {
        return this._needViewProcesing;
    }
    
    public int getPropertyCount() {
        return this._beanProperties.size();
    }
    
    @Override
    public Collection<Object> getKnownPropertyNames() {
        final ArrayList<Object> names = new ArrayList<Object>();
        for (final SettableBeanProperty prop : this._beanProperties) {
            names.add(prop.getName());
        }
        return names;
    }
    
    @Deprecated
    public final Class<?> getBeanClass() {
        return this._beanType.getRawClass();
    }
    
    @Override
    public JavaType getValueType() {
        return this._beanType;
    }
    
    public Iterator<SettableBeanProperty> properties() {
        if (this._beanProperties == null) {
            throw new IllegalStateException("Can only call after BeanDeserializer has been resolved");
        }
        return this._beanProperties.iterator();
    }
    
    public Iterator<SettableBeanProperty> creatorProperties() {
        if (this._propertyBasedCreator == null) {
            return Collections.emptyList().iterator();
        }
        return this._propertyBasedCreator.properties().iterator();
    }
    
    public SettableBeanProperty findProperty(final PropertyName propertyName) {
        return this.findProperty(propertyName.getSimpleName());
    }
    
    public SettableBeanProperty findProperty(final String propertyName) {
        SettableBeanProperty prop = (this._beanProperties == null) ? null : this._beanProperties.find(propertyName);
        if (prop == null && this._propertyBasedCreator != null) {
            prop = this._propertyBasedCreator.findCreatorProperty(propertyName);
        }
        return prop;
    }
    
    public SettableBeanProperty findProperty(final int propertyIndex) {
        SettableBeanProperty prop = (this._beanProperties == null) ? null : this._beanProperties.find(propertyIndex);
        if (prop == null && this._propertyBasedCreator != null) {
            prop = this._propertyBasedCreator.findCreatorProperty(propertyIndex);
        }
        return prop;
    }
    
    @Override
    public SettableBeanProperty findBackReference(final String logicalName) {
        if (this._backRefs == null) {
            return null;
        }
        return this._backRefs.get(logicalName);
    }
    
    @Override
    public ValueInstantiator getValueInstantiator() {
        return this._valueInstantiator;
    }
    
    public void replaceProperty(final SettableBeanProperty original, final SettableBeanProperty replacement) {
        this._beanProperties.replace(original, replacement);
    }
    
    public abstract Object deserializeFromObject(final JsonParser p0, final DeserializationContext p1) throws IOException;
    
    @Override
    public Object deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        if (this._objectIdReader != null) {
            if (p.canReadObjectId()) {
                final Object id = p.getObjectId();
                if (id != null) {
                    final Object ob = typeDeserializer.deserializeTypedFromObject(p, ctxt);
                    return this._handleTypedObjectId(p, ctxt, ob, id);
                }
            }
            JsonToken t = p.getCurrentToken();
            if (t != null) {
                if (t.isScalarValue()) {
                    return this.deserializeFromObjectId(p, ctxt);
                }
                if (t == JsonToken.START_OBJECT) {
                    t = p.nextToken();
                }
                if (t == JsonToken.FIELD_NAME && this._objectIdReader.maySerializeAsObject() && this._objectIdReader.isValidReferencePropertyName(p.getCurrentName(), p)) {
                    return this.deserializeFromObjectId(p, ctxt);
                }
            }
        }
        return typeDeserializer.deserializeTypedFromObject(p, ctxt);
    }
    
    protected Object _handleTypedObjectId(final JsonParser p, final DeserializationContext ctxt, final Object pojo, final Object rawId) throws IOException {
        final JsonDeserializer<Object> idDeser = this._objectIdReader.getDeserializer();
        Object id;
        if (idDeser.handledType() == rawId.getClass()) {
            id = rawId;
        }
        else {
            id = this._convertObjectId(p, ctxt, rawId, idDeser);
        }
        final ReadableObjectId roid = ctxt.findObjectId(id, this._objectIdReader.generator, this._objectIdReader.resolver);
        roid.bindItem(pojo);
        final SettableBeanProperty idProp = this._objectIdReader.idProperty;
        if (idProp != null) {
            return idProp.setAndReturn(pojo, id);
        }
        return pojo;
    }
    
    protected Object _convertObjectId(final JsonParser p, final DeserializationContext ctxt, final Object rawId, final JsonDeserializer<Object> idDeser) throws IOException {
        final TokenBuffer buf = new TokenBuffer(p, ctxt);
        if (rawId instanceof String) {
            buf.writeString((String)rawId);
        }
        else if (rawId instanceof Long) {
            buf.writeNumber((long)rawId);
        }
        else if (rawId instanceof Integer) {
            buf.writeNumber((int)rawId);
        }
        else {
            buf.writeObject(rawId);
        }
        final JsonParser bufParser = buf.asParser();
        bufParser.nextToken();
        return idDeser.deserialize(bufParser, ctxt);
    }
    
    protected Object deserializeWithObjectId(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        return this.deserializeFromObject(p, ctxt);
    }
    
    protected Object deserializeFromObjectId(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final Object id = this._objectIdReader.readObjectReference(p, ctxt);
        final ReadableObjectId roid = ctxt.findObjectId(id, this._objectIdReader.generator, this._objectIdReader.resolver);
        final Object pojo = roid.resolve();
        if (pojo == null) {
            throw new UnresolvedForwardReference(p, "Could not resolve Object Id [" + id + "] (for " + this._beanType + ").", p.getCurrentLocation(), roid);
        }
        return pojo;
    }
    
    protected Object deserializeFromObjectUsingNonDefault(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final JsonDeserializer<Object> delegateDeser = this._delegateDeserializer();
        if (delegateDeser != null) {
            return this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
        }
        if (this._propertyBasedCreator != null) {
            return this._deserializeUsingPropertyBased(p, ctxt);
        }
        final Class<?> raw = this._beanType.getRawClass();
        if (ClassUtil.isNonStaticInnerClass(raw)) {
            return ctxt.handleMissingInstantiator(raw, null, p, "can only instantiate non-static inner class by using default, no-argument constructor", new Object[0]);
        }
        return ctxt.handleMissingInstantiator(raw, this.getValueInstantiator(), p, "cannot deserialize from Object value (no delegate- or property-based Creator)", new Object[0]);
    }
    
    protected abstract Object _deserializeUsingPropertyBased(final JsonParser p0, final DeserializationContext p1) throws IOException;
    
    public Object deserializeFromNumber(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (this._objectIdReader != null) {
            return this.deserializeFromObjectId(p, ctxt);
        }
        final JsonDeserializer<Object> delegateDeser = this._delegateDeserializer();
        final JsonParser.NumberType nt = p.getNumberType();
        if (nt == JsonParser.NumberType.INT) {
            if (delegateDeser != null && !this._valueInstantiator.canCreateFromInt()) {
                final Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
                if (this._injectables != null) {
                    this.injectValues(ctxt, bean);
                }
                return bean;
            }
            return this._valueInstantiator.createFromInt(ctxt, p.getIntValue());
        }
        else if (nt == JsonParser.NumberType.LONG) {
            if (delegateDeser != null && !this._valueInstantiator.canCreateFromInt()) {
                final Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
                if (this._injectables != null) {
                    this.injectValues(ctxt, bean);
                }
                return bean;
            }
            return this._valueInstantiator.createFromLong(ctxt, p.getLongValue());
        }
        else {
            if (delegateDeser != null) {
                final Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
                if (this._injectables != null) {
                    this.injectValues(ctxt, bean);
                }
                return bean;
            }
            return ctxt.handleMissingInstantiator(this.handledType(), this.getValueInstantiator(), p, "no suitable creator method found to deserialize from Number value (%s)", p.getNumberValue());
        }
    }
    
    public Object deserializeFromString(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (this._objectIdReader != null) {
            return this.deserializeFromObjectId(p, ctxt);
        }
        final JsonDeserializer<Object> delegateDeser = this._delegateDeserializer();
        if (delegateDeser != null && !this._valueInstantiator.canCreateFromString()) {
            final Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
            if (this._injectables != null) {
                this.injectValues(ctxt, bean);
            }
            return bean;
        }
        return this._valueInstantiator.createFromString(ctxt, p.getText());
    }
    
    public Object deserializeFromDouble(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final JsonParser.NumberType t = p.getNumberType();
        if (t == JsonParser.NumberType.DOUBLE || t == JsonParser.NumberType.FLOAT) {
            final JsonDeserializer<Object> delegateDeser = this._delegateDeserializer();
            if (delegateDeser != null && !this._valueInstantiator.canCreateFromDouble()) {
                final Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
                if (this._injectables != null) {
                    this.injectValues(ctxt, bean);
                }
                return bean;
            }
            return this._valueInstantiator.createFromDouble(ctxt, p.getDoubleValue());
        }
        else {
            final JsonDeserializer<Object> delegateDeser = this._delegateDeserializer();
            if (delegateDeser != null) {
                return this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
            }
            return ctxt.handleMissingInstantiator(this.handledType(), this.getValueInstantiator(), p, "no suitable creator method found to deserialize from Number value (%s)", p.getNumberValue());
        }
    }
    
    public Object deserializeFromBoolean(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final JsonDeserializer<Object> delegateDeser = this._delegateDeserializer();
        if (delegateDeser != null && !this._valueInstantiator.canCreateFromBoolean()) {
            final Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
            if (this._injectables != null) {
                this.injectValues(ctxt, bean);
            }
            return bean;
        }
        final boolean value = p.getCurrentToken() == JsonToken.VALUE_TRUE;
        return this._valueInstantiator.createFromBoolean(ctxt, value);
    }
    
    public Object deserializeFromArray(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        JsonDeserializer<Object> delegateDeser = this._arrayDelegateDeserializer;
        if (delegateDeser != null || (delegateDeser = this._delegateDeserializer) != null) {
            final Object bean = this._valueInstantiator.createUsingArrayDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
            if (this._injectables != null) {
                this.injectValues(ctxt, bean);
            }
            return bean;
        }
        if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
            final JsonToken t = p.nextToken();
            if (t == JsonToken.END_ARRAY && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)) {
                return null;
            }
            final Object value = this.deserialize(p, ctxt);
            if (p.nextToken() != JsonToken.END_ARRAY) {
                this.handleMissingEndArrayForSingle(p, ctxt);
            }
            return value;
        }
        else {
            if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)) {
                return ctxt.handleUnexpectedToken(this.handledType(), p);
            }
            final JsonToken t = p.nextToken();
            if (t == JsonToken.END_ARRAY) {
                return null;
            }
            return ctxt.handleUnexpectedToken(this.handledType(), JsonToken.START_ARRAY, p, null, new Object[0]);
        }
    }
    
    public Object deserializeFromEmbedded(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (this._objectIdReader != null) {
            return this.deserializeFromObjectId(p, ctxt);
        }
        final JsonDeserializer<Object> delegateDeser = this._delegateDeserializer();
        if (delegateDeser != null && !this._valueInstantiator.canCreateFromString()) {
            final Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
            if (this._injectables != null) {
                this.injectValues(ctxt, bean);
            }
            return bean;
        }
        Object value = p.getEmbeddedObject();
        if (value != null && !this._beanType.isTypeOrSuperTypeOf(value.getClass())) {
            value = ctxt.handleWeirdNativeValue(this._beanType, value, p);
        }
        return value;
    }
    
    private final JsonDeserializer<Object> _delegateDeserializer() {
        JsonDeserializer<Object> deser = this._delegateDeserializer;
        if (deser == null) {
            deser = this._arrayDelegateDeserializer;
        }
        return deser;
    }
    
    protected void injectValues(final DeserializationContext ctxt, final Object bean) throws IOException {
        for (final ValueInjector injector : this._injectables) {
            injector.inject(ctxt, bean);
        }
    }
    
    protected Object handleUnknownProperties(final DeserializationContext ctxt, final Object bean, final TokenBuffer unknownTokens) throws IOException {
        unknownTokens.writeEndObject();
        final JsonParser bufferParser = unknownTokens.asParser();
        while (bufferParser.nextToken() != JsonToken.END_OBJECT) {
            final String propName = bufferParser.getCurrentName();
            bufferParser.nextToken();
            this.handleUnknownProperty(bufferParser, ctxt, bean, propName);
        }
        return bean;
    }
    
    protected void handleUnknownVanilla(final JsonParser p, final DeserializationContext ctxt, final Object bean, final String propName) throws IOException {
        if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
            this.handleIgnoredProperty(p, ctxt, bean, propName);
        }
        else if (this._anySetter != null) {
            try {
                this._anySetter.deserializeAndSet(p, ctxt, bean, propName);
            }
            catch (Exception e) {
                this.wrapAndThrow(e, bean, propName, ctxt);
            }
        }
        else {
            this.handleUnknownProperty(p, ctxt, bean, propName);
        }
    }
    
    @Override
    protected void handleUnknownProperty(final JsonParser p, final DeserializationContext ctxt, final Object beanOrClass, final String propName) throws IOException {
        if (this._ignoreAllUnknown) {
            p.skipChildren();
            return;
        }
        if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
            this.handleIgnoredProperty(p, ctxt, beanOrClass, propName);
        }
        super.handleUnknownProperty(p, ctxt, beanOrClass, propName);
    }
    
    protected void handleIgnoredProperty(final JsonParser p, final DeserializationContext ctxt, final Object beanOrClass, final String propName) throws IOException {
        if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)) {
            throw IgnoredPropertyException.from(p, beanOrClass, propName, this.getKnownPropertyNames());
        }
        p.skipChildren();
    }
    
    protected Object handlePolymorphic(final JsonParser p, final DeserializationContext ctxt, Object bean, final TokenBuffer unknownTokens) throws IOException {
        final JsonDeserializer<Object> subDeser = this._findSubclassDeserializer(ctxt, bean, unknownTokens);
        if (subDeser != null) {
            if (unknownTokens != null) {
                unknownTokens.writeEndObject();
                final JsonParser p2 = unknownTokens.asParser();
                p2.nextToken();
                bean = subDeser.deserialize(p2, ctxt, bean);
            }
            if (p != null) {
                bean = subDeser.deserialize(p, ctxt, bean);
            }
            return bean;
        }
        if (unknownTokens != null) {
            bean = this.handleUnknownProperties(ctxt, bean, unknownTokens);
        }
        if (p != null) {
            bean = this.deserialize(p, ctxt, bean);
        }
        return bean;
    }
    
    protected JsonDeserializer<Object> _findSubclassDeserializer(final DeserializationContext ctxt, final Object bean, final TokenBuffer unknownTokens) throws IOException {
        JsonDeserializer<Object> subDeser;
        synchronized (this) {
            subDeser = ((this._subDeserializers == null) ? null : this._subDeserializers.get(new ClassKey(bean.getClass())));
        }
        if (subDeser != null) {
            return subDeser;
        }
        final JavaType type = ctxt.constructType(bean.getClass());
        subDeser = ctxt.findRootValueDeserializer(type);
        if (subDeser != null) {
            synchronized (this) {
                if (this._subDeserializers == null) {
                    this._subDeserializers = new HashMap<ClassKey, JsonDeserializer<Object>>();
                }
                this._subDeserializers.put(new ClassKey(bean.getClass()), subDeser);
            }
        }
        return subDeser;
    }
    
    public void wrapAndThrow(final Throwable t, final Object bean, final String fieldName, final DeserializationContext ctxt) throws IOException {
        throw JsonMappingException.wrapWithPath(this.throwOrReturnThrowable(t, ctxt), bean, fieldName);
    }
    
    private Throwable throwOrReturnThrowable(Throwable t, final DeserializationContext ctxt) throws IOException {
        while (t instanceof InvocationTargetException && t.getCause() != null) {
            t = t.getCause();
        }
        ClassUtil.throwIfError(t);
        final boolean wrap = ctxt == null || ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS);
        if (t instanceof IOException) {
            if (!wrap || !(t instanceof JsonProcessingException)) {
                throw (IOException)t;
            }
        }
        else if (!wrap) {
            ClassUtil.throwIfRTE(t);
        }
        return t;
    }
    
    protected Object wrapInstantiationProblem(Throwable t, final DeserializationContext ctxt) throws IOException {
        while (t instanceof InvocationTargetException && t.getCause() != null) {
            t = t.getCause();
        }
        ClassUtil.throwIfError(t);
        if (t instanceof IOException) {
            throw (IOException)t;
        }
        final boolean wrap = ctxt == null || ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS);
        if (!wrap) {
            ClassUtil.throwIfRTE(t);
        }
        return ctxt.handleInstantiationProblem(this._beanType.getRawClass(), null, t);
    }
    
    static {
        TEMP_PROPERTY_NAME = new PropertyName("#temporary-name");
    }
}
