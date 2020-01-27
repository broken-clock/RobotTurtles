// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.interceptor;

import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInvocation;
import java.io.Serializable;
import org.aopalliance.intercept.MethodInterceptor;

public class CacheInterceptor extends CacheAspectSupport implements MethodInterceptor, Serializable
{
    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        final Method method = invocation.getMethod();
        final Invoker aopAllianceInvoker = new Invoker() {
            @Override
            public Object invoke() {
                try {
                    return invocation.proceed();
                }
                catch (Throwable ex) {
                    throw new ThrowableWrapper(ex);
                }
            }
        };
        try {
            return this.execute(aopAllianceInvoker, invocation.getThis(), method, invocation.getArguments());
        }
        catch (ThrowableWrapper th) {
            throw th.original;
        }
    }
    
    private static class ThrowableWrapper extends RuntimeException
    {
        private final Throwable original;
        
        ThrowableWrapper(final Throwable original) {
            this.original = original;
        }
    }
}
