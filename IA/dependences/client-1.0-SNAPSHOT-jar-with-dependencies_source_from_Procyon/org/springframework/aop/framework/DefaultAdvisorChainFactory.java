// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.framework;

import org.aopalliance.intercept.Interceptor;
import org.springframework.aop.MethodMatcher;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.adapter.AdvisorAdapterRegistry;
import org.springframework.aop.IntroductionAdvisor;
import java.util.Collection;
import java.util.Arrays;
import org.springframework.aop.support.MethodMatchers;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Method;
import java.io.Serializable;

public class DefaultAdvisorChainFactory implements AdvisorChainFactory, Serializable
{
    @Override
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(final Advised config, final Method method, final Class<?> targetClass) {
        final List<Object> interceptorList = new ArrayList<Object>(config.getAdvisors().length);
        final boolean hasIntroductions = hasMatchingIntroductions(config, targetClass);
        final AdvisorAdapterRegistry registry = GlobalAdvisorAdapterRegistry.getInstance();
        for (final Advisor advisor : config.getAdvisors()) {
            if (advisor instanceof PointcutAdvisor) {
                final PointcutAdvisor pointcutAdvisor = (PointcutAdvisor)advisor;
                if (config.isPreFiltered() || pointcutAdvisor.getPointcut().getClassFilter().matches(targetClass)) {
                    final MethodInterceptor[] interceptors = registry.getInterceptors(advisor);
                    final MethodMatcher mm = pointcutAdvisor.getPointcut().getMethodMatcher();
                    if (MethodMatchers.matches(mm, method, targetClass, hasIntroductions)) {
                        if (mm.isRuntime()) {
                            for (final MethodInterceptor interceptor : interceptors) {
                                interceptorList.add(new InterceptorAndDynamicMethodMatcher(interceptor, mm));
                            }
                        }
                        else {
                            interceptorList.addAll(Arrays.asList(interceptors));
                        }
                    }
                }
            }
            else if (advisor instanceof IntroductionAdvisor) {
                final IntroductionAdvisor ia = (IntroductionAdvisor)advisor;
                if (config.isPreFiltered() || ia.getClassFilter().matches(targetClass)) {
                    final Interceptor[] interceptors2 = registry.getInterceptors(advisor);
                    interceptorList.addAll(Arrays.asList(interceptors2));
                }
            }
            else {
                final Interceptor[] interceptors3 = registry.getInterceptors(advisor);
                interceptorList.addAll(Arrays.asList(interceptors3));
            }
        }
        return interceptorList;
    }
    
    private static boolean hasMatchingIntroductions(final Advised config, final Class<?> targetClass) {
        for (int i = 0; i < config.getAdvisors().length; ++i) {
            final Advisor advisor = config.getAdvisors()[i];
            if (advisor instanceof IntroductionAdvisor) {
                final IntroductionAdvisor ia = (IntroductionAdvisor)advisor;
                if (ia.getClassFilter().matches(targetClass)) {
                    return true;
                }
            }
        }
        return false;
    }
}
