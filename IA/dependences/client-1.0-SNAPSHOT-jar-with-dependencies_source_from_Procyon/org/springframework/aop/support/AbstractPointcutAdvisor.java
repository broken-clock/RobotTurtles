// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.support;

import org.springframework.util.ObjectUtils;
import org.aopalliance.aop.Advice;
import java.io.Serializable;
import org.springframework.core.Ordered;
import org.springframework.aop.PointcutAdvisor;

public abstract class AbstractPointcutAdvisor implements PointcutAdvisor, Ordered, Serializable
{
    private Integer order;
    
    public void setOrder(final int order) {
        this.order = order;
    }
    
    @Override
    public int getOrder() {
        if (this.order != null) {
            return this.order;
        }
        final Advice advice = this.getAdvice();
        if (advice instanceof Ordered) {
            return ((Ordered)advice).getOrder();
        }
        return Integer.MAX_VALUE;
    }
    
    @Override
    public boolean isPerInstance() {
        return true;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PointcutAdvisor)) {
            return false;
        }
        final PointcutAdvisor otherAdvisor = (PointcutAdvisor)other;
        return ObjectUtils.nullSafeEquals(this.getAdvice(), otherAdvisor.getAdvice()) && ObjectUtils.nullSafeEquals(this.getPointcut(), otherAdvisor.getPointcut());
    }
    
    @Override
    public int hashCode() {
        return PointcutAdvisor.class.hashCode();
    }
}
