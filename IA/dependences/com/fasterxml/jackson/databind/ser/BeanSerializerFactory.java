// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import java.util.HashMap;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.ser.impl.FilteredBeanPropertyWriter;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import java.lang.reflect.Type;
import com.fasterxml.jackson.databind.ser.impl.PropertyBasedObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import java.util.List;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.ser.std.MapSerializer;
import java.util.Set;
import com.fasterxml.jackson.databind.MapperFeature;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import java.util.Collection;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import java.util.Iterator;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.cfg.SerializerFactoryConfig;
import java.io.Serializable;

public class BeanSerializerFactory extends BasicSerializerFactory implements Serializable
{
    private static final long serialVersionUID = 1L;
    public static final BeanSerializerFactory instance;
    
    protected BeanSerializerFactory(final SerializerFactoryConfig config) {
        super(config);
    }
    
    @Override
    public SerializerFactory withConfig(final SerializerFactoryConfig config) {
        if (this._factoryConfig == config) {
            return this;
        }
        if (this.getClass() != BeanSerializerFactory.class) {
            throw new IllegalStateException("Subtype of BeanSerializerFactory (" + this.getClass().getName() + ") has not properly overridden method 'withAdditionalSerializers': cannot instantiate subtype with additional serializer definitions");
        }
        return new BeanSerializerFactory(config);
    }
    
    @Override
    protected Iterable<Serializers> customSerializers() {
        return this._factoryConfig.serializers();
    }
    
    @Override
    public JsonSerializer<Object> createSerializer(final SerializerProvider prov, final JavaType origType) throws JsonMappingException {
        final SerializationConfig config = prov.getConfig();
        BeanDescription beanDesc = config.introspect(origType);
        JsonSerializer<?> ser = this.findSerializerFromAnnotation(prov, beanDesc.getClassInfo());
        if (ser != null) {
            return (JsonSerializer<Object>)ser;
        }
        final AnnotationIntrospector intr = config.getAnnotationIntrospector();
        JavaType type;
        if (intr == null) {
            type = origType;
        }
        else {
            try {
                type = intr.refineSerializationType(config, beanDesc.getClassInfo(), origType);
            }
            catch (JsonMappingException e) {
                return prov.reportBadTypeDefinition(beanDesc, e.getMessage(), new Object[0]);
            }
        }
        boolean staticTyping;
        if (type == origType) {
            staticTyping = false;
        }
        else {
            staticTyping = true;
            if (!type.hasRawClass(origType.getRawClass())) {
                beanDesc = config.introspect(type);
            }
        }
        final Converter<Object, Object> conv = beanDesc.findSerializationConverter();
        if (conv == null) {
            return (JsonSerializer<Object>)this._createSerializer2(prov, type, beanDesc, staticTyping);
        }
        final JavaType delegateType = conv.getOutputType(prov.getTypeFactory());
        if (!delegateType.hasRawClass(type.getRawClass())) {
            beanDesc = config.introspect(delegateType);
            ser = this.findSerializerFromAnnotation(prov, beanDesc.getClassInfo());
        }
        if (ser == null && !delegateType.isJavaLangObject()) {
            ser = this._createSerializer2(prov, delegateType, beanDesc, true);
        }
        return new StdDelegatingSerializer(conv, delegateType, ser);
    }
    
