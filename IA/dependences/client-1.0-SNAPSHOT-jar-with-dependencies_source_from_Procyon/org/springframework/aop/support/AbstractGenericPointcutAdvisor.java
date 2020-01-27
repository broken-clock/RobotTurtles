// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.support;

import org.aopalliance.aop.Advice;

public abstract class AbstractGenericPointcutAdvisor extends AbstractPointcutAdvisor
{
    private Advice advice;
    
    public void setAdvice(final Advice advice) {
        this.advice = advice;
    }
    
    @Override
    public Advice getAdvice() {
        return this.advice;
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + ": advice [" + this.getAdvice() + "]";
    }
}
