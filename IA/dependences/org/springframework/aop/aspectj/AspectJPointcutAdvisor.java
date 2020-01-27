// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.aspectj;

import org.springframework.util.ObjectUtils;
import org.aopalliance.aop.Advice;
import org.springframework.util.Assert;
import org.springframework.aop.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.aop.PointcutAdvisor;

public class AspectJPointcutAdvisor implements PointcutAdvisor, Ordered
{
    private final AbstractAspectJAdvice advice;
    private final Pointcut pointcut;
    private Integer order;
    
    public AspectJPointcutAdvisor(final AbstractAspectJAdvice advice) {
        Assert.notNull(advice, "Advice must not be null");
        this.advice = advice;
        this.pointcut = advice.buildSafePointcut();
    }
    
    public void setOrder(final int order) {
        this.order = order;
    }
    
    @Override
    public boolean isPerInstance() {
        return true;
    }
    
    @Override
    public Advice getAdvice() {
        return this.advice;
    }
    
    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }
    
    @Override
    public int getOrder() {
        if (this.order != null) {
            return this.order;
        }
        return this.advice.getOrder();
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AspectJPointcutAdvisor)) {
            return false;
        }
        final AspectJPointcutAdvisor otherAdvisor = (AspectJPointcutAdvisor)other;
        return ObjectUtils.nullSafeEquals(this.advice, otherAdvisor.advice);
    }
    
    @Override
    public int hashCode() {
        return AspectJPointcutAdvisor.class.hashCode();
    }
}
