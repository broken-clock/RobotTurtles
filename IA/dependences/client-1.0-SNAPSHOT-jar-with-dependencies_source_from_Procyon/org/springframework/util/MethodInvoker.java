// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Method;

public class MethodInvoker
{
    private Class<?> targetClass;
    private Object targetObject;
    private String targetMethod;
    private String staticMethod;
    private Object[] arguments;
    private Method methodObject;
    
    public MethodInvoker() {
        this.arguments = new Object[0];
    }
    
    public void setTargetClass(final Class<?> targetClass) {
        this.targetClass = targetClass;
    }
    
    public Class<?> getTargetClass() {
        return this.targetClass;
    }
    
    public void setTargetObject(final Object targetObject) {
        this.targetObject = targetObject;
        if (targetObject != null) {
            this.targetClass = targetObject.getClass();
        }
    }
    
    public Object getTargetObject() {
        return this.targetObject;
    }
    
    public void setTargetMethod(final String targetMethod) {
        this.targetMethod = targetMethod;
    }
    
    public String getTargetMethod() {
        return this.targetMethod;
    }
    
    public void setStaticMethod(final String staticMethod) {
        this.staticMethod = staticMethod;
    }
    
    public void setArguments(final Object[] arguments) {
        this.arguments = ((arguments != null) ? arguments : new Object[0]);
    }
    
    public Object[] getArguments() {
        return this.arguments;
    }
    
    public void prepare() throws ClassNotFoundException, NoSuchMethodException {
        if (this.staticMethod != null) {
            final int lastDotIndex = this.staticMethod.lastIndexOf(46);
            if (lastDotIndex == -1 || lastDotIndex == this.staticMethod.length()) {
                throw new IllegalArgumentException("staticMethod must be a fully qualified class plus method name: e.g. 'example.MyExampleClass.myExampleMethod'");
            }
            final String className = this.staticMethod.substring(0, lastDotIndex);
            final String methodName = this.staticMethod.substring(lastDotIndex + 1);
            this.targetClass = this.resolveClassName(className);
            this.targetMethod = methodName;
        }
        final Class<?> targetClass = this.getTargetClass();
        final String targetMethod = this.getTargetMethod();
        if (targetClass == null) {
            throw new IllegalArgumentException("Either 'targetClass' or 'targetObject' is required");
        }
        if (targetMethod == null) {
            throw new IllegalArgumentException("Property 'targetMethod' is required");
        }
        final Object[] arguments = this.getArguments();
        final Class<?>[] argTypes = (Class<?>[])new Class[arguments.length];
        for (int i = 0; i < arguments.length; ++i) {
            argTypes[i] = ((arguments[i] != null) ? arguments[i].getClass() : Object.class);
        }
        try {
            this.methodObject = targetClass.getMethod(targetMethod, argTypes);
        }
        catch (NoSuchMethodException ex) {
            this.methodObject = this.findMatchingMethod();
            if (this.methodObject == null) {
                throw ex;
            }
        }
    }
    
    protected Class<?> resolveClassName(final String className) throws ClassNotFoundException {
        return ClassUtils.forName(className, ClassUtils.getDefaultClassLoader());
    }
    
    protected Method findMatchingMethod() {
        final String targetMethod = this.getTargetMethod();
        final Object[] arguments = this.getArguments();
        final int argCount = arguments.length;
        final Method[] candidates = ReflectionUtils.getAllDeclaredMethods(this.getTargetClass());
        int minTypeDiffWeight = Integer.MAX_VALUE;
        Method matchingMethod = null;
        for (final Method candidate : candidates) {
            if (candidate.getName().equals(targetMethod)) {
                final Class<?>[] paramTypes = candidate.getParameterTypes();
                if (paramTypes.length == argCount) {
                    final int typeDiffWeight = getTypeDifferenceWeight(paramTypes, arguments);
                    if (typeDiffWeight < minTypeDiffWeight) {
                        minTypeDiffWeight = typeDiffWeight;
                        matchingMethod = candidate;
                    }
                }
            }
        }
        return matchingMethod;
    }
    
    public Method getPreparedMethod() throws IllegalStateException {
        if (this.methodObject == null) {
            throw new IllegalStateException("prepare() must be called prior to invoke() on MethodInvoker");
        }
        return this.methodObject;
    }
    
    public boolean isPrepared() {
        return this.methodObject != null;
    }
    
    public Object invoke() throws InvocationTargetException, IllegalAccessException {
        final Object targetObject = this.getTargetObject();
        final Method preparedMethod = this.getPreparedMethod();
        if (targetObject == null && !Modifier.isStatic(preparedMethod.getModifiers())) {
            throw new IllegalArgumentException("Target method must not be non-static without a target");
        }
        ReflectionUtils.makeAccessible(preparedMethod);
        return preparedMethod.invoke(targetObject, this.getArguments());
    }
    
    public static int getTypeDifferenceWeight(final Class<?>[] paramTypes, final Object[] args) {
        int result = 0;
        for (int i = 0; i < paramTypes.length; ++i) {
            if (!ClassUtils.isAssignableValue(paramTypes[i], args[i])) {
                return Integer.MAX_VALUE;
            }
            if (args[i] != null) {
                final Class<?> paramType = paramTypes[i];
                Class<?> superClass = args[i].getClass().getSuperclass();
                while (superClass != null) {
                    if (paramType.equals(superClass)) {
                        result += 2;
                        superClass = null;
                    }
                    else if (ClassUtils.isAssignable(paramType, superClass)) {
                        result += 2;
                        superClass = superClass.getSuperclass();
                    }
                    else {
                        superClass = null;
                    }
                }
                if (paramType.isInterface()) {
                    ++result;
                }
            }
        }
        return result;
    }
}
