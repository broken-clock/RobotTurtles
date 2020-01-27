// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.aspectj;

import org.aopalliance.intercept.MethodInvocation;
import java.lang.reflect.Method;
import org.springframework.aop.AfterAdvice;
import org.aopalliance.intercept.MethodInterceptor;

public class AspectJAfterThrowingAdvice extends AbstractAspectJAdvice implements MethodInterceptor, AfterAdvice
{
    public AspectJAfterThrowingAdvice(final Method aspectJBeforeAdviceMethod, final AspectJExpressionPointcut pointcut, final AspectInstanceFactory aif) {
        super(aspectJBeforeAdviceMethod, pointcut, aif);
    }
    
    @Override
    public boolean isBeforeAdvice() {
        return false;
    }
    
    @Override
    public boolean isAfterAdvice() {
        return true;
    }
    
    @Override
    public void setThrowingName(final String name) {
        this.setThrowingNameNoCheck(name);
    }
    
    @Override
    public Object invoke(final MethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        }
        catch (Throwable t) {
            if (this.shouldInvokeOnThrowing(t)) {
                this.invokeAdviceMethod(this.getJoinPointMatch(), null, t);
            }
            throw t;
        }
    }
    
    private boolean shouldInvokeOnThrowing(final Throwable t) {
        return this.getDiscoveredThrowingType().isAssignableFrom(t.getClass());
    }
}
