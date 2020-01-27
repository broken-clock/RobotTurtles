// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.method;

import org.springframework.core.annotation.AnnotationUtils;
import java.lang.annotation.Annotation;
import org.springframework.util.ClassUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.Assert;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.MethodParameter;
import java.lang.reflect.Method;
import org.springframework.beans.factory.BeanFactory;
import org.apache.commons.logging.Log;

public class HandlerMethod
{
    protected final Log logger;
    private final Object bean;
    private final BeanFactory beanFactory;
    private final Method method;
    private final Method bridgedMethod;
    private final MethodParameter[] parameters;
    
    public HandlerMethod(final Object bean, final Method method) {
        this.logger = LogFactory.getLog(HandlerMethod.class);
        Assert.notNull(bean, "Bean is required");
        Assert.notNull(method, "Method is required");
        this.bean = bean;
        this.beanFactory = null;
        this.method = method;
        this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
        this.parameters = this.initMethodParameters();
    }
    
    public HandlerMethod(final Object bean, final String methodName, final Class<?>... parameterTypes) throws NoSuchMethodException {
        this.logger = LogFactory.getLog(HandlerMethod.class);
        Assert.notNull(bean, "Bean is required");
        Assert.notNull(methodName, "Method name is required");
        this.bean = bean;
        this.beanFactory = null;
        this.method = bean.getClass().getMethod(methodName, parameterTypes);
        this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(this.method);
        this.parameters = this.initMethodParameters();
    }
    
    public HandlerMethod(final String beanName, final BeanFactory beanFactory, final Method method) {
        this.logger = LogFactory.getLog(HandlerMethod.class);
        Assert.hasText(beanName, "Bean name is required");
        Assert.notNull(beanFactory, "BeanFactory is required");
        Assert.notNull(method, "Method is required");
        Assert.isTrue(beanFactory.containsBean(beanName), "BeanFactory [" + beanFactory + "] does not contain bean [" + beanName + "]");
        this.bean = beanName;
        this.beanFactory = beanFactory;
        this.method = method;
        this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
        this.parameters = this.initMethodParameters();
    }
    
    protected HandlerMethod(final HandlerMethod handlerMethod) {
        this.logger = LogFactory.getLog(HandlerMethod.class);
        Assert.notNull(handlerMethod, "HandlerMethod is required");
        this.bean = handlerMethod.bean;
        this.beanFactory = handlerMethod.beanFactory;
        this.method = handlerMethod.method;
        this.bridgedMethod = handlerMethod.bridgedMethod;
        this.parameters = handlerMethod.parameters;
    }
    
    private HandlerMethod(final HandlerMethod handlerMethod, final Object handler) {
        this.logger = LogFactory.getLog(HandlerMethod.class);
        Assert.notNull(handlerMethod, "HandlerMethod is required");
        Assert.notNull(handler, "Handler object is required");
        this.bean = handler;
        this.beanFactory = handlerMethod.beanFactory;
        this.method = handlerMethod.method;
        this.bridgedMethod = handlerMethod.bridgedMethod;
        this.parameters = handlerMethod.parameters;
    }
    
    private MethodParameter[] initMethodParameters() {
        final int count = this.bridgedMethod.getParameterTypes().length;
        final MethodParameter[] result = new MethodParameter[count];
        for (int i = 0; i < count; ++i) {
            result[i] = new HandlerMethodParameter(i);
        }
        return result;
    }
    
    public Object getBean() {
        return this.bean;
    }
    
    public Method getMethod() {
        return this.method;
    }
    
    public Class<?> getBeanType() {
        final Class<?> clazz = (this.bean instanceof String) ? this.beanFactory.getType((String)this.bean) : this.bean.getClass();
        return ClassUtils.getUserClass(clazz);
    }
    
    protected Method getBridgedMethod() {
        return this.bridgedMethod;
    }
    
    public MethodParameter[] getMethodParameters() {
        return this.parameters;
    }
    
    public MethodParameter getReturnType() {
        return new HandlerMethodParameter(-1);
    }
    
    public MethodParameter getReturnValueType(final Object returnValue) {
        return new ReturnValueMethodParameter(returnValue);
    }
    
    public boolean isVoid() {
        return Void.TYPE.equals(this.getReturnType().getParameterType());
    }
    
    public <A extends Annotation> A getMethodAnnotation(final Class<A> annotationType) {
        return AnnotationUtils.findAnnotation(this.method, annotationType);
    }
    
    public HandlerMethod createWithResolvedBean() {
        Object handler = this.bean;
        if (this.bean instanceof String) {
            final String beanName = (String)this.bean;
            handler = this.beanFactory.getBean(beanName);
        }
        return new HandlerMethod(this, handler);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj instanceof HandlerMethod) {
            final HandlerMethod other = (HandlerMethod)obj;
            return this.bean.equals(other.bean) && this.method.equals(other.method);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.bean.hashCode() * 31 + this.method.hashCode();
    }
    
    @Override
    public String toString() {
        return this.method.toGenericString();
    }
    
    private class HandlerMethodParameter extends MethodParameter
    {
        public HandlerMethodParameter(final int index) {
            super(HandlerMethod.this.bridgedMethod, index);
        }
        
        @Override
        public Class<?> getContainingClass() {
            return HandlerMethod.this.getBeanType();
        }
        
        @Override
        public <T extends Annotation> T getMethodAnnotation(final Class<T> annotationType) {
            return HandlerMethod.this.getMethodAnnotation(annotationType);
        }
    }
    
    private class ReturnValueMethodParameter extends HandlerMethodParameter
    {
        private final Object returnValue;
        
        public ReturnValueMethodParameter(final Object returnValue) {
            super(-1);
            this.returnValue = returnValue;
        }
        
        @Override
        public Class<?> getParameterType() {
            return (this.returnValue != null) ? this.returnValue.getClass() : super.getParameterType();
        }
    }
}
