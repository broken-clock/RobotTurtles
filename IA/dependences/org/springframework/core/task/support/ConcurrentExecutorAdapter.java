// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.task.support;

import org.springframework.util.Assert;
import org.springframework.core.task.TaskExecutor;
import java.util.concurrent.Executor;

public class ConcurrentExecutorAdapter implements Executor
{
    private final TaskExecutor taskExecutor;
    
    public ConcurrentExecutorAdapter(final TaskExecutor taskExecutor) {
        Assert.notNull(taskExecutor, "TaskExecutor must not be null");
        this.taskExecutor = taskExecutor;
    }
    
    @Override
    public void execute(final Runnable command) {
        this.taskExecutor.execute(command);
    }
}
