// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.support;

import org.springframework.aop.MethodMatcher;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;

public abstract class StaticMethodMatcherPointcut extends StaticMethodMatcher implements Pointcut
{
    private ClassFilter classFilter;
    
    public StaticMethodMatcherPointcut() {
        this.classFilter = ClassFilter.TRUE;
    }
    
    public void setClassFilter(final ClassFilter classFilter) {
        this.classFilter = classFilter;
    }
    
    @Override
    public ClassFilter getClassFilter() {
        return this.classFilter;
    }
    
    @Override
    public final MethodMatcher getMethodMatcher() {
        return this;
    }
}
