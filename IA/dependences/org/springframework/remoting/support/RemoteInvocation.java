// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.support;

import org.springframework.util.ClassUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.aopalliance.intercept.MethodInvocation;
import java.util.Map;
import java.io.Serializable;

public class RemoteInvocation implements Serializable
{
    private static final long serialVersionUID = 6876024250231820554L;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] arguments;
    private Map<String, Serializable> attributes;
    
    public RemoteInvocation(final MethodInvocation methodInvocation) {
        this.methodName = methodInvocation.getMethod().getName();
        this.parameterTypes = methodInvocation.getMethod().getParameterTypes();
        this.arguments = methodInvocation.getArguments();
    }
    
    public RemoteInvocation(final String methodName, final Class<?>[] parameterTypes, final Object[] arguments) {
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.arguments = arguments;
    }
    
    public RemoteInvocation() {
    }
    
    public void setMethodName(final String methodName) {
        this.methodName = methodName;
    }
    
    public String getMethodName() {
        return this.methodName;
    }
    
    public void setParameterTypes(final Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }
    
    public Class<?>[] getParameterTypes() {
        return this.parameterTypes;
    }
    
    public void setArguments(final Object[] arguments) {
        this.arguments = arguments;
    }
    
    public Object[] getArguments() {
        return this.arguments;
    }
    
    public void addAttribute(final String key, final Serializable value) throws IllegalStateException {
        if (this.attributes == null) {
            this.attributes = new HashMap<String, Serializable>();
        }
        if (this.attributes.containsKey(key)) {
            throw new IllegalStateException("There is already an attribute with key '" + key + "' bound");
        }
        this.attributes.put(key, value);
    }
    
    public Serializable getAttribute(final String key) {
        if (this.attributes == null) {
            return null;
        }
        return this.attributes.get(key);
    }
    
    public void setAttributes(final Map<String, Serializable> attributes) {
        this.attributes = attributes;
    }
    
    public Map<String, Serializable> getAttributes() {
        return this.attributes;
    }
    
    public Object invoke(final Object targetObject) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final Method method = targetObject.getClass().getMethod(this.methodName, this.parameterTypes);
        return method.invoke(targetObject, this.arguments);
    }
    
    @Override
    public String toString() {
        return "RemoteInvocation: method name '" + this.methodName + "'; parameter types " + ClassUtils.classNamesToString(this.parameterTypes);
    }
}
