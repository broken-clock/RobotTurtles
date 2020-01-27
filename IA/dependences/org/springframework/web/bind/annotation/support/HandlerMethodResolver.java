// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.bind.annotation.support;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.InitBinder;
import java.util.Iterator;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import java.util.Collection;
import java.util.Arrays;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import org.springframework.web.bind.annotation.RequestMapping;
import java.lang.reflect.Method;
import java.util.Set;

public class HandlerMethodResolver
{
    private final Set<Method> handlerMethods;
    private final Set<Method> initBinderMethods;
    private final Set<Method> modelAttributeMethods;
    private RequestMapping typeLevelMapping;
    private boolean sessionAttributesFound;
    private final Set<String> sessionAttributeNames;
    private final Set<Class<?>> sessionAttributeTypes;
    private final Set<String> actualSessionAttributeNames;
    
    public HandlerMethodResolver() {
        this.handlerMethods = new LinkedHashSet<Method>();
        this.initBinderMethods = new LinkedHashSet<Method>();
        this.modelAttributeMethods = new LinkedHashSet<Method>();
        this.sessionAttributeNames = new HashSet<String>();
        this.sessionAttributeTypes = new HashSet<Class<?>>();
        this.actualSessionAttributeNames = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>(4));
    }
    
    public void init(final Class<?> handlerType) {
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
                    if (HandlerMethodResolver.this.isHandlerMethod(specificMethod) && (bridgedMethod == specificMethod || !HandlerMethodResolver.this.isHandlerMethod(bridgedMethod))) {
                        HandlerMethodResolver.this.handlerMethods.add(specificMethod);
                    }
                    else if (HandlerMethodResolver.this.isInitBinderMethod(specificMethod) && (bridgedMethod == specificMethod || !HandlerMethodResolver.this.isInitBinderMethod(bridgedMethod))) {
                        HandlerMethodResolver.this.initBinderMethods.add(specificMethod);
                    }
                    else if (HandlerMethodResolver.this.isModelAttributeMethod(specificMethod) && (bridgedMethod == specificMethod || !HandlerMethodResolver.this.isModelAttributeMethod(bridgedMethod))) {
                        HandlerMethodResolver.this.modelAttributeMethods.add(specificMethod);
                    }
                }
            }, ReflectionUtils.USER_DECLARED_METHODS);
        }
        this.typeLevelMapping = AnnotationUtils.findAnnotation(handlerType, RequestMapping.class);
        final SessionAttributes sessionAttributes = AnnotationUtils.findAnnotation(handlerType, SessionAttributes.class);
        this.sessionAttributesFound = (sessionAttributes != null);
        if (this.sessionAttributesFound) {
            this.sessionAttributeNames.addAll(Arrays.asList(sessionAttributes.value()));
            this.sessionAttributeTypes.addAll(Arrays.asList(sessionAttributes.types()));
        }
    }
    
    protected boolean isHandlerMethod(final Method method) {
        return AnnotationUtils.findAnnotation(method, RequestMapping.class) != null;
    }
    
    protected boolean isInitBinderMethod(final Method method) {
        return AnnotationUtils.findAnnotation(method, InitBinder.class) != null;
    }
    
    protected boolean isModelAttributeMethod(final Method method) {
        return AnnotationUtils.findAnnotation(method, ModelAttribute.class) != null;
    }
    
    public final boolean hasHandlerMethods() {
        return !this.handlerMethods.isEmpty();
    }
    
    public final Set<Method> getHandlerMethods() {
        return this.handlerMethods;
    }
    
    public final Set<Method> getInitBinderMethods() {
        return this.initBinderMethods;
    }
    
    public final Set<Method> getModelAttributeMethods() {
        return this.modelAttributeMethods;
    }
    
    public boolean hasTypeLevelMapping() {
        return this.typeLevelMapping != null;
    }
    
    public RequestMapping getTypeLevelMapping() {
        return this.typeLevelMapping;
    }
    
    public boolean hasSessionAttributes() {
        return this.sessionAttributesFound;
    }
    
    public boolean isSessionAttribute(final String attrName, final Class<?> attrType) {
        if (this.sessionAttributeNames.contains(attrName) || this.sessionAttributeTypes.contains(attrType)) {
            this.actualSessionAttributeNames.add(attrName);
            return true;
        }
        return false;
    }
    
    public Set<String> getActualSessionAttributeNames() {
        return this.actualSessionAttributeNames;
    }
}
