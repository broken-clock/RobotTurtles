// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.task.support;

import java.util.concurrent.TimeUnit;
import java.util.List;
import org.springframework.util.Assert;
import org.springframework.core.task.TaskExecutor;
import java.util.concurrent.AbstractExecutorService;

public class ExecutorServiceAdapter extends AbstractExecutorService
{
    private final TaskExecutor taskExecutor;
    
    public ExecutorServiceAdapter(final TaskExecutor taskExecutor) {
        Assert.notNull(taskExecutor, "TaskExecutor must not be null");
        this.taskExecutor = taskExecutor;
    }
    
    @Override
    public void execute(final Runnable task) {
        this.taskExecutor.execute(task);
    }
    
    @Override
    public void shutdown() {
        throw new IllegalStateException("Manual shutdown not supported - ExecutorServiceAdapter is dependent on an external lifecycle");
    }
    
    @Override
    public List<Runnable> shutdownNow() {
        throw new IllegalStateException("Manual shutdown not supported - ExecutorServiceAdapter is dependent on an external lifecycle");
    }
    
    @Override
    public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
        throw new IllegalStateException("Manual shutdown not supported - ExecutorServiceAdapter is dependent on an external lifecycle");
    }
    
    @Override
    public boolean isShutdown() {
        return false;
    }
    
    @Override
    public boolean isTerminated() {
        return false;
    }
}
