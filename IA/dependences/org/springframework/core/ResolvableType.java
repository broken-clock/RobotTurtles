// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.io.ObjectStreamException;
import org.springframework.util.StringUtils;
import java.lang.reflect.Array;
import java.lang.reflect.WildcardType;
import org.springframework.util.ObjectUtils;
import java.util.Map;
import java.util.Collection;
import java.lang.reflect.GenericArrayType;
import org.springframework.util.ClassUtils;
import java.lang.reflect.TypeVariable;
import org.springframework.util.Assert;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.springframework.util.ConcurrentReferenceHashMap;
import java.io.Serializable;

public final class ResolvableType implements Serializable
{
    public static final ResolvableType NONE;
    private static final ResolvableType[] EMPTY_TYPES_ARRAY;
    private static final ConcurrentReferenceHashMap<ResolvableType, ResolvableType> cache;
    private final Type type;
    private final SerializableTypeWrapper.TypeProvider typeProvider;
    private final VariableResolver variableResolver;
    private final ResolvableType componentType;
    private final Class<?> resolved;
    private ResolvableType superType;
    private ResolvableType[] interfaces;
    private ResolvableType[] generics;
    
    private ResolvableType(final Type type, final SerializableTypeWrapper.TypeProvider typeProvider, final VariableResolver variableResolver, final ResolvableType componentType) {
        this.type = type;
        this.typeProvider = typeProvider;
        this.variableResolver = variableResolver;
        this.componentType = componentType;
        this.resolved = this.resolveClass();
    }
    
    private ResolvableType(final Type type, final SerializableTypeWrapper.TypeProvider typeProvider, final VariableResolver variableResolver) {
        this.type = type;
        this.typeProvider = typeProvider;
        this.variableResolver = variableResolver;
        this.componentType = null;
        this.resolved = null;
    }
    
    public Type getType() {
        return SerializableTypeWrapper.unwrap(this.type);
    }
    
    public Class<?> getRawClass() {
        Type rawType = this.type;
        if (rawType instanceof ParameterizedType) {
            rawType = ((ParameterizedType)rawType).getRawType();
        }
        return (Class<?>)((rawType instanceof Class) ? ((Class)rawType) : null);
    }
    
    public Object getSource() {
        final Object source = (this.typeProvider != null) ? this.typeProvider.getSource() : null;
        return (source != null) ? source : this.type;
    }
    
    public boolean isAssignableFrom(final ResolvableType type) {
        return this.isAssignableFrom(type, false);
    }
    
