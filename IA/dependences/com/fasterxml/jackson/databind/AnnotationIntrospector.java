// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import java.util.List;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.introspect.Annotated;
import java.lang.annotation.Annotation;
import com.fasterxml.jackson.core.Version;
import java.util.Collections;
import java.util.Collection;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;
import java.io.Serializable;
import com.fasterxml.jackson.core.Versioned;

public abstract class AnnotationIntrospector implements Versioned, Serializable
{
    public static AnnotationIntrospector nopInstance() {
        return NopAnnotationIntrospector.instance;
    }
    
    public static AnnotationIntrospector pair(final AnnotationIntrospector a1, final AnnotationIntrospector a2) {
        return new AnnotationIntrospectorPair(a1, a2);
    }
    
    public Collection<AnnotationIntrospector> allIntrospectors() {
        return Collections.singletonList(this);
    }
    
    public Collection<AnnotationIntrospector> allIntrospectors(final Collection<AnnotationIntrospector> result) {
        result.add(this);
        return result;
    }
    
    @Override
    public abstract Version version();
    
    public boolean isAnnotationBundle(final Annotation ann) {
        return false;
    }
    
    public ObjectIdInfo findObjectIdInfo(final Annotated ann) {
        return null;
    }
    
    public ObjectIdInfo findObjectReferenceInfo(final Annotated ann, final ObjectIdInfo objectIdInfo) {
        return objectIdInfo;
    }
    
    public PropertyName findRootName(final AnnotatedClass ac) {
        return null;
    }
    
    public JsonIgnoreProperties.Value findPropertyIgnorals(final Annotated ac) {
        return JsonIgnoreProperties.Value.empty();
    }
    
    public Boolean isIgnorableType(final AnnotatedClass ac) {
        return null;
    }
    
    public Object findFilterId(final Annotated ann) {
        return null;
    }
    
    public Object findNamingStrategy(final AnnotatedClass ac) {
        return null;
    }
    
    public String findClassDescription(final AnnotatedClass ac) {
        return null;
    }
    
    @Deprecated
    public String[] findPropertiesToIgnore(final Annotated ac, final boolean forSerialization) {
        return null;
    }
    
    @Deprecated
    public String[] findPropertiesToIgnore(final Annotated ac) {
        return null;
    }
    
    @Deprecated
    public Boolean findIgnoreUnknownProperties(final AnnotatedClass ac) {
        return null;
    }
    
    public VisibilityChecker<?> findAutoDetectVisibility(final AnnotatedClass ac, final VisibilityChecker<?> checker) {
        return checker;
    }
    
    public TypeResolverBuilder<?> findTypeResolver(final MapperConfig<?> config, final AnnotatedClass ac, final JavaType baseType) {
        return null;
    }
    
    public TypeResolverBuilder<?> findPropertyTypeResolver(final MapperConfig<?> config, final AnnotatedMember am, final JavaType baseType) {
        return null;
    }
    
    public TypeResolverBuilder<?> findPropertyContentTypeResolver(final MapperConfig<?> config, final AnnotatedMember am, final JavaType containerType) {
        return null;
    }
    
    public List<NamedType> findSubtypes(final Annotated a) {
        return null;
    }
    
    public String findTypeName(final AnnotatedClass ac) {
        return null;
    }
    
    public Boolean isTypeId(final AnnotatedMember member) {
        return null;
    }
    
    public ReferenceProperty findReferenceType(final AnnotatedMember member) {
        return null;
    }
    
    public NameTransformer findUnwrappingNameTransformer(final AnnotatedMember member) {
        return null;
    }
    
    public boolean hasIgnoreMarker(final AnnotatedMember m) {
        return false;
    }
    
    public JacksonInject.Value findInjectableValue(final AnnotatedMember m) {
        final Object id = this.findInjectableValueId(m);
        if (id != null) {
            return JacksonInject.Value.forId(id);
        }
        return null;
    }
    
    public Boolean hasRequiredMarker(final AnnotatedMember m) {
        return null;
    }
    
