// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.context.request.async;

import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.util.Assert;
import org.apache.commons.logging.Log;

public class DeferredResult<T>
{
    private static final Log logger;
    private static final Object RESULT_NONE;
    private final Long timeout;
    private final Object timeoutResult;
    private Runnable timeoutCallback;
    private Runnable completionCallback;
    private DeferredResultHandler resultHandler;
    private Object result;
    private boolean expired;
    
    public DeferredResult() {
        this(null, DeferredResult.RESULT_NONE);
    }
    
    public DeferredResult(final long timeout) {
        this(timeout, DeferredResult.RESULT_NONE);
    }
    
    public DeferredResult(final Long timeout, final Object timeoutResult) {
        this.result = DeferredResult.RESULT_NONE;
        this.timeoutResult = timeoutResult;
        this.timeout = timeout;
    }
    
    public final boolean isSetOrExpired() {
        return this.result != DeferredResult.RESULT_NONE || this.expired;
    }
    
    public boolean hasResult() {
        return this.result != DeferredResult.RESULT_NONE;
    }
    
    public Object getResult() {
        return this.hasResult() ? this.result : null;
    }
    
    final Long getTimeoutValue() {
        return this.timeout;
    }
    
    public void onTimeout(final Runnable callback) {
        this.timeoutCallback = callback;
    }
    
    public void onCompletion(final Runnable callback) {
        this.completionCallback = callback;
    }
    
    public final void setResultHandler(final DeferredResultHandler resultHandler) {
        Assert.notNull(resultHandler, "DeferredResultHandler is required");
        synchronized (this) {
            this.resultHandler = resultHandler;
            if (this.result != DeferredResult.RESULT_NONE && !this.expired) {
                try {
                    this.resultHandler.handleResult(this.result);
                }
                catch (Throwable t) {
                    DeferredResult.logger.trace("DeferredResult not handled", t);
                }
            }
        }
    }
    
    public boolean setResult(final T result) {
        return this.setResultInternal(result);
    }
    
    private boolean setResultInternal(final Object result) {
        synchronized (this) {
            if (this.isSetOrExpired()) {
                return false;
            }
            this.result = result;
        }
        if (this.resultHandler != null) {
            this.resultHandler.handleResult(this.result);
        }
        return true;
    }
    
    public boolean setErrorResult(final Object result) {
        return this.setResultInternal(result);
    }
    
    final DeferredResultProcessingInterceptor getInterceptor() {
        return new DeferredResultProcessingInterceptorAdapter() {
            @Override
            public <S> boolean handleTimeout(final NativeWebRequest request, final DeferredResult<S> deferredResult) {
                if (DeferredResult.this.timeoutCallback != null) {
                    DeferredResult.this.timeoutCallback.run();
                }
                if (DeferredResult.this.timeoutResult != DeferredResult.RESULT_NONE) {
                    DeferredResult.this.setResultInternal(DeferredResult.this.timeoutResult);
                }
                return true;
            }
            
            @Override
            public <S> void afterCompletion(final NativeWebRequest request, final DeferredResult<S> deferredResult) {
                synchronized (DeferredResult.this) {
                    DeferredResult.this.expired = true;
                }
                if (DeferredResult.this.completionCallback != null) {
                    DeferredResult.this.completionCallback.run();
                }
            }
        };
    }
    
    static {
        logger = LogFactory.getLog(DeferredResult.class);
        RESULT_NONE = new Object();
    }
    
    public interface DeferredResultHandler
    {
        void handleResult(final Object p0);
    }
}
