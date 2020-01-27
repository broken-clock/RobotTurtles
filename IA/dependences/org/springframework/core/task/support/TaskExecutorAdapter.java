// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.task.support;

import org.springframework.util.concurrent.ListenableFutureTask;
import org.springframework.util.concurrent.ListenableFuture;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.util.Assert;
import java.util.concurrent.Executor;
import org.springframework.core.task.AsyncListenableTaskExecutor;

public class TaskExecutorAdapter implements AsyncListenableTaskExecutor
{
    private final Executor concurrentExecutor;
    
    public TaskExecutorAdapter(final Executor concurrentExecutor) {
        Assert.notNull(concurrentExecutor, "Executor must not be null");
        this.concurrentExecutor = concurrentExecutor;
    }
    
    @Override
    public void execute(final Runnable task) {
        try {
            this.concurrentExecutor.execute(task);
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + this.concurrentExecutor + "] did not accept task: " + task, ex);
        }
    }
    
    @Override
    public void execute(final Runnable task, final long startTimeout) {
        this.execute(task);
    }
    
    @Override
    public Future<?> submit(final Runnable task) {
        try {
            if (this.concurrentExecutor instanceof ExecutorService) {
                return ((ExecutorService)this.concurrentExecutor).submit(task);
            }
            final FutureTask<Object> future = new FutureTask<Object>(task, null);
            this.concurrentExecutor.execute(future);
            return future;
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + this.concurrentExecutor + "] did not accept task: " + task, ex);
        }
    }
    
    @Override
    public <T> Future<T> submit(final Callable<T> task) {
        try {
            if (this.concurrentExecutor instanceof ExecutorService) {
                return ((ExecutorService)this.concurrentExecutor).submit(task);
            }
            final FutureTask<T> future = new FutureTask<T>(task);
            this.concurrentExecutor.execute(future);
            return future;
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + this.concurrentExecutor + "] did not accept task: " + task, ex);
        }
    }
    
    @Override
    public ListenableFuture<?> submitListenable(final Runnable task) {
        try {
            final ListenableFutureTask<Object> future = new ListenableFutureTask<Object>(task, null);
            this.concurrentExecutor.execute(future);
            return future;
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + this.concurrentExecutor + "] did not accept task: " + task, ex);
        }
    }
    
    @Override
    public <T> ListenableFuture<T> submitListenable(final Callable<T> task) {
        try {
            final ListenableFutureTask<T> future = new ListenableFutureTask<T>(task);
            this.concurrentExecutor.execute(future);
            return future;
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + this.concurrentExecutor + "] did not accept task: " + task, ex);
        }
    }
}