    private boolean isAssignableFrom(final ResolvableType type, final boolean checkingGeneric) {
        Assert.notNull(type, "Type must not be null");
        if (this == ResolvableType.NONE || type == ResolvableType.NONE) {
            return false;
        }
        if (this.isArray()) {
            return type.isArray() && this.getComponentType().isAssignableFrom(type.getComponentType());
        }
        final WildcardBounds ourBounds = WildcardBounds.get(this);
        final WildcardBounds typeBounds = WildcardBounds.get(type);
        if (typeBounds != null) {
            return ourBounds != null && ourBounds.isSameKind(typeBounds) && ourBounds.isAssignableFrom(typeBounds.getBounds());
        }
        if (ourBounds != null) {
            return ourBounds.isAssignableFrom(type);
        }
        boolean checkGenerics = true;
        Class<?> ourResolved = null;
        if (this.type instanceof TypeVariable) {
            final TypeVariable<?> variable = (TypeVariable<?>)this.type;
            if (this.variableResolver != null) {
                final ResolvableType resolved = this.variableResolver.resolveVariable(variable);
                if (resolved != null) {
                    ourResolved = resolved.resolve();
                }
            }
            if (ourResolved == null && type.variableResolver != null) {
                final ResolvableType resolved = type.variableResolver.resolveVariable(variable);
                if (resolved != null) {
                    ourResolved = resolved.resolve();
                    checkGenerics = false;
                }
            }
        }
        if (ourResolved == null) {
            ourResolved = this.resolve(Object.class);
        }
        final Class<?> typeResolved = type.resolve(Object.class);
        Label_0267: {
            if (checkingGeneric) {
                if (ourResolved.equals(typeResolved)) {
                    break Label_0267;
                }
            }
            else if (ClassUtils.isAssignable(ourResolved, typeResolved)) {
                break Label_0267;
            }
            return false;
        }
        if (checkGenerics) {
            final ResolvableType[] ourGenerics = this.getGenerics();
            final ResolvableType[] typeGenerics = type.as(ourResolved).getGenerics();
            if (ourGenerics.length != typeGenerics.length) {
                return false;
            }
            for (int i = 0; i < ourGenerics.length; ++i) {
                if (!ourGenerics[i].isAssignableFrom(typeGenerics[i], true)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isArray() {
        return this != ResolvableType.NONE && ((this.type instanceof Class && ((Class)this.type).isArray()) || this.type instanceof GenericArrayType || this.resolveType().isArray());
    }
    
    public ResolvableType getComponentType() {
        if (this == ResolvableType.NONE) {
            return ResolvableType.NONE;
        }
        if (this.componentType != null) {
            return this.componentType;
        }
        if (this.type instanceof Class) {
            final Class<?> componentType = (Class<?>)((Class)this.type).getComponentType();
            return forType(componentType, this.variableResolver);
        }
        if (this.type instanceof GenericArrayType) {
            return forType(((GenericArrayType)this.type).getGenericComponentType(), this.variableResolver);
        }
        return this.resolveType().getComponentType();
    }
    
    public ResolvableType asCollection() {
        return this.as(Collection.class);
    }
    
    public ResolvableType asMap() {
        return this.as(Map.class);
    }
    
    public ResolvableType as(final Class<?> type) {
        if (this == ResolvableType.NONE) {
            return ResolvableType.NONE;
        }
        if (ObjectUtils.nullSafeEquals(this.resolve(), type)) {
            return this;
        }
        for (final ResolvableType interfaceType : this.getInterfaces()) {
            final ResolvableType interfaceAsType = interfaceType.as(type);
            if (interfaceAsType != ResolvableType.NONE) {
                return interfaceAsType;
            }
        }
        return this.getSuperType().as(type);
    }
    
    public ResolvableType getSuperType() {
        final Class<?> resolved = this.resolve();
        if (resolved == null || resolved.getGenericSuperclass() == null) {
            return ResolvableType.NONE;
        }
        if (this.superType == null) {
            this.superType = forType(SerializableTypeWrapper.forGenericSuperclass(resolved), this.asVariableResolver());
        }
        return this.superType;
    }
    
    public ResolvableType[] getInterfaces() {
        final Class<?> resolved = this.resolve();
        if (resolved == null || ObjectUtils.isEmpty(resolved.getGenericInterfaces())) {
            return ResolvableType.EMPTY_TYPES_ARRAY;
        }
        if (this.interfaces == null) {
            this.interfaces = forTypes(SerializableTypeWrapper.forGenericInterfaces(resolved), this.asVariableResolver());
        }
        return this.interfaces;
    }
    
    public boolean hasGenerics() {
        return this.getGenerics().length > 0;
    }
    
    public boolean hasUnresolvableGenerics() {
        if (this == ResolvableType.NONE) {
            return false;
        }
        final ResolvableType[] generics2;
        final ResolvableType[] generics = generics2 = this.getGenerics();
        for (final ResolvableType generic : generics2) {
            if (generic.isUnresolvableTypeVariable() || generic.isWildcardWithoutBounds()) {
                return true;
            }
        }
        final Class<?> resolved = this.resolve();
        if (resolved != null) {
            for (final Type genericInterface : resolved.getGenericInterfaces()) {
                if (genericInterface instanceof Class && forClass((Class<?>)genericInterface).hasGenerics()) {
                    return true;
                }
            }
            return this.getSuperType().hasUnresolvableGenerics();
        }
        return false;
    }
    
    private boolean isUnresolvableTypeVariable() {
        if (this.type instanceof TypeVariable) {
            if (this.variableResolver == null) {
                return true;
            }
            final TypeVariable<?> variable = (TypeVariable<?>)this.type;
            final ResolvableType resolved = this.variableResolver.resolveVariable(variable);
            if (resolved == null || resolved.isUnresolvableTypeVariable()) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isWildcardWithoutBounds() {
        if (this.type instanceof WildcardType) {
            final WildcardType wt = (WildcardType)this.type;
            if (wt.getLowerBounds().length == 0) {
                final Type[] upperBounds = wt.getUpperBounds();
                if (upperBounds.length == 0 || (upperBounds.length == 1 && Object.class.equals(upperBounds[0]))) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public ResolvableType getNested(final int nestingLevel) {
        return this.getNested(nestingLevel, null);
    }
    
    public ResolvableType getNested(final int nestingLevel, final Map<Integer, Integer> typeIndexesPerLevel) {
        ResolvableType result = this;
        for (int i = 2; i <= nestingLevel; ++i) {
            if (result.isArray()) {
                result = result.getComponentType();
            }
            else {
                while (result != ResolvableType.NONE && !result.hasGenerics()) {
                    result = result.getSuperType();
                }
                Integer index = (typeIndexesPerLevel != null) ? typeIndexesPerLevel.get(i) : null;
                index = ((index == null) ? (result.getGenerics().length - 1) : index);
                result = result.getGeneric(index);
            }
        }
        return result;
    }
    
    public ResolvableType getGeneric(final int... indexes) {
        try {
            if (indexes == null || indexes.length == 0) {
                return this.getGenerics()[0];
            }
            ResolvableType generic = this;
            for (final int index : indexes) {
                generic = generic.getGenerics()[index];
            }
            return generic;
        }
        catch (IndexOutOfBoundsException ex) {
            return ResolvableType.NONE;
        }
    }
    
    public ResolvableType[] getGenerics() {
        if (this == ResolvableType.NONE) {
            return ResolvableType.EMPTY_TYPES_ARRAY;
        }
        if (this.generics == null) {
            if (this.type instanceof Class) {
                final Class<?> typeClass = (Class<?>)this.type;
                this.generics = forTypes(SerializableTypeWrapper.forTypeParameters(typeClass), this.variableResolver);
            }
            else if (this.type instanceof ParameterizedType) {
                final Type[] actualTypeArguments = ((ParameterizedType)this.type).getActualTypeArguments();
                final ResolvableType[] generics = new ResolvableType[actualTypeArguments.length];
                for (int i = 0; i < actualTypeArguments.length; ++i) {
                    generics[i] = forType(actualTypeArguments[i], this.variableResolver);
                }
                this.generics = generics;
            }
            else {
                this.generics = this.resolveType().getGenerics();
            }
        }
        return this.generics;
    }
    
    public Class<?>[] resolveGenerics() {
        return this.resolveGenerics(null);
    }
    
    public Class<?>[] resolveGenerics(final Class<?> fallback) {
        final ResolvableType[] generics = this.getGenerics();
        final Class<?>[] resolvedGenerics = (Class<?>[])new Class[generics.length];
        for (int i = 0; i < generics.length; ++i) {
            resolvedGenerics[i] = generics[i].resolve(fallback);
        }
        return resolvedGenerics;
    }
    
    public Class<?> resolveGeneric(final int... indexes) {
        return this.getGeneric(indexes).resolve();
    }
    
    public Class<?> resolve() {
        return this.resolve(null);
    }
    
    public Class<?> resolve(final Class<?> fallback) {
        return (this.resolved != null) ? this.resolved : fallback;
    }
    
    private Class<?> resolveClass() {
        if (this.type instanceof Class || this.type == null) {
            return (Class<?>)this.type;
        }
        if (this.type instanceof GenericArrayType) {
            final Class<?> resolvedComponent = this.getComponentType().resolve();
            return (resolvedComponent != null) ? Array.newInstance(resolvedComponent, 0).getClass() : null;
        }
        return this.resolveType().resolve();
    }
    
    ResolvableType resolveType() {
        if (this.type instanceof ParameterizedType) {
            return forType(((ParameterizedType)this.type).getRawType(), this.variableResolver);
        }
        if (this.type instanceof WildcardType) {
            Type resolved = this.resolveBounds(((WildcardType)this.type).getUpperBounds());
            if (resolved == null) {
                resolved = this.resolveBounds(((WildcardType)this.type).getLowerBounds());
            }
            return forType(resolved, this.variableResolver);
        }
        if (this.type instanceof TypeVariable) {
            final TypeVariable<?> variable = (TypeVariable<?>)this.type;
            if (this.variableResolver != null) {
                final ResolvableType resolved2 = this.variableResolver.resolveVariable(variable);
                if (resolved2 != null) {
                    return resolved2;
                }
            }
            return forType(this.resolveBounds(variable.getBounds()), this.variableResolver);
        }
        return ResolvableType.NONE;
    }
    
    private Type resolveBounds(final Type[] bounds) {
        if (ObjectUtils.isEmpty(bounds) || Object.class.equals(bounds[0])) {
            return null;
        }
        return bounds[0];
    }
    
    private ResolvableType resolveVariable(final TypeVariable<?> variable) {
        if (this.type instanceof TypeVariable) {
            return this.resolveType().resolveVariable(variable);
        }
        if (this.type instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType)this.type;
            final TypeVariable<?>[] variables = this.resolve().getTypeParameters();
            for (int i = 0; i < variables.length; ++i) {
                if (ObjectUtils.nullSafeEquals(variables[i].getName(), variable.getName())) {
                    final Type actualType = parameterizedType.getActualTypeArguments()[i];
                    return forType(actualType, this.variableResolver);
                }
            }
            if (parameterizedType.getOwnerType() != null) {
                return forType(parameterizedType.getOwnerType(), this.variableResolver).resolveVariable(variable);
            }
        }
        if (this.variableResolver != null) {
            return this.variableResolver.resolveVariable(variable);
        }
        return null;
    }
    
    @Override
    public String toString() {
        if (this.isArray()) {
            return this.getComponentType() + "[]";
        }
        if (this.resolved == null) {
            return "?";
        }
        if (this.type instanceof TypeVariable) {
            final TypeVariable<?> variable = (TypeVariable<?>)this.type;
            if (this.variableResolver == null || this.variableResolver.resolveVariable(variable) == null) {
                return "?";
            }
        }
        final StringBuilder result = new StringBuilder(this.resolved.getName());
        if (this.hasGenerics()) {
            result.append('<');
            result.append(StringUtils.arrayToDelimitedString(this.getGenerics(), ", "));
            result.append('>');
        }
        return result.toString();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ResolvableType)) {
            return false;
        }
        final ResolvableType other = (ResolvableType)obj;
        return ObjectUtils.nullSafeEquals(this.type, other.type) && ObjectUtils.nullSafeEquals(this.getSource(), other.getSource()) && this.variableResolverSourceEquals(other.variableResolver) && ObjectUtils.nullSafeEquals(this.componentType, other.componentType);
    }
    
    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.type);
    }
    
    private Object readResolve() throws ObjectStreamException {
        return (this.type == null) ? ResolvableType.NONE : this;
    }
    
    VariableResolver asVariableResolver() {
        if (this == ResolvableType.NONE) {
            return null;
        }
        return new DefaultVariableResolver();
    }
    
    private boolean variableResolverSourceEquals(final VariableResolver other) {
        if (this.variableResolver == null) {
            return other == null;
        }
        return other != null && ObjectUtils.nullSafeEquals(this.variableResolver.getSource(), other.getSource());
    }
    
    private static ResolvableType[] forTypes(final Type[] types, final VariableResolver owner) {
        final ResolvableType[] result = new ResolvableType[types.length];
        for (int i = 0; i < types.length; ++i) {
            result[i] = forType(types[i], owner);
        }
        return result;
    }
    
    public static ResolvableType forClass(final Class<?> sourceClass) {
        Assert.notNull(sourceClass, "Source class must not be null");
        return forType(sourceClass);
    }
    
    public static ResolvableType forClass(final Class<?> sourceClass, final Class<?> implementationClass) {
        Assert.notNull(sourceClass, "Source class must not be null");
        final ResolvableType asType = forType(implementationClass).as(sourceClass);
        return (asType == ResolvableType.NONE) ? forType(sourceClass) : asType;
    }
    
    public static ResolvableType forField(final Field field) {
        Assert.notNull(field, "Field must not be null");
        return forType(null, new SerializableTypeWrapper.FieldTypeProvider(field), null);
    }
    
    public static ResolvableType forField(final Field field, final Class<?> implementationClass) {
        Assert.notNull(field, "Field must not be null");
        final ResolvableType owner = forType(implementationClass).as(field.getDeclaringClass());
        return forType(null, new SerializableTypeWrapper.FieldTypeProvider(field), owner.asVariableResolver());
    }
    
    public static ResolvableType forField(final Field field, ResolvableType implementationType) {
        Assert.notNull(field, "Field must not be null");
        implementationType = ((implementationType == null) ? ResolvableType.NONE : implementationType);
        final ResolvableType owner = implementationType.as(field.getDeclaringClass());
        return forType(null, new SerializableTypeWrapper.FieldTypeProvider(field), owner.asVariableResolver());
    }
    
    public static ResolvableType forField(final Field field, final int nestingLevel) {
        Assert.notNull(field, "Field must not be null");
        return forType(null, new SerializableTypeWrapper.FieldTypeProvider(field), null).getNested(nestingLevel);
    }
    
    public static ResolvableType forField(final Field field, final int nestingLevel, final Class<?> implementationClass) {
        Assert.notNull(field, "Field must not be null");
        final ResolvableType owner = forType(implementationClass).as(field.getDeclaringClass());
        return forType(null, new SerializableTypeWrapper.FieldTypeProvider(field), owner.asVariableResolver()).getNested(nestingLevel);
    }
    
    public static ResolvableType forConstructorParameter(final Constructor<?> constructor, final int parameterIndex) {
        Assert.notNull(constructor, "Constructor must not be null");
        return forMethodParameter(new MethodParameter(constructor, parameterIndex));
    }
    
    public static ResolvableType forConstructorParameter(final Constructor<?> constructor, final int parameterIndex, final Class<?> implementationClass) {
        Assert.notNull(constructor, "Constructor must not be null");
        final MethodParameter methodParameter = new MethodParameter(constructor, parameterIndex);
        methodParameter.setContainingClass(implementationClass);
        return forMethodParameter(methodParameter);
    }
    
    public static ResolvableType forMethodReturnType(final Method method) {
        Assert.notNull(method, "Method must not be null");
        return forMethodParameter(MethodParameter.forMethodOrConstructor(method, -1));
    }
    
    public static ResolvableType forMethodReturnType(final Method method, final Class<?> implementationClass) {
        Assert.notNull(method, "Method must not be null");
        final MethodParameter methodParameter = MethodParameter.forMethodOrConstructor(method, -1);
        methodParameter.setContainingClass(implementationClass);
        return forMethodParameter(methodParameter);
    }
    
    public static ResolvableType forMethodParameter(final Method method, final int parameterIndex) {
        Assert.notNull(method, "Method must not be null");
        return forMethodParameter(new MethodParameter(method, parameterIndex));
    }
    
    public static ResolvableType forMethodParameter(final Method method, final int parameterIndex, final Class<?> implementationClass) {
        Assert.notNull(method, "Method must not be null");
        final MethodParameter methodParameter = new MethodParameter(method, parameterIndex);
        methodParameter.setContainingClass(implementationClass);
        return forMethodParameter(methodParameter);
    }
    
    public static ResolvableType forMethodParameter(final MethodParameter methodParameter) {
        return forMethodParameter(methodParameter, (Type)null);
    }
    
    public static ResolvableType forMethodParameter(final MethodParameter methodParameter, ResolvableType implementationType) {
        Assert.notNull(methodParameter, "MethodParameter must not be null");
        implementationType = ((implementationType == null) ? forType(methodParameter.getContainingClass()) : implementationType);
        final ResolvableType owner = implementationType.as(methodParameter.getDeclaringClass());
        return forType(null, new SerializableTypeWrapper.MethodParameterTypeProvider(methodParameter), owner.asVariableResolver()).getNested(methodParameter.getNestingLevel(), methodParameter.typeIndexesPerLevel);
    }
    
    public static ResolvableType forMethodParameter(final MethodParameter methodParameter, final Type targetType) {
        Assert.notNull(methodParameter, "MethodParameter must not be null");
        final ResolvableType owner = forType(methodParameter.getContainingClass()).as(methodParameter.getDeclaringClass());
        return forType(targetType, new SerializableTypeWrapper.MethodParameterTypeProvider(methodParameter), owner.asVariableResolver()).getNested(methodParameter.getNestingLevel(), methodParameter.typeIndexesPerLevel);
    }
    
    public static ResolvableType forArrayComponent(final ResolvableType componentType) {
        Assert.notNull(componentType, "ComponentType must not be null");
        final Class<?> arrayClass = Array.newInstance(componentType.resolve(), 0).getClass();
        return new ResolvableType(arrayClass, null, null, componentType);
    }
    
    public static ResolvableType forClassWithGenerics(final Class<?> sourceClass, final Class<?>... generics) {
        Assert.notNull(sourceClass, "Source class must not be null");
        Assert.notNull(generics, "Generics must not be null");
        final ResolvableType[] resolvableGenerics = new ResolvableType[generics.length];
        for (int i = 0; i < generics.length; ++i) {
            resolvableGenerics[i] = forClass(generics[i]);
        }
        return forClassWithGenerics(sourceClass, resolvableGenerics);
    }
    
    public static ResolvableType forClassWithGenerics(final Class<?> sourceClass, final ResolvableType... generics) {
        Assert.notNull(sourceClass, "Source class must not be null");
        Assert.notNull(generics, "Generics must not be null");
        final TypeVariable<?>[] typeVariables = sourceClass.getTypeParameters();
        return forType(sourceClass, new TypeVariablesVariableResolver(typeVariables, generics));
    }
    
    public static ResolvableType forType(final Type type) {
        return forType(type, null, null);
    }
    
    public static ResolvableType forType(final Type type, final ResolvableType owner) {
        VariableResolver variableResolver = null;
        if (owner != null) {
            variableResolver = owner.asVariableResolver();
        }
        return forType(type, variableResolver);
    }
    
    static ResolvableType forType(final Type type, final VariableResolver variableResolver) {
        return forType(type, null, variableResolver);
    }
    
    static ResolvableType forType(Type type, final SerializableTypeWrapper.TypeProvider typeProvider, final VariableResolver variableResolver) {
        if (type == null && typeProvider != null) {
            type = SerializableTypeWrapper.forTypeProvider(typeProvider);
        }
        if (type == null) {
            return ResolvableType.NONE;
        }
        ResolvableType.cache.purgeUnreferencedEntries();
        final ResolvableType key = new ResolvableType(type, typeProvider, variableResolver);
        ResolvableType resolvableType = ResolvableType.cache.get(key);
        if (resolvableType == null) {
            resolvableType = new ResolvableType(type, typeProvider, variableResolver, null);
            ResolvableType.cache.put(resolvableType, resolvableType);
        }
        return resolvableType;
    }
    
    static {
        NONE = new ResolvableType(null, null, null, null);
        EMPTY_TYPES_ARRAY = new ResolvableType[0];
        cache = new ConcurrentReferenceHashMap<ResolvableType, ResolvableType>(256);
    }
    
    private class DefaultVariableResolver implements VariableResolver
    {
        @Override
        public ResolvableType resolveVariable(final TypeVariable<?> variable) {
            return ResolvableType.this.resolveVariable(variable);
        }
        
        @Override
        public Object getSource() {
            return ResolvableType.this;
        }
    }
    
    private static class TypeVariablesVariableResolver implements VariableResolver
    {
        private final TypeVariable<?>[] typeVariables;
        private final ResolvableType[] generics;
        
        public TypeVariablesVariableResolver(final TypeVariable<?>[] typeVariables, final ResolvableType[] generics) {
            Assert.isTrue(typeVariables.length == generics.length, "Mismatched number of generics specified");
            this.typeVariables = typeVariables;
            this.generics = generics;
        }
        
        @Override
        public ResolvableType resolveVariable(final TypeVariable<?> variable) {
            for (int i = 0; i < this.typeVariables.length; ++i) {
                if (SerializableTypeWrapper.unwrap(this.typeVariables[i]).equals(SerializableTypeWrapper.unwrap(variable))) {
                    return this.generics[i];
                }
            }
            return null;
        }
        
        @Override
        public Object getSource() {
            return this.generics;
        }
    }
    
    private static class WildcardBounds
    {
        private final Kind kind;
        private final ResolvableType[] bounds;
        
        public WildcardBounds(final Kind kind, final ResolvableType[] bounds) {
            this.kind = kind;
            this.bounds = bounds;
        }
        
        public boolean isSameKind(final WildcardBounds bounds) {
            return this.kind == bounds.kind;
        }
        
        public boolean isAssignableFrom(final ResolvableType... types) {
            for (final ResolvableType bound : this.bounds) {
                for (final ResolvableType type : types) {
                    if (!this.isAssignable(bound, type)) {
                        return false;
                    }
                }
            }
            return true;
        }
        
        private boolean isAssignable(final ResolvableType source, final ResolvableType from) {
            return (this.kind == Kind.UPPER) ? source.isAssignableFrom(from) : from.isAssignableFrom(source);
        }
        
        public ResolvableType[] getBounds() {
            return this.bounds;
        }
        
        public static WildcardBounds get(final ResolvableType type) {
            ResolvableType resolveToWildcard;
            for (resolveToWildcard = type; !(resolveToWildcard.getType() instanceof WildcardType); resolveToWildcard = resolveToWildcard.resolveType()) {
                if (resolveToWildcard == ResolvableType.NONE) {
                    return null;
                }
            }
            final WildcardType wildcardType = (WildcardType)resolveToWildcard.type;
            final Kind boundsType = (wildcardType.getLowerBounds().length > 0) ? Kind.LOWER : Kind.UPPER;
            final Type[] bounds = (boundsType == Kind.UPPER) ? wildcardType.getUpperBounds() : wildcardType.getLowerBounds();
            final ResolvableType[] resolvableBounds = new ResolvableType[bounds.length];
            for (int i = 0; i < bounds.length; ++i) {
                resolvableBounds[i] = ResolvableType.forType(bounds[i], type.variableResolver);
            }
            return new WildcardBounds(boundsType, resolvableBounds);
        }
        
        enum Kind
        {
            UPPER, 
            LOWER;
        }
    }
    
    interface VariableResolver extends Serializable
    {
        Object getSource();
        
        ResolvableType resolveVariable(final TypeVariable<?> p0);
    }
}
