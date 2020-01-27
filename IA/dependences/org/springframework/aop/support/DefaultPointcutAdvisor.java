// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.support;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import java.io.Serializable;

public class DefaultPointcutAdvisor extends AbstractGenericPointcutAdvisor implements Serializable
{
    private Pointcut pointcut;
    
    public DefaultPointcutAdvisor() {
        this.pointcut = Pointcut.TRUE;
    }
    
    public DefaultPointcutAdvisor(final Advice advice) {
        this(Pointcut.TRUE, advice);
    }
    
    public DefaultPointcutAdvisor(final Pointcut pointcut, final Advice advice) {
        this.pointcut = Pointcut.TRUE;
        this.pointcut = pointcut;
        this.setAdvice(advice);
    }
    
    public void setPointcut(final Pointcut pointcut) {
        this.pointcut = ((pointcut != null) ? pointcut : Pointcut.TRUE);
    }
    
    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + ": pointcut [" + this.getPointcut() + "]; advice [" + this.getAdvice() + "]";
    }
}
