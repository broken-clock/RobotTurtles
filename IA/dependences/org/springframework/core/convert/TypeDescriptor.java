// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert;

import java.util.HashMap;
import org.springframework.util.ObjectUtils;
import java.util.Collection;
import java.lang.reflect.Type;
import org.springframework.util.ClassUtils;
import java.lang.reflect.Field;
import org.springframework.util.Assert;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import java.util.Map;
import java.lang.annotation.Annotation;
import java.io.Serializable;

public class TypeDescriptor implements Serializable
{
    static final Annotation[] EMPTY_ANNOTATION_ARRAY;
    private static final Map<Class<?>, TypeDescriptor> commonTypesCache;
    private static final Class<?>[] CACHED_COMMON_TYPES;
    private final Class<?> type;
    private final ResolvableType resolvableType;
    private final Annotation[] annotations;
    
    public TypeDescriptor(final MethodParameter methodParameter) {
        Assert.notNull(methodParameter, "MethodParameter must not be null");
        if (methodParameter.getNestingLevel() != 1) {
            throw new IllegalArgumentException("MethodParameter argument must have its nestingLevel set to 1");
        }
        this.resolvableType = ResolvableType.forMethodParameter(methodParameter);
        this.type = this.resolvableType.resolve(Object.class);
        this.annotations = ((methodParameter.getParameterIndex() == -1) ? this.nullSafeAnnotations(methodParameter.getMethodAnnotations()) : this.nullSafeAnnotations(methodParameter.getParameterAnnotations()));
    }
    
    public TypeDescriptor(final Field field) {
        Assert.notNull(field, "Field must not be null");
        this.resolvableType = ResolvableType.forField(field);
        this.type = this.resolvableType.resolve(Object.class);
        this.annotations = this.nullSafeAnnotations(field.getAnnotations());
    }
    
    public TypeDescriptor(final Property property) {
        Assert.notNull(property, "Property must not be null");
        this.resolvableType = ResolvableType.forMethodParameter(property.getMethodParameter());
        this.type = this.resolvableType.resolve(property.getType());
        this.annotations = this.nullSafeAnnotations(property.getAnnotations());
    }
    
    protected TypeDescriptor(final ResolvableType resolvableType, final Class<?> type, final Annotation[] annotations) {
        this.resolvableType = resolvableType;
        this.type = ((type != null) ? type : resolvableType.resolve(Object.class));
        this.annotations = this.nullSafeAnnotations(annotations);
    }
    
    private Annotation[] nullSafeAnnotations(final Annotation[] annotations) {
        return (annotations != null) ? annotations : TypeDescriptor.EMPTY_ANNOTATION_ARRAY;
    }
    
    public Class<?> getObjectType() {
        return ClassUtils.resolvePrimitiveIfNecessary(this.getType());
    }
    
    public Class<?> getType() {
        return this.type;
    }
    
    public ResolvableType getResolvableType() {
        return this.resolvableType;
    }
    
    public Object getSource() {
        return (this.resolvableType == null) ? null : this.resolvableType.getSource();
    }
    
    public TypeDescriptor narrow(final Object value) {
        if (value == null) {
            return this;
        }
        final ResolvableType narrowed = ResolvableType.forType(value.getClass(), this.resolvableType);
        return new TypeDescriptor(narrowed, null, this.annotations);
    }
    
    public TypeDescriptor upcast(final Class<?> superType) {
        if (superType == null) {
            return null;
        }
        Assert.isAssignable(superType, this.getType());
        return new TypeDescriptor(this.resolvableType.as(superType), superType, this.annotations);
    }
    
    public String getName() {
        return ClassUtils.getQualifiedName(this.getType());
    }
    
    public boolean isPrimitive() {
        return this.getType().isPrimitive();
    }
    
    public Annotation[] getAnnotations() {
        return this.annotations;
    }
    
    public boolean hasAnnotation(final Class<? extends Annotation> annotationType) {
        return this.getAnnotation(annotationType) != null;
    }
    
    public <T extends Annotation> T getAnnotation(final Class<T> annotationType) {
        for (final Annotation annotation : this.getAnnotations()) {
            if (annotation.annotationType().equals(annotationType)) {
                return (T)annotation;
            }
        }
        for (final Annotation metaAnn : this.getAnnotations()) {
            final T ann = metaAnn.annotationType().getAnnotation(annotationType);
            if (ann != null) {
                return ann;
            }
        }
        return null;
    }
    
