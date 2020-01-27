// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.context.request.async;

import org.apache.commons.logging.LogFactory;
import java.util.Iterator;
import org.springframework.web.context.request.NativeWebRequest;
import java.util.List;
import org.apache.commons.logging.Log;

class DeferredResultInterceptorChain
{
    private static Log logger;
    private final List<DeferredResultProcessingInterceptor> interceptors;
    private int preProcessingIndex;
    
    public DeferredResultInterceptorChain(final List<DeferredResultProcessingInterceptor> interceptors) {
        this.preProcessingIndex = -1;
        this.interceptors = interceptors;
    }
    
    public void applyBeforeConcurrentHandling(final NativeWebRequest request, final DeferredResult<?> deferredResult) throws Exception {
        for (final DeferredResultProcessingInterceptor interceptor : this.interceptors) {
            interceptor.beforeConcurrentHandling(request, deferredResult);
        }
    }
    
    public void applyPreProcess(final NativeWebRequest request, final DeferredResult<?> deferredResult) throws Exception {
        for (final DeferredResultProcessingInterceptor interceptor : this.interceptors) {
            interceptor.preProcess(request, deferredResult);
            ++this.preProcessingIndex;
        }
    }
    
    public Object applyPostProcess(final NativeWebRequest request, final DeferredResult<?> deferredResult, final Object concurrentResult) {
        try {
            for (int i = this.preProcessingIndex; i >= 0; --i) {
                this.interceptors.get(i).postProcess(request, deferredResult, concurrentResult);
            }
        }
        catch (Throwable t) {
            return t;
        }
        return concurrentResult;
    }
    
    public void triggerAfterTimeout(final NativeWebRequest request, final DeferredResult<?> deferredResult) throws Exception {
        for (final DeferredResultProcessingInterceptor interceptor : this.interceptors) {
            if (deferredResult.isSetOrExpired()) {
                return;
            }
            if (!interceptor.handleTimeout(request, deferredResult)) {
                break;
            }
        }
    }
    
    public void triggerAfterCompletion(final NativeWebRequest request, final DeferredResult<?> deferredResult) {
        for (int i = this.preProcessingIndex; i >= 0; --i) {
            try {
                this.interceptors.get(i).afterCompletion(request, deferredResult);
            }
            catch (Throwable t) {
                DeferredResultInterceptorChain.logger.error("afterCompletion error", t);
            }
        }
    }
    
    static {
        DeferredResultInterceptorChain.logger = LogFactory.getLog(DeferredResultInterceptorChain.class);
    }
}
