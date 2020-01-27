// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.aspectj;

import org.aspectj.weaver.tools.JoinPointMatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.JoinPoint;
import org.springframework.aop.ProxyMethodInvocation;
import org.aopalliance.intercept.MethodInvocation;
import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInterceptor;

public class AspectJAroundAdvice extends AbstractAspectJAdvice implements MethodInterceptor
{
    public AspectJAroundAdvice(final Method aspectJAroundAdviceMethod, final AspectJExpressionPointcut pointcut, final AspectInstanceFactory aif) {
        super(aspectJAroundAdviceMethod, pointcut, aif);
    }
    
    @Override
    public boolean isBeforeAdvice() {
        return false;
    }
    
    @Override
    public boolean isAfterAdvice() {
        return false;
    }
    
    @Override
    protected boolean supportsProceedingJoinPoint() {
        return true;
    }
    
    @Override
    public Object invoke(final MethodInvocation mi) throws Throwable {
        if (!(mi instanceof ProxyMethodInvocation)) {
            throw new IllegalStateException("MethodInvocation is not a Spring ProxyMethodInvocation: " + mi);
        }
        final ProxyMethodInvocation pmi = (ProxyMethodInvocation)mi;
        final ProceedingJoinPoint pjp = this.lazyGetProceedingJoinPoint(pmi);
        final JoinPointMatch jpm = this.getJoinPointMatch(pmi);
        return this.invokeAdviceMethod((JoinPoint)pjp, jpm, null, null);
    }
    
    protected ProceedingJoinPoint lazyGetProceedingJoinPoint(final ProxyMethodInvocation rmi) {
        return (ProceedingJoinPoint)new MethodInvocationProceedingJoinPoint(rmi);
    }
}
