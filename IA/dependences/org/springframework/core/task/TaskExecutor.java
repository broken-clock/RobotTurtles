// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.task;

import java.util.concurrent.Executor;

public interface TaskExecutor extends Executor
{
    void execute(final Runnable p0);
}