    public boolean isAssignableTo(final TypeDescriptor typeDescriptor) {
        final boolean typesAssignable = typeDescriptor.getObjectType().isAssignableFrom(this.getObjectType());
        if (!typesAssignable) {
            return false;
        }
        if (this.isArray() && typeDescriptor.isArray()) {
            return this.getElementTypeDescriptor().isAssignableTo(typeDescriptor.getElementTypeDescriptor());
        }
        if (this.isCollection() && typeDescriptor.isCollection()) {
            return this.isNestedAssignable(this.getElementTypeDescriptor(), typeDescriptor.getElementTypeDescriptor());
        }
        return !this.isMap() || !typeDescriptor.isMap() || (this.isNestedAssignable(this.getMapKeyTypeDescriptor(), typeDescriptor.getMapKeyTypeDescriptor()) && this.isNestedAssignable(this.getMapValueTypeDescriptor(), typeDescriptor.getMapValueTypeDescriptor()));
    }
    
    private boolean isNestedAssignable(final TypeDescriptor nestedTypeDescriptor, final TypeDescriptor otherNestedTypeDescriptor) {
        return nestedTypeDescriptor == null || otherNestedTypeDescriptor == null || nestedTypeDescriptor.isAssignableTo(otherNestedTypeDescriptor);
    }
    
    public boolean isCollection() {
        return Collection.class.isAssignableFrom(this.getType());
    }
    
    public boolean isArray() {
        return this.getType().isArray();
    }
    
    public TypeDescriptor getElementTypeDescriptor() {
        this.assertCollectionOrArray();
        if (this.resolvableType.isArray()) {
            return getRelatedIfResolvable(this, this.resolvableType.getComponentType());
        }
        return getRelatedIfResolvable(this, this.resolvableType.asCollection().getGeneric(new int[0]));
    }
    
    public TypeDescriptor elementTypeDescriptor(final Object element) {
        return this.narrow(element, this.getElementTypeDescriptor());
    }
    
    public boolean isMap() {
        return Map.class.isAssignableFrom(this.getType());
    }
    
    public TypeDescriptor getMapKeyTypeDescriptor() {
        this.assertMap();
        return getRelatedIfResolvable(this, this.resolvableType.asMap().getGeneric(0));
    }
    
    public TypeDescriptor getMapKeyTypeDescriptor(final Object mapKey) {
        return this.narrow(mapKey, this.getMapKeyTypeDescriptor());
    }
    
    public TypeDescriptor getMapValueTypeDescriptor() {
        this.assertMap();
        return getRelatedIfResolvable(this, this.resolvableType.asMap().getGeneric(1));
    }
    
    public TypeDescriptor getMapValueTypeDescriptor(final Object mapValue) {
        return this.narrow(mapValue, this.getMapValueTypeDescriptor());
    }
    
    @Deprecated
    public Class<?> getElementType() {
        return this.getType(this.getElementTypeDescriptor());
    }
    
    @Deprecated
    public Class<?> getMapKeyType() {
        return this.getType(this.getMapKeyTypeDescriptor());
    }
    
    @Deprecated
    public Class<?> getMapValueType() {
        return this.getType(this.getMapValueTypeDescriptor());
    }
    
    private Class<?> getType(final TypeDescriptor typeDescriptor) {
        return (typeDescriptor == null) ? null : typeDescriptor.getType();
    }
    
    private void assertCollectionOrArray() {
        if (!this.isCollection() && !this.isArray()) {
            throw new IllegalStateException("Not a java.util.Collection or Array");
        }
    }
    
    private void assertMap() {
        if (!this.isMap()) {
            throw new IllegalStateException("Not a java.util.Map");
        }
    }
    
