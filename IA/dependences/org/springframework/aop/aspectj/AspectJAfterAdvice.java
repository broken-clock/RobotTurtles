// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.aspectj;

import org.aopalliance.intercept.MethodInvocation;
import java.lang.reflect.Method;
import org.springframework.aop.AfterAdvice;
import org.aopalliance.intercept.MethodInterceptor;

public class AspectJAfterAdvice extends AbstractAspectJAdvice implements MethodInterceptor, AfterAdvice
{
    public AspectJAfterAdvice(final Method aspectJBeforeAdviceMethod, final AspectJExpressionPointcut pointcut, final AspectInstanceFactory aif) {
        super(aspectJBeforeAdviceMethod, pointcut, aif);
    }
    
    @Override
    public Object invoke(final MethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        }
        finally {
            this.invokeAdviceMethod(this.getJoinPointMatch(), null, null);
        }
    }
    
    @Override
    public boolean isBeforeAdvice() {
        return false;
    }
    
    @Override
    public boolean isAfterAdvice() {
        return true;
    }
}