    protected JsonSerializer<?> _createSerializer2(final SerializerProvider prov, final JavaType type, final BeanDescription beanDesc, boolean staticTyping) throws JsonMappingException {
        JsonSerializer<?> ser = null;
        final SerializationConfig config = prov.getConfig();
        if (type.isContainerType()) {
            if (!staticTyping) {
                staticTyping = this.usesStaticTyping(config, beanDesc, null);
            }
            ser = this.buildContainerSerializer(prov, type, beanDesc, staticTyping);
            if (ser != null) {
                return ser;
            }
        }
        else {
            if (type.isReferenceType()) {
                ser = this.findReferenceSerializer(prov, (ReferenceType)type, beanDesc, staticTyping);
            }
            else {
                for (final Serializers serializers : this.customSerializers()) {
                    ser = serializers.findSerializer(config, type, beanDesc);
                    if (ser != null) {
                        break;
                    }
                }
            }
            if (ser == null) {
                ser = this.findSerializerByAnnotations(prov, type, beanDesc);
            }
        }
        if (ser == null) {
            ser = this.findSerializerByLookup(type, config, beanDesc, staticTyping);
            if (ser == null) {
                ser = this.findSerializerByPrimaryType(prov, type, beanDesc, staticTyping);
                if (ser == null) {
                    ser = this.findBeanSerializer(prov, type, beanDesc);
                    if (ser == null) {
                        ser = this.findSerializerByAddonType(config, type, beanDesc, staticTyping);
                        if (ser == null) {
                            ser = prov.getUnknownTypeSerializer(beanDesc.getBeanClass());
                        }
                    }
                }
            }
        }
        if (ser != null && this._factoryConfig.hasSerializerModifiers()) {
            for (final BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                ser = mod.modifySerializer(config, beanDesc, ser);
            }
        }
        return ser;
    }
    
    public JsonSerializer<Object> findBeanSerializer(final SerializerProvider prov, final JavaType type, final BeanDescription beanDesc) throws JsonMappingException {
        if (!this.isPotentialBeanType(type.getRawClass()) && !type.isEnumType()) {
            return null;
        }
        return this.constructBeanSerializer(prov, beanDesc);
    }
    
    public TypeSerializer findPropertyTypeSerializer(final JavaType baseType, final SerializationConfig config, final AnnotatedMember accessor) throws JsonMappingException {
        final AnnotationIntrospector ai = config.getAnnotationIntrospector();
        final TypeResolverBuilder<?> b = ai.findPropertyTypeResolver(config, accessor, baseType);
        TypeSerializer typeSer;
        if (b == null) {
            typeSer = this.createTypeSerializer(config, baseType);
        }
        else {
            final Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByClass(config, accessor, baseType);
            typeSer = b.buildTypeSerializer(config, baseType, subtypes);
        }
        return typeSer;
    }
    
    public TypeSerializer findPropertyContentTypeSerializer(final JavaType containerType, final SerializationConfig config, final AnnotatedMember accessor) throws JsonMappingException {
        final JavaType contentType = containerType.getContentType();
        final AnnotationIntrospector ai = config.getAnnotationIntrospector();
        final TypeResolverBuilder<?> b = ai.findPropertyContentTypeResolver(config, accessor, containerType);
        TypeSerializer typeSer;
        if (b == null) {
            typeSer = this.createTypeSerializer(config, contentType);
        }
        else {
            final Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByClass(config, accessor, contentType);
            typeSer = b.buildTypeSerializer(config, contentType, subtypes);
        }
        return typeSer;
    }
    
