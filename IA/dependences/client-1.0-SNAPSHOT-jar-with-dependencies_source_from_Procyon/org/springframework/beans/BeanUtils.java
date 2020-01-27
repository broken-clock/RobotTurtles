// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

import java.util.Collections;
import java.util.WeakHashMap;
import org.apache.commons.logging.LogFactory;
import java.util.List;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Locale;
import java.net.URL;
import java.net.URI;
import java.util.Date;
import org.springframework.core.MethodParameter;
import java.beans.PropertyEditor;
import java.beans.PropertyDescriptor;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Constructor;
import org.springframework.util.Assert;
import java.util.Map;
import org.apache.commons.logging.Log;

public abstract class BeanUtils
{
    private static final Log logger;
    private static final Map<Class<?>, Boolean> unknownEditorTypes;
    
    public static <T> T instantiate(final Class<T> clazz) throws BeanInstantiationException {
        Assert.notNull(clazz, "Class must not be null");
        if (clazz.isInterface()) {
            throw new BeanInstantiationException(clazz, "Specified class is an interface");
        }
        try {
            return clazz.newInstance();
        }
        catch (InstantiationException ex) {
            throw new BeanInstantiationException(clazz, "Is it an abstract class?", ex);
        }
        catch (IllegalAccessException ex2) {
            throw new BeanInstantiationException(clazz, "Is the constructor accessible?", ex2);
        }
    }
    
    public static <T> T instantiateClass(final Class<T> clazz) throws BeanInstantiationException {
        Assert.notNull(clazz, "Class must not be null");
        if (clazz.isInterface()) {
            throw new BeanInstantiationException(clazz, "Specified class is an interface");
        }
        try {
            return instantiateClass(clazz.getDeclaredConstructor((Class<?>[])new Class[0]), new Object[0]);
        }
        catch (NoSuchMethodException ex) {
            throw new BeanInstantiationException(clazz, "No default constructor found", ex);
        }
    }
    
    public static <T> T instantiateClass(final Class<?> clazz, final Class<T> assignableTo) throws BeanInstantiationException {
        Assert.isAssignable(assignableTo, clazz);
        return instantiateClass(clazz);
    }
    
    public static <T> T instantiateClass(final Constructor<T> ctor, final Object... args) throws BeanInstantiationException {
        Assert.notNull(ctor, "Constructor must not be null");
        try {
            ReflectionUtils.makeAccessible(ctor);
            return ctor.newInstance(args);
        }
        catch (InstantiationException ex) {
            throw new BeanInstantiationException(ctor.getDeclaringClass(), "Is it an abstract class?", ex);
        }
        catch (IllegalAccessException ex2) {
            throw new BeanInstantiationException(ctor.getDeclaringClass(), "Is the constructor accessible?", ex2);
        }
        catch (IllegalArgumentException ex3) {
            throw new BeanInstantiationException(ctor.getDeclaringClass(), "Illegal arguments for constructor", ex3);
        }
        catch (InvocationTargetException ex4) {
            throw new BeanInstantiationException(ctor.getDeclaringClass(), "Constructor threw exception", ex4.getTargetException());
        }
    }
    
    public static Method findMethod(final Class<?> clazz, final String methodName, final Class<?>... paramTypes) {
        try {
            return clazz.getMethod(methodName, paramTypes);
        }
        catch (NoSuchMethodException ex) {
            return findDeclaredMethod(clazz, methodName, paramTypes);
        }
    }
    
    public static Method findDeclaredMethod(final Class<?> clazz, final String methodName, final Class<?>... paramTypes) {
        try {
            return clazz.getDeclaredMethod(methodName, paramTypes);
        }
        catch (NoSuchMethodException ex) {
            if (clazz.getSuperclass() != null) {
                return findDeclaredMethod(clazz.getSuperclass(), methodName, paramTypes);
            }
            return null;
        }
    }
    
    public static Method findMethodWithMinimalParameters(final Class<?> clazz, final String methodName) throws IllegalArgumentException {
        Method targetMethod = findMethodWithMinimalParameters(clazz.getMethods(), methodName);
        if (targetMethod == null) {
            targetMethod = findDeclaredMethodWithMinimalParameters(clazz, methodName);
        }
        return targetMethod;
    }
    
