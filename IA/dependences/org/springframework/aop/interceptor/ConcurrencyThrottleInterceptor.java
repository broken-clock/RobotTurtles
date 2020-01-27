// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.interceptor;

import org.aopalliance.intercept.MethodInvocation;
import java.io.Serializable;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.util.ConcurrencyThrottleSupport;

public class ConcurrencyThrottleInterceptor extends ConcurrencyThrottleSupport implements MethodInterceptor, Serializable
{
    public ConcurrencyThrottleInterceptor() {
        this.setConcurrencyLimit(1);
    }
    
    @Override
    public Object invoke(final MethodInvocation methodInvocation) throws Throwable {
        this.beforeAccess();
        try {
            return methodInvocation.proceed();
        }
        finally {
            this.afterAccess();
        }
    }
}
