// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.context.request.async;

import org.apache.commons.logging.LogFactory;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.web.context.request.NativeWebRequest;
import java.util.Collection;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import org.springframework.util.Assert;
import java.util.LinkedHashMap;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import java.util.Map;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.util.UrlPathHelper;
import org.apache.commons.logging.Log;

public final class WebAsyncManager
{
    private static final Object RESULT_NONE;
    private static final Log logger;
    private static final UrlPathHelper urlPathHelper;
    private static final CallableProcessingInterceptor timeoutCallableInterceptor;
    private static final DeferredResultProcessingInterceptor timeoutDeferredResultInterceptor;
    private AsyncWebRequest asyncWebRequest;
    private AsyncTaskExecutor taskExecutor;
    private Object concurrentResult;
    private Object[] concurrentResultContext;
    private final Map<Object, CallableProcessingInterceptor> callableInterceptors;
    private final Map<Object, DeferredResultProcessingInterceptor> deferredResultInterceptors;
    
    WebAsyncManager() {
        this.taskExecutor = new SimpleAsyncTaskExecutor(this.getClass().getSimpleName());
        this.concurrentResult = WebAsyncManager.RESULT_NONE;
        this.callableInterceptors = new LinkedHashMap<Object, CallableProcessingInterceptor>();
        this.deferredResultInterceptors = new LinkedHashMap<Object, DeferredResultProcessingInterceptor>();
    }
    
    public void setAsyncWebRequest(final AsyncWebRequest asyncWebRequest) {
        Assert.notNull(asyncWebRequest, "AsyncWebRequest must not be null");
        Assert.state(!this.isConcurrentHandlingStarted(), "Can't set AsyncWebRequest with concurrent handling in progress");
        (this.asyncWebRequest = asyncWebRequest).addCompletionHandler(new Runnable() {
            @Override
            public void run() {
                asyncWebRequest.removeAttribute(WebAsyncUtils.WEB_ASYNC_MANAGER_ATTRIBUTE, 0);
            }
        });
    }
    
    public void setTaskExecutor(final AsyncTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }
    
    public boolean isConcurrentHandlingStarted() {
        return this.asyncWebRequest != null && this.asyncWebRequest.isAsyncStarted();
    }
    
    public boolean hasConcurrentResult() {
        return this.concurrentResult != WebAsyncManager.RESULT_NONE;
    }
    
    public Object getConcurrentResult() {
        return this.concurrentResult;
    }
    
    public Object[] getConcurrentResultContext() {
        return this.concurrentResultContext;
    }
    
    public CallableProcessingInterceptor getCallableInterceptor(final Object key) {
        return this.callableInterceptors.get(key);
    }
    
    public DeferredResultProcessingInterceptor getDeferredResultInterceptor(final Object key) {
        return this.deferredResultInterceptors.get(key);
    }
    
    public void registerCallableInterceptor(final Object key, final CallableProcessingInterceptor interceptor) {
        Assert.notNull(key, "Key is required");
        Assert.notNull(interceptor, "CallableProcessingInterceptor  is required");
        this.callableInterceptors.put(key, interceptor);
    }
    
    public void registerCallableInterceptors(final CallableProcessingInterceptor... interceptors) {
        Assert.notNull(interceptors, "A CallableProcessingInterceptor is required");
        for (final CallableProcessingInterceptor interceptor : interceptors) {
            final String key = interceptor.getClass().getName() + ":" + interceptor.hashCode();
            this.callableInterceptors.put(key, interceptor);
        }
    }
    
    public void registerDeferredResultInterceptor(final Object key, final DeferredResultProcessingInterceptor interceptor) {
        Assert.notNull(key, "Key is required");
        Assert.notNull(interceptor, "DeferredResultProcessingInterceptor is required");
        this.deferredResultInterceptors.put(key, interceptor);
    }
    
    public void registerDeferredResultInterceptors(final DeferredResultProcessingInterceptor... interceptors) {
        Assert.notNull(interceptors, "A DeferredResultProcessingInterceptor is required");
        for (final DeferredResultProcessingInterceptor interceptor : interceptors) {
            final String key = interceptors.getClass().getName() + ":" + interceptors.hashCode();
            this.deferredResultInterceptors.put(key, interceptor);
        }
    }
    
    public void clearConcurrentResult() {
        this.concurrentResult = WebAsyncManager.RESULT_NONE;
        this.concurrentResultContext = null;
    }
    
    public void startCallableProcessing(final Callable<?> callable, final Object... processingContext) throws Exception {
        Assert.notNull(callable, "Callable must not be null");
        this.startCallableProcessing(new WebAsyncTask<Object>(callable), processingContext);
    }
    
