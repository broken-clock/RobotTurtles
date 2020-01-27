// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.context.request.async;

import org.apache.commons.logging.LogFactory;
import java.util.Iterator;
import java.util.concurrent.Callable;
import org.springframework.web.context.request.NativeWebRequest;
import java.util.List;
import org.apache.commons.logging.Log;

class CallableInterceptorChain
{
    private static Log logger;
    private final List<CallableProcessingInterceptor> interceptors;
    private int preProcessIndex;
    
    public CallableInterceptorChain(final List<CallableProcessingInterceptor> interceptors) {
        this.preProcessIndex = -1;
        this.interceptors = interceptors;
    }
    
    public void applyBeforeConcurrentHandling(final NativeWebRequest request, final Callable<?> task) throws Exception {
        for (final CallableProcessingInterceptor interceptor : this.interceptors) {
            interceptor.beforeConcurrentHandling(request, task);
        }
    }
    
    public void applyPreProcess(final NativeWebRequest request, final Callable<?> task) throws Exception {
        for (final CallableProcessingInterceptor interceptor : this.interceptors) {
            interceptor.preProcess(request, task);
            ++this.preProcessIndex;
        }
    }
    
    public Object applyPostProcess(final NativeWebRequest request, final Callable<?> task, final Object concurrentResult) {
        Throwable exceptionResult = null;
        for (int i = this.preProcessIndex; i >= 0; --i) {
            try {
                this.interceptors.get(i).postProcess(request, task, concurrentResult);
            }
            catch (Throwable t) {
                if (exceptionResult != null) {
                    CallableInterceptorChain.logger.error("postProcess error", t);
                }
                else {
                    exceptionResult = t;
                }
            }
        }
        return (exceptionResult != null) ? exceptionResult : concurrentResult;
    }
    
    public Object triggerAfterTimeout(final NativeWebRequest request, final Callable<?> task) {
        for (final CallableProcessingInterceptor interceptor : this.interceptors) {
            try {
                final Object result = interceptor.handleTimeout(request, task);
                if (result == CallableProcessingInterceptor.RESPONSE_HANDLED) {
                    break;
                }
                if (result != CallableProcessingInterceptor.RESULT_NONE) {
                    return result;
                }
                continue;
            }
            catch (Throwable t) {
                return t;
            }
        }
        return CallableProcessingInterceptor.RESULT_NONE;
    }
    
    public void triggerAfterCompletion(final NativeWebRequest request, final Callable<?> task) {
        for (int i = this.interceptors.size() - 1; i >= 0; --i) {
            try {
                this.interceptors.get(i).afterCompletion(request, task);
            }
            catch (Throwable t) {
                CallableInterceptorChain.logger.error("afterCompletion error", t);
            }
        }
    }
    
    static {
        CallableInterceptorChain.logger = LogFactory.getLog(CallableInterceptorChain.class);
    }
}
