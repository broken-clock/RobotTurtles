// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.interceptor;

import org.springframework.core.task.AsyncTaskExecutor;
import java.lang.reflect.Method;
import org.springframework.util.ReflectionUtils;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ClassUtils;
import org.springframework.aop.support.AopUtils;
import org.aopalliance.intercept.MethodInvocation;
import java.util.concurrent.Executor;
import org.springframework.core.Ordered;
import org.aopalliance.intercept.MethodInterceptor;

public class AsyncExecutionInterceptor extends AsyncExecutionAspectSupport implements MethodInterceptor, Ordered
{
    public AsyncExecutionInterceptor(final Executor executor) {
        super(executor);
    }
    
    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        final Class<?> targetClass = (invocation.getThis() != null) ? AopUtils.getTargetClass(invocation.getThis()) : null;
        Method specificMethod = ClassUtils.getMostSpecificMethod(invocation.getMethod(), targetClass);
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
        final AsyncTaskExecutor executor = this.determineAsyncExecutor(specificMethod);
        if (executor == null) {
            throw new IllegalStateException("No executor specified and no default executor set on AsyncExecutionInterceptor either");
        }
        final Future<?> result = executor.submit((Callable<?>)new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                try {
                    final Object result = invocation.proceed();
                    if (result instanceof Future) {
                        return ((Future)result).get();
                    }
                }
                catch (Throwable ex) {
                    ReflectionUtils.rethrowException(ex);
                }
                return null;
            }
        });
        if (Future.class.isAssignableFrom(invocation.getMethod().getReturnType())) {
            return result;
        }
        return null;
    }
    
    @Override
    protected String getExecutorQualifier(final Method method) {
        return null;
    }
    
    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
