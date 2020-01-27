// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.support;

import org.springframework.util.ObjectUtils;
import org.springframework.core.ControlFlow;
import org.springframework.core.ControlFlowFactory;
import java.lang.reflect.Method;
import org.springframework.util.Assert;
import java.io.Serializable;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;

public class ControlFlowPointcut implements Pointcut, ClassFilter, MethodMatcher, Serializable
{
    private Class<?> clazz;
    private String methodName;
    private int evaluations;
    
    public ControlFlowPointcut(final Class<?> clazz) {
        this(clazz, null);
    }
    
    public ControlFlowPointcut(final Class<?> clazz, final String methodName) {
        Assert.notNull(clazz, "Class must not be null");
        this.clazz = clazz;
        this.methodName = methodName;
    }
    
    @Override
    public boolean matches(final Class<?> clazz) {
        return true;
    }
    
    @Override
    public boolean matches(final Method method, final Class<?> targetClass) {
        return true;
    }
    
    @Override
    public boolean isRuntime() {
        return true;
    }
    
    @Override
    public boolean matches(final Method method, final Class<?> targetClass, final Object[] args) {
        ++this.evaluations;
        final ControlFlow cflow = ControlFlowFactory.createControlFlow();
        return (this.methodName != null) ? cflow.under(this.clazz, this.methodName) : cflow.under(this.clazz);
    }
    
    public int getEvaluations() {
        return this.evaluations;
    }
    
    @Override
    public ClassFilter getClassFilter() {
        return this;
    }
    
    @Override
    public MethodMatcher getMethodMatcher() {
        return this;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ControlFlowPointcut)) {
            return false;
        }
        final ControlFlowPointcut that = (ControlFlowPointcut)other;
        return this.clazz.equals(that.clazz) && ObjectUtils.nullSafeEquals(that.methodName, this.methodName);
    }
    
    @Override
    public int hashCode() {
        int code = 17;
        code = 37 * code + this.clazz.hashCode();
        if (this.methodName != null) {
            code = 37 * code + this.methodName.hashCode();
        }
        return code;
    }
}
