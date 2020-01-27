// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

import java.util.Collections;
import java.util.HashSet;
import java.util.Arrays;
import java.io.Externalizable;
import java.io.Serializable;
import java.util.Iterator;
import java.lang.reflect.Proxy;
import java.lang.reflect.Method;
import org.springframework.util.ClassUtils;
import java.util.Collection;
import org.springframework.util.Assert;
import java.util.Set;

public abstract class Conventions
{
    private static final String PLURAL_SUFFIX = "List";
    private static final Set<Class<?>> IGNORED_INTERFACES;
    
    public static String getVariableName(final Object value) {
        Assert.notNull(value, "Value must not be null");
        boolean pluralize = false;
        Class<?> valueClass;
        if (value.getClass().isArray()) {
            valueClass = value.getClass().getComponentType();
            pluralize = true;
        }
        else if (value instanceof Collection) {
            final Collection<?> collection = (Collection<?>)value;
            if (collection.isEmpty()) {
                throw new IllegalArgumentException("Cannot generate variable name for an empty Collection");
            }
            final Object valueToCheck = peekAhead(collection);
            valueClass = getClassForValue(valueToCheck);
            pluralize = true;
        }
        else {
            valueClass = getClassForValue(value);
        }
        final String name = ClassUtils.getShortNameAsProperty(valueClass);
        return pluralize ? pluralize(name) : name;
    }
    
    public static String getVariableNameForParameter(final MethodParameter parameter) {
        Assert.notNull(parameter, "MethodParameter must not be null");
        boolean pluralize = false;
        Class<?> valueClass;
        if (parameter.getParameterType().isArray()) {
            valueClass = parameter.getParameterType().getComponentType();
            pluralize = true;
        }
        else if (Collection.class.isAssignableFrom(parameter.getParameterType())) {
            valueClass = GenericCollectionTypeResolver.getCollectionParameterType(parameter);
            if (valueClass == null) {
                throw new IllegalArgumentException("Cannot generate variable name for non-typed Collection parameter type");
            }
            pluralize = true;
        }
        else {
            valueClass = parameter.getParameterType();
        }
        final String name = ClassUtils.getShortNameAsProperty(valueClass);
        return pluralize ? pluralize(name) : name;
    }
    
    public static String getVariableNameForReturnType(final Method method) {
        return getVariableNameForReturnType(method, method.getReturnType(), null);
    }
    
    public static String getVariableNameForReturnType(final Method method, final Object value) {
        return getVariableNameForReturnType(method, method.getReturnType(), value);
    }
    
    public static String getVariableNameForReturnType(final Method method, final Class<?> resolvedType, final Object value) {
        Assert.notNull(method, "Method must not be null");
        if (!Object.class.equals(resolvedType)) {
            boolean pluralize = false;
            Class<?> valueClass;
            if (resolvedType.isArray()) {
                valueClass = resolvedType.getComponentType();
                pluralize = true;
            }
            else if (Collection.class.isAssignableFrom(resolvedType)) {
                valueClass = GenericCollectionTypeResolver.getCollectionReturnType(method);
                if (valueClass == null) {
                    if (!(value instanceof Collection)) {
                        throw new IllegalArgumentException("Cannot generate variable name for non-typed Collection return type and a non-Collection value");
                    }
                    final Collection<?> collection = (Collection<?>)value;
                    if (collection.isEmpty()) {
                        throw new IllegalArgumentException("Cannot generate variable name for non-typed Collection return type and an empty Collection value");
                    }
                    final Object valueToCheck = peekAhead(collection);
                    valueClass = getClassForValue(valueToCheck);
                }
                pluralize = true;
            }
            else {
                valueClass = resolvedType;
            }
            final String name = ClassUtils.getShortNameAsProperty(valueClass);
            return pluralize ? pluralize(name) : name;
        }
        if (value == null) {
            throw new IllegalArgumentException("Cannot generate variable name for an Object return type with null value");
        }
        return getVariableName(value);
    }
    
    public static String attributeNameToPropertyName(final String attributeName) {
        Assert.notNull(attributeName, "'attributeName' must not be null");
        if (!attributeName.contains("-")) {
            return attributeName;
        }
        final char[] chars = attributeName.toCharArray();
        final char[] result = new char[chars.length - 1];
        int currPos = 0;
        boolean upperCaseNext = false;
        for (final char c : chars) {
            if (c == '-') {
                upperCaseNext = true;
            }
            else if (upperCaseNext) {
                result[currPos++] = Character.toUpperCase(c);
                upperCaseNext = false;
            }
            else {
                result[currPos++] = c;
            }
        }
        return new String(result, 0, currPos);
    }
    
    public static String getQualifiedAttributeName(final Class<?> enclosingClass, final String attributeName) {
        Assert.notNull(enclosingClass, "'enclosingClass' must not be null");
        Assert.notNull(attributeName, "'attributeName' must not be null");
        return enclosingClass.getName() + "." + attributeName;
    }
    
    private static Class<?> getClassForValue(final Object value) {
        Class<?> valueClass = value.getClass();
        if (Proxy.isProxyClass(valueClass)) {
            final Class<?>[] interfaces;
            final Class<?>[] ifcs = interfaces = valueClass.getInterfaces();
            for (final Class<?> ifc : interfaces) {
                if (!Conventions.IGNORED_INTERFACES.contains(ifc)) {
                    return ifc;
                }
            }
        }
        else if (valueClass.getName().lastIndexOf(36) != -1 && valueClass.getDeclaringClass() == null) {
            valueClass = valueClass.getSuperclass();
        }
        return valueClass;
    }
    
    private static String pluralize(final String name) {
        return name + "List";
    }
    
    private static <E> E peekAhead(final Collection<E> collection) {
        final Iterator<E> it = collection.iterator();
        if (!it.hasNext()) {
            throw new IllegalStateException("Unable to peek ahead in non-empty collection - no element found");
        }
        final E value = it.next();
        if (value == null) {
            throw new IllegalStateException("Unable to peek ahead in non-empty collection - only null element found");
        }
        return value;
    }
    
    static {
        IGNORED_INTERFACES = Collections.unmodifiableSet((Set<? extends Class<?>>)new HashSet<Class<?>>((Collection<? extends Class<?>>)Arrays.asList(Serializable.class, Externalizable.class, Cloneable.class, Comparable.class)));
    }
}
