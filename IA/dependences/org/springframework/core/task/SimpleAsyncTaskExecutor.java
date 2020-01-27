// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.task;

import org.springframework.util.ConcurrencyThrottleSupport;
import org.springframework.util.concurrent.ListenableFutureTask;
import org.springframework.util.concurrent.ListenableFuture;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Future;
import org.springframework.util.Assert;
import java.util.concurrent.ThreadFactory;
import java.io.Serializable;
import org.springframework.util.CustomizableThreadCreator;

public class SimpleAsyncTaskExecutor extends CustomizableThreadCreator implements AsyncListenableTaskExecutor, Serializable
{
    public static final int UNBOUNDED_CONCURRENCY = -1;
    public static final int NO_CONCURRENCY = 0;
    private final ConcurrencyThrottleAdapter concurrencyThrottle;
    private ThreadFactory threadFactory;
    
    public SimpleAsyncTaskExecutor() {
        this.concurrencyThrottle = new ConcurrencyThrottleAdapter();
    }
    
    public SimpleAsyncTaskExecutor(final String threadNamePrefix) {
        super(threadNamePrefix);
        this.concurrencyThrottle = new ConcurrencyThrottleAdapter();
    }
    
    public SimpleAsyncTaskExecutor(final ThreadFactory threadFactory) {
        this.concurrencyThrottle = new ConcurrencyThrottleAdapter();
        this.threadFactory = threadFactory;
    }
    
    public void setThreadFactory(final ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }
    
    public final ThreadFactory getThreadFactory() {
        return this.threadFactory;
    }
    
    public void setConcurrencyLimit(final int concurrencyLimit) {
        this.concurrencyThrottle.setConcurrencyLimit(concurrencyLimit);
    }
    
    public final int getConcurrencyLimit() {
        return this.concurrencyThrottle.getConcurrencyLimit();
    }
    
    public final boolean isThrottleActive() {
        return this.concurrencyThrottle.isThrottleActive();
    }
    
    @Override
    public void execute(final Runnable task) {
        this.execute(task, Long.MAX_VALUE);
    }
    
    @Override
    public void execute(final Runnable task, final long startTimeout) {
        Assert.notNull(task, "Runnable must not be null");
        if (this.isThrottleActive() && startTimeout > 0L) {
            this.concurrencyThrottle.beforeAccess();
            this.doExecute(new ConcurrencyThrottlingRunnable(task));
        }
        else {
            this.doExecute(task);
        }
    }
    
    @Override
    public Future<?> submit(final Runnable task) {
        final FutureTask<Object> future = new FutureTask<Object>(task, null);
        this.execute(future, Long.MAX_VALUE);
        return future;
    }
    
    @Override
    public <T> Future<T> submit(final Callable<T> task) {
        final FutureTask<T> future = new FutureTask<T>(task);
        this.execute(future, Long.MAX_VALUE);
        return future;
    }
    
    @Override
    public ListenableFuture<?> submitListenable(final Runnable task) {
        final ListenableFutureTask<Object> future = new ListenableFutureTask<Object>(task, null);
        this.execute(future, Long.MAX_VALUE);
        return future;
    }
    
    @Override
    public <T> ListenableFuture<T> submitListenable(final Callable<T> task) {
        final ListenableFutureTask<T> future = new ListenableFutureTask<T>(task);
        this.execute(future, Long.MAX_VALUE);
        return future;
    }
    
    protected void doExecute(final Runnable task) {
        final Thread thread = (this.threadFactory != null) ? this.threadFactory.newThread(task) : this.createThread(task);
        thread.start();
    }
    
    private static class ConcurrencyThrottleAdapter extends ConcurrencyThrottleSupport
    {
        @Override
        protected void beforeAccess() {
            super.beforeAccess();
        }
        
        @Override
        protected void afterAccess() {
            super.afterAccess();
        }
    }
    
    private class ConcurrencyThrottlingRunnable implements Runnable
    {
        private final Runnable target;
        
        public ConcurrencyThrottlingRunnable(final Runnable target) {
            this.target = target;
        }
        
        @Override
        public void run() {
            try {
                this.target.run();
            }
            finally {
                SimpleAsyncTaskExecutor.this.concurrencyThrottle.afterAccess();
            }
        }
    }
}
