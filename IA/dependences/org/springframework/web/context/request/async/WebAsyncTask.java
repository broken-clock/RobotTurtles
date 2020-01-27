// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.context.request.async;

import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.util.Assert;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.task.AsyncTaskExecutor;
import java.util.concurrent.Callable;

public class WebAsyncTask<V>
{
    private final Callable<V> callable;
    private final Long timeout;
    private final String executorName;
    private final AsyncTaskExecutor executor;
    private Callable<V> timeoutCallback;
    private Runnable completionCallback;
    private BeanFactory beanFactory;
    
    public WebAsyncTask(final Callable<V> callable) {
        this(null, null, null, callable);
    }
    
    public WebAsyncTask(final long timeout, final Callable<V> callable) {
        this(timeout, null, null, callable);
    }
    
    public WebAsyncTask(final Long timeout, final String executorName, final Callable<V> callable) {
        this(timeout, null, executorName, callable);
        Assert.notNull(this.executor, "Executor name must not be null");
    }
    
    public WebAsyncTask(final Long timeout, final AsyncTaskExecutor executor, final Callable<V> callable) {
        this(timeout, executor, null, callable);
        Assert.notNull(executor, "Executor must not be null");
    }
    
    private WebAsyncTask(final Long timeout, final AsyncTaskExecutor executor, final String executorName, final Callable<V> callable) {
        Assert.notNull(callable, "Callable must not be null");
        this.callable = callable;
        this.timeout = timeout;
        this.executor = executor;
        this.executorName = executorName;
    }
    
    public Callable<?> getCallable() {
        return this.callable;
    }
    
    public Long getTimeout() {
        return this.timeout;
    }
    
    public AsyncTaskExecutor getExecutor() {
        if (this.executor != null) {
            return this.executor;
        }
        if (this.executorName != null) {
            Assert.state(this.beanFactory != null, "A BeanFactory is required to look up a task executor bean");
            return this.beanFactory.getBean(this.executorName, AsyncTaskExecutor.class);
        }
        return null;
    }
    
    public void setBeanFactory(final BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
    
    public void onTimeout(final Callable<V> callback) {
        this.timeoutCallback = callback;
    }
    
    public void onCompletion(final Runnable callback) {
        this.completionCallback = callback;
    }
    
    CallableProcessingInterceptor getInterceptor() {
        return new CallableProcessingInterceptorAdapter() {
            @Override
            public <T> Object handleTimeout(final NativeWebRequest request, final Callable<T> task) throws Exception {
                return (WebAsyncTask.this.timeoutCallback != null) ? WebAsyncTask.this.timeoutCallback.call() : CallableProcessingInterceptor.RESULT_NONE;
            }
            
            @Override
            public <T> void afterCompletion(final NativeWebRequest request, final Callable<T> task) throws Exception {
                if (WebAsyncTask.this.completionCallback != null) {
                    WebAsyncTask.this.completionCallback.run();
                }
            }
        };
    }
}
