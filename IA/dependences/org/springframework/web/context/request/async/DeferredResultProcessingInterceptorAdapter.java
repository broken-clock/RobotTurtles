// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.context.request.async;

import org.springframework.web.context.request.NativeWebRequest;

public abstract class DeferredResultProcessingInterceptorAdapter implements DeferredResultProcessingInterceptor
{
    @Override
    public <T> void beforeConcurrentHandling(final NativeWebRequest request, final DeferredResult<T> deferredResult) throws Exception {
    }
    
    @Override
    public <T> void preProcess(final NativeWebRequest request, final DeferredResult<T> deferredResult) throws Exception {
    }
    
    @Override
    public <T> void postProcess(final NativeWebRequest request, final DeferredResult<T> deferredResult, final Object concurrentResult) throws Exception {
    }
    
    @Override
    public <T> boolean handleTimeout(final NativeWebRequest request, final DeferredResult<T> deferredResult) throws Exception {
        return true;
    }
    
    @Override
    public <T> void afterCompletion(final NativeWebRequest request, final DeferredResult<T> deferredResult) throws Exception {
    }
}
