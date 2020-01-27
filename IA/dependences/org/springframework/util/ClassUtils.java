// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;
import java.security.AccessControlException;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.HashSet;
import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.lang.reflect.Method;
import java.beans.Introspector;
import java.lang.reflect.Array;
import java.util.Map;

public abstract class ClassUtils
{
    public static final String ARRAY_SUFFIX = "[]";
    private static final String INTERNAL_ARRAY_PREFIX = "[";
    private static final String NON_PRIMITIVE_ARRAY_PREFIX = "[L";
    private static final char PACKAGE_SEPARATOR = '.';
    private static final char INNER_CLASS_SEPARATOR = '$';
    public static final String CGLIB_CLASS_SEPARATOR = "$$";
    public static final String CLASS_FILE_SUFFIX = ".class";
    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap;
    private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap;
    private static final Map<String, Class<?>> primitiveTypeNameMap;
    private static final Map<String, Class<?>> commonClassCache;
    
    private static void registerCommonClasses(final Class<?>... commonClasses) {
        for (final Class<?> clazz : commonClasses) {
            ClassUtils.commonClassCache.put(clazz.getName(), clazz);
        }
    }
    
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        }
        catch (Throwable t) {}
        if (cl == null) {
            cl = ClassUtils.class.getClassLoader();
        }
        return cl;
    }
    
    public static ClassLoader overrideThreadContextClassLoader(final ClassLoader classLoaderToUse) {
        final Thread currentThread = Thread.currentThread();
        final ClassLoader threadContextClassLoader = currentThread.getContextClassLoader();
        if (classLoaderToUse != null && !classLoaderToUse.equals(threadContextClassLoader)) {
            currentThread.setContextClassLoader(classLoaderToUse);
            return threadContextClassLoader;
        }
        return null;
    }
    
    public static Class<?> forName(final String name, final ClassLoader classLoader) throws ClassNotFoundException, LinkageError {
        Assert.notNull(name, "Name must not be null");
        Class<?> clazz = resolvePrimitiveClassName(name);
        if (clazz == null) {
            clazz = ClassUtils.commonClassCache.get(name);
        }
        if (clazz != null) {
            return clazz;
        }
        if (name.endsWith("[]")) {
            final String elementClassName = name.substring(0, name.length() - "[]".length());
            final Class<?> elementClass = forName(elementClassName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }
        if (name.startsWith("[L") && name.endsWith(";")) {
            final String elementName = name.substring("[L".length(), name.length() - 1);
            final Class<?> elementClass = forName(elementName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }
        if (name.startsWith("[")) {
            final String elementName = name.substring("[".length());
            final Class<?> elementClass = forName(elementName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }
        ClassLoader classLoaderToUse = classLoader;
        if (classLoaderToUse == null) {
            classLoaderToUse = getDefaultClassLoader();
        }
        try {
            return classLoaderToUse.loadClass(name);
        }
        catch (ClassNotFoundException ex) {
            final int lastDotIndex = name.lastIndexOf(46);
            if (lastDotIndex != -1) {
                final String innerClassName = name.substring(0, lastDotIndex) + '$' + name.substring(lastDotIndex + 1);
                try {
                    return classLoaderToUse.loadClass(innerClassName);
                }
                catch (ClassNotFoundException ex2) {}
            }
            throw ex;
        }
    }
    
    public static Class<?> resolveClassName(final String className, final ClassLoader classLoader) throws IllegalArgumentException {
        try {
            return forName(className, classLoader);
        }
        catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException("Cannot find class [" + className + "]", ex);
        }
        catch (LinkageError ex2) {
            throw new IllegalArgumentException("Error loading class [" + className + "]: problem with class file or dependent class.", ex2);
        }
    }
    
    public static Class<?> resolvePrimitiveClassName(final String name) {
        Class<?> result = null;
        if (name != null && name.length() <= 8) {
            result = ClassUtils.primitiveTypeNameMap.get(name);
        }
        return result;
    }
    
    public static boolean isPresent(final String className, final ClassLoader classLoader) {
        try {
            forName(className, classLoader);
            return true;
        }
        catch (Throwable ex) {
            return false;
        }
    }
    
    public static Class<?> getUserClass(final Object instance) {
        Assert.notNull(instance, "Instance must not be null");
        return getUserClass(instance.getClass());
    }
    
    public static Class<?> getUserClass(final Class<?> clazz) {
        if (clazz != null && clazz.getName().contains("$$")) {
            final Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && !Object.class.equals(superClass)) {
                return superClass;
            }
        }
        return clazz;
    }
    
    public static boolean isCacheSafe(final Class<?> clazz, final ClassLoader classLoader) {
        Assert.notNull(clazz, "Class must not be null");
        final ClassLoader target = clazz.getClassLoader();
        if (target == null) {
            return false;
        }
        ClassLoader cur = classLoader;
        if (cur == target) {
            return true;
        }
        while (cur != null) {
            cur = cur.getParent();
            if (cur == target) {
                return true;
            }
        }
        return false;
    }
    
    public static String getShortName(final String className) {
        Assert.hasLength(className, "Class name must not be empty");
        final int lastDotIndex = className.lastIndexOf(46);
        int nameEndIndex = className.indexOf("$$");
        if (nameEndIndex == -1) {
            nameEndIndex = className.length();
        }
        String shortName = className.substring(lastDotIndex + 1, nameEndIndex);
        shortName = shortName.replace('$', '.');
        return shortName;
    }
    
    public static String getShortName(final Class<?> clazz) {
        return getShortName(getQualifiedName(clazz));
    }
    
    public static String getShortNameAsProperty(final Class<?> clazz) {
        String shortName = getShortName(clazz);
        final int dotIndex = shortName.lastIndexOf(46);
        shortName = ((dotIndex != -1) ? shortName.substring(dotIndex + 1) : shortName);
        return Introspector.decapitalize(shortName);
    }
    
    public static String getClassFileName(final Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        final String className = clazz.getName();
        final int lastDotIndex = className.lastIndexOf(46);
        return className.substring(lastDotIndex + 1) + ".class";
    }
    
    public static String getPackageName(final Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return getPackageName(clazz.getName());
    }
    
    public static String getPackageName(final String fqClassName) {
        Assert.notNull(fqClassName, "Class name must not be null");
        final int lastDotIndex = fqClassName.lastIndexOf(46);
        return (lastDotIndex != -1) ? fqClassName.substring(0, lastDotIndex) : "";
    }
    
    public static String getQualifiedName(final Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        if (clazz.isArray()) {
            return getQualifiedNameForArray(clazz);
        }
        return clazz.getName();
    }
    
    private static String getQualifiedNameForArray(Class<?> clazz) {
        final StringBuilder result = new StringBuilder();
        while (clazz.isArray()) {
            clazz = clazz.getComponentType();
            result.append("[]");
        }
        result.insert(0, clazz.getName());
        return result.toString();
    }
    
    public static String getQualifiedMethodName(final Method method) {
        Assert.notNull(method, "Method must not be null");
        return method.getDeclaringClass().getName() + "." + method.getName();
    }
    
    public static String getDescriptiveType(final Object value) {
        if (value == null) {
            return null;
        }
        final Class<?> clazz = value.getClass();
        if (Proxy.isProxyClass(clazz)) {
            final StringBuilder result = new StringBuilder(clazz.getName());
            result.append(" implementing ");
            final Class<?>[] ifcs = clazz.getInterfaces();
            for (int i = 0; i < ifcs.length; ++i) {
                result.append(ifcs[i].getName());
                if (i < ifcs.length - 1) {
                    result.append(',');
                }
            }
            return result.toString();
        }
        if (clazz.isArray()) {
            return getQualifiedNameForArray(clazz);
        }
        return clazz.getName();
    }
    
    public static boolean matchesTypeName(final Class<?> clazz, final String typeName) {
        return typeName != null && (typeName.equals(clazz.getName()) || typeName.equals(clazz.getSimpleName()) || (clazz.isArray() && typeName.equals(getQualifiedNameForArray(clazz))));
    }
    
    public static boolean hasConstructor(final Class<?> clazz, final Class<?>... paramTypes) {
        return getConstructorIfAvailable(clazz, paramTypes) != null;
    }
    
    public static <T> Constructor<T> getConstructorIfAvailable(final Class<T> clazz, final Class<?>... paramTypes) {
        Assert.notNull(clazz, "Class must not be null");
        try {
            return clazz.getConstructor(paramTypes);
        }
        catch (NoSuchMethodException ex) {
            return null;
        }
    }
    
    public static boolean hasMethod(final Class<?> clazz, final String methodName, final Class<?>... paramTypes) {
        return getMethodIfAvailable(clazz, methodName, paramTypes) != null;
    }
    
    public static Method getMethod(final Class<?> clazz, final String methodName, final Class<?>... paramTypes) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(methodName, "Method name must not be null");
        if (paramTypes != null) {
            try {
                return clazz.getMethod(methodName, paramTypes);
            }
            catch (NoSuchMethodException ex) {
                throw new IllegalStateException("Expected method not found: " + ex);
            }
        }
        final Set<Method> candidates = new HashSet<Method>(1);
        final Method[] methods2;
        final Method[] methods = methods2 = clazz.getMethods();
        for (final Method method : methods2) {
            if (methodName.equals(method.getName())) {
                candidates.add(method);
            }
        }
        if (candidates.size() == 1) {
            return candidates.iterator().next();
        }
        if (candidates.isEmpty()) {
            throw new IllegalStateException("Expected method not found: " + clazz + "." + methodName);
        }
        throw new IllegalStateException("No unique method found: " + clazz + "." + methodName);
    }
    
    public static Method getMethodIfAvailable(final Class<?> clazz, final String methodName, final Class<?>... paramTypes) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(methodName, "Method name must not be null");
        if (paramTypes != null) {
            try {
                return clazz.getMethod(methodName, paramTypes);
            }
            catch (NoSuchMethodException ex) {
                return null;
            }
        }
        final Set<Method> candidates = new HashSet<Method>(1);
        final Method[] methods2;
        final Method[] methods = methods2 = clazz.getMethods();
        for (final Method method : methods2) {
            if (methodName.equals(method.getName())) {
                candidates.add(method);
            }
        }
        if (candidates.size() == 1) {
            return candidates.iterator().next();
        }
        return null;
    }
    
    public static int getMethodCountForName(final Class<?> clazz, final String methodName) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(methodName, "Method name must not be null");
        int count = 0;
        final Method[] declaredMethods2;
        final Method[] declaredMethods = declaredMethods2 = clazz.getDeclaredMethods();
        for (final Method method : declaredMethods2) {
            if (methodName.equals(method.getName())) {
                ++count;
            }
        }
        final Class<?>[] interfaces;
        final Class<?>[] ifcs = interfaces = clazz.getInterfaces();
        for (final Class<?> ifc : interfaces) {
            count += getMethodCountForName(ifc, methodName);
        }
        if (clazz.getSuperclass() != null) {
            count += getMethodCountForName(clazz.getSuperclass(), methodName);
        }
        return count;
    }
    
    public static boolean hasAtLeastOneMethodWithName(final Class<?> clazz, final String methodName) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(methodName, "Method name must not be null");
        final Method[] declaredMethods2;
        final Method[] declaredMethods = declaredMethods2 = clazz.getDeclaredMethods();
        for (final Method method : declaredMethods2) {
            if (method.getName().equals(methodName)) {
                return true;
            }
        }
        final Class<?>[] interfaces;
        final Class<?>[] ifcs = interfaces = clazz.getInterfaces();
        for (final Class<?> ifc : interfaces) {
            if (hasAtLeastOneMethodWithName(ifc, methodName)) {
                return true;
            }
        }
        return clazz.getSuperclass() != null && hasAtLeastOneMethodWithName(clazz.getSuperclass(), methodName);
    }
    
    public static Method getMostSpecificMethod(final Method method, final Class<?> targetClass) {
        if (method != null && isOverridable(method, targetClass) && targetClass != null && !targetClass.equals(method.getDeclaringClass())) {
            try {
                if (Modifier.isPublic(method.getModifiers())) {
                    try {
                        return targetClass.getMethod(method.getName(), method.getParameterTypes());
                    }
                    catch (NoSuchMethodException ex) {
                        return method;
                    }
                }
                final Method specificMethod = ReflectionUtils.findMethod(targetClass, method.getName(), method.getParameterTypes());
                return (specificMethod != null) ? specificMethod : method;
            }
            catch (AccessControlException ex2) {}
        }
        return method;
    }
    
    public static boolean isUserLevelMethod(final Method method) {
        Assert.notNull(method, "Method must not be null");
        return method.isBridge() || (!method.isSynthetic() && !isGroovyObjectMethod(method));
    }
    
    private static boolean isGroovyObjectMethod(final Method method) {
        return method.getDeclaringClass().getName().equals("groovy.lang.GroovyObject");
    }
    
    private static boolean isOverridable(final Method method, final Class<?> targetClass) {
        return !Modifier.isPrivate(method.getModifiers()) && (Modifier.isPublic(method.getModifiers()) || Modifier.isProtected(method.getModifiers()) || getPackageName(method.getDeclaringClass()).equals(getPackageName(targetClass)));
    }
    
    public static Method getStaticMethod(final Class<?> clazz, final String methodName, final Class<?>... args) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(methodName, "Method name must not be null");
        try {
            final Method method = clazz.getMethod(methodName, args);
            return Modifier.isStatic(method.getModifiers()) ? method : null;
        }
        catch (NoSuchMethodException ex) {
            return null;
        }
    }
    
    public static boolean isPrimitiveWrapper(final Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return ClassUtils.primitiveWrapperTypeMap.containsKey(clazz);
    }
    
    public static boolean isPrimitiveOrWrapper(final Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return clazz.isPrimitive() || isPrimitiveWrapper(clazz);
    }
    
    public static boolean isPrimitiveArray(final Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return clazz.isArray() && clazz.getComponentType().isPrimitive();
    }
    
    public static boolean isPrimitiveWrapperArray(final Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return clazz.isArray() && isPrimitiveWrapper(clazz.getComponentType());
    }
    
    public static Class<?> resolvePrimitiveIfNecessary(final Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return (clazz.isPrimitive() && clazz != Void.TYPE) ? ClassUtils.primitiveTypeToWrapperMap.get(clazz) : clazz;
    }
    
    public static boolean isAssignable(final Class<?> lhsType, final Class<?> rhsType) {
        Assert.notNull(lhsType, "Left-hand side type must not be null");
        Assert.notNull(rhsType, "Right-hand side type must not be null");
        if (lhsType.isAssignableFrom(rhsType)) {
            return true;
        }
        if (lhsType.isPrimitive()) {
            final Class<?> resolvedPrimitive = ClassUtils.primitiveWrapperTypeMap.get(rhsType);
            if (resolvedPrimitive != null && lhsType.equals(resolvedPrimitive)) {
                return true;
            }
        }
        else {
            final Class<?> resolvedWrapper = ClassUtils.primitiveTypeToWrapperMap.get(rhsType);
            if (resolvedWrapper != null && lhsType.isAssignableFrom(resolvedWrapper)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isAssignableValue(final Class<?> type, final Object value) {
        Assert.notNull(type, "Type must not be null");
        return (value != null) ? isAssignable(type, value.getClass()) : (!type.isPrimitive());
    }
    
    public static String convertResourcePathToClassName(final String resourcePath) {
        Assert.notNull(resourcePath, "Resource path must not be null");
        return resourcePath.replace('/', '.');
    }
    
    public static String convertClassNameToResourcePath(final String className) {
        Assert.notNull(className, "Class name must not be null");
        return className.replace('.', '/');
    }
    
    public static String addResourcePathToPackagePath(final Class<?> clazz, final String resourceName) {
        Assert.notNull(resourceName, "Resource name must not be null");
        if (!resourceName.startsWith("/")) {
            return classPackageAsResourcePath(clazz) + "/" + resourceName;
        }
        return classPackageAsResourcePath(clazz) + resourceName;
    }
    
    public static String classPackageAsResourcePath(final Class<?> clazz) {
        if (clazz == null) {
            return "";
        }
        final String className = clazz.getName();
        final int packageEndIndex = className.lastIndexOf(46);
        if (packageEndIndex == -1) {
            return "";
        }
        final String packageName = className.substring(0, packageEndIndex);
        return packageName.replace('.', '/');
    }
    
    public static String classNamesToString(final Class<?>... classes) {
        return classNamesToString(Arrays.asList(classes));
    }
    
    public static String classNamesToString(final Collection<Class<?>> classes) {
        if (CollectionUtils.isEmpty(classes)) {
            return "[]";
        }
        final StringBuilder sb = new StringBuilder("[");
        final Iterator<Class<?>> it = classes.iterator();
        while (it.hasNext()) {
            final Class<?> clazz = it.next();
            sb.append(clazz.getName());
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    public static Class<?>[] toClassArray(final Collection<Class<?>> collection) {
        if (collection == null) {
            return null;
        }
        return collection.toArray(new Class[collection.size()]);
    }
    
    public static Class<?>[] getAllInterfaces(final Object instance) {
        Assert.notNull(instance, "Instance must not be null");
        return getAllInterfacesForClass(instance.getClass());
    }
    
    public static Class<?>[] getAllInterfacesForClass(final Class<?> clazz) {
        return getAllInterfacesForClass(clazz, null);
    }
    
    public static Class<?>[] getAllInterfacesForClass(final Class<?> clazz, final ClassLoader classLoader) {
        final Set<Class<?>> ifcs = getAllInterfacesForClassAsSet(clazz, classLoader);
        return ifcs.toArray(new Class[ifcs.size()]);
    }
    
    public static Set<Class<?>> getAllInterfacesAsSet(final Object instance) {
        Assert.notNull(instance, "Instance must not be null");
        return getAllInterfacesForClassAsSet(instance.getClass());
    }
    
    public static Set<Class<?>> getAllInterfacesForClassAsSet(final Class<?> clazz) {
        return getAllInterfacesForClassAsSet(clazz, null);
    }
    
    public static Set<Class<?>> getAllInterfacesForClassAsSet(Class<?> clazz, final ClassLoader classLoader) {
        Assert.notNull(clazz, "Class must not be null");
        if (clazz.isInterface() && isVisible(clazz, classLoader)) {
            return Collections.singleton(clazz);
        }
        final Set<Class<?>> interfaces = new LinkedHashSet<Class<?>>();
        while (clazz != null) {
            final Class<?>[] interfaces2;
            final Class<?>[] ifcs = interfaces2 = clazz.getInterfaces();
            for (final Class<?> ifc : interfaces2) {
                interfaces.addAll(getAllInterfacesForClassAsSet(ifc, classLoader));
            }
            clazz = clazz.getSuperclass();
        }
        return interfaces;
    }
    
    public static Class<?> createCompositeInterface(final Class<?>[] interfaces, final ClassLoader classLoader) {
        Assert.notEmpty(interfaces, "Interfaces must not be empty");
        Assert.notNull(classLoader, "ClassLoader must not be null");
        return Proxy.getProxyClass(classLoader, interfaces);
    }
    
    public static Class<?> determineCommonAncestor(final Class<?> clazz1, final Class<?> clazz2) {
        if (clazz1 == null) {
            return clazz2;
        }
        if (clazz2 == null) {
            return clazz1;
        }
        if (clazz1.isAssignableFrom(clazz2)) {
            return clazz1;
        }
        if (clazz2.isAssignableFrom(clazz1)) {
            return clazz2;
        }
        Class<?> ancestor = clazz1;
        do {
            ancestor = ancestor.getSuperclass();
            if (ancestor == null || Object.class.equals(ancestor)) {
                return null;
            }
        } while (!ancestor.isAssignableFrom(clazz2));
        return ancestor;
    }
    
    public static boolean isVisible(final Class<?> clazz, final ClassLoader classLoader) {
        if (classLoader == null) {
            return true;
        }
        try {
            final Class<?> actualClass = classLoader.loadClass(clazz.getName());
            return clazz == actualClass;
        }
        catch (ClassNotFoundException ex) {
            return false;
        }
    }
    
    public static boolean isCglibProxy(final Object object) {
        return isCglibProxyClass(object.getClass());
    }
    
    public static boolean isCglibProxyClass(final Class<?> clazz) {
        return clazz != null && isCglibProxyClassName(clazz.getName());
    }
    
    public static boolean isCglibProxyClassName(final String className) {
        return className != null && className.contains("$$");
    }
    
    static {
        primitiveWrapperTypeMap = new HashMap<Class<?>, Class<?>>(8);
        primitiveTypeToWrapperMap = new HashMap<Class<?>, Class<?>>(8);
        primitiveTypeNameMap = new HashMap<String, Class<?>>(32);
        commonClassCache = new HashMap<String, Class<?>>(32);
        ClassUtils.primitiveWrapperTypeMap.put(Boolean.class, Boolean.TYPE);
        ClassUtils.primitiveWrapperTypeMap.put(Byte.class, Byte.TYPE);
        ClassUtils.primitiveWrapperTypeMap.put(Character.class, Character.TYPE);
        ClassUtils.primitiveWrapperTypeMap.put(Double.class, Double.TYPE);
        ClassUtils.primitiveWrapperTypeMap.put(Float.class, Float.TYPE);
        ClassUtils.primitiveWrapperTypeMap.put(Integer.class, Integer.TYPE);
        ClassUtils.primitiveWrapperTypeMap.put(Long.class, Long.TYPE);
        ClassUtils.primitiveWrapperTypeMap.put(Short.class, Short.TYPE);
        for (final Map.Entry<Class<?>, Class<?>> entry : ClassUtils.primitiveWrapperTypeMap.entrySet()) {
            ClassUtils.primitiveTypeToWrapperMap.put(entry.getValue(), entry.getKey());
            registerCommonClasses(entry.getKey());
        }
        final Set<Class<?>> primitiveTypes = new HashSet<Class<?>>(32);
        primitiveTypes.addAll(ClassUtils.primitiveWrapperTypeMap.values());
        primitiveTypes.addAll((Collection<? extends Class<?>>)Arrays.asList(boolean[].class, byte[].class, char[].class, double[].class, float[].class, int[].class, long[].class, short[].class));
        primitiveTypes.add(Void.TYPE);
        for (final Class<?> primitiveType : primitiveTypes) {
            ClassUtils.primitiveTypeNameMap.put(primitiveType.getName(), primitiveType);
        }
        registerCommonClasses(Boolean[].class, Byte[].class, Character[].class, Double[].class, Float[].class, Integer[].class, Long[].class, Short[].class);
        registerCommonClasses(Number.class, Number[].class, String.class, String[].class, Object.class, Object[].class, Class.class, Class[].class);
        registerCommonClasses(Throwable.class, Exception.class, RuntimeException.class, Error.class, StackTraceElement.class, StackTraceElement[].class);
    }
}
