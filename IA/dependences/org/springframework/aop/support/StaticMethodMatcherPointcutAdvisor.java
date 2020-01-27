// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.support;

import org.springframework.aop.Pointcut;
import org.springframework.util.Assert;
import org.aopalliance.aop.Advice;
import java.io.Serializable;
import org.springframework.core.Ordered;
import org.springframework.aop.PointcutAdvisor;

public abstract class StaticMethodMatcherPointcutAdvisor extends StaticMethodMatcherPointcut implements PointcutAdvisor, Ordered, Serializable
{
    private int order;
    private Advice advice;
    
    public StaticMethodMatcherPointcutAdvisor() {
        this.order = Integer.MAX_VALUE;
    }
    
    public StaticMethodMatcherPointcutAdvisor(final Advice advice) {
        this.order = Integer.MAX_VALUE;
        Assert.notNull(advice, "Advice must not be null");
        this.advice = advice;
    }
    
    public void setOrder(final int order) {
        this.order = order;
    }
    
    @Override
    public int getOrder() {
        return this.order;
    }
    
    public void setAdvice(final Advice advice) {
        this.advice = advice;
    }
    
    @Override
    public Advice getAdvice() {
        return this.advice;
    }
    
    @Override
    public boolean isPerInstance() {
        return true;
    }
    
    @Override
    public Pointcut getPointcut() {
        return this;
    }
}
