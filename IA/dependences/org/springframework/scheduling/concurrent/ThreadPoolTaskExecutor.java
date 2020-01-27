// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.concurrent;

import org.springframework.util.concurrent.ListenableFutureTask;
import org.springframework.util.concurrent.ListenableFuture;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.util.Assert;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.core.task.AsyncListenableTaskExecutor;

public class ThreadPoolTaskExecutor extends ExecutorConfigurationSupport implements AsyncListenableTaskExecutor, SchedulingTaskExecutor
{
    private final Object poolSizeMonitor;
    private int corePoolSize;
    private int maxPoolSize;
    private int keepAliveSeconds;
    private boolean allowCoreThreadTimeOut;
    private int queueCapacity;
    private ThreadPoolExecutor threadPoolExecutor;
    
    public ThreadPoolTaskExecutor() {
        this.poolSizeMonitor = new Object();
        this.corePoolSize = 1;
        this.maxPoolSize = Integer.MAX_VALUE;
        this.keepAliveSeconds = 60;
        this.allowCoreThreadTimeOut = false;
        this.queueCapacity = Integer.MAX_VALUE;
    }
    
    public void setCorePoolSize(final int corePoolSize) {
        synchronized (this.poolSizeMonitor) {
            this.corePoolSize = corePoolSize;
            if (this.threadPoolExecutor != null) {
                this.threadPoolExecutor.setCorePoolSize(corePoolSize);
            }
        }
    }
    
    public int getCorePoolSize() {
        synchronized (this.poolSizeMonitor) {
            return this.corePoolSize;
        }
    }
    
    public void setMaxPoolSize(final int maxPoolSize) {
        synchronized (this.poolSizeMonitor) {
            this.maxPoolSize = maxPoolSize;
            if (this.threadPoolExecutor != null) {
                this.threadPoolExecutor.setMaximumPoolSize(maxPoolSize);
            }
        }
    }
    
    public int getMaxPoolSize() {
        synchronized (this.poolSizeMonitor) {
            return this.maxPoolSize;
        }
    }
    
    public void setKeepAliveSeconds(final int keepAliveSeconds) {
        synchronized (this.poolSizeMonitor) {
            this.keepAliveSeconds = keepAliveSeconds;
            if (this.threadPoolExecutor != null) {
                this.threadPoolExecutor.setKeepAliveTime(keepAliveSeconds, TimeUnit.SECONDS);
            }
        }
    }
    
    public int getKeepAliveSeconds() {
        synchronized (this.poolSizeMonitor) {
            return this.keepAliveSeconds;
        }
    }
    
    public void setAllowCoreThreadTimeOut(final boolean allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
    }
    
    public void setQueueCapacity(final int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }
    
    @Override
    protected ExecutorService initializeExecutor(final ThreadFactory threadFactory, final RejectedExecutionHandler rejectedExecutionHandler) {
        final BlockingQueue<Runnable> queue = this.createQueue(this.queueCapacity);
        final ThreadPoolExecutor executor = new ThreadPoolExecutor(this.corePoolSize, this.maxPoolSize, this.keepAliveSeconds, TimeUnit.SECONDS, queue, threadFactory, rejectedExecutionHandler);
        if (this.allowCoreThreadTimeOut) {
            executor.allowCoreThreadTimeOut(true);
        }
        return this.threadPoolExecutor = executor;
    }
    
    protected BlockingQueue<Runnable> createQueue(final int queueCapacity) {
        if (queueCapacity > 0) {
            return new LinkedBlockingQueue<Runnable>(queueCapacity);
        }
        return new SynchronousQueue<Runnable>();
    }
    
    public ThreadPoolExecutor getThreadPoolExecutor() throws IllegalStateException {
        Assert.state(this.threadPoolExecutor != null, "ThreadPoolTaskExecutor not initialized");
        return this.threadPoolExecutor;
    }
    
    public int getPoolSize() {
        if (this.threadPoolExecutor == null) {
            return this.corePoolSize;
        }
        return this.threadPoolExecutor.getPoolSize();
    }
    
    public int getActiveCount() {
        if (this.threadPoolExecutor == null) {
            return 0;
        }
        return this.threadPoolExecutor.getActiveCount();
    }
    
    @Override
    public void execute(final Runnable task) {
        final Executor executor = this.getThreadPoolExecutor();
        try {
            executor.execute(task);
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }
    
    @Override
    public void execute(final Runnable task, final long startTimeout) {
        this.execute(task);
    }
    
    @Override
    public Future<?> submit(final Runnable task) {
        final ExecutorService executor = this.getThreadPoolExecutor();
        try {
            return executor.submit(task);
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }
    
    @Override
    public <T> Future<T> submit(final Callable<T> task) {
        final ExecutorService executor = this.getThreadPoolExecutor();
        try {
            return executor.submit(task);
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }
    
    @Override
    public ListenableFuture<?> submitListenable(final Runnable task) {
        final ExecutorService executor = this.getThreadPoolExecutor();
        try {
            final ListenableFutureTask<Object> future = new ListenableFutureTask<Object>(task, null);
            executor.execute(future);
            return future;
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }
    
    @Override
    public <T> ListenableFuture<T> submitListenable(final Callable<T> task) {
        final ExecutorService executor = this.getThreadPoolExecutor();
        try {
            final ListenableFutureTask<T> future = new ListenableFutureTask<T>(task);
            executor.execute(future);
            return future;
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }
    
    @Override
    public boolean prefersShortLivedTasks() {
        return true;
    }
}