    private TypeDescriptor narrow(final Object value, final TypeDescriptor typeDescriptor) {
        if (typeDescriptor != null) {
            return typeDescriptor.narrow(value);
        }
        return (value != null) ? new TypeDescriptor(this.resolvableType, value.getClass(), this.annotations) : null;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TypeDescriptor)) {
            return false;
        }
        final TypeDescriptor other = (TypeDescriptor)obj;
        if (!ObjectUtils.nullSafeEquals(this.type, other.type)) {
            return false;
        }
        if (this.getAnnotations().length != other.getAnnotations().length) {
            return false;
        }
        for (final Annotation ann : this.getAnnotations()) {
            if (other.getAnnotation(ann.annotationType()) == null) {
                return false;
            }
        }
        if (this.isCollection() || this.isArray()) {
            return ObjectUtils.nullSafeEquals(this.getElementTypeDescriptor(), other.getElementTypeDescriptor());
        }
        return !this.isMap() || (ObjectUtils.nullSafeEquals(this.getMapKeyTypeDescriptor(), other.getMapKeyTypeDescriptor()) && ObjectUtils.nullSafeEquals(this.getMapValueTypeDescriptor(), other.getMapValueTypeDescriptor()));
    }
    
    @Override
    public int hashCode() {
        return this.getType().hashCode();
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (final Annotation ann : this.getAnnotations()) {
            builder.append("@").append(ann.annotationType().getName()).append(' ');
        }
        builder.append(this.resolvableType.toString());
        return builder.toString();
    }
    
    public static TypeDescriptor valueOf(Class<?> type) {
        if (type == null) {
            type = Object.class;
        }
        final TypeDescriptor desc = TypeDescriptor.commonTypesCache.get(type);
        return (desc != null) ? desc : new TypeDescriptor(ResolvableType.forClass(type), null, null);
    }
    
    public static TypeDescriptor collection(final Class<?> collectionType, final TypeDescriptor elementTypeDescriptor) {
        Assert.notNull(collectionType, "collectionType must not be null");
        if (!Collection.class.isAssignableFrom(collectionType)) {
            throw new IllegalArgumentException("collectionType must be a java.util.Collection");
        }
        final ResolvableType element = (elementTypeDescriptor != null) ? elementTypeDescriptor.resolvableType : null;
        return new TypeDescriptor(ResolvableType.forClassWithGenerics(collectionType, element), null, null);
    }
    
    public static TypeDescriptor map(final Class<?> mapType, final TypeDescriptor keyTypeDescriptor, final TypeDescriptor valueTypeDescriptor) {
        if (!Map.class.isAssignableFrom(mapType)) {
            throw new IllegalArgumentException("mapType must be a java.util.Map");
        }
        final ResolvableType key = (keyTypeDescriptor != null) ? keyTypeDescriptor.resolvableType : null;
        final ResolvableType value = (valueTypeDescriptor != null) ? valueTypeDescriptor.resolvableType : null;
        return new TypeDescriptor(ResolvableType.forClassWithGenerics(mapType, key, value), null, null);
    }
    
    public static TypeDescriptor array(final TypeDescriptor elementTypeDescriptor) {
        if (elementTypeDescriptor == null) {
            return null;
        }
        return new TypeDescriptor(ResolvableType.forArrayComponent(elementTypeDescriptor.resolvableType), null, elementTypeDescriptor.getAnnotations());
    }
    
    public static TypeDescriptor nested(final MethodParameter methodParameter, final int nestingLevel) {
        if (methodParameter.getNestingLevel() != 1) {
            throw new IllegalArgumentException("methodParameter nesting level must be 1: use the nestingLevel parameter to specify the desired nestingLevel for nested type traversal");
        }
        return nested(new TypeDescriptor(methodParameter), nestingLevel);
    }
    
    public static TypeDescriptor nested(final Field field, final int nestingLevel) {
        return nested(new TypeDescriptor(field), nestingLevel);
    }
    
    public static TypeDescriptor nested(final Property property, final int nestingLevel) {
        return nested(new TypeDescriptor(property), nestingLevel);
    }
    
    public static TypeDescriptor forObject(final Object source) {
        return (source != null) ? valueOf(source.getClass()) : null;
    }
    
    private static TypeDescriptor nested(final TypeDescriptor typeDescriptor, final int nestingLevel) {
        ResolvableType nested = typeDescriptor.resolvableType;
        for (int i = 0; i < nestingLevel; ++i) {
            if (!Object.class.equals(nested.getType())) {
                nested = nested.getNested(2);
            }
        }
        Assert.state(nested != ResolvableType.NONE, "Unable to obtain nested generic from " + typeDescriptor + " at level " + nestingLevel);
        return getRelatedIfResolvable(typeDescriptor, nested);
    }
    
    private static TypeDescriptor getRelatedIfResolvable(final TypeDescriptor source, final ResolvableType type) {
        if (type.resolve() == null) {
            return null;
        }
        return new TypeDescriptor(type, null, source.annotations);
    }
    
    static {
        EMPTY_ANNOTATION_ARRAY = new Annotation[0];
        commonTypesCache = new HashMap<Class<?>, TypeDescriptor>();
        CACHED_COMMON_TYPES = new Class[] { Boolean.TYPE, Boolean.class, Byte.TYPE, Byte.class, Character.TYPE, Character.class, Double.TYPE, Double.class, Integer.TYPE, Integer.class, Long.TYPE, Long.class, Float.TYPE, Float.class, Short.TYPE, Short.class, String.class, Object.class };
        for (final Class<?> preCachedClass : TypeDescriptor.CACHED_COMMON_TYPES) {
            TypeDescriptor.commonTypesCache.put(preCachedClass, valueOf(preCachedClass));
        }
    }
}
