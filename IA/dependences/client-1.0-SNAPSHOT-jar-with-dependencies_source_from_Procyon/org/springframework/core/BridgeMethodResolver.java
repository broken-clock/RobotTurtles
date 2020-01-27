// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

import java.lang.reflect.Type;
import org.springframework.util.ClassUtils;
import java.util.Iterator;
import java.util.Arrays;
import java.util.List;
import org.springframework.util.ReflectionUtils;
import java.util.ArrayList;
import java.lang.reflect.Method;

public abstract class BridgeMethodResolver
{
    public static Method findBridgedMethod(final Method bridgeMethod) {
        if (bridgeMethod == null || !bridgeMethod.isBridge()) {
            return bridgeMethod;
        }
        final List<Method> candidateMethods = new ArrayList<Method>();
        final Method[] allDeclaredMethods;
        final Method[] methods = allDeclaredMethods = ReflectionUtils.getAllDeclaredMethods(bridgeMethod.getDeclaringClass());
        for (final Method candidateMethod : allDeclaredMethods) {
            if (isBridgedCandidateFor(candidateMethod, bridgeMethod)) {
                candidateMethods.add(candidateMethod);
            }
        }
        if (candidateMethods.size() == 1) {
            return candidateMethods.get(0);
        }
        final Method bridgedMethod = searchCandidates(candidateMethods, bridgeMethod);
        if (bridgedMethod != null) {
            return bridgedMethod;
        }
        return bridgeMethod;
    }
    
    private static boolean isBridgedCandidateFor(final Method candidateMethod, final Method bridgeMethod) {
        return !candidateMethod.isBridge() && !candidateMethod.equals(bridgeMethod) && candidateMethod.getName().equals(bridgeMethod.getName()) && candidateMethod.getParameterTypes().length == bridgeMethod.getParameterTypes().length;
    }
    
    private static Method searchCandidates(final List<Method> candidateMethods, final Method bridgeMethod) {
        if (candidateMethods.isEmpty()) {
            return null;
        }
        Method previousMethod = null;
        boolean sameSig = true;
        for (final Method candidateMethod : candidateMethods) {
            if (isBridgeMethodFor(bridgeMethod, candidateMethod, bridgeMethod.getDeclaringClass())) {
                return candidateMethod;
            }
            if (previousMethod != null) {
                sameSig = (sameSig && Arrays.equals(candidateMethod.getGenericParameterTypes(), previousMethod.getGenericParameterTypes()));
            }
            previousMethod = candidateMethod;
        }
        return sameSig ? candidateMethods.get(0) : null;
    }
    
    static boolean isBridgeMethodFor(final Method bridgeMethod, final Method candidateMethod, final Class<?> declaringClass) {
        if (isResolvedTypeMatch(candidateMethod, bridgeMethod, declaringClass)) {
            return true;
        }
        final Method method = findGenericDeclaration(bridgeMethod);
        return method != null && isResolvedTypeMatch(method, candidateMethod, declaringClass);
    }
    
    private static Method findGenericDeclaration(final Method bridgeMethod) {
        for (Class<?> superclass = bridgeMethod.getDeclaringClass().getSuperclass(); superclass != null && !Object.class.equals(superclass); superclass = superclass.getSuperclass()) {
            final Method method = searchForMatch(superclass, bridgeMethod);
            if (method != null && !method.isBridge()) {
                return method;
            }
        }
        final Class<?>[] allInterfacesForClass;
        final Class<?>[] interfaces = allInterfacesForClass = ClassUtils.getAllInterfacesForClass(bridgeMethod.getDeclaringClass());
        for (final Class<?> ifc : allInterfacesForClass) {
            final Method method2 = searchForMatch(ifc, bridgeMethod);
            if (method2 != null && !method2.isBridge()) {
                return method2;
            }
        }
        return null;
    }
    
    private static boolean isResolvedTypeMatch(final Method genericMethod, final Method candidateMethod, final Class<?> declaringClass) {
        final Type[] genericParameters = genericMethod.getGenericParameterTypes();
        final Class<?>[] candidateParameters = candidateMethod.getParameterTypes();
        if (genericParameters.length != candidateParameters.length) {
            return false;
        }
        for (int i = 0; i < candidateParameters.length; ++i) {
            final ResolvableType genericParameter = ResolvableType.forMethodParameter(genericMethod, i, declaringClass);
            final Class<?> candidateParameter = candidateParameters[i];
            if (candidateParameter.isArray() && !candidateParameter.getComponentType().equals(genericParameter.getComponentType().resolve(Object.class))) {
                return false;
            }
            if (!candidateParameter.equals(genericParameter.resolve(Object.class))) {
                return false;
            }
        }
        return true;
    }
    
    private static Method searchForMatch(final Class<?> type, final Method bridgeMethod) {
        return ReflectionUtils.findMethod(type, bridgeMethod.getName(), bridgeMethod.getParameterTypes());
    }
    
    public static boolean isVisibilityBridgeMethodPair(final Method bridgeMethod, final Method bridgedMethod) {
        return bridgeMethod == bridgedMethod || (Arrays.equals(bridgeMethod.getParameterTypes(), bridgedMethod.getParameterTypes()) && bridgeMethod.getReturnType().equals(bridgedMethod.getReturnType()));
    }
}