    public static Method findDeclaredMethodWithMinimalParameters(final Class<?> clazz, final String methodName) throws IllegalArgumentException {
        Method targetMethod = findMethodWithMinimalParameters(clazz.getDeclaredMethods(), methodName);
        if (targetMethod == null && clazz.getSuperclass() != null) {
            targetMethod = findDeclaredMethodWithMinimalParameters(clazz.getSuperclass(), methodName);
        }
        return targetMethod;
    }
    
    public static Method findMethodWithMinimalParameters(final Method[] methods, final String methodName) throws IllegalArgumentException {
        Method targetMethod = null;
        int numMethodsFoundWithCurrentMinimumArgs = 0;
        for (final Method method : methods) {
            if (method.getName().equals(methodName)) {
                final int numParams = method.getParameterTypes().length;
                if (targetMethod == null || numParams < targetMethod.getParameterTypes().length) {
                    targetMethod = method;
                    numMethodsFoundWithCurrentMinimumArgs = 1;
                }
                else if (targetMethod.getParameterTypes().length == numParams) {
                    ++numMethodsFoundWithCurrentMinimumArgs;
                }
            }
        }
        if (numMethodsFoundWithCurrentMinimumArgs > 1) {
            throw new IllegalArgumentException("Cannot resolve method '" + methodName + "' to a unique method. Attempted to resolve to overloaded method with " + "the least number of parameters, but there were " + numMethodsFoundWithCurrentMinimumArgs + " candidates.");
        }
        return targetMethod;
    }
    
    public static Method resolveSignature(final String signature, final Class<?> clazz) {
        Assert.hasText(signature, "'signature' must not be empty");
        Assert.notNull(clazz, "Class must not be null");
        final int firstParen = signature.indexOf("(");
        final int lastParen = signature.indexOf(")");
        if (firstParen > -1 && lastParen == -1) {
            throw new IllegalArgumentException("Invalid method signature '" + signature + "': expected closing ')' for args list");
        }
        if (lastParen > -1 && firstParen == -1) {
            throw new IllegalArgumentException("Invalid method signature '" + signature + "': expected opening '(' for args list");
        }
        if (firstParen == -1 && lastParen == -1) {
            return findMethodWithMinimalParameters(clazz, signature);
        }
        final String methodName = signature.substring(0, firstParen);
        final String[] parameterTypeNames = StringUtils.commaDelimitedListToStringArray(signature.substring(firstParen + 1, lastParen));
        final Class<?>[] parameterTypes = (Class<?>[])new Class[parameterTypeNames.length];
        for (int i = 0; i < parameterTypeNames.length; ++i) {
            final String parameterTypeName = parameterTypeNames[i].trim();
            try {
                parameterTypes[i] = ClassUtils.forName(parameterTypeName, clazz.getClassLoader());
            }
            catch (Throwable ex) {
                throw new IllegalArgumentException("Invalid method signature: unable to resolve type [" + parameterTypeName + "] for argument " + i + ". Root cause: " + ex);
            }
        }
        return findMethod(clazz, methodName, parameterTypes);
    }
    
    public static PropertyDescriptor[] getPropertyDescriptors(final Class<?> clazz) throws BeansException {
        final CachedIntrospectionResults cr = CachedIntrospectionResults.forClass(clazz);
        return cr.getPropertyDescriptors();
    }
    
    public static PropertyDescriptor getPropertyDescriptor(final Class<?> clazz, final String propertyName) throws BeansException {
        final CachedIntrospectionResults cr = CachedIntrospectionResults.forClass(clazz);
        return cr.getPropertyDescriptor(propertyName);
    }
    
    public static PropertyDescriptor findPropertyForMethod(final Method method) throws BeansException {
        Assert.notNull(method, "Method must not be null");
        final PropertyDescriptor[] propertyDescriptors;
        final PropertyDescriptor[] pds = propertyDescriptors = getPropertyDescriptors(method.getDeclaringClass());
        for (final PropertyDescriptor pd : propertyDescriptors) {
            if (method.equals(pd.getReadMethod()) || method.equals(pd.getWriteMethod())) {
                return pd;
            }
        }
        return null;
    }
    
