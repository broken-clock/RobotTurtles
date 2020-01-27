// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.framework;

import java.util.HashMap;
import org.springframework.aop.support.AopUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.aopalliance.intercept.MethodInterceptor;
import java.lang.reflect.AccessibleObject;
import org.springframework.core.BridgeMethodResolver;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Method;
import org.springframework.aop.ProxyMethodInvocation;

public class ReflectiveMethodInvocation implements ProxyMethodInvocation, Cloneable
{
    protected final Object proxy;
    protected final Object target;
    protected final Method method;
    protected Object[] arguments;
    private final Class<?> targetClass;
    private Map<String, Object> userAttributes;
    protected final List<?> interceptorsAndDynamicMethodMatchers;
    private int currentInterceptorIndex;
    
    protected ReflectiveMethodInvocation(final Object proxy, final Object target, final Method method, final Object[] arguments, final Class<?> targetClass, final List<Object> interceptorsAndDynamicMethodMatchers) {
        this.currentInterceptorIndex = -1;
        this.proxy = proxy;
        this.target = target;
        this.targetClass = targetClass;
        this.method = BridgeMethodResolver.findBridgedMethod(method);
        this.arguments = arguments;
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
    }
    
    @Override
    public final Object getProxy() {
        return this.proxy;
    }
    
    @Override
    public final Object getThis() {
        return this.target;
    }
    
    @Override
    public final AccessibleObject getStaticPart() {
        return this.method;
    }
    
    @Override
    public final Method getMethod() {
        return this.method;
    }
    
    @Override
    public final Object[] getArguments() {
        return (this.arguments != null) ? this.arguments : new Object[0];
    }
    
    @Override
    public void setArguments(final Object[] arguments) {
        this.arguments = arguments;
    }
    
    @Override
    public Object proceed() throws Throwable {
        if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
            return this.invokeJoinpoint();
        }
        final Object interceptorOrInterceptionAdvice = this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
        if (!(interceptorOrInterceptionAdvice instanceof InterceptorAndDynamicMethodMatcher)) {
            return ((MethodInterceptor)interceptorOrInterceptionAdvice).invoke(this);
        }
        final InterceptorAndDynamicMethodMatcher dm = (InterceptorAndDynamicMethodMatcher)interceptorOrInterceptionAdvice;
        if (dm.methodMatcher.matches(this.method, this.targetClass, this.arguments)) {
            return dm.interceptor.invoke(this);
        }
        return this.proceed();
    }
    
    protected Object invokeJoinpoint() throws Throwable {
        return AopUtils.invokeJoinpointUsingReflection(this.target, this.method, this.arguments);
    }
    
    @Override
    public MethodInvocation invocableClone() {
        Object[] cloneArguments = null;
        if (this.arguments != null) {
            cloneArguments = new Object[this.arguments.length];
            System.arraycopy(this.arguments, 0, cloneArguments, 0, this.arguments.length);
        }
        return this.invocableClone(cloneArguments);
    }
    
    @Override
    public MethodInvocation invocableClone(final Object[] arguments) {
        if (this.userAttributes == null) {
            this.userAttributes = new HashMap<String, Object>();
        }
        try {
            final ReflectiveMethodInvocation clone = (ReflectiveMethodInvocation)this.clone();
            clone.arguments = arguments;
            return clone;
        }
        catch (CloneNotSupportedException ex) {
            throw new IllegalStateException("Should be able to clone object of type [" + this.getClass() + "]: " + ex);
        }
    }
    
    @Override
    public void setUserAttribute(final String key, final Object value) {
        if (value != null) {
            if (this.userAttributes == null) {
                this.userAttributes = new HashMap<String, Object>();
            }
            this.userAttributes.put(key, value);
        }
        else if (this.userAttributes != null) {
            this.userAttributes.remove(key);
        }
    }
    
    @Override
    public Object getUserAttribute(final String key) {
        return (this.userAttributes != null) ? this.userAttributes.get(key) : null;
    }
    
    public Map<String, Object> getUserAttributes() {
        if (this.userAttributes == null) {
            this.userAttributes = new HashMap<String, Object>();
        }
        return this.userAttributes;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ReflectiveMethodInvocation: ");
        sb.append(this.method).append("; ");
        if (this.target == null) {
            sb.append("target is null");
        }
        else {
            sb.append("target is of class [").append(this.target.getClass().getName()).append(']');
        }
        return sb.toString();
    }
}