    public void startCallableProcessing(final WebAsyncTask<?> webAsyncTask, final Object... processingContext) throws Exception {
        Assert.notNull(webAsyncTask, "WebAsyncTask must not be null");
        Assert.state(this.asyncWebRequest != null, "AsyncWebRequest must not be null");
        final Long timeout = webAsyncTask.getTimeout();
        if (timeout != null) {
            this.asyncWebRequest.setTimeout(timeout);
        }
        final AsyncTaskExecutor executor = webAsyncTask.getExecutor();
        if (executor != null) {
            this.taskExecutor = executor;
        }
        final List<CallableProcessingInterceptor> interceptors = new ArrayList<CallableProcessingInterceptor>();
        interceptors.add(webAsyncTask.getInterceptor());
        interceptors.addAll(this.callableInterceptors.values());
        interceptors.add(WebAsyncManager.timeoutCallableInterceptor);
        final Callable<?> callable = webAsyncTask.getCallable();
        final CallableInterceptorChain interceptorChain = new CallableInterceptorChain(interceptors);
        this.asyncWebRequest.addTimeoutHandler(new Runnable() {
            @Override
            public void run() {
                WebAsyncManager.logger.debug("Processing timeout");
                final Object result = interceptorChain.triggerAfterTimeout(WebAsyncManager.this.asyncWebRequest, callable);
                if (result != CallableProcessingInterceptor.RESULT_NONE) {
                    WebAsyncManager.this.setConcurrentResultAndDispatch(result);
                }
            }
        });
        this.asyncWebRequest.addCompletionHandler(new Runnable() {
            @Override
            public void run() {
                interceptorChain.triggerAfterCompletion(WebAsyncManager.this.asyncWebRequest, callable);
            }
        });
        interceptorChain.applyBeforeConcurrentHandling(this.asyncWebRequest, callable);
        this.startAsyncProcessing(processingContext);
        this.taskExecutor.submit(new Runnable() {
            @Override
            public void run() {
                Object result = null;
                try {
                    interceptorChain.applyPreProcess(WebAsyncManager.this.asyncWebRequest, callable);
                    result = callable.call();
                }
                catch (Throwable t) {
                    result = t;
                }
                finally {
                    result = interceptorChain.applyPostProcess(WebAsyncManager.this.asyncWebRequest, callable, result);
                }
                WebAsyncManager.this.setConcurrentResultAndDispatch(result);
            }
        });
    }
    
    private void setConcurrentResultAndDispatch(final Object result) {
        synchronized (this) {
            if (this.hasConcurrentResult()) {
                return;
            }
            this.concurrentResult = result;
        }
        if (this.asyncWebRequest.isAsyncComplete()) {
            WebAsyncManager.logger.error("Could not complete async processing due to timeout or network error");
            return;
        }
        WebAsyncManager.logger.debug("Concurrent result value [" + this.concurrentResult + "]");
        WebAsyncManager.logger.debug("Dispatching request to resume processing");
        this.asyncWebRequest.dispatch();
    }
    
    public void startDeferredResultProcessing(final DeferredResult<?> deferredResult, final Object... processingContext) throws Exception {
        Assert.notNull(deferredResult, "DeferredResult must not be null");
        Assert.state(this.asyncWebRequest != null, "AsyncWebRequest must not be null");
        final Long timeout = deferredResult.getTimeoutValue();
        if (timeout != null) {
            this.asyncWebRequest.setTimeout(timeout);
        }
        final List<DeferredResultProcessingInterceptor> interceptors = new ArrayList<DeferredResultProcessingInterceptor>();
        interceptors.add(deferredResult.getInterceptor());
        interceptors.addAll(this.deferredResultInterceptors.values());
        interceptors.add(WebAsyncManager.timeoutDeferredResultInterceptor);
        final DeferredResultInterceptorChain interceptorChain = new DeferredResultInterceptorChain(interceptors);
        this.asyncWebRequest.addTimeoutHandler(new Runnable() {
            @Override
            public void run() {
                try {
                    interceptorChain.triggerAfterTimeout(WebAsyncManager.this.asyncWebRequest, deferredResult);
                }
                catch (Throwable t) {
                    WebAsyncManager.this.setConcurrentResultAndDispatch(t);
                }
            }
        });
        this.asyncWebRequest.addCompletionHandler(new Runnable() {
            @Override
            public void run() {
                interceptorChain.triggerAfterCompletion(WebAsyncManager.this.asyncWebRequest, deferredResult);
            }
        });
        interceptorChain.applyBeforeConcurrentHandling(this.asyncWebRequest, deferredResult);
        this.startAsyncProcessing(processingContext);
        try {
            interceptorChain.applyPreProcess(this.asyncWebRequest, deferredResult);
            deferredResult.setResultHandler(new DeferredResult.DeferredResultHandler() {
                @Override
                public void handleResult(Object result) {
                    result = interceptorChain.applyPostProcess(WebAsyncManager.this.asyncWebRequest, deferredResult, result);
                    WebAsyncManager.this.setConcurrentResultAndDispatch(result);
                }
            });
        }
        catch (Throwable t) {
            this.setConcurrentResultAndDispatch(t);
        }
    }
    
    private void startAsyncProcessing(final Object[] processingContext) {
        this.clearConcurrentResult();
        this.concurrentResultContext = processingContext;
        this.asyncWebRequest.startAsync();
        if (WebAsyncManager.logger.isDebugEnabled()) {
            final HttpServletRequest request = this.asyncWebRequest.getNativeRequest(HttpServletRequest.class);
            final String requestUri = WebAsyncManager.urlPathHelper.getRequestUri(request);
            WebAsyncManager.logger.debug("Concurrent handling starting for " + request.getMethod() + " [" + requestUri + "]");
        }
    }
    
    static {
        RESULT_NONE = new Object();
        logger = LogFactory.getLog(WebAsyncManager.class);
        urlPathHelper = new UrlPathHelper();
        timeoutCallableInterceptor = new TimeoutCallableProcessingInterceptor();
        timeoutDeferredResultInterceptor = new TimeoutDeferredResultProcessingInterceptor();
    }
}