    public static PropertyEditor findEditorByConvention(final Class<?> targetType) {
        if (targetType == null || targetType.isArray() || BeanUtils.unknownEditorTypes.containsKey(targetType)) {
            return null;
        }
        ClassLoader cl = targetType.getClassLoader();
        if (cl == null) {
            try {
                cl = ClassLoader.getSystemClassLoader();
                if (cl == null) {
                    return null;
                }
            }
            catch (Throwable ex) {
                if (BeanUtils.logger.isDebugEnabled()) {
                    BeanUtils.logger.debug("Could not access system ClassLoader: " + ex);
                }
                return null;
            }
        }
        final String editorName = targetType.getName() + "Editor";
        try {
            final Class<?> editorClass = cl.loadClass(editorName);
            if (!PropertyEditor.class.isAssignableFrom(editorClass)) {
                if (BeanUtils.logger.isWarnEnabled()) {
                    BeanUtils.logger.warn("Editor class [" + editorName + "] does not implement [java.beans.PropertyEditor] interface");
                }
                BeanUtils.unknownEditorTypes.put(targetType, Boolean.TRUE);
                return null;
            }
            return instantiateClass(editorClass);
        }
        catch (ClassNotFoundException ex2) {
            if (BeanUtils.logger.isDebugEnabled()) {
                BeanUtils.logger.debug("No property editor [" + editorName + "] found for type " + targetType.getName() + " according to 'Editor' suffix convention");
            }
            BeanUtils.unknownEditorTypes.put(targetType, Boolean.TRUE);
            return null;
        }
    }
    
    public static Class<?> findPropertyType(final String propertyName, final Class<?>... beanClasses) {
        if (beanClasses != null) {
            for (final Class<?> beanClass : beanClasses) {
                final PropertyDescriptor pd = getPropertyDescriptor(beanClass, propertyName);
                if (pd != null) {
                    return pd.getPropertyType();
                }
            }
        }
        return Object.class;
    }
    
    public static MethodParameter getWriteMethodParameter(final PropertyDescriptor pd) {
        if (pd instanceof GenericTypeAwarePropertyDescriptor) {
            return new MethodParameter(((GenericTypeAwarePropertyDescriptor)pd).getWriteMethodParameter());
        }
        return new MethodParameter(pd.getWriteMethod(), 0);
    }
    
    public static boolean isSimpleProperty(final Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return isSimpleValueType(clazz) || (clazz.isArray() && isSimpleValueType(clazz.getComponentType()));
    }
    
    public static boolean isSimpleValueType(final Class<?> clazz) {
        return ClassUtils.isPrimitiveOrWrapper(clazz) || clazz.isEnum() || CharSequence.class.isAssignableFrom(clazz) || Number.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz) || clazz.equals(URI.class) || clazz.equals(URL.class) || clazz.equals(Locale.class) || clazz.equals(Class.class);
    }
    
    public static void copyProperties(final Object source, final Object target) throws BeansException {
        copyProperties(source, target, (Class<?>)null, (String[])null);
    }
    
    public static void copyProperties(final Object source, final Object target, final Class<?> editable) throws BeansException {
        copyProperties(source, target, editable, (String[])null);
    }
    
    public static void copyProperties(final Object source, final Object target, final String... ignoreProperties) throws BeansException {
        copyProperties(source, target, (Class<?>)null, ignoreProperties);
    }
    
    private static void copyProperties(final Object source, final Object target, final Class<?> editable, final String... ignoreProperties) throws BeansException {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");
        Class<?> actualEditable = target.getClass();
        if (editable != null) {
            if (!editable.isInstance(target)) {
                throw new IllegalArgumentException("Target class [" + target.getClass().getName() + "] not assignable to Editable class [" + editable.getName() + "]");
            }
            actualEditable = editable;
        }
        final PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
        final List<String> ignoreList = (ignoreProperties != null) ? Arrays.asList(ignoreProperties) : null;
        for (final PropertyDescriptor targetPd : targetPds) {
            final Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null && (ignoreProperties == null || !ignoreList.contains(targetPd.getName()))) {
                final PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null) {
                    final Method readMethod = sourcePd.getReadMethod();
                    if (readMethod != null && ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                        try {
                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }
                            final Object value = readMethod.invoke(source, new Object[0]);
                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }
                            writeMethod.invoke(target, value);
                        }
                        catch (Throwable ex) {
                            throw new FatalBeanException("Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                        }
                    }
                }
            }
        }
    }
    
    static {
        logger = LogFactory.getLog(BeanUtils.class);
        unknownEditorTypes = Collections.synchronizedMap(new WeakHashMap<Class<?>, Boolean>());
    }
}
