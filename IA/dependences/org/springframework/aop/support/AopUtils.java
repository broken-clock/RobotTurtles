// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.support;

import org.springframework.aop.AopInvocationException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.Advisor;
import java.util.Iterator;
import java.util.Set;
import org.springframework.aop.MethodMatcher;
import java.util.Collection;
import java.util.HashSet;
import org.springframework.aop.IntroductionAwareMethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Method;
import org.springframework.aop.TargetClassAware;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import java.lang.reflect.Proxy;
import org.springframework.aop.SpringProxy;

public abstract class AopUtils
{
    public static boolean isAopProxy(final Object object) {
        return object instanceof SpringProxy && (Proxy.isProxyClass(object.getClass()) || ClassUtils.isCglibProxyClass(object.getClass()));
    }
    
    public static boolean isJdkDynamicProxy(final Object object) {
        return object instanceof SpringProxy && Proxy.isProxyClass(object.getClass());
    }
    
    public static boolean isCglibProxy(final Object object) {
        return object instanceof SpringProxy && ClassUtils.isCglibProxy(object);
    }
    
    @Deprecated
    public static boolean isCglibProxyClass(final Class<?> clazz) {
        return ClassUtils.isCglibProxyClass(clazz);
    }
    
    @Deprecated
    public static boolean isCglibProxyClassName(final String className) {
        return ClassUtils.isCglibProxyClassName(className);
    }
    
    public static Class<?> getTargetClass(final Object candidate) {
        Assert.notNull(candidate, "Candidate object must not be null");
        Class<?> result = null;
        if (candidate instanceof TargetClassAware) {
            result = ((TargetClassAware)candidate).getTargetClass();
        }
        if (result == null) {
            result = (isCglibProxy(candidate) ? candidate.getClass().getSuperclass() : candidate.getClass());
        }
        return result;
    }
    
    public static boolean isEqualsMethod(final Method method) {
        return ReflectionUtils.isEqualsMethod(method);
    }
    
    public static boolean isHashCodeMethod(final Method method) {
        return ReflectionUtils.isHashCodeMethod(method);
    }
    
    public static boolean isToStringMethod(final Method method) {
        return ReflectionUtils.isToStringMethod(method);
    }
    
    public static boolean isFinalizeMethod(final Method method) {
        return method != null && method.getName().equals("finalize") && method.getParameterTypes().length == 0;
    }
    
    public static Method getMostSpecificMethod(final Method method, final Class<?> targetClass) {
        final Method resolvedMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        return BridgeMethodResolver.findBridgedMethod(resolvedMethod);
    }
    
    public static boolean canApply(final Pointcut pc, final Class<?> targetClass) {
        return canApply(pc, targetClass, false);
    }
    
    public static boolean canApply(final Pointcut pc, final Class<?> targetClass, final boolean hasIntroductions) {
        Assert.notNull(pc, "Pointcut must not be null");
        if (!pc.getClassFilter().matches(targetClass)) {
            return false;
        }
        final MethodMatcher methodMatcher = pc.getMethodMatcher();
        IntroductionAwareMethodMatcher introductionAwareMethodMatcher = null;
        if (methodMatcher instanceof IntroductionAwareMethodMatcher) {
            introductionAwareMethodMatcher = (IntroductionAwareMethodMatcher)methodMatcher;
        }
        final Set<Class<?>> classes = new HashSet<Class<?>>(ClassUtils.getAllInterfacesForClassAsSet(targetClass));
        classes.add(targetClass);
        for (final Class<?> clazz : classes) {
            final Method[] methods2;
            final Method[] methods = methods2 = clazz.getMethods();
            for (final Method method : methods2) {
                if ((introductionAwareMethodMatcher != null && introductionAwareMethodMatcher.matches(method, targetClass, hasIntroductions)) || methodMatcher.matches(method, targetClass)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean canApply(final Advisor advisor, final Class<?> targetClass) {
        return canApply(advisor, targetClass, false);
    }
    
    public static boolean canApply(final Advisor advisor, final Class<?> targetClass, final boolean hasIntroductions) {
        if (advisor instanceof IntroductionAdvisor) {
            return ((IntroductionAdvisor)advisor).getClassFilter().matches(targetClass);
        }
        if (advisor instanceof PointcutAdvisor) {
            final PointcutAdvisor pca = (PointcutAdvisor)advisor;
            return canApply(pca.getPointcut(), targetClass, hasIntroductions);
        }
        return true;
    }
    
    public static List<Advisor> findAdvisorsThatCanApply(final List<Advisor> candidateAdvisors, final Class<?> clazz) {
        if (candidateAdvisors.isEmpty()) {
            return candidateAdvisors;
        }
        final List<Advisor> eligibleAdvisors = new LinkedList<Advisor>();
        for (final Advisor candidate : candidateAdvisors) {
            if (candidate instanceof IntroductionAdvisor && canApply(candidate, clazz)) {
                eligibleAdvisors.add(candidate);
            }
        }
        final boolean hasIntroductions = !eligibleAdvisors.isEmpty();
        for (final Advisor candidate2 : candidateAdvisors) {
            if (candidate2 instanceof IntroductionAdvisor) {
                continue;
            }
            if (!canApply(candidate2, clazz, hasIntroductions)) {
                continue;
            }
            eligibleAdvisors.add(candidate2);
        }
        return eligibleAdvisors;
    }
    
    public static Object invokeJoinpointUsingReflection(final Object target, final Method method, final Object[] args) throws Throwable {
        try {
            ReflectionUtils.makeAccessible(method);
            return method.invoke(target, args);
        }
        catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }
        catch (IllegalArgumentException ex2) {
            throw new AopInvocationException("AOP configuration seems to be invalid: tried calling method [" + method + "] on target [" + target + "]", ex2);
        }
        catch (IllegalAccessException ex3) {
            throw new AopInvocationException("Could not access method [" + method + "]", ex3);
        }
    }
}
