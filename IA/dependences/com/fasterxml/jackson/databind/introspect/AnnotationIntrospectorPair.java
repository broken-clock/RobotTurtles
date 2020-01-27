// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import java.util.List;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyName;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import com.fasterxml.jackson.core.Version;
import java.io.Serializable;
import com.fasterxml.jackson.databind.AnnotationIntrospector;

public class AnnotationIntrospectorPair extends AnnotationIntrospector implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final AnnotationIntrospector _primary;
    protected final AnnotationIntrospector _secondary;
    
    public AnnotationIntrospectorPair(final AnnotationIntrospector p, final AnnotationIntrospector s) {
        this._primary = p;
        this._secondary = s;
    }
    
    @Override
    public Version version() {
        return this._primary.version();
    }
    
    public static AnnotationIntrospector create(final AnnotationIntrospector primary, final AnnotationIntrospector secondary) {
        if (primary == null) {
            return secondary;
        }
        if (secondary == null) {
            return primary;
        }
        return new AnnotationIntrospectorPair(primary, secondary);
    }
    
    @Override
    public Collection<AnnotationIntrospector> allIntrospectors() {
        return this.allIntrospectors(new ArrayList<AnnotationIntrospector>());
    }
    
    @Override
    public Collection<AnnotationIntrospector> allIntrospectors(final Collection<AnnotationIntrospector> result) {
        this._primary.allIntrospectors(result);
        this._secondary.allIntrospectors(result);
        return result;
    }
    
    @Override
    public boolean isAnnotationBundle(final Annotation ann) {
        return this._primary.isAnnotationBundle(ann) || this._secondary.isAnnotationBundle(ann);
    }
    
    @Override
    public PropertyName findRootName(final AnnotatedClass ac) {
        final PropertyName name1 = this._primary.findRootName(ac);
        if (name1 == null) {
            return this._secondary.findRootName(ac);
        }
        if (name1.hasSimpleName()) {
            return name1;
        }
        final PropertyName name2 = this._secondary.findRootName(ac);
        return (name2 == null) ? name1 : name2;
    }
    
    @Override
    public JsonIgnoreProperties.Value findPropertyIgnorals(final Annotated a) {
        final JsonIgnoreProperties.Value v2 = this._secondary.findPropertyIgnorals(a);
        final JsonIgnoreProperties.Value v3 = this._primary.findPropertyIgnorals(a);
        return (v2 == null) ? v3 : v2.withOverrides(v3);
    }
    
    @Override
    public Boolean isIgnorableType(final AnnotatedClass ac) {
        Boolean result = this._primary.isIgnorableType(ac);
        if (result == null) {
            result = this._secondary.isIgnorableType(ac);
        }
        return result;
    }
    
    @Override
    public Object findFilterId(final Annotated ann) {
        Object id = this._primary.findFilterId(ann);
        if (id == null) {
            id = this._secondary.findFilterId(ann);
        }
        return id;
    }
    
    @Override
    public Object findNamingStrategy(final AnnotatedClass ac) {
        Object str = this._primary.findNamingStrategy(ac);
        if (str == null) {
            str = this._secondary.findNamingStrategy(ac);
        }
        return str;
    }
    
    @Override
    public String findClassDescription(final AnnotatedClass ac) {
        String str = this._primary.findClassDescription(ac);
        if (str == null || str.isEmpty()) {
            str = this._secondary.findClassDescription(ac);
        }
        return str;
    }
    
    @Deprecated
    @Override
    public String[] findPropertiesToIgnore(final Annotated ac) {
        String[] result = this._primary.findPropertiesToIgnore(ac);
        if (result == null) {
            result = this._secondary.findPropertiesToIgnore(ac);
        }
        return result;
    }
    
    @Deprecated
    @Override
    public String[] findPropertiesToIgnore(final Annotated ac, final boolean forSerialization) {
        String[] result = this._primary.findPropertiesToIgnore(ac, forSerialization);
        if (result == null) {
            result = this._secondary.findPropertiesToIgnore(ac, forSerialization);
        }
        return result;
    }
    
    @Deprecated
    @Override
    public Boolean findIgnoreUnknownProperties(final AnnotatedClass ac) {
        Boolean result = this._primary.findIgnoreUnknownProperties(ac);
        if (result == null) {
            result = this._secondary.findIgnoreUnknownProperties(ac);
        }
        return result;
    }
    
    @Override
    public VisibilityChecker<?> findAutoDetectVisibility(final AnnotatedClass ac, VisibilityChecker<?> checker) {
        checker = this._secondary.findAutoDetectVisibility(ac, checker);
        return this._primary.findAutoDetectVisibility(ac, checker);
    }
    
    @Override
    public TypeResolverBuilder<?> findTypeResolver(final MapperConfig<?> config, final AnnotatedClass ac, final JavaType baseType) {
        TypeResolverBuilder<?> b = this._primary.findTypeResolver(config, ac, baseType);
        if (b == null) {
            b = this._secondary.findTypeResolver(config, ac, baseType);
        }
        return b;
    }
    
    @Override
    public TypeResolverBuilder<?> findPropertyTypeResolver(final MapperConfig<?> config, final AnnotatedMember am, final JavaType baseType) {
        TypeResolverBuilder<?> b = this._primary.findPropertyTypeResolver(config, am, baseType);
        if (b == null) {
            b = this._secondary.findPropertyTypeResolver(config, am, baseType);
        }
        return b;
    }
    
    @Override
    public TypeResolverBuilder<?> findPropertyContentTypeResolver(final MapperConfig<?> config, final AnnotatedMember am, final JavaType baseType) {
        TypeResolverBuilder<?> b = this._primary.findPropertyContentTypeResolver(config, am, baseType);
        if (b == null) {
            b = this._secondary.findPropertyContentTypeResolver(config, am, baseType);
        }
        return b;
    }
    
    @Override
    public List<NamedType> findSubtypes(final Annotated a) {
        final List<NamedType> types1 = this._primary.findSubtypes(a);
        final List<NamedType> types2 = this._secondary.findSubtypes(a);
        if (types1 == null || types1.isEmpty()) {
            return types2;
        }
        if (types2 == null || types2.isEmpty()) {
            return types1;
        }
        final ArrayList<NamedType> result = new ArrayList<NamedType>(types1.size() + types2.size());
        result.addAll(types1);
        result.addAll(types2);
        return result;
    }
    
    @Override
    public String findTypeName(final AnnotatedClass ac) {
        String name = this._primary.findTypeName(ac);
        if (name == null || name.length() == 0) {
            name = this._secondary.findTypeName(ac);
        }
        return name;
    }
    
    @Override
    public ReferenceProperty findReferenceType(final AnnotatedMember member) {
        final ReferenceProperty r = this._primary.findReferenceType(member);
        return (r == null) ? this._secondary.findReferenceType(member) : r;
    }
    
    @Override
    public NameTransformer findUnwrappingNameTransformer(final AnnotatedMember member) {
        final NameTransformer r = this._primary.findUnwrappingNameTransformer(member);
        return (r == null) ? this._secondary.findUnwrappingNameTransformer(member) : r;
    }
    
    @Override
    public JacksonInject.Value findInjectableValue(final AnnotatedMember m) {
        final JacksonInject.Value r = this._primary.findInjectableValue(m);
        return (r == null) ? this._secondary.findInjectableValue(m) : r;
    }
    
    @Override
    public boolean hasIgnoreMarker(final AnnotatedMember m) {
        return this._primary.hasIgnoreMarker(m) || this._secondary.hasIgnoreMarker(m);
    }
    
    @Override
    public Boolean hasRequiredMarker(final AnnotatedMember m) {
        final Boolean r = this._primary.hasRequiredMarker(m);
        return (r == null) ? this._secondary.hasRequiredMarker(m) : r;
    }
    
    @Deprecated
    @Override
    public Object findInjectableValueId(final AnnotatedMember m) {
        final Object r = this._primary.findInjectableValueId(m);
        return (r == null) ? this._secondary.findInjectableValueId(m) : r;
    }
    
    @Override
    public Object findSerializer(final Annotated am) {
        final Object r = this._primary.findSerializer(am);
        if (this._isExplicitClassOrOb(r, JsonSerializer.None.class)) {
            return r;
        }
        return this._explicitClassOrOb(this._secondary.findSerializer(am), JsonSerializer.None.class);
    }
    
    @Override
    public Object findKeySerializer(final Annotated a) {
        final Object r = this._primary.findKeySerializer(a);
        if (this._isExplicitClassOrOb(r, JsonSerializer.None.class)) {
            return r;
        }
        return this._explicitClassOrOb(this._secondary.findKeySerializer(a), JsonSerializer.None.class);
    }
    
    @Override
    public Object findContentSerializer(final Annotated a) {
        final Object r = this._primary.findContentSerializer(a);
        if (this._isExplicitClassOrOb(r, JsonSerializer.None.class)) {
            return r;
        }
        return this._explicitClassOrOb(this._secondary.findContentSerializer(a), JsonSerializer.None.class);
    }
    
    @Override
    public Object findNullSerializer(final Annotated a) {
        final Object r = this._primary.findNullSerializer(a);
        if (this._isExplicitClassOrOb(r, JsonSerializer.None.class)) {
            return r;
        }
        return this._explicitClassOrOb(this._secondary.findNullSerializer(a), JsonSerializer.None.class);
    }
    
    @Deprecated
    @Override
    public JsonInclude.Include findSerializationInclusion(final Annotated a, JsonInclude.Include defValue) {
        defValue = this._secondary.findSerializationInclusion(a, defValue);
        defValue = this._primary.findSerializationInclusion(a, defValue);
        return defValue;
    }
    
    @Deprecated
    @Override
    public JsonInclude.Include findSerializationInclusionForContent(final Annotated a, JsonInclude.Include defValue) {
        defValue = this._secondary.findSerializationInclusionForContent(a, defValue);
        defValue = this._primary.findSerializationInclusionForContent(a, defValue);
        return defValue;
    }
    
    @Override
    public JsonInclude.Value findPropertyInclusion(final Annotated a) {
        final JsonInclude.Value v2 = this._secondary.findPropertyInclusion(a);
        final JsonInclude.Value v3 = this._primary.findPropertyInclusion(a);
        if (v2 == null) {
            return v3;
        }
        return v2.withOverrides(v3);
    }
    
    @Override
    public JsonSerialize.Typing findSerializationTyping(final Annotated a) {
        final JsonSerialize.Typing r = this._primary.findSerializationTyping(a);
        return (r == null) ? this._secondary.findSerializationTyping(a) : r;
    }
    
    @Override
    public Object findSerializationConverter(final Annotated a) {
        final Object r = this._primary.findSerializationConverter(a);
        return (r == null) ? this._secondary.findSerializationConverter(a) : r;
    }
    
    @Override
    public Object findSerializationContentConverter(final AnnotatedMember a) {
        final Object r = this._primary.findSerializationContentConverter(a);
        return (r == null) ? this._secondary.findSerializationContentConverter(a) : r;
    }
    
    @Override
    public Class<?>[] findViews(final Annotated a) {
        Class<?>[] result = this._primary.findViews(a);
        if (result == null) {
            result = this._secondary.findViews(a);
        }
        return result;
    }
    
    @Override
    public Boolean isTypeId(final AnnotatedMember member) {
        final Boolean b = this._primary.isTypeId(member);
        return (b == null) ? this._secondary.isTypeId(member) : b;
    }
    
    @Override
    public ObjectIdInfo findObjectIdInfo(final Annotated ann) {
        final ObjectIdInfo r = this._primary.findObjectIdInfo(ann);
        return (r == null) ? this._secondary.findObjectIdInfo(ann) : r;
    }
    
    @Override
    public ObjectIdInfo findObjectReferenceInfo(final Annotated ann, ObjectIdInfo objectIdInfo) {
        objectIdInfo = this._secondary.findObjectReferenceInfo(ann, objectIdInfo);
        objectIdInfo = this._primary.findObjectReferenceInfo(ann, objectIdInfo);
        return objectIdInfo;
    }
    
    @Override
    public JsonFormat.Value findFormat(final Annotated ann) {
        final JsonFormat.Value v1 = this._primary.findFormat(ann);
        final JsonFormat.Value v2 = this._secondary.findFormat(ann);
        if (v2 == null) {
            return v1;
        }
        return v2.withOverrides(v1);
    }
    
    @Override
    public PropertyName findWrapperName(final Annotated ann) {
        PropertyName name = this._primary.findWrapperName(ann);
        if (name == null) {
            name = this._secondary.findWrapperName(ann);
        }
        else if (name == PropertyName.USE_DEFAULT) {
            final PropertyName name2 = this._secondary.findWrapperName(ann);
            if (name2 != null) {
                name = name2;
            }
        }
        return name;
    }
    
    @Override
    public String findPropertyDefaultValue(final Annotated ann) {
        final String str = this._primary.findPropertyDefaultValue(ann);
        return (str == null || str.isEmpty()) ? this._secondary.findPropertyDefaultValue(ann) : str;
    }
    
    @Override
    public String findPropertyDescription(final Annotated ann) {
        final String r = this._primary.findPropertyDescription(ann);
        return (r == null) ? this._secondary.findPropertyDescription(ann) : r;
    }
    
    @Override
    public Integer findPropertyIndex(final Annotated ann) {
        final Integer r = this._primary.findPropertyIndex(ann);
        return (r == null) ? this._secondary.findPropertyIndex(ann) : r;
    }
    
    @Override
    public String findImplicitPropertyName(final AnnotatedMember ann) {
        final String r = this._primary.findImplicitPropertyName(ann);
        return (r == null) ? this._secondary.findImplicitPropertyName(ann) : r;
    }
    
    @Override
    public List<PropertyName> findPropertyAliases(final Annotated ann) {
        final List<PropertyName> r = this._primary.findPropertyAliases(ann);
        return (r == null) ? this._secondary.findPropertyAliases(ann) : r;
    }
    
    @Override
    public JsonProperty.Access findPropertyAccess(final Annotated ann) {
        JsonProperty.Access acc = this._primary.findPropertyAccess(ann);
        if (acc != null && acc != JsonProperty.Access.AUTO) {
            return acc;
        }
        acc = this._secondary.findPropertyAccess(ann);
        if (acc != null) {
            return acc;
        }
        return JsonProperty.Access.AUTO;
    }
    
    @Override
    public AnnotatedMethod resolveSetterConflict(final MapperConfig<?> config, final AnnotatedMethod setter1, final AnnotatedMethod setter2) {
        AnnotatedMethod res = this._primary.resolveSetterConflict(config, setter1, setter2);
        if (res == null) {
            res = this._secondary.resolveSetterConflict(config, setter1, setter2);
        }
        return res;
    }
    
    @Override
    public JavaType refineSerializationType(final MapperConfig<?> config, final Annotated a, final JavaType baseType) throws JsonMappingException {
        final JavaType t = this._secondary.refineSerializationType(config, a, baseType);
        return this._primary.refineSerializationType(config, a, t);
    }
    
    @Deprecated
    @Override
    public Class<?> findSerializationType(final Annotated a) {
        final Class<?> r = this._primary.findSerializationType(a);
        return (r == null) ? this._secondary.findSerializationType(a) : r;
    }
    
    @Deprecated
    @Override
    public Class<?> findSerializationKeyType(final Annotated am, final JavaType baseType) {
        final Class<?> r = this._primary.findSerializationKeyType(am, baseType);
        return (r == null) ? this._secondary.findSerializationKeyType(am, baseType) : r;
    }
    
    @Deprecated
    @Override
    public Class<?> findSerializationContentType(final Annotated am, final JavaType baseType) {
        final Class<?> r = this._primary.findSerializationContentType(am, baseType);
        return (r == null) ? this._secondary.findSerializationContentType(am, baseType) : r;
    }
    
    @Override
    public String[] findSerializationPropertyOrder(final AnnotatedClass ac) {
        final String[] r = this._primary.findSerializationPropertyOrder(ac);
        return (r == null) ? this._secondary.findSerializationPropertyOrder(ac) : r;
    }
    
    @Override
    public Boolean findSerializationSortAlphabetically(final Annotated ann) {
        final Boolean r = this._primary.findSerializationSortAlphabetically(ann);
        return (r == null) ? this._secondary.findSerializationSortAlphabetically(ann) : r;
    }
    
    @Override
    public void findAndAddVirtualProperties(final MapperConfig<?> config, final AnnotatedClass ac, final List<BeanPropertyWriter> properties) {
        this._primary.findAndAddVirtualProperties(config, ac, properties);
        this._secondary.findAndAddVirtualProperties(config, ac, properties);
    }
    
    @Override
    public PropertyName findNameForSerialization(final Annotated a) {
        PropertyName n = this._primary.findNameForSerialization(a);
        if (n == null) {
            n = this._secondary.findNameForSerialization(a);
        }
        else if (n == PropertyName.USE_DEFAULT) {
            final PropertyName n2 = this._secondary.findNameForSerialization(a);
            if (n2 != null) {
                n = n2;
            }
        }
        return n;
    }
    
    @Override
    public Boolean hasAsValue(final Annotated a) {
        Boolean b = this._primary.hasAsValue(a);
        if (b == null) {
            b = this._secondary.hasAsValue(a);
        }
        return b;
    }
    
    @Override
    public Boolean hasAnyGetter(final Annotated a) {
        Boolean b = this._primary.hasAnyGetter(a);
        if (b == null) {
            b = this._secondary.hasAnyGetter(a);
        }
        return b;
    }
    
    @Override
    public String[] findEnumValues(final Class<?> enumType, final Enum<?>[] enumValues, String[] names) {
        names = this._secondary.findEnumValues(enumType, enumValues, names);
        names = this._primary.findEnumValues(enumType, enumValues, names);
        return names;
    }
    
    @Override
    public Enum<?> findDefaultEnumValue(final Class<Enum<?>> enumCls) {
        final Enum<?> en = this._primary.findDefaultEnumValue(enumCls);
        return (en == null) ? this._secondary.findDefaultEnumValue(enumCls) : en;
    }
    
    @Deprecated
    @Override
    public String findEnumValue(final Enum<?> value) {
        final String r = this._primary.findEnumValue(value);
        return (r == null) ? this._secondary.findEnumValue(value) : r;
    }
    
    @Deprecated
    @Override
    public boolean hasAsValueAnnotation(final AnnotatedMethod am) {
        return this._primary.hasAsValueAnnotation(am) || this._secondary.hasAsValueAnnotation(am);
    }
    
    @Deprecated
    @Override
    public boolean hasAnyGetterAnnotation(final AnnotatedMethod am) {
        return this._primary.hasAnyGetterAnnotation(am) || this._secondary.hasAnyGetterAnnotation(am);
    }
    
    @Override
    public Object findDeserializer(final Annotated a) {
        final Object r = this._primary.findDeserializer(a);
        if (this._isExplicitClassOrOb(r, JsonDeserializer.None.class)) {
            return r;
        }
        return this._explicitClassOrOb(this._secondary.findDeserializer(a), JsonDeserializer.None.class);
    }
    
    @Override
    public Object findKeyDeserializer(final Annotated a) {
        final Object r = this._primary.findKeyDeserializer(a);
        if (this._isExplicitClassOrOb(r, KeyDeserializer.None.class)) {
            return r;
        }
        return this._explicitClassOrOb(this._secondary.findKeyDeserializer(a), KeyDeserializer.None.class);
    }
    
    @Override
    public Object findContentDeserializer(final Annotated am) {
        final Object r = this._primary.findContentDeserializer(am);
        if (this._isExplicitClassOrOb(r, JsonDeserializer.None.class)) {
            return r;
        }
        return this._explicitClassOrOb(this._secondary.findContentDeserializer(am), JsonDeserializer.None.class);
    }
    
    @Override
    public Object findDeserializationConverter(final Annotated a) {
        final Object ob = this._primary.findDeserializationConverter(a);
        return (ob == null) ? this._secondary.findDeserializationConverter(a) : ob;
    }
    
    @Override
    public Object findDeserializationContentConverter(final AnnotatedMember a) {
        final Object ob = this._primary.findDeserializationContentConverter(a);
        return (ob == null) ? this._secondary.findDeserializationContentConverter(a) : ob;
    }
    
    @Override
    public JavaType refineDeserializationType(final MapperConfig<?> config, final Annotated a, final JavaType baseType) throws JsonMappingException {
        final JavaType t = this._secondary.refineDeserializationType(config, a, baseType);
        return this._primary.refineDeserializationType(config, a, t);
    }
    
    @Deprecated
    @Override
    public Class<?> findDeserializationType(final Annotated am, final JavaType baseType) {
        final Class<?> r = this._primary.findDeserializationType(am, baseType);
        return (r != null) ? r : this._secondary.findDeserializationType(am, baseType);
    }
    
    @Deprecated
    @Override
    public Class<?> findDeserializationKeyType(final Annotated am, final JavaType baseKeyType) {
        final Class<?> result = this._primary.findDeserializationKeyType(am, baseKeyType);
        return (result == null) ? this._secondary.findDeserializationKeyType(am, baseKeyType) : result;
    }
    
    @Deprecated
    @Override
    public Class<?> findDeserializationContentType(final Annotated am, final JavaType baseContentType) {
        final Class<?> result = this._primary.findDeserializationContentType(am, baseContentType);
        return (result == null) ? this._secondary.findDeserializationContentType(am, baseContentType) : result;
    }
    
    @Override
    public Object findValueInstantiator(final AnnotatedClass ac) {
        final Object result = this._primary.findValueInstantiator(ac);
        return (result == null) ? this._secondary.findValueInstantiator(ac) : result;
    }
    
    @Override
    public Class<?> findPOJOBuilder(final AnnotatedClass ac) {
        final Class<?> result = this._primary.findPOJOBuilder(ac);
        return (result == null) ? this._secondary.findPOJOBuilder(ac) : result;
    }
    
    @Override
    public JsonPOJOBuilder.Value findPOJOBuilderConfig(final AnnotatedClass ac) {
        final JsonPOJOBuilder.Value result = this._primary.findPOJOBuilderConfig(ac);
        return (result == null) ? this._secondary.findPOJOBuilderConfig(ac) : result;
    }
    
    @Override
    public PropertyName findNameForDeserialization(final Annotated a) {
        PropertyName n = this._primary.findNameForDeserialization(a);
        if (n == null) {
            n = this._secondary.findNameForDeserialization(a);
        }
        else if (n == PropertyName.USE_DEFAULT) {
            final PropertyName n2 = this._secondary.findNameForDeserialization(a);
            if (n2 != null) {
                n = n2;
            }
        }
        return n;
    }
    
    @Override
    public Boolean hasAnySetter(final Annotated a) {
        Boolean b = this._primary.hasAnySetter(a);
        if (b == null) {
            b = this._secondary.hasAnySetter(a);
        }
        return b;
    }
    
    @Override
    public JsonSetter.Value findSetterInfo(final Annotated a) {
        final JsonSetter.Value v2 = this._secondary.findSetterInfo(a);
        final JsonSetter.Value v3 = this._primary.findSetterInfo(a);
        return (v2 == null) ? v3 : v2.withOverrides(v3);
    }
    
    @Override
    public Boolean findMergeInfo(final Annotated a) {
        Boolean b = this._primary.findMergeInfo(a);
        if (b == null) {
            b = this._secondary.findMergeInfo(a);
        }
        return b;
    }
    
    @Deprecated
    @Override
    public boolean hasCreatorAnnotation(final Annotated a) {
        return this._primary.hasCreatorAnnotation(a) || this._secondary.hasCreatorAnnotation(a);
    }
    
    @Deprecated
    @Override
    public JsonCreator.Mode findCreatorBinding(final Annotated a) {
        final JsonCreator.Mode mode = this._primary.findCreatorBinding(a);
        if (mode != null) {
            return mode;
        }
        return this._secondary.findCreatorBinding(a);
    }
    
    @Override
    public JsonCreator.Mode findCreatorAnnotation(final MapperConfig<?> config, final Annotated a) {
        final JsonCreator.Mode mode = this._primary.findCreatorAnnotation(config, a);
        return (mode == null) ? this._secondary.findCreatorAnnotation(config, a) : mode;
    }
    
    @Deprecated
    @Override
    public boolean hasAnySetterAnnotation(final AnnotatedMethod am) {
        return this._primary.hasAnySetterAnnotation(am) || this._secondary.hasAnySetterAnnotation(am);
    }
    
    protected boolean _isExplicitClassOrOb(final Object maybeCls, final Class<?> implicit) {
        return maybeCls != null && maybeCls != implicit && (!(maybeCls instanceof Class) || !ClassUtil.isBogusClass((Class<?>)maybeCls));
    }
    
    protected Object _explicitClassOrOb(final Object maybeCls, final Class<?> implicit) {
        if (maybeCls == null || maybeCls == implicit) {
            return null;
        }
        if (maybeCls instanceof Class && ClassUtil.isBogusClass((Class<?>)maybeCls)) {
            return null;
        }
        return maybeCls;
    }
}