    protected JsonSerializer<Object> constructBeanSerializer(final SerializerProvider prov, final BeanDescription beanDesc) throws JsonMappingException {
        if (beanDesc.getBeanClass() == Object.class) {
            return prov.getUnknownTypeSerializer(Object.class);
        }
        final SerializationConfig config = prov.getConfig();
        BeanSerializerBuilder builder = this.constructBeanSerializerBuilder(beanDesc);
        builder.setConfig(config);
        List<BeanPropertyWriter> props = this.findBeanProperties(prov, beanDesc, builder);
        if (props == null) {
            props = new ArrayList<BeanPropertyWriter>();
        }
        else {
            props = this.removeOverlappingTypeIds(prov, beanDesc, builder, props);
        }
        prov.getAnnotationIntrospector().findAndAddVirtualProperties(config, beanDesc.getClassInfo(), props);
        if (this._factoryConfig.hasSerializerModifiers()) {
            for (final BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                props = mod.changeProperties(config, beanDesc, props);
            }
        }
        props = this.filterBeanProperties(config, beanDesc, props);
        if (this._factoryConfig.hasSerializerModifiers()) {
            for (final BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                props = mod.orderProperties(config, beanDesc, props);
            }
        }
        builder.setObjectIdWriter(this.constructObjectIdHandler(prov, beanDesc, props));
        builder.setProperties(props);
        builder.setFilterId(this.findFilterId(config, beanDesc));
        final AnnotatedMember anyGetter = beanDesc.findAnyGetter();
        if (anyGetter != null) {
            final JavaType type = anyGetter.getType();
            final boolean staticTyping = config.isEnabled(MapperFeature.USE_STATIC_TYPING);
            final JavaType valueType = type.getContentType();
            final TypeSerializer typeSer = this.createTypeSerializer(config, valueType);
            JsonSerializer<?> anySer = this.findSerializerFromAnnotation(prov, anyGetter);
            if (anySer == null) {
                anySer = MapSerializer.construct((Set<String>)null, type, staticTyping, typeSer, null, null, null);
            }
            final PropertyName name = PropertyName.construct(anyGetter.getName());
            final BeanProperty.Std anyProp = new BeanProperty.Std(name, valueType, null, anyGetter, PropertyMetadata.STD_OPTIONAL);
            builder.setAnyGetter(new AnyGetterWriter(anyProp, anyGetter, anySer));
        }
        this.processViews(config, builder);
        if (this._factoryConfig.hasSerializerModifiers()) {
            for (final BeanSerializerModifier mod2 : this._factoryConfig.serializerModifiers()) {
                builder = mod2.updateBuilder(config, beanDesc, builder);
            }
        }
        JsonSerializer<Object> ser = null;
        try {
            ser = (JsonSerializer<Object>)builder.build();
        }
        catch (RuntimeException e) {
            prov.reportBadTypeDefinition(beanDesc, "Failed to construct BeanSerializer for %s: (%s) %s", beanDesc.getType(), e.getClass().getName(), e.getMessage());
        }
        if (ser == null && beanDesc.hasKnownClassAnnotations()) {
            return builder.createDummy();
        }
        return ser;
    }
    
    protected ObjectIdWriter constructObjectIdHandler(final SerializerProvider prov, final BeanDescription beanDesc, final List<BeanPropertyWriter> props) throws JsonMappingException {
        final ObjectIdInfo objectIdInfo = beanDesc.getObjectIdInfo();
        if (objectIdInfo == null) {
            return null;
        }
        final Class<?> implClass = objectIdInfo.getGeneratorType();
        if (implClass == ObjectIdGenerators.PropertyGenerator.class) {
            final String propName = objectIdInfo.getPropertyName().getSimpleName();
            BeanPropertyWriter idProp = null;
            for (int i = 0, len = props.size(); i != len; ++i) {
                final BeanPropertyWriter prop = props.get(i);
                if (propName.equals(prop.getName())) {
                    idProp = prop;
                    if (i > 0) {
                        props.remove(i);
                        props.add(0, idProp);
                    }
                    final JavaType idType = idProp.getType();
                    final ObjectIdGenerator<?> gen = new PropertyBasedObjectIdGenerator(objectIdInfo, idProp);
                    return ObjectIdWriter.construct(idType, null, gen, objectIdInfo.getAlwaysAsId());
                }
            }
            throw new IllegalArgumentException("Invalid Object Id definition for " + beanDesc.getBeanClass().getName() + ": cannot find property with name '" + propName + "'");
        }
        final JavaType type = prov.constructType(implClass);
        final JavaType idType2 = prov.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
        final ObjectIdGenerator<?> gen = prov.objectIdGeneratorInstance(beanDesc.getClassInfo(), objectIdInfo);
        return ObjectIdWriter.construct(idType2, objectIdInfo.getPropertyName(), gen, objectIdInfo.getAlwaysAsId());
    }
    
