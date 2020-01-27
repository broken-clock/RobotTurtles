// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.ParameterizedType;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.util.Assert;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.io.Serializable;
import org.springframework.beans.factory.ObjectFactory;
import java.util.Iterator;
import java.util.Set;
import org.springframework.util.ClassUtils;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.lang.reflect.Constructor;

abstract class AutowireUtils
{
    public static void sortConstructors(final Constructor<?>[] constructors) {
        Arrays.sort(constructors, new Comparator<Constructor<?>>() {
            @Override
            public int compare(final Constructor<?> c1, final Constructor<?> c2) {
                final boolean p1 = Modifier.isPublic(c1.getModifiers());
                final boolean p2 = Modifier.isPublic(c2.getModifiers());
                if (p1 != p2) {
                    return p1 ? -1 : 1;
                }
                final int c1pl = c1.getParameterTypes().length;
                final int c2pl = c2.getParameterTypes().length;
                return new Integer(c1pl).compareTo(c2pl) * -1;
            }
        });
    }
    
    public static void sortFactoryMethods(final Method[] factoryMethods) {
        Arrays.sort(factoryMethods, new Comparator<Method>() {
            @Override
            public int compare(final Method fm1, final Method fm2) {
                final boolean p1 = Modifier.isPublic(fm1.getModifiers());
                final boolean p2 = Modifier.isPublic(fm2.getModifiers());
                if (p1 != p2) {
                    return p1 ? -1 : 1;
                }
                final int c1pl = fm1.getParameterTypes().length;
                final int c2pl = fm2.getParameterTypes().length;
                return new Integer(c1pl).compareTo(c2pl) * -1;
            }
        });
    }
    
    public static boolean isExcludedFromDependencyCheck(final PropertyDescriptor pd) {
        final Method wm = pd.getWriteMethod();
        if (wm == null) {
            return false;
        }
        if (!wm.getDeclaringClass().getName().contains("$$")) {
            return false;
        }
        final Class<?> superclass = wm.getDeclaringClass().getSuperclass();
        return !ClassUtils.hasMethod(superclass, wm.getName(), wm.getParameterTypes());
    }
    
    public static boolean isSetterDefinedInInterface(final PropertyDescriptor pd, final Set<Class<?>> interfaces) {
        final Method setter = pd.getWriteMethod();
        if (setter != null) {
            final Class<?> targetClass = setter.getDeclaringClass();
            for (final Class<?> ifc : interfaces) {
                if (ifc.isAssignableFrom(targetClass) && ClassUtils.hasMethod(ifc, setter.getName(), setter.getParameterTypes())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static Object resolveAutowiringValue(Object autowiringValue, final Class<?> requiredType) {
        if (autowiringValue instanceof ObjectFactory && !requiredType.isInstance(autowiringValue)) {
            final ObjectFactory<?> factory = (ObjectFactory<?>)autowiringValue;
            if (!(autowiringValue instanceof Serializable) || !requiredType.isInterface()) {
                return factory.getObject();
            }
            autowiringValue = Proxy.newProxyInstance(requiredType.getClassLoader(), new Class[] { requiredType }, new ObjectFactoryDelegatingInvocationHandler(factory));
        }
        return autowiringValue;
    }
    
    public static Class<?> resolveReturnTypeForFactoryMethod(final Method method, final Object[] args, final ClassLoader classLoader) {
        Assert.notNull(method, "Method must not be null");
        Assert.notNull(args, "Argument array must not be null");
        Assert.notNull(classLoader, "ClassLoader must not be null");
        final TypeVariable<Method>[] declaredTypeVariables = method.getTypeParameters();
        final Type genericReturnType = method.getGenericReturnType();
        final Type[] methodParameterTypes = method.getGenericParameterTypes();
        Assert.isTrue(args.length == methodParameterTypes.length, "Argument array does not match parameter count");
        boolean locallyDeclaredTypeVariableMatchesReturnType = false;
        for (final TypeVariable<Method> currentTypeVariable : declaredTypeVariables) {
            if (currentTypeVariable.equals(genericReturnType)) {
                locallyDeclaredTypeVariableMatchesReturnType = true;
                break;
            }
        }
        if (locallyDeclaredTypeVariableMatchesReturnType) {
            int i = 0;
            while (i < methodParameterTypes.length) {
                final Type methodParameterType = methodParameterTypes[i];
                final Object arg = args[i];
                if (methodParameterType.equals(genericReturnType)) {
                    if (arg instanceof TypedStringValue) {
                        final TypedStringValue typedValue = (TypedStringValue)arg;
                        if (typedValue.hasTargetType()) {
                            return typedValue.getTargetType();
                        }
                        try {
                            return typedValue.resolveTargetType(classLoader);
                        }
                        catch (ClassNotFoundException ex) {
                            throw new IllegalStateException("Failed to resolve value type [" + typedValue.getTargetTypeName() + "] for factory method argument", ex);
                        }
                    }
                    if (arg != null && !(arg instanceof BeanMetadataElement)) {
                        return arg.getClass();
                    }
                    return method.getReturnType();
                }
                else {
                    if (methodParameterType instanceof ParameterizedType) {
                        final ParameterizedType parameterizedType = (ParameterizedType)methodParameterType;
                        final Type[] actualTypeArguments2;
                        final Type[] actualTypeArguments = actualTypeArguments2 = parameterizedType.getActualTypeArguments();
                        final int length2 = actualTypeArguments2.length;
                        int k = 0;
                        while (k < length2) {
                            final Type typeArg = actualTypeArguments2[k];
                            if (typeArg.equals(genericReturnType)) {
                                if (arg instanceof Class) {
                                    return (Class<?>)arg;
                                }
                                String className = null;
                                if (arg instanceof String) {
                                    className = (String)arg;
                                }
                                else if (arg instanceof TypedStringValue) {
                                    final TypedStringValue typedValue2 = (TypedStringValue)arg;
                                    final String targetTypeName = typedValue2.getTargetTypeName();
                                    if (targetTypeName == null || Class.class.getName().equals(targetTypeName)) {
                                        className = typedValue2.getValue();
                                    }
                                }
                                if (className != null) {
                                    try {
                                        return ClassUtils.forName(className, classLoader);
                                    }
                                    catch (ClassNotFoundException ex2) {
                                        throw new IllegalStateException("Could not resolve class name [" + arg + "] for factory method argument", ex2);
                                    }
                                }
                                return method.getReturnType();
                            }
                            else {
                                ++k;
                            }
                        }
                    }
                    ++i;
                }
            }
        }
        return method.getReturnType();
    }
    
    private static class ObjectFactoryDelegatingInvocationHandler implements InvocationHandler, Serializable
    {
        private final ObjectFactory<?> objectFactory;
        
        public ObjectFactoryDelegatingInvocationHandler(final ObjectFactory<?> objectFactory) {
            this.objectFactory = objectFactory;
        }
        
        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            final String methodName = method.getName();
            if (methodName.equals("equals")) {
                return proxy == args[0];
            }
            if (methodName.equals("hashCode")) {
                return System.identityHashCode(proxy);
            }
            if (methodName.equals("toString")) {
                return this.objectFactory.toString();
            }
            try {
                return method.invoke(this.objectFactory.getObject(), args);
            }
            catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }
    }
}
