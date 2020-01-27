// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.aspectj;

import java.lang.reflect.Method;
import org.springframework.aop.MethodBeforeAdvice;

public class AspectJMethodBeforeAdvice extends AbstractAspectJAdvice implements MethodBeforeAdvice
{
    public AspectJMethodBeforeAdvice(final Method aspectJBeforeAdviceMethod, final AspectJExpressionPointcut pointcut, final AspectInstanceFactory aif) {
        super(aspectJBeforeAdviceMethod, pointcut, aif);
    }
    
    @Override
    public void before(final Method method, final Object[] args, final Object target) throws Throwable {
        this.invokeAdviceMethod(this.getJoinPointMatch(), null, null);
    }
    
    @Override
    public boolean isBeforeAdvice() {
        return true;
    }
    
    @Override
    public boolean isAfterAdvice() {
        return false;
    }
}
