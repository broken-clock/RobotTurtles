// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.support;

import org.springframework.util.ObjectUtils;
import org.springframework.util.Assert;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.ClassFilter;
import java.io.Serializable;
import org.springframework.aop.Pointcut;

public class ComposablePointcut implements Pointcut, Serializable
{
    private static final long serialVersionUID = -2743223737633663832L;
    private ClassFilter classFilter;
    private MethodMatcher methodMatcher;
    
    public ComposablePointcut() {
        this.classFilter = ClassFilter.TRUE;
        this.methodMatcher = MethodMatcher.TRUE;
    }
    
    public ComposablePointcut(final Pointcut pointcut) {
        Assert.notNull(pointcut, "Pointcut must not be null");
        this.classFilter = pointcut.getClassFilter();
        this.methodMatcher = pointcut.getMethodMatcher();
    }
    
    public ComposablePointcut(final ClassFilter classFilter) {
        Assert.notNull(classFilter, "ClassFilter must not be null");
        this.classFilter = classFilter;
        this.methodMatcher = MethodMatcher.TRUE;
    }
    
    public ComposablePointcut(final MethodMatcher methodMatcher) {
        Assert.notNull(methodMatcher, "MethodMatcher must not be null");
        this.classFilter = ClassFilter.TRUE;
        this.methodMatcher = methodMatcher;
    }
    
    public ComposablePointcut(final ClassFilter classFilter, final MethodMatcher methodMatcher) {
        Assert.notNull(classFilter, "ClassFilter must not be null");
        Assert.notNull(methodMatcher, "MethodMatcher must not be null");
        this.classFilter = classFilter;
        this.methodMatcher = methodMatcher;
    }
    
    public ComposablePointcut union(final ClassFilter other) {
        this.classFilter = ClassFilters.union(this.classFilter, other);
        return this;
    }
    
    public ComposablePointcut intersection(final ClassFilter other) {
        this.classFilter = ClassFilters.intersection(this.classFilter, other);
        return this;
    }
    
    public ComposablePointcut union(final MethodMatcher other) {
        this.methodMatcher = MethodMatchers.union(this.methodMatcher, other);
        return this;
    }
    
    public ComposablePointcut intersection(final MethodMatcher other) {
        this.methodMatcher = MethodMatchers.intersection(this.methodMatcher, other);
        return this;
    }
    
    public ComposablePointcut union(final Pointcut other) {
        this.methodMatcher = MethodMatchers.union(this.methodMatcher, this.classFilter, other.getMethodMatcher(), other.getClassFilter());
        this.classFilter = ClassFilters.union(this.classFilter, other.getClassFilter());
        return this;
    }
    
    public ComposablePointcut intersection(final Pointcut other) {
        this.classFilter = ClassFilters.intersection(this.classFilter, other.getClassFilter());
        this.methodMatcher = MethodMatchers.intersection(this.methodMatcher, other.getMethodMatcher());
        return this;
    }
    
    @Override
    public ClassFilter getClassFilter() {
        return this.classFilter;
    }
    
    @Override
    public MethodMatcher getMethodMatcher() {
        return this.methodMatcher;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ComposablePointcut)) {
            return false;
        }
        final ComposablePointcut that = (ComposablePointcut)other;
        return ObjectUtils.nullSafeEquals(that.classFilter, this.classFilter) && ObjectUtils.nullSafeEquals(that.methodMatcher, this.methodMatcher);
    }
    
    @Override
    public int hashCode() {
        int code = 17;
        if (this.classFilter != null) {
            code = 37 * code + this.classFilter.hashCode();
        }
        if (this.methodMatcher != null) {
            code = 37 * code + this.methodMatcher.hashCode();
        }
        return code;
    }
    
    @Override
    public String toString() {
        return "ComposablePointcut: " + this.classFilter + ", " + this.methodMatcher;
    }
}