    protected BeanPropertyWriter constructFilteredBeanWriter(final BeanPropertyWriter writer, final Class<?>[] inViews) {
        return FilteredBeanPropertyWriter.constructViewBased(writer, inViews);
    }
    
    protected PropertyBuilder constructPropertyBuilder(final SerializationConfig config, final BeanDescription beanDesc) {
        return new PropertyBuilder(config, beanDesc);
    }
    
    protected BeanSerializerBuilder constructBeanSerializerBuilder(final BeanDescription beanDesc) {
        return new BeanSerializerBuilder(beanDesc);
    }
    
    protected boolean isPotentialBeanType(final Class<?> type) {
        return ClassUtil.canBeABeanType(type) == null && !ClassUtil.isProxyType(type);
    }
    
    protected List<BeanPropertyWriter> findBeanProperties(final SerializerProvider prov, final BeanDescription beanDesc, final BeanSerializerBuilder builder) throws JsonMappingException {
        final List<BeanPropertyDefinition> properties = beanDesc.findProperties();
        final SerializationConfig config = prov.getConfig();
        this.removeIgnorableTypes(config, beanDesc, properties);
        if (config.isEnabled(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS)) {
            this.removeSetterlessGetters(config, beanDesc, properties);
        }
        if (properties.isEmpty()) {
            return null;
        }
        final boolean staticTyping = this.usesStaticTyping(config, beanDesc, null);
        final PropertyBuilder pb = this.constructPropertyBuilder(config, beanDesc);
        final ArrayList<BeanPropertyWriter> result = new ArrayList<BeanPropertyWriter>(properties.size());
        for (final BeanPropertyDefinition property : properties) {
            final AnnotatedMember accessor = property.getAccessor();
            if (property.isTypeId()) {
                if (accessor == null) {
                    continue;
                }
                builder.setTypeId(accessor);
            }
            else {
                final AnnotationIntrospector.ReferenceProperty refType = property.findReferenceType();
                if (refType != null && refType.isBackReference()) {
                    continue;
                }
                if (accessor instanceof AnnotatedMethod) {
                    result.add(this._constructWriter(prov, property, pb, staticTyping, accessor));
                }
                else {
                    result.add(this._constructWriter(prov, property, pb, staticTyping, accessor));
                }
            }
        }
        return result;
    }
    
    protected List<BeanPropertyWriter> filterBeanProperties(final SerializationConfig config, final BeanDescription beanDesc, final List<BeanPropertyWriter> props) {
        final JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(beanDesc.getBeanClass(), beanDesc.getClassInfo());
        if (ignorals != null) {
            final Set<String> ignored = ignorals.findIgnoredForSerialization();
            if (!ignored.isEmpty()) {
                final Iterator<BeanPropertyWriter> it = props.iterator();
                while (it.hasNext()) {
                    if (ignored.contains(it.next().getName())) {
                        it.remove();
                    }
                }
            }
        }
        return props;
    }
    
