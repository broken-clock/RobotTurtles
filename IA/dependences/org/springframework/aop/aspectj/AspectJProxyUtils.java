// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.aspectj;

import org.springframework.aop.PointcutAdvisor;
import java.util.Iterator;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.aop.Advisor;
import java.util.List;

public abstract class AspectJProxyUtils
{
    public static boolean makeAdvisorChainAspectJCapableIfNecessary(final List<Advisor> advisors) {
        if (!advisors.isEmpty()) {
            boolean foundAspectJAdvice = false;
            for (final Advisor advisor : advisors) {
                if (isAspectJAdvice(advisor)) {
                    foundAspectJAdvice = true;
                }
            }
            if (foundAspectJAdvice && !advisors.contains(ExposeInvocationInterceptor.ADVISOR)) {
                advisors.add(0, ExposeInvocationInterceptor.ADVISOR);
                return true;
            }
        }
        return false;
    }
    
    private static boolean isAspectJAdvice(final Advisor advisor) {
        return advisor instanceof InstantiationModelAwarePointcutAdvisor || advisor.getAdvice() instanceof AbstractAspectJAdvice || (advisor instanceof PointcutAdvisor && ((PointcutAdvisor)advisor).getPointcut() instanceof AspectJExpressionPointcut);
    }
}
