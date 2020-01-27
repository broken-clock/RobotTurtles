// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Arrays;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.regex.Pattern;

public abstract class ReflectionUtils
{
    private static final Pattern CGLIB_RENAMED_METHOD_PATTERN;
    public static FieldFilter COPYABLE_FIELDS;
    public static MethodFilter NON_BRIDGED_METHODS;
    public static MethodFilter USER_DECLARED_METHODS;
    
    public static Field findField(final Class<?> clazz, final String name) {
        return findField(clazz, name, null);
    }
    
    public static Field findField(final Class<?> clazz, final String name, final Class<?> type) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.isTrue(name != null || type != null, "Either name or type of the field must be specified");
        for (Class<?> searchType = clazz; !Object.class.equals(searchType) && searchType != null; searchType = searchType.getSuperclass()) {
            final Field[] declaredFields;
            final Field[] fields = declaredFields = searchType.getDeclaredFields();
            for (final Field field : declaredFields) {
                if ((name == null || name.equals(field.getName())) && (type == null || type.equals(field.getType()))) {
                    return field;
                }
            }
        }
        return null;
    }
    
    public static void setField(final Field field, final Object target, final Object value) {
        try {
            field.set(target, value);
        }
        catch (IllegalAccessException ex) {
            handleReflectionException(ex);
            throw new IllegalStateException("Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
        }
    }
    
    public static Object getField(final Field field, final Object target) {
        try {
            return field.get(target);
        }
        catch (IllegalAccessException ex) {
            handleReflectionException(ex);
            throw new IllegalStateException("Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
        }
    }
    
    public static Method findMethod(final Class<?> clazz, final String name) {
        return findMethod(clazz, name, (Class<?>[])new Class[0]);
    }
    
    public static Method findMethod(final Class<?> clazz, final String name, final Class<?>... paramTypes) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(name, "Method name must not be null");
        for (Class<?> searchType = clazz; searchType != null; searchType = searchType.getSuperclass()) {
            final Method[] array;
            final Method[] methods = array = (searchType.isInterface() ? searchType.getMethods() : searchType.getDeclaredMethods());
            for (final Method method : array) {
                if (name.equals(method.getName()) && (paramTypes == null || Arrays.equals(paramTypes, method.getParameterTypes()))) {
                    return method;
                }
            }
        }
        return null;
    }
    
    public static Object invokeMethod(final Method method, final Object target) {
        return invokeMethod(method, target, new Object[0]);
    }
    
    public static Object invokeMethod(final Method method, final Object target, final Object... args) {
        try {
            return method.invoke(target, args);
        }
        catch (Exception ex) {
            handleReflectionException(ex);
            throw new IllegalStateException("Should never get here");
        }
    }
    
    public static Object invokeJdbcMethod(final Method method, final Object target) throws SQLException {
        return invokeJdbcMethod(method, target, new Object[0]);
    }
    
    public static Object invokeJdbcMethod(final Method method, final Object target, final Object... args) throws SQLException {
        try {
            return method.invoke(target, args);
        }
        catch (IllegalAccessException ex) {
            handleReflectionException(ex);
        }
        catch (InvocationTargetException ex2) {
            if (ex2.getTargetException() instanceof SQLException) {
                throw (SQLException)ex2.getTargetException();
            }
            handleInvocationTargetException(ex2);
        }
        throw new IllegalStateException("Should never get here");
    }
    
    public static void handleReflectionException(final Exception ex) {
        if (ex instanceof NoSuchMethodException) {
            throw new IllegalStateException("Method not found: " + ex.getMessage());
        }
        if (ex instanceof IllegalAccessException) {
            throw new IllegalStateException("Could not access method: " + ex.getMessage());
        }
        if (ex instanceof InvocationTargetException) {
            handleInvocationTargetException((InvocationTargetException)ex);
        }
        if (ex instanceof RuntimeException) {
            throw (RuntimeException)ex;
        }
        throw new UndeclaredThrowableException(ex);
    }
    
    public static void handleInvocationTargetException(final InvocationTargetException ex) {
        rethrowRuntimeException(ex.getTargetException());
    }
    
    public static void rethrowRuntimeException(final Throwable ex) {
        if (ex instanceof RuntimeException) {
            throw (RuntimeException)ex;
        }
        if (ex instanceof Error) {
            throw (Error)ex;
        }
        throw new UndeclaredThrowableException(ex);
    }
    
    public static void rethrowException(final Throwable ex) throws Exception {
        if (ex instanceof Exception) {
            throw (Exception)ex;
        }
        if (ex instanceof Error) {
            throw (Error)ex;
        }
        throw new UndeclaredThrowableException(ex);
    }
    
    public static boolean declaresException(final Method method, final Class<?> exceptionType) {
        Assert.notNull(method, "Method must not be null");
        final Class<?>[] exceptionTypes;
        final Class<?>[] declaredExceptions = exceptionTypes = method.getExceptionTypes();
        for (final Class<?> declaredException : exceptionTypes) {
            if (declaredException.isAssignableFrom(exceptionType)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isPublicStaticFinal(final Field field) {
        final int modifiers = field.getModifiers();
        return Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
    }
    
    public static boolean isEqualsMethod(final Method method) {
        if (method == null || !method.getName().equals("equals")) {
            return false;
        }
        final Class<?>[] paramTypes = method.getParameterTypes();
        return paramTypes.length == 1 && paramTypes[0] == Object.class;
    }
    
    public static boolean isHashCodeMethod(final Method method) {
        return method != null && method.getName().equals("hashCode") && method.getParameterTypes().length == 0;
    }
    
    public static boolean isToStringMethod(final Method method) {
        return method != null && method.getName().equals("toString") && method.getParameterTypes().length == 0;
    }
    
    public static boolean isObjectMethod(final Method method) {
        if (method == null) {
            return false;
        }
        try {
            Object.class.getDeclaredMethod(method.getName(), method.getParameterTypes());
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }
    
    public static boolean isCglibRenamedMethod(final Method renamedMethod) {
        return ReflectionUtils.CGLIB_RENAMED_METHOD_PATTERN.matcher(renamedMethod.getName()).matches();
    }
    
    public static void makeAccessible(final Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }
    
    public static void makeAccessible(final Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !method.isAccessible()) {
            method.setAccessible(true);
        }
    }
    
    public static void makeAccessible(final Constructor<?> ctor) {
        if ((!Modifier.isPublic(ctor.getModifiers()) || !Modifier.isPublic(ctor.getDeclaringClass().getModifiers())) && !ctor.isAccessible()) {
            ctor.setAccessible(true);
        }
    }
    
    public static void doWithMethods(final Class<?> clazz, final MethodCallback mc) throws IllegalArgumentException {
        doWithMethods(clazz, mc, null);
    }
    
    public static void doWithMethods(final Class<?> clazz, final MethodCallback mc, final MethodFilter mf) throws IllegalArgumentException {
        final Method[] declaredMethods;
        final Method[] methods = declaredMethods = clazz.getDeclaredMethods();
        for (final Method method : declaredMethods) {
            if (mf == null || mf.matches(method)) {
                try {
                    mc.doWith(method);
                }
                catch (IllegalAccessException ex) {
                    throw new IllegalStateException("Shouldn't be illegal to access method '" + method.getName() + "': " + ex);
                }
            }
        }
        if (clazz.getSuperclass() != null) {
            doWithMethods(clazz.getSuperclass(), mc, mf);
        }
        else if (clazz.isInterface()) {
            for (final Class<?> superIfc : clazz.getInterfaces()) {
                doWithMethods(superIfc, mc, mf);
            }
        }
    }
    
    public static Method[] getAllDeclaredMethods(final Class<?> leafClass) throws IllegalArgumentException {
        final List<Method> methods = new ArrayList<Method>(32);
        doWithMethods(leafClass, new MethodCallback() {
            @Override
            public void doWith(final Method method) {
                methods.add(method);
            }
        });
        return methods.toArray(new Method[methods.size()]);
    }
    
    public static Method[] getUniqueDeclaredMethods(final Class<?> leafClass) throws IllegalArgumentException {
        final List<Method> methods = new ArrayList<Method>(32);
        doWithMethods(leafClass, new MethodCallback() {
            @Override
            public void doWith(final Method method) {
                boolean knownSignature = false;
                Method methodBeingOverriddenWithCovariantReturnType = null;
                for (final Method existingMethod : methods) {
                    if (method.getName().equals(existingMethod.getName()) && Arrays.equals(method.getParameterTypes(), existingMethod.getParameterTypes())) {
                        if (existingMethod.getReturnType() != method.getReturnType() && existingMethod.getReturnType().isAssignableFrom(method.getReturnType())) {
                            methodBeingOverriddenWithCovariantReturnType = existingMethod;
                            break;
                        }
                        knownSignature = true;
                        break;
                    }
                }
                if (methodBeingOverriddenWithCovariantReturnType != null) {
                    methods.remove(methodBeingOverriddenWithCovariantReturnType);
                }
                if (!knownSignature && !ReflectionUtils.isCglibRenamedMethod(method)) {
                    methods.add(method);
                }
            }
        });
        return methods.toArray(new Method[methods.size()]);
    }
    
    public static void doWithFields(final Class<?> clazz, final FieldCallback fc) throws IllegalArgumentException {
        doWithFields(clazz, fc, null);
    }
    
    public static void doWithFields(final Class<?> clazz, final FieldCallback fc, final FieldFilter ff) throws IllegalArgumentException {
        Class<?> targetClass = clazz;
        do {
            final Field[] declaredFields;
            final Field[] fields = declaredFields = targetClass.getDeclaredFields();
            for (final Field field : declaredFields) {
                if (ff == null || ff.matches(field)) {
                    try {
                        fc.doWith(field);
                    }
                    catch (IllegalAccessException ex) {
                        throw new IllegalStateException("Shouldn't be illegal to access field '" + field.getName() + "': " + ex);
                    }
                }
            }
            targetClass = targetClass.getSuperclass();
        } while (targetClass != null && targetClass != Object.class);
    }
    
    public static void shallowCopyFieldState(final Object src, final Object dest) throws IllegalArgumentException {
        if (src == null) {
            throw new IllegalArgumentException("Source for field copy cannot be null");
        }
        if (dest == null) {
            throw new IllegalArgumentException("Destination for field copy cannot be null");
        }
        if (!src.getClass().isAssignableFrom(dest.getClass())) {
            throw new IllegalArgumentException("Destination class [" + dest.getClass().getName() + "] must be same or subclass as source class [" + src.getClass().getName() + "]");
        }
        doWithFields(src.getClass(), new FieldCallback() {
            @Override
            public void doWith(final Field field) throws IllegalArgumentException, IllegalAccessException {
                ReflectionUtils.makeAccessible(field);
                final Object srcValue = field.get(src);
                field.set(dest, srcValue);
            }
        }, ReflectionUtils.COPYABLE_FIELDS);
    }
    
    static {
        CGLIB_RENAMED_METHOD_PATTERN = Pattern.compile("CGLIB\\$(.+)\\$\\d+");
        ReflectionUtils.COPYABLE_FIELDS = new FieldFilter() {
            @Override
            public boolean matches(final Field field) {
                return !Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers());
            }
        };
        ReflectionUtils.NON_BRIDGED_METHODS = new MethodFilter() {
            @Override
            public boolean matches(final Method method) {
                return !method.isBridge();
            }
        };
        ReflectionUtils.USER_DECLARED_METHODS = new MethodFilter() {
            @Override
            public boolean matches(final Method method) {
                return !method.isBridge() && method.getDeclaringClass() != Object.class;
            }
        };
    }
    
    public interface FieldFilter
    {
        boolean matches(final Field p0);
    }
    
    public interface FieldCallback
    {
        void doWith(final Field p0) throws IllegalArgumentException, IllegalAccessException;
    }
    
    public interface MethodFilter
    {
        boolean matches(final Method p0);
    }
    
    public interface MethodCallback
    {
        void doWith(final Method p0) throws IllegalArgumentException, IllegalAccessException;
    }
}
