// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.annotation;

import org.springframework.core.annotation.AnnotationUtils;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import org.springframework.aop.interceptor.AsyncExecutionInterceptor;

public class AnnotationAsyncExecutionInterceptor extends AsyncExecutionInterceptor
{
    public AnnotationAsyncExecutionInterceptor(final Executor defaultExecutor) {
        super(defaultExecutor);
    }
    
    @Override
    protected String getExecutorQualifier(final Method method) {
        Async async = AnnotationUtils.findAnnotation(method, Async.class);
        if (async == null) {
            async = AnnotationUtils.findAnnotation(method.getDeclaringClass(), Async.class);
        }
        return (async != null) ? async.value() : null;
    }
}
