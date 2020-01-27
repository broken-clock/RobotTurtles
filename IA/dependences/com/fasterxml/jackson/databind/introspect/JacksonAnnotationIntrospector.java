// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonValueInstantiator;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.ser.VirtualBeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.AttributePropertyWriter;
import com.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.type.MapLikeType;
import java.io.Closeable;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ser.std.RawSerializer;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeId;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.util.ArrayList;
import java.util.Collections;
import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import java.util.HashMap;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.reflect.Field;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.cfg.PackageVersion;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.util.LRUMap;
import com.fasterxml.jackson.databind.ext.Java7Support;
import java.lang.annotation.Annotation;
import java.io.Serializable;
import com.fasterxml.jackson.databind.AnnotationIntrospector;

public class JacksonAnnotationIntrospector extends AnnotationIntrospector implements Serializable
{
    private static final long serialVersionUID = 1L;
    private static final Class<? extends Annotation>[] ANNOTATIONS_TO_INFER_SER;
    private static final Class<? extends Annotation>[] ANNOTATIONS_TO_INFER_DESER;
    private static final Java7Support _java7Helper;
    protected transient LRUMap<Class<?>, Boolean> _annotationsInside;
    protected boolean _cfgConstructorPropertiesImpliesCreator;
    
    public JacksonAnnotationIntrospector() {
        this._annotationsInside = new LRUMap<Class<?>, Boolean>(48, 48);
        this._cfgConstructorPropertiesImpliesCreator = true;
    }
    
    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }
    
    protected Object readResolve() {
        if (this._annotationsInside == null) {
            this._annotationsInside = new LRUMap<Class<?>, Boolean>(48, 48);
        }
        return this;
    }
    
    public JacksonAnnotationIntrospector setConstructorPropertiesImpliesCreator(final boolean b) {
        this._cfgConstructorPropertiesImpliesCreator = b;
        return this;
    }
    
    @Override
    public boolean isAnnotationBundle(final Annotation ann) {
        final Class<?> type = ann.annotationType();
        Boolean b = this._annotationsInside.get(type);
        if (b == null) {
            b = (type.getAnnotation(JacksonAnnotationsInside.class) != null);
            this._annotationsInside.putIfAbsent(type, b);
        }
        return b;
    }
    
    @Deprecated
    @Override
    public String findEnumValue(final Enum<?> value) {
        try {
            final Field f = value.getClass().getField(value.name());
            if (f != null) {
                final JsonProperty prop = f.getAnnotation(JsonProperty.class);
                if (prop != null) {
                    final String n = prop.value();
                    if (n != null && !n.isEmpty()) {
                        return n;
                    }
                }
            }
        }
        catch (SecurityException ex) {}
        catch (NoSuchFieldException ex2) {}
        return value.name();
    }
    
    @Override
    public String[] findEnumValues(final Class<?> enumType, final Enum<?>[] enumValues, final String[] names) {
        HashMap<String, String> expl = null;
        for (final Field f : ClassUtil.getDeclaredFields(enumType)) {
            if (f.isEnumConstant()) {
                final JsonProperty prop = f.getAnnotation(JsonProperty.class);
                if (prop != null) {
                    final String n = prop.value();
                    if (!n.isEmpty()) {
                        if (expl == null) {
                            expl = new HashMap<String, String>();
                        }
                        expl.put(f.getName(), n);
                    }
                }
            }
        }
        if (expl != null) {
            for (int i = 0, end = enumValues.length; i < end; ++i) {
                final String defName = enumValues[i].name();
                final String explValue = expl.get(defName);
                if (explValue != null) {
                    names[i] = explValue;
                }
            }
        }
        return names;
    }
    
    @Override
    public Enum<?> findDefaultEnumValue(final Class<Enum<?>> enumCls) {
        return ClassUtil.findFirstAnnotatedEnumValue(enumCls, JsonEnumDefaultValue.class);
    }
    
    @Override
    public PropertyName findRootName(final AnnotatedClass ac) {
        final JsonRootName ann = this._findAnnotation(ac, JsonRootName.class);
        if (ann == null) {
            return null;
        }
        String ns = ann.namespace();
        if (ns != null && ns.length() == 0) {
            ns = null;
        }
        return PropertyName.construct(ann.value(), ns);
    }
    
    @Override
    public JsonIgnoreProperties.Value findPropertyIgnorals(final Annotated a) {
        final JsonIgnoreProperties v = this._findAnnotation(a, JsonIgnoreProperties.class);
        if (v == null) {
            return JsonIgnoreProperties.Value.empty();
        }
        return JsonIgnoreProperties.Value.from(v);
    }
    
    @Override
    public Boolean isIgnorableType(final AnnotatedClass ac) {
        final JsonIgnoreType ignore = this._findAnnotation(ac, JsonIgnoreType.class);
        return (ignore == null) ? null : Boolean.valueOf(ignore.value());
    }
    
    @Override
    public Object findFilterId(final Annotated a) {
        final JsonFilter ann = this._findAnnotation(a, JsonFilter.class);
        if (ann != null) {
            final String id = ann.value();
            if (id.length() > 0) {
                return id;
            }
        }
        return null;
    }
    
    @Override
    public Object findNamingStrategy(final AnnotatedClass ac) {
        final JsonNaming ann = this._findAnnotation(ac, JsonNaming.class);
        return (ann == null) ? null : ann.value();
    }
    
    @Override
    public String findClassDescription(final AnnotatedClass ac) {
        final JsonClassDescription ann = this._findAnnotation(ac, JsonClassDescription.class);
        return (ann == null) ? null : ann.value();
    }
    
    @Override
    public VisibilityChecker<?> findAutoDetectVisibility(final AnnotatedClass ac, final VisibilityChecker<?> checker) {
        final JsonAutoDetect ann = this._findAnnotation(ac, JsonAutoDetect.class);
        return (VisibilityChecker<?>)((ann == null) ? checker : checker.with(ann));
    }
    
    @Override
    public String findImplicitPropertyName(final AnnotatedMember m) {
        final PropertyName n = this._findConstructorName(m);
        return (n == null) ? null : n.getSimpleName();
    }
    
    @Override
    public List<PropertyName> findPropertyAliases(final Annotated m) {
        final JsonAlias ann = this._findAnnotation(m, JsonAlias.class);
        if (ann == null) {
            return null;
        }
        final String[] strs = ann.value();
        final int len = strs.length;
        if (len == 0) {
            return Collections.emptyList();
        }
        final List<PropertyName> result = new ArrayList<PropertyName>(len);
        for (int i = 0; i < len; ++i) {
            result.add(PropertyName.construct(strs[i]));
        }
        return result;
    }
    
    @Override
    public boolean hasIgnoreMarker(final AnnotatedMember m) {
        return this._isIgnorable(m);
    }
    
    @Override
    public Boolean hasRequiredMarker(final AnnotatedMember m) {
        final JsonProperty ann = this._findAnnotation(m, JsonProperty.class);
        if (ann != null) {
            return ann.required();
        }
        return null;
    }
    
    @Override
    public JsonProperty.Access findPropertyAccess(final Annotated m) {
        final JsonProperty ann = this._findAnnotation(m, JsonProperty.class);
        if (ann != null) {
            return ann.access();
        }
        return null;
    }
    
    @Override
    public String findPropertyDescription(final Annotated ann) {
        final JsonPropertyDescription desc = this._findAnnotation(ann, JsonPropertyDescription.class);
        return (desc == null) ? null : desc.value();
    }
    
    @Override
    public Integer findPropertyIndex(final Annotated ann) {
        final JsonProperty prop = this._findAnnotation(ann, JsonProperty.class);
        if (prop != null) {
            final int ix = prop.index();
            if (ix != -1) {
                return ix;
            }
        }
        return null;
    }
    
    @Override
    public String findPropertyDefaultValue(final Annotated ann) {
        final JsonProperty prop = this._findAnnotation(ann, JsonProperty.class);
        if (prop == null) {
            return null;
        }
        final String str = prop.defaultValue();
        return str.isEmpty() ? null : str;
    }
    
    @Override
    public JsonFormat.Value findFormat(final Annotated ann) {
        final JsonFormat f = this._findAnnotation(ann, JsonFormat.class);
        return (f == null) ? null : new JsonFormat.Value(f);
    }
    
    @Override
    public ReferenceProperty findReferenceType(final AnnotatedMember member) {
        final JsonManagedReference ref1 = this._findAnnotation(member, JsonManagedReference.class);
        if (ref1 != null) {
            return ReferenceProperty.managed(ref1.value());
        }
        final JsonBackReference ref2 = this._findAnnotation(member, JsonBackReference.class);
        if (ref2 != null) {
            return ReferenceProperty.back(ref2.value());
        }
        return null;
    }
    
    @Override
    public NameTransformer findUnwrappingNameTransformer(final AnnotatedMember member) {
        final JsonUnwrapped ann = this._findAnnotation(member, JsonUnwrapped.class);
        if (ann == null || !ann.enabled()) {
            return null;
        }
        final String prefix = ann.prefix();
        final String suffix = ann.suffix();
        return NameTransformer.simpleTransformer(prefix, suffix);
    }
    
    @Override
    public JacksonInject.Value findInjectableValue(final AnnotatedMember m) {
        final JacksonInject ann = this._findAnnotation(m, JacksonInject.class);
        if (ann == null) {
            return null;
        }
        JacksonInject.Value v = JacksonInject.Value.from(ann);
        if (!v.hasId()) {
            Object id;
            if (!(m instanceof AnnotatedMethod)) {
                id = m.getRawType().getName();
            }
            else {
                final AnnotatedMethod am = (AnnotatedMethod)m;
                if (am.getParameterCount() == 0) {
                    id = m.getRawType().getName();
                }
                else {
                    id = am.getRawParameterType(0).getName();
                }
            }
            v = v.withId(id);
        }
        return v;
    }
    
    @Deprecated
    @Override
    public Object findInjectableValueId(final AnnotatedMember m) {
        final JacksonInject.Value v = this.findInjectableValue(m);
        return (v == null) ? null : v.getId();
    }
    
    @Override
    public Class<?>[] findViews(final Annotated a) {
        final JsonView ann = this._findAnnotation(a, JsonView.class);
        return (Class<?>[])((ann == null) ? null : ann.value());
    }
    
    @Override
    public AnnotatedMethod resolveSetterConflict(final MapperConfig<?> config, final AnnotatedMethod setter1, final AnnotatedMethod setter2) {
        final Class<?> cls1 = setter1.getRawParameterType(0);
        final Class<?> cls2 = setter2.getRawParameterType(0);
        if (cls1.isPrimitive()) {
            if (!cls2.isPrimitive()) {
                return setter1;
            }
        }
        else if (cls2.isPrimitive()) {
            return setter2;
        }
        if (cls1 == String.class) {
            if (cls2 != String.class) {
                return setter1;
            }
        }
        else if (cls2 == String.class) {
            return setter2;
        }
        return null;
    }
    
    @Override
    public TypeResolverBuilder<?> findTypeResolver(final MapperConfig<?> config, final AnnotatedClass ac, final JavaType baseType) {
        return this._findTypeResolver(config, ac, baseType);
    }
    
    @Override
    public TypeResolverBuilder<?> findPropertyTypeResolver(final MapperConfig<?> config, final AnnotatedMember am, final JavaType baseType) {
        if (baseType.isContainerType() || baseType.isReferenceType()) {
            return null;
        }
        return this._findTypeResolver(config, am, baseType);
    }
    
    @Override
    public TypeResolverBuilder<?> findPropertyContentTypeResolver(final MapperConfig<?> config, final AnnotatedMember am, final JavaType containerType) {
        if (containerType.getContentType() == null) {
            throw new IllegalArgumentException("Must call method with a container or reference type (got " + containerType + ")");
        }
        return this._findTypeResolver(config, am, containerType);
    }
    
    @Override
    public List<NamedType> findSubtypes(final Annotated a) {
        final JsonSubTypes t = this._findAnnotation(a, JsonSubTypes.class);
        if (t == null) {
            return null;
        }
        final JsonSubTypes.Type[] types = t.value();
        final ArrayList<NamedType> result = new ArrayList<NamedType>(types.length);
        for (final JsonSubTypes.Type type : types) {
            result.add(new NamedType(type.value(), type.name()));
        }
        return result;
    }
    
    @Override
    public String findTypeName(final AnnotatedClass ac) {
        final JsonTypeName tn = this._findAnnotation(ac, JsonTypeName.class);
        return (tn == null) ? null : tn.value();
    }
    
    @Override
    public Boolean isTypeId(final AnnotatedMember member) {
        return this._hasAnnotation(member, JsonTypeId.class);
    }
    
    @Override
    public ObjectIdInfo findObjectIdInfo(final Annotated ann) {
        final JsonIdentityInfo info = this._findAnnotation(ann, JsonIdentityInfo.class);
        if (info == null || info.generator() == ObjectIdGenerators.None.class) {
            return null;
        }
        final PropertyName name = PropertyName.construct(info.property());
        return new ObjectIdInfo(name, info.scope(), info.generator(), info.resolver());
    }
    
    @Override
    public ObjectIdInfo findObjectReferenceInfo(final Annotated ann, ObjectIdInfo objectIdInfo) {
        final JsonIdentityReference ref = this._findAnnotation(ann, JsonIdentityReference.class);
        if (ref == null) {
            return objectIdInfo;
        }
        if (objectIdInfo == null) {
            objectIdInfo = ObjectIdInfo.empty();
        }
        return objectIdInfo.withAlwaysAsId(ref.alwaysAsId());
    }
    
    @Override
    public Object findSerializer(final Annotated a) {
        final JsonSerialize ann = this._findAnnotation(a, JsonSerialize.class);
        if (ann != null) {
            final Class<? extends JsonSerializer> serClass = ann.using();
            if (serClass != JsonSerializer.None.class) {
                return serClass;
            }
        }
        final JsonRawValue annRaw = this._findAnnotation(a, JsonRawValue.class);
        if (annRaw != null && annRaw.value()) {
            final Class<?> cls = a.getRawType();
            return new RawSerializer(cls);
        }
        return null;
    }
    
    @Override
    public Object findKeySerializer(final Annotated a) {
        final JsonSerialize ann = this._findAnnotation(a, JsonSerialize.class);
        if (ann != null) {
            final Class<? extends JsonSerializer> serClass = ann.keyUsing();
            if (serClass != JsonSerializer.None.class) {
                return serClass;
            }
        }
        return null;
    }
    
    @Override
    public Object findContentSerializer(final Annotated a) {
        final JsonSerialize ann = this._findAnnotation(a, JsonSerialize.class);
        if (ann != null) {
            final Class<? extends JsonSerializer> serClass = ann.contentUsing();
            if (serClass != JsonSerializer.None.class) {
                return serClass;
            }
        }
        return null;
    }
    
    @Override
    public Object findNullSerializer(final Annotated a) {
        final JsonSerialize ann = this._findAnnotation(a, JsonSerialize.class);
        if (ann != null) {
            final Class<? extends JsonSerializer> serClass = ann.nullsUsing();
            if (serClass != JsonSerializer.None.class) {
                return serClass;
            }
        }
        return null;
    }
    
    @Override
    public JsonInclude.Value findPropertyInclusion(final Annotated a) {
        final JsonInclude inc = this._findAnnotation(a, JsonInclude.class);
        JsonInclude.Value value = (inc == null) ? JsonInclude.Value.empty() : JsonInclude.Value.from(inc);
        if (value.getValueInclusion() == JsonInclude.Include.USE_DEFAULTS) {
            value = this._refinePropertyInclusion(a, value);
        }
        return value;
    }
    
    private JsonInclude.Value _refinePropertyInclusion(final Annotated a, final JsonInclude.Value value) {
        final JsonSerialize ann = this._findAnnotation(a, JsonSerialize.class);
        if (ann != null) {
            switch (ann.include()) {
                case ALWAYS: {
                    return value.withValueInclusion(JsonInclude.Include.ALWAYS);
                }
                case NON_NULL: {
                    return value.withValueInclusion(JsonInclude.Include.NON_NULL);
                }
                case NON_DEFAULT: {
                    return value.withValueInclusion(JsonInclude.Include.NON_DEFAULT);
                }
                case NON_EMPTY: {
                    return value.withValueInclusion(JsonInclude.Include.NON_EMPTY);
                }
            }
        }
        return value;
    }
    
    @Override
    public JsonSerialize.Typing findSerializationTyping(final Annotated a) {
        final JsonSerialize ann = this._findAnnotation(a, JsonSerialize.class);
        return (ann == null) ? null : ann.typing();
    }
    
    @Override
    public Object findSerializationConverter(final Annotated a) {
        final JsonSerialize ann = this._findAnnotation(a, JsonSerialize.class);
        return (ann == null) ? null : this._classIfExplicit(ann.converter(), Converter.None.class);
    }
    
    @Override
    public Object findSerializationContentConverter(final AnnotatedMember a) {
        final JsonSerialize ann = this._findAnnotation(a, JsonSerialize.class);
        return (ann == null) ? null : this._classIfExplicit(ann.contentConverter(), Converter.None.class);
    }
    
    @Override
    public JavaType refineSerializationType(final MapperConfig<?> config, final Annotated a, final JavaType baseType) throws JsonMappingException {
        JavaType type = baseType;
        final TypeFactory tf = config.getTypeFactory();
        final JsonSerialize jsonSer = this._findAnnotation(a, JsonSerialize.class);
        final Class<?> serClass = (jsonSer == null) ? null : this._classIfExplicit(jsonSer.as());
        if (serClass != null) {
            if (type.hasRawClass(serClass)) {
                type = type.withStaticTyping();
            }
            else {
                final Class<?> currRaw = type.getRawClass();
                try {
                    if (serClass.isAssignableFrom(currRaw)) {
                        type = tf.constructGeneralizedType(type, serClass);
                    }
                    else if (currRaw.isAssignableFrom(serClass)) {
                        type = tf.constructSpecializedType(type, serClass);
                    }
                    else {
                        if (!this._primitiveAndWrapper(currRaw, serClass)) {
                            throw new JsonMappingException(null, String.format("Cannot refine serialization type %s into %s; types not related", type, serClass.getName()));
                        }
                        type = type.withStaticTyping();
                    }
                }
                catch (IllegalArgumentException iae) {
                    throw new JsonMappingException(null, String.format("Failed to widen type %s with annotation (value %s), from '%s': %s", type, serClass.getName(), a.getName(), iae.getMessage()), iae);
                }
            }
        }
        if (type.isMapLikeType()) {
            JavaType keyType = type.getKeyType();
            final Class<?> keyClass = (jsonSer == null) ? null : this._classIfExplicit(jsonSer.keyAs());
            if (keyClass != null) {
                if (keyType.hasRawClass(keyClass)) {
                    keyType = keyType.withStaticTyping();
                }
                else {
                    final Class<?> currRaw2 = keyType.getRawClass();
                    try {
                        if (keyClass.isAssignableFrom(currRaw2)) {
                            keyType = tf.constructGeneralizedType(keyType, keyClass);
                        }
                        else if (currRaw2.isAssignableFrom(keyClass)) {
                            keyType = tf.constructSpecializedType(keyType, keyClass);
                        }
                        else {
                            if (!this._primitiveAndWrapper(currRaw2, keyClass)) {
                                throw new JsonMappingException(null, String.format("Cannot refine serialization key type %s into %s; types not related", keyType, keyClass.getName()));
                            }
                            keyType = keyType.withStaticTyping();
                        }
                    }
                    catch (IllegalArgumentException iae2) {
                        throw new JsonMappingException(null, String.format("Failed to widen key type of %s with concrete-type annotation (value %s), from '%s': %s", type, keyClass.getName(), a.getName(), iae2.getMessage()), iae2);
                    }
                }
                type = ((MapLikeType)type).withKeyType(keyType);
            }
        }
        JavaType contentType = type.getContentType();
        if (contentType != null) {
            final Class<?> contentClass = (jsonSer == null) ? null : this._classIfExplicit(jsonSer.contentAs());
            if (contentClass != null) {
                if (contentType.hasRawClass(contentClass)) {
                    contentType = contentType.withStaticTyping();
                }
                else {
                    final Class<?> currRaw2 = contentType.getRawClass();
                    try {
                        if (contentClass.isAssignableFrom(currRaw2)) {
                            contentType = tf.constructGeneralizedType(contentType, contentClass);
                        }
                        else if (currRaw2.isAssignableFrom(contentClass)) {
                            contentType = tf.constructSpecializedType(contentType, contentClass);
                        }
                        else {
                            if (!this._primitiveAndWrapper(currRaw2, contentClass)) {
                                throw new JsonMappingException(null, String.format("Cannot refine serialization content type %s into %s; types not related", contentType, contentClass.getName()));
                            }
                            contentType = contentType.withStaticTyping();
                        }
                    }
                    catch (IllegalArgumentException iae2) {
                        throw new JsonMappingException(null, String.format("Internal error: failed to refine value type of %s with concrete-type annotation (value %s), from '%s': %s", type, contentClass.getName(), a.getName(), iae2.getMessage()), iae2);
                    }
                }
                type = type.withContentType(contentType);
            }
        }
        return type;
    }
    
    @Deprecated
    @Override
    public Class<?> findSerializationType(final Annotated am) {
        return null;
    }
    
    @Deprecated
    @Override
    public Class<?> findSerializationKeyType(final Annotated am, final JavaType baseType) {
        return null;
    }
    
    @Deprecated
    @Override
    public Class<?> findSerializationContentType(final Annotated am, final JavaType baseType) {
        return null;
    }
    
    @Override
    public String[] findSerializationPropertyOrder(final AnnotatedClass ac) {
        final JsonPropertyOrder order = this._findAnnotation(ac, JsonPropertyOrder.class);
        return (String[])((order == null) ? null : order.value());
    }
    
    @Override
    public Boolean findSerializationSortAlphabetically(final Annotated ann) {
        return this._findSortAlpha(ann);
    }
    
    private final Boolean _findSortAlpha(final Annotated ann) {
        final JsonPropertyOrder order = this._findAnnotation(ann, JsonPropertyOrder.class);
        if (order != null && order.alphabetic()) {
            return Boolean.TRUE;
        }
        return null;
    }
    
    @Override
    public void findAndAddVirtualProperties(final MapperConfig<?> config, final AnnotatedClass ac, final List<BeanPropertyWriter> properties) {
        final JsonAppend ann = this._findAnnotation(ac, JsonAppend.class);
        if (ann == null) {
            return;
        }
        final boolean prepend = ann.prepend();
        JavaType propType = null;
        final JsonAppend.Attr[] attrs = ann.attrs();
        for (int i = 0, len = attrs.length; i < len; ++i) {
            if (propType == null) {
                propType = config.constructType(Object.class);
            }
            final BeanPropertyWriter bpw = this._constructVirtualProperty(attrs[i], config, ac, propType);
            if (prepend) {
                properties.add(i, bpw);
            }
            else {
                properties.add(bpw);
            }
        }
        final JsonAppend.Prop[] props = ann.props();
        for (int j = 0, len2 = props.length; j < len2; ++j) {
            final BeanPropertyWriter bpw2 = this._constructVirtualProperty(props[j], config, ac);
            if (prepend) {
                properties.add(j, bpw2);
            }
            else {
                properties.add(bpw2);
            }
        }
    }
    
    protected BeanPropertyWriter _constructVirtualProperty(final JsonAppend.Attr attr, final MapperConfig<?> config, final AnnotatedClass ac, final JavaType type) {
        final PropertyMetadata metadata = attr.required() ? PropertyMetadata.STD_REQUIRED : PropertyMetadata.STD_OPTIONAL;
        final String attrName = attr.value();
        PropertyName propName = this._propertyName(attr.propName(), attr.propNamespace());
        if (!propName.hasSimpleName()) {
            propName = PropertyName.construct(attrName);
        }
        final AnnotatedMember member = new VirtualAnnotatedMember(ac, ac.getRawType(), attrName, type);
        final SimpleBeanPropertyDefinition propDef = SimpleBeanPropertyDefinition.construct(config, member, propName, metadata, attr.include());
        return AttributePropertyWriter.construct(attrName, propDef, ac.getAnnotations(), type);
    }
    
    protected BeanPropertyWriter _constructVirtualProperty(final JsonAppend.Prop prop, final MapperConfig<?> config, final AnnotatedClass ac) {
        final PropertyMetadata metadata = prop.required() ? PropertyMetadata.STD_REQUIRED : PropertyMetadata.STD_OPTIONAL;
        final PropertyName propName = this._propertyName(prop.name(), prop.namespace());
        final JavaType type = config.constructType(prop.type());
        final AnnotatedMember member = new VirtualAnnotatedMember(ac, ac.getRawType(), propName.getSimpleName(), type);
        final SimpleBeanPropertyDefinition propDef = SimpleBeanPropertyDefinition.construct(config, member, propName, metadata, prop.include());
        final Class<?> implClass = prop.value();
        final HandlerInstantiator hi = config.getHandlerInstantiator();
        VirtualBeanPropertyWriter bpw = (hi == null) ? null : hi.virtualPropertyWriterInstance(config, implClass);
        if (bpw == null) {
            bpw = ClassUtil.createInstance(implClass, config.canOverrideAccessModifiers());
        }
        return bpw.withConfig(config, ac, propDef, type);
    }
    
    @Override
    public PropertyName findNameForSerialization(final Annotated a) {
        boolean useDefault = false;
        final JsonGetter jg = this._findAnnotation(a, JsonGetter.class);
        if (jg != null) {
            final String s = jg.value();
            if (!s.isEmpty()) {
                return PropertyName.construct(s);
            }
            useDefault = true;
        }
        final JsonProperty pann = this._findAnnotation(a, JsonProperty.class);
        if (pann != null) {
            return PropertyName.construct(pann.value());
        }
        if (useDefault || this._hasOneOf(a, JacksonAnnotationIntrospector.ANNOTATIONS_TO_INFER_SER)) {
            return PropertyName.USE_DEFAULT;
        }
        return null;
    }
    
    @Override
    public Boolean hasAsValue(final Annotated a) {
        final JsonValue ann = this._findAnnotation(a, JsonValue.class);
        if (ann == null) {
            return null;
        }
        return ann.value();
    }
    
    @Override
    public Boolean hasAnyGetter(final Annotated a) {
        final JsonAnyGetter ann = this._findAnnotation(a, JsonAnyGetter.class);
        if (ann == null) {
            return null;
        }
        return ann.enabled();
    }
    
    @Deprecated
    @Override
    public boolean hasAnyGetterAnnotation(final AnnotatedMethod am) {
        return this._hasAnnotation(am, JsonAnyGetter.class);
    }
    
    @Deprecated
    @Override
    public boolean hasAsValueAnnotation(final AnnotatedMethod am) {
        final JsonValue ann = this._findAnnotation(am, JsonValue.class);
        return ann != null && ann.value();
    }
    
    @Override
    public Object findDeserializer(final Annotated a) {
        final JsonDeserialize ann = this._findAnnotation(a, JsonDeserialize.class);
        if (ann != null) {
            final Class<? extends JsonDeserializer> deserClass = ann.using();
            if (deserClass != JsonDeserializer.None.class) {
                return deserClass;
            }
        }
        return null;
    }
    
    @Override
    public Object findKeyDeserializer(final Annotated a) {
        final JsonDeserialize ann = this._findAnnotation(a, JsonDeserialize.class);
        if (ann != null) {
            final Class<? extends KeyDeserializer> deserClass = ann.keyUsing();
            if (deserClass != KeyDeserializer.None.class) {
                return deserClass;
            }
        }
        return null;
    }
    
    @Override
    public Object findContentDeserializer(final Annotated a) {
        final JsonDeserialize ann = this._findAnnotation(a, JsonDeserialize.class);
        if (ann != null) {
            final Class<? extends JsonDeserializer> deserClass = ann.contentUsing();
            if (deserClass != JsonDeserializer.None.class) {
                return deserClass;
            }
        }
        return null;
    }
    
    @Override
    public Object findDeserializationConverter(final Annotated a) {
        final JsonDeserialize ann = this._findAnnotation(a, JsonDeserialize.class);
        return (ann == null) ? null : this._classIfExplicit(ann.converter(), Converter.None.class);
    }
    
    @Override
    public Object findDeserializationContentConverter(final AnnotatedMember a) {
        final JsonDeserialize ann = this._findAnnotation(a, JsonDeserialize.class);
        return (ann == null) ? null : this._classIfExplicit(ann.contentConverter(), Converter.None.class);
    }
    
    @Override
    public JavaType refineDeserializationType(final MapperConfig<?> config, final Annotated a, final JavaType baseType) throws JsonMappingException {
        JavaType type = baseType;
        final TypeFactory tf = config.getTypeFactory();
        final JsonDeserialize jsonDeser = this._findAnnotation(a, JsonDeserialize.class);
        final Class<?> valueClass = (jsonDeser == null) ? null : this._classIfExplicit(jsonDeser.as());
        if (valueClass != null && !type.hasRawClass(valueClass) && !this._primitiveAndWrapper(type, valueClass)) {
            try {
                type = tf.constructSpecializedType(type, valueClass);
            }
            catch (IllegalArgumentException iae) {
                throw new JsonMappingException(null, String.format("Failed to narrow type %s with annotation (value %s), from '%s': %s", type, valueClass.getName(), a.getName(), iae.getMessage()), iae);
            }
        }
        if (type.isMapLikeType()) {
            JavaType keyType = type.getKeyType();
            final Class<?> keyClass = (jsonDeser == null) ? null : this._classIfExplicit(jsonDeser.keyAs());
            if (keyClass != null && !this._primitiveAndWrapper(keyType, keyClass)) {
                try {
                    keyType = tf.constructSpecializedType(keyType, keyClass);
                    type = ((MapLikeType)type).withKeyType(keyType);
                }
                catch (IllegalArgumentException iae2) {
                    throw new JsonMappingException(null, String.format("Failed to narrow key type of %s with concrete-type annotation (value %s), from '%s': %s", type, keyClass.getName(), a.getName(), iae2.getMessage()), iae2);
                }
            }
        }
        JavaType contentType = type.getContentType();
        if (contentType != null) {
            final Class<?> contentClass = (jsonDeser == null) ? null : this._classIfExplicit(jsonDeser.contentAs());
            if (contentClass != null && !this._primitiveAndWrapper(contentType, contentClass)) {
                try {
                    contentType = tf.constructSpecializedType(contentType, contentClass);
                    type = type.withContentType(contentType);
                }
                catch (IllegalArgumentException iae2) {
                    throw new JsonMappingException(null, String.format("Failed to narrow value type of %s with concrete-type annotation (value %s), from '%s': %s", type, contentClass.getName(), a.getName(), iae2.getMessage()), iae2);
                }
            }
        }
        return type;
    }
    
    @Deprecated
    @Override
    public Class<?> findDeserializationContentType(final Annotated am, final JavaType baseContentType) {
        return null;
    }
    
    @Deprecated
    @Override
    public Class<?> findDeserializationType(final Annotated am, final JavaType baseType) {
        return null;
    }
    
    @Deprecated
    @Override
    public Class<?> findDeserializationKeyType(final Annotated am, final JavaType baseKeyType) {
        return null;
    }
    
    @Override
    public Object findValueInstantiator(final AnnotatedClass ac) {
        final JsonValueInstantiator ann = this._findAnnotation(ac, JsonValueInstantiator.class);
        return (ann == null) ? null : ann.value();
    }
    
    @Override
    public Class<?> findPOJOBuilder(final AnnotatedClass ac) {
        final JsonDeserialize ann = this._findAnnotation(ac, JsonDeserialize.class);
        return (ann == null) ? null : this._classIfExplicit(ann.builder());
    }
    
    @Override
    public JsonPOJOBuilder.Value findPOJOBuilderConfig(final AnnotatedClass ac) {
        final JsonPOJOBuilder ann = this._findAnnotation(ac, JsonPOJOBuilder.class);
        return (ann == null) ? null : new JsonPOJOBuilder.Value(ann);
    }
    
    @Override
    public PropertyName findNameForDeserialization(final Annotated a) {
        boolean useDefault = false;
        final JsonSetter js = this._findAnnotation(a, JsonSetter.class);
        if (js != null) {
            final String s = js.value();
            if (!s.isEmpty()) {
                return PropertyName.construct(s);
            }
            useDefault = true;
        }
        final JsonProperty pann = this._findAnnotation(a, JsonProperty.class);
        if (pann != null) {
            return PropertyName.construct(pann.value());
        }
        if (useDefault || this._hasOneOf(a, JacksonAnnotationIntrospector.ANNOTATIONS_TO_INFER_DESER)) {
            return PropertyName.USE_DEFAULT;
        }
        return null;
    }
    
    @Override
    public Boolean hasAnySetter(final Annotated a) {
        final JsonAnySetter ann = this._findAnnotation(a, JsonAnySetter.class);
        return (ann == null) ? null : Boolean.valueOf(ann.enabled());
    }
    
    @Override
    public JsonSetter.Value findSetterInfo(final Annotated a) {
        return JsonSetter.Value.from(this._findAnnotation(a, JsonSetter.class));
    }
    
    @Override
    public Boolean findMergeInfo(final Annotated a) {
        final JsonMerge ann = this._findAnnotation(a, JsonMerge.class);
        return (ann == null) ? null : ann.value().asBoolean();
    }
    
    @Deprecated
    @Override
    public boolean hasAnySetterAnnotation(final AnnotatedMethod am) {
        return this._hasAnnotation(am, JsonAnySetter.class);
    }
    
    @Deprecated
    @Override
    public boolean hasCreatorAnnotation(final Annotated a) {
        final JsonCreator ann = this._findAnnotation(a, JsonCreator.class);
        if (ann != null) {
            return ann.mode() != JsonCreator.Mode.DISABLED;
        }
        if (this._cfgConstructorPropertiesImpliesCreator && a instanceof AnnotatedConstructor && JacksonAnnotationIntrospector._java7Helper != null) {
            final Boolean b = JacksonAnnotationIntrospector._java7Helper.hasCreatorAnnotation(a);
            if (b != null) {
                return b;
            }
        }
        return false;
    }
    
    @Deprecated
    @Override
    public JsonCreator.Mode findCreatorBinding(final Annotated a) {
        final JsonCreator ann = this._findAnnotation(a, JsonCreator.class);
        return (ann == null) ? null : ann.mode();
    }
    
    @Override
    public JsonCreator.Mode findCreatorAnnotation(final MapperConfig<?> config, final Annotated a) {
        final JsonCreator ann = this._findAnnotation(a, JsonCreator.class);
        if (ann != null) {
            return ann.mode();
        }
        if (this._cfgConstructorPropertiesImpliesCreator && config.isEnabled(MapperFeature.INFER_CREATOR_FROM_CONSTRUCTOR_PROPERTIES) && a instanceof AnnotatedConstructor && JacksonAnnotationIntrospector._java7Helper != null) {
            final Boolean b = JacksonAnnotationIntrospector._java7Helper.hasCreatorAnnotation(a);
            if (b != null && b) {
                return JsonCreator.Mode.PROPERTIES;
            }
        }
        return null;
    }
    
    protected boolean _isIgnorable(final Annotated a) {
        final JsonIgnore ann = this._findAnnotation(a, JsonIgnore.class);
        if (ann != null) {
            return ann.value();
        }
        if (JacksonAnnotationIntrospector._java7Helper != null) {
            final Boolean b = JacksonAnnotationIntrospector._java7Helper.findTransient(a);
            if (b != null) {
                return b;
            }
        }
        return false;
    }
    
    protected Class<?> _classIfExplicit(final Class<?> cls) {
        if (cls == null || ClassUtil.isBogusClass(cls)) {
            return null;
        }
        return cls;
    }
    
    protected Class<?> _classIfExplicit(Class<?> cls, final Class<?> implicit) {
        cls = this._classIfExplicit(cls);
        return (cls == null || cls == implicit) ? null : cls;
    }
    
    protected PropertyName _propertyName(final String localName, final String namespace) {
        if (localName.isEmpty()) {
            return PropertyName.USE_DEFAULT;
        }
        if (namespace == null || namespace.isEmpty()) {
            return PropertyName.construct(localName);
        }
        return PropertyName.construct(localName, namespace);
    }
    
    protected PropertyName _findConstructorName(final Annotated a) {
        if (a instanceof AnnotatedParameter) {
            final AnnotatedParameter p = (AnnotatedParameter)a;
            final AnnotatedWithParams ctor = p.getOwner();
            if (ctor != null && JacksonAnnotationIntrospector._java7Helper != null) {
                final PropertyName name = JacksonAnnotationIntrospector._java7Helper.findConstructorName(p);
                if (name != null) {
                    return name;
                }
            }
        }
        return null;
    }
    
    protected TypeResolverBuilder<?> _findTypeResolver(final MapperConfig<?> config, final Annotated ann, final JavaType baseType) {
        final JsonTypeInfo info = this._findAnnotation(ann, JsonTypeInfo.class);
        final JsonTypeResolver resAnn = this._findAnnotation(ann, JsonTypeResolver.class);
        TypeResolverBuilder<?> b;
        if (resAnn != null) {
            if (info == null) {
                return null;
            }
            b = config.typeResolverBuilderInstance(ann, resAnn.value());
        }
        else {
            if (info == null) {
                return null;
            }
            if (info.use() == JsonTypeInfo.Id.NONE) {
                return this._constructNoTypeResolverBuilder();
            }
            b = this._constructStdTypeResolverBuilder();
        }
        final JsonTypeIdResolver idResInfo = this._findAnnotation(ann, JsonTypeIdResolver.class);
        final TypeIdResolver idRes = (idResInfo == null) ? null : config.typeIdResolverInstance(ann, idResInfo.value());
        if (idRes != null) {
            idRes.init(baseType);
        }
        b = (TypeResolverBuilder<?>)b.init(info.use(), idRes);
        JsonTypeInfo.As inclusion = info.include();
        if (inclusion == JsonTypeInfo.As.EXTERNAL_PROPERTY && ann instanceof AnnotatedClass) {
            inclusion = JsonTypeInfo.As.PROPERTY;
        }
        b = (TypeResolverBuilder<?>)b.inclusion(inclusion);
        b = (TypeResolverBuilder<?>)b.typeProperty(info.property());
        final Class<?> defaultImpl = info.defaultImpl();
        if (defaultImpl != JsonTypeInfo.None.class && !defaultImpl.isAnnotation()) {
            b = (TypeResolverBuilder<?>)b.defaultImpl(defaultImpl);
        }
        b = (TypeResolverBuilder<?>)b.typeIdVisibility(info.visible());
        return b;
    }
    
    protected StdTypeResolverBuilder _constructStdTypeResolverBuilder() {
        return new StdTypeResolverBuilder();
    }
    
    protected StdTypeResolverBuilder _constructNoTypeResolverBuilder() {
        return StdTypeResolverBuilder.noTypeInfoBuilder();
    }
    
    private boolean _primitiveAndWrapper(final Class<?> baseType, final Class<?> refinement) {
        if (baseType.isPrimitive()) {
            return baseType == ClassUtil.primitiveType(refinement);
        }
        return refinement.isPrimitive() && refinement == ClassUtil.primitiveType(baseType);
    }
    
    private boolean _primitiveAndWrapper(final JavaType baseType, final Class<?> refinement) {
        if (baseType.isPrimitive()) {
            return baseType.hasRawClass(ClassUtil.primitiveType(refinement));
        }
        return refinement.isPrimitive() && refinement == ClassUtil.primitiveType(baseType.getRawClass());
    }
    
    static {
        ANNOTATIONS_TO_INFER_SER = new Class[] { JsonSerialize.class, JsonView.class, JsonFormat.class, JsonTypeInfo.class, JsonRawValue.class, JsonUnwrapped.class, JsonBackReference.class, JsonManagedReference.class };
        ANNOTATIONS_TO_INFER_DESER = new Class[] { JsonDeserialize.class, JsonView.class, JsonFormat.class, JsonTypeInfo.class, JsonUnwrapped.class, JsonBackReference.class, JsonManagedReference.class, JsonMerge.class };
        Java7Support x = null;
        try {
            x = Java7Support.instance();
        }
        catch (Throwable t) {}
        _java7Helper = x;
    }
}
