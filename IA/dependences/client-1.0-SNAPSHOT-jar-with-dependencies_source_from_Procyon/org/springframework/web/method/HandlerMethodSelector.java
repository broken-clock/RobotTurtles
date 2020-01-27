// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.method;

import java.util.Iterator;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ClassUtils;
import java.util.Collection;
import java.util.Arrays;
import java.lang.reflect.Proxy;
import java.util.LinkedHashSet;
import java.lang.reflect.Method;
import java.util.Set;
import org.springframework.util.ReflectionUtils;

public abstract class HandlerMethodSelector
{
    public static Set<Method> selectMethods(final Class<?> handlerType, final ReflectionUtils.MethodFilter handlerMethodFilter) {
        final Set<Method> handlerMethods = new LinkedHashSet<Method>();
        final Set<Class<?>> handlerTypes = new LinkedHashSet<Class<?>>();
        Class<?> specificHandlerType = null;
        if (!Proxy.isProxyClass(handlerType)) {
            handlerTypes.add(handlerType);
            specificHandlerType = handlerType;
        }
        handlerTypes.addAll(Arrays.asList(handlerType.getInterfaces()));
        for (final Class<?> currentHandlerType : handlerTypes) {
            final Class<?> targetClass = (specificHandlerType != null) ? specificHandlerType : currentHandlerType;
            ReflectionUtils.doWithMethods(currentHandlerType, new ReflectionUtils.MethodCallback() {
                @Override
                public void doWith(final Method method) {
                    final Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
                    final Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
                    if (handlerMethodFilter.matches(specificMethod) && (bridgedMethod == specificMethod || !handlerMethodFilter.matches(bridgedMethod))) {
                        handlerMethods.add(specificMethod);
                    }
                }
            }, ReflectionUtils.USER_DECLARED_METHODS);
        }
        return handlerMethods;
    }
}