    public Class<?>[] findViews(final Annotated a) {
        return null;
    }
    
    public JsonFormat.Value findFormat(final Annotated memberOrClass) {
        return JsonFormat.Value.empty();
    }
    
    public PropertyName findWrapperName(final Annotated ann) {
        return null;
    }
    
    public String findPropertyDefaultValue(final Annotated ann) {
        return null;
    }
    
    public String findPropertyDescription(final Annotated ann) {
        return null;
    }
    
    public Integer findPropertyIndex(final Annotated ann) {
        return null;
    }
    
    public String findImplicitPropertyName(final AnnotatedMember member) {
        return null;
    }
    
    public List<PropertyName> findPropertyAliases(final Annotated ann) {
        return null;
    }
    
    public JsonProperty.Access findPropertyAccess(final Annotated ann) {
        return null;
    }
    
    public AnnotatedMethod resolveSetterConflict(final MapperConfig<?> config, final AnnotatedMethod setter1, final AnnotatedMethod setter2) {
        return null;
    }
    
    @Deprecated
    public Object findInjectableValueId(final AnnotatedMember m) {
        return null;
    }
    
    public Object findSerializer(final Annotated am) {
        return null;
    }
    
    public Object findKeySerializer(final Annotated am) {
        return null;
    }
    
    public Object findContentSerializer(final Annotated am) {
        return null;
    }
    
    public Object findNullSerializer(final Annotated am) {
        return null;
    }
    
    public JsonSerialize.Typing findSerializationTyping(final Annotated a) {
        return null;
    }
    
    public Object findSerializationConverter(final Annotated a) {
        return null;
    }
    
    public Object findSerializationContentConverter(final AnnotatedMember a) {
        return null;
    }
    
    public JsonInclude.Value findPropertyInclusion(final Annotated a) {
        return JsonInclude.Value.empty();
    }
    
    @Deprecated
    public JsonInclude.Include findSerializationInclusion(final Annotated a, final JsonInclude.Include defValue) {
        return defValue;
    }
    
    @Deprecated
    public JsonInclude.Include findSerializationInclusionForContent(final Annotated a, final JsonInclude.Include defValue) {
        return defValue;
    }
    
    public JavaType refineSerializationType(final MapperConfig<?> config, final Annotated a, final JavaType baseType) throws JsonMappingException {
        return baseType;
    }
    
    @Deprecated
    public Class<?> findSerializationType(final Annotated a) {
        return null;
    }
    
    @Deprecated
    public Class<?> findSerializationKeyType(final Annotated am, final JavaType baseType) {
        return null;
    }
    
    @Deprecated
    public Class<?> findSerializationContentType(final Annotated am, final JavaType baseType) {
        return null;
    }
    
    public String[] findSerializationPropertyOrder(final AnnotatedClass ac) {
        return null;
    }
    
    public Boolean findSerializationSortAlphabetically(final Annotated ann) {
        return null;
    }
    
    public void findAndAddVirtualProperties(final MapperConfig<?> config, final AnnotatedClass ac, final List<BeanPropertyWriter> properties) {
    }
    
    public PropertyName findNameForSerialization(final Annotated a) {
        return null;
    }
    
    public Boolean hasAsValue(final Annotated a) {
        if (a instanceof AnnotatedMethod && this.hasAsValueAnnotation((AnnotatedMethod)a)) {
            return true;
        }
        return null;
    }
    
    public Boolean hasAnyGetter(final Annotated a) {
        if (a instanceof AnnotatedMethod && this.hasAnyGetterAnnotation((AnnotatedMethod)a)) {
            return true;
        }
        return null;
    }
    
    public String[] findEnumValues(final Class<?> enumType, final Enum<?>[] enumValues, final String[] names) {
        return names;
    }
    
    public Enum<?> findDefaultEnumValue(final Class<Enum<?>> enumCls) {
        return null;
    }
    
    @Deprecated
    public String findEnumValue(final Enum<?> value) {
        return value.name();
    }
    
    @Deprecated
    public boolean hasAsValueAnnotation(final AnnotatedMethod am) {
        return false;
    }
    
