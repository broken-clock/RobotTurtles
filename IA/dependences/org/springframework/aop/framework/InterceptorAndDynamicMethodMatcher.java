// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.framework;

import org.springframework.aop.MethodMatcher;
import org.aopalliance.intercept.MethodInterceptor;

class InterceptorAndDynamicMethodMatcher
{
    final MethodInterceptor interceptor;
    final MethodMatcher methodMatcher;
    
    public InterceptorAndDynamicMethodMatcher(final MethodInterceptor interceptor, final MethodMatcher methodMatcher) {
        this.interceptor = interceptor;
        this.methodMatcher = methodMatcher;
    }
}