    protected void processViews(final SerializationConfig config, final BeanSerializerBuilder builder) {
        final List<BeanPropertyWriter> props = builder.getProperties();
        final boolean includeByDefault = config.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION);
        final int propCount = props.size();
        int viewsFound = 0;
        final BeanPropertyWriter[] filtered = new BeanPropertyWriter[propCount];
        for (int i = 0; i < propCount; ++i) {
            final BeanPropertyWriter bpw = props.get(i);
            final Class<?>[] views = bpw.getViews();
            if (views == null) {
                if (includeByDefault) {
                    filtered[i] = bpw;
                }
            }
            else {
                ++viewsFound;
                filtered[i] = this.constructFilteredBeanWriter(bpw, views);
            }
        }
        if (includeByDefault && viewsFound == 0) {
            return;
        }
        builder.setFilteredProperties(filtered);
    }
    
    protected void removeIgnorableTypes(final SerializationConfig config, final BeanDescription beanDesc, final List<BeanPropertyDefinition> properties) {
        final AnnotationIntrospector intr = config.getAnnotationIntrospector();
        final HashMap<Class<?>, Boolean> ignores = new HashMap<Class<?>, Boolean>();
        final Iterator<BeanPropertyDefinition> it = properties.iterator();
        while (it.hasNext()) {
            final BeanPropertyDefinition property = it.next();
            final AnnotatedMember accessor = property.getAccessor();
            if (accessor == null) {
                it.remove();
            }
            else {
                final Class<?> type = property.getRawPrimaryType();
                Boolean result = ignores.get(type);
                if (result == null) {
                    result = config.getConfigOverride(type).getIsIgnoredType();
                    if (result == null) {
                        final BeanDescription desc = config.introspectClassAnnotations(type);
                        final AnnotatedClass ac = desc.getClassInfo();
                        result = intr.isIgnorableType(ac);
                        if (result == null) {
                            result = Boolean.FALSE;
                        }
                    }
                    ignores.put(type, result);
                }
                if (!result) {
                    continue;
                }
                it.remove();
            }
        }
    }
    
    protected void removeSetterlessGetters(final SerializationConfig config, final BeanDescription beanDesc, final List<BeanPropertyDefinition> properties) {
        final Iterator<BeanPropertyDefinition> it = properties.iterator();
        while (it.hasNext()) {
            final BeanPropertyDefinition property = it.next();
            if (!property.couldDeserialize() && !property.isExplicitlyIncluded()) {
                it.remove();
            }
        }
    }
    
    protected List<BeanPropertyWriter> removeOverlappingTypeIds(final SerializerProvider prov, final BeanDescription beanDesc, final BeanSerializerBuilder builder, final List<BeanPropertyWriter> props) {
        for (int i = 0, end = props.size(); i < end; ++i) {
            final BeanPropertyWriter bpw = props.get(i);
            final TypeSerializer td = bpw.getTypeSerializer();
            if (td != null) {
                if (td.getTypeInclusion() == JsonTypeInfo.As.EXTERNAL_PROPERTY) {
                    final String n = td.getPropertyName();
                    final PropertyName typePropName = PropertyName.construct(n);
                    for (final BeanPropertyWriter w2 : props) {
                        if (w2 != bpw && w2.wouldConflictWithName(typePropName)) {
                            bpw.assignTypeSerializer(null);
                            break;
                        }
                    }
                }
            }
        }
        return props;
    }
    
    protected BeanPropertyWriter _constructWriter(final SerializerProvider prov, final BeanPropertyDefinition propDef, final PropertyBuilder pb, final boolean staticTyping, final AnnotatedMember accessor) throws JsonMappingException {
        final PropertyName name = propDef.getFullName();
        final JavaType type = accessor.getType();
        final BeanProperty.Std property = new BeanProperty.Std(name, type, propDef.getWrapperName(), accessor, propDef.getMetadata());
        JsonSerializer<?> annotatedSerializer = this.findSerializerFromAnnotation(prov, accessor);
        if (annotatedSerializer instanceof ResolvableSerializer) {
            ((ResolvableSerializer)annotatedSerializer).resolve(prov);
        }
        annotatedSerializer = prov.handlePrimaryContextualization(annotatedSerializer, property);
        TypeSerializer contentTypeSer = null;
        if (type.isContainerType() || type.isReferenceType()) {
            contentTypeSer = this.findPropertyContentTypeSerializer(type, prov.getConfig(), accessor);
        }
        final TypeSerializer typeSer = this.findPropertyTypeSerializer(type, prov.getConfig(), accessor);
        return pb.buildWriter(prov, propDef, type, annotatedSerializer, typeSer, contentTypeSer, accessor, staticTyping);
    }
    
    static {
        instance = new BeanSerializerFactory(null);
    }
}