    @Deprecated
    public boolean hasAnyGetterAnnotation(final AnnotatedMethod am) {
        return false;
    }
    
    public Object findDeserializer(final Annotated am) {
        return null;
    }
    
    public Object findKeyDeserializer(final Annotated am) {
        return null;
    }
    
    public Object findContentDeserializer(final Annotated am) {
        return null;
    }
    
    public Object findDeserializationConverter(final Annotated a) {
        return null;
    }
    
    public Object findDeserializationContentConverter(final AnnotatedMember a) {
        return null;
    }
    
    public JavaType refineDeserializationType(final MapperConfig<?> config, final Annotated a, final JavaType baseType) throws JsonMappingException {
        return baseType;
    }
    
    @Deprecated
    public Class<?> findDeserializationType(final Annotated am, final JavaType baseType) {
        return null;
    }
    
    @Deprecated
    public Class<?> findDeserializationKeyType(final Annotated am, final JavaType baseKeyType) {
        return null;
    }
    
    @Deprecated
    public Class<?> findDeserializationContentType(final Annotated am, final JavaType baseContentType) {
        return null;
    }
    
    public Object findValueInstantiator(final AnnotatedClass ac) {
        return null;
    }
    
    public Class<?> findPOJOBuilder(final AnnotatedClass ac) {
        return null;
    }
    
    public JsonPOJOBuilder.Value findPOJOBuilderConfig(final AnnotatedClass ac) {
        return null;
    }
    
    public PropertyName findNameForDeserialization(final Annotated a) {
        return null;
    }
    
    public Boolean hasAnySetter(final Annotated a) {
        return null;
    }
    
    public JsonSetter.Value findSetterInfo(final Annotated a) {
        return JsonSetter.Value.empty();
    }
    
    public Boolean findMergeInfo(final Annotated a) {
        return null;
    }
    
    public JsonCreator.Mode findCreatorAnnotation(final MapperConfig<?> config, final Annotated a) {
        if (this.hasCreatorAnnotation(a)) {
            JsonCreator.Mode mode = this.findCreatorBinding(a);
            if (mode == null) {
                mode = JsonCreator.Mode.DEFAULT;
            }
            return mode;
        }
        return null;
    }
    
    @Deprecated
    public boolean hasCreatorAnnotation(final Annotated a) {
        return false;
    }
    
    @Deprecated
    public JsonCreator.Mode findCreatorBinding(final Annotated a) {
        return null;
    }
    
    @Deprecated
    public boolean hasAnySetterAnnotation(final AnnotatedMethod am) {
        return false;
    }
    
    protected <A extends Annotation> A _findAnnotation(final Annotated annotated, final Class<A> annoClass) {
        return annotated.getAnnotation(annoClass);
    }
    
    protected boolean _hasAnnotation(final Annotated annotated, final Class<? extends Annotation> annoClass) {
        return annotated.hasAnnotation(annoClass);
    }
    
    protected boolean _hasOneOf(final Annotated annotated, final Class<? extends Annotation>[] annoClasses) {
        return annotated.hasOneOf(annoClasses);
    }
    
    public static class ReferenceProperty
    {
        private final Type _type;
        private final String _name;
        
        public ReferenceProperty(final Type t, final String n) {
            this._type = t;
            this._name = n;
        }
        
        public static ReferenceProperty managed(final String name) {
            return new ReferenceProperty(Type.MANAGED_REFERENCE, name);
        }
        
        public static ReferenceProperty back(final String name) {
            return new ReferenceProperty(Type.BACK_REFERENCE, name);
        }
        
        public Type getType() {
            return this._type;
        }
        
        public String getName() {
            return this._name;
        }
        
        public boolean isManagedReference() {
            return this._type == Type.MANAGED_REFERENCE;
        }
        
        public boolean isBackReference() {
            return this._type == Type.BACK_REFERENCE;
        }
        
        public enum Type
        {
            MANAGED_REFERENCE, 
            BACK_REFERENCE;
        }
    }
}
