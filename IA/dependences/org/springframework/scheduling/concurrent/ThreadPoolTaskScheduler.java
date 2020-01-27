// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.Date;
import org.springframework.scheduling.support.TaskUtils;
import java.util.concurrent.ScheduledFuture;
import org.springframework.scheduling.Trigger;
import org.springframework.util.concurrent.ListenableFutureTask;
import org.springframework.util.concurrent.ListenableFuture;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import org.springframework.core.task.TaskRejectedException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.springframework.util.Assert;
import org.springframework.util.ErrorHandler;
import java.util.concurrent.ScheduledExecutorService;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.core.task.AsyncListenableTaskExecutor;

public class ThreadPoolTaskScheduler extends ExecutorConfigurationSupport implements AsyncListenableTaskExecutor, SchedulingTaskExecutor, TaskScheduler
{
    private volatile int poolSize;
    private volatile ScheduledExecutorService scheduledExecutor;
    private volatile ErrorHandler errorHandler;
    
    public ThreadPoolTaskScheduler() {
        this.poolSize = 1;
    }
    
    public void setPoolSize(final int poolSize) {
        Assert.isTrue(poolSize > 0, "'poolSize' must be 1 or higher");
        this.poolSize = poolSize;
        if (this.scheduledExecutor instanceof ScheduledThreadPoolExecutor) {
            ((ScheduledThreadPoolExecutor)this.scheduledExecutor).setCorePoolSize(poolSize);
        }
    }
    
    public void setErrorHandler(final ErrorHandler errorHandler) {
        Assert.notNull(errorHandler, "'errorHandler' must not be null");
        this.errorHandler = errorHandler;
    }
    
    @Override
    protected ExecutorService initializeExecutor(final ThreadFactory threadFactory, final RejectedExecutionHandler rejectedExecutionHandler) {
        return this.scheduledExecutor = this.createExecutor(this.poolSize, threadFactory, rejectedExecutionHandler);
    }
    
    protected ScheduledExecutorService createExecutor(final int poolSize, final ThreadFactory threadFactory, final RejectedExecutionHandler rejectedExecutionHandler) {
        return new ScheduledThreadPoolExecutor(poolSize, threadFactory, rejectedExecutionHandler);
    }
    
    public ScheduledExecutorService getScheduledExecutor() throws IllegalStateException {
        Assert.state(this.scheduledExecutor != null, "ThreadPoolTaskScheduler not initialized");
        return this.scheduledExecutor;
    }
    
    public ScheduledThreadPoolExecutor getScheduledThreadPoolExecutor() throws IllegalStateException {
        Assert.state(this.scheduledExecutor instanceof ScheduledThreadPoolExecutor, "No ScheduledThreadPoolExecutor available");
        return (ScheduledThreadPoolExecutor)this.scheduledExecutor;
    }
    
    public int getPoolSize() {
        if (this.scheduledExecutor == null) {
            return this.poolSize;
        }
        return this.getScheduledThreadPoolExecutor().getPoolSize();
    }
    
    public int getActiveCount() {
        if (this.scheduledExecutor == null) {
            return 0;
        }
        return this.getScheduledThreadPoolExecutor().getActiveCount();
    }
    
    @Override
    public void execute(final Runnable task) {
        final Executor executor = this.getScheduledExecutor();
        try {
            executor.execute(this.errorHandlingTask(task, false));
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
        final ExecutorService executor = this.getScheduledExecutor();
        try {
            return executor.submit(this.errorHandlingTask(task, false));
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }
    
    @Override
    public <T> Future<T> submit(final Callable<T> task) {
        final ExecutorService executor = this.getScheduledExecutor();
        try {
            Callable<T> taskToUse = task;
            if (this.errorHandler != null) {
                taskToUse = new DelegatingErrorHandlingCallable<T>(task, this.errorHandler);
            }
            return executor.submit(taskToUse);
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }
    
    @Override
    public ListenableFuture<?> submitListenable(final Runnable task) {
        final ExecutorService executor = this.getScheduledExecutor();
        try {
            final ListenableFutureTask<Object> future = new ListenableFutureTask<Object>(task, null);
            executor.execute(this.errorHandlingTask(future, false));
            return future;
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }
    
    @Override
    public <T> ListenableFuture<T> submitListenable(final Callable<T> task) {
        final ExecutorService executor = this.getScheduledExecutor();
        try {
            final ListenableFutureTask<T> future = new ListenableFutureTask<T>(task);
            executor.execute(this.errorHandlingTask(future, false));
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
    
    @Override
    public ScheduledFuture<?> schedule(final Runnable task, final Trigger trigger) {
        final ScheduledExecutorService executor = this.getScheduledExecutor();
        try {
            final ErrorHandler errorHandler = (this.errorHandler != null) ? this.errorHandler : TaskUtils.getDefaultErrorHandler(true);
            return new ReschedulingRunnable(task, trigger, executor, errorHandler).schedule();
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }
    
    @Override
    public ScheduledFuture<?> schedule(final Runnable task, final Date startTime) {
        final ScheduledExecutorService executor = this.getScheduledExecutor();
        final long initialDelay = startTime.getTime() - System.currentTimeMillis();
        try {
            return executor.schedule(this.errorHandlingTask(task, false), initialDelay, TimeUnit.MILLISECONDS);
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }
    
    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(final Runnable task, final Date startTime, final long period) {
        final ScheduledExecutorService executor = this.getScheduledExecutor();
        final long initialDelay = startTime.getTime() - System.currentTimeMillis();
        try {
            return executor.scheduleAtFixedRate(this.errorHandlingTask(task, true), initialDelay, period, TimeUnit.MILLISECONDS);
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }
    
    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(final Runnable task, final long period) {
        final ScheduledExecutorService executor = this.getScheduledExecutor();
        try {
            return executor.scheduleAtFixedRate(this.errorHandlingTask(task, true), 0L, period, TimeUnit.MILLISECONDS);
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }
    
    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(final Runnable task, final Date startTime, final long delay) {
        final ScheduledExecutorService executor = this.getScheduledExecutor();
        final long initialDelay = startTime.getTime() - System.currentTimeMillis();
        try {
            return executor.scheduleWithFixedDelay(this.errorHandlingTask(task, true), initialDelay, delay, TimeUnit.MILLISECONDS);
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }
    
    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(final Runnable task, final long delay) {
        final ScheduledExecutorService executor = this.getScheduledExecutor();
        try {
            return executor.scheduleWithFixedDelay(this.errorHandlingTask(task, true), 0L, delay, TimeUnit.MILLISECONDS);
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
        }
    }
    
    private Runnable errorHandlingTask(final Runnable task, final boolean isRepeatingTask) {
        return TaskUtils.decorateTaskWithErrorHandler(task, this.errorHandler, isRepeatingTask);
    }
    
    private static class DelegatingErrorHandlingCallable<V> implements Callable<V>
    {
        private final Callable<V> delegate;
        private final ErrorHandler errorHandler;
        
        public DelegatingErrorHandlingCallable(final Callable<V> delegate, final ErrorHandler errorHandler) {
            this.delegate = delegate;
            this.errorHandler = errorHandler;
        }
        
        @Override
        public V call() throws Exception {
            try {
                return this.delegate.call();
            }
            catch (Throwable t) {
                this.errorHandler.handleError(t);
                return null;
            }
        }
    }
}
