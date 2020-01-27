// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.context.request.async;

import java.util.concurrent.Callable;
import org.springframework.web.context.request.NativeWebRequest;

public abstract class CallableProcessingInterceptorAdapter implements CallableProcessingInterceptor
{
    @Override
    public <T> void beforeConcurrentHandling(final NativeWebRequest request, final Callable<T> task) throws Exception {
    }
    
    @Override
    public <T> void preProcess(final NativeWebRequest request, final Callable<T> task) throws Exception {
    }
    
    @Override
    public <T> void postProcess(final NativeWebRequest request, final Callable<T> task, final Object concurrentResult) throws Exception {
    }
    
    @Override
    public <T> Object handleTimeout(final NativeWebRequest request, final Callable<T> task) throws Exception {
        return CallableProcessingInterceptorAdapter.RESULT_NONE;
    }
    
    @Override
    public <T> void afterCompletion(final NativeWebRequest request, final Callable<T> task) throws Exception {
    }
}
