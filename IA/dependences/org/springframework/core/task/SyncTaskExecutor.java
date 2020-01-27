// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.task;

import org.springframework.util.Assert;
import java.io.Serializable;

public class SyncTaskExecutor implements TaskExecutor, Serializable
{
    @Override
    public void execute(final Runnable task) {
        Assert.notNull(task, "Runnable must not be null");
        task.run();
    }
}
