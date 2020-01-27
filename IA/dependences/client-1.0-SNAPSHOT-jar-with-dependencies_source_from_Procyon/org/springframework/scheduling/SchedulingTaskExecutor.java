// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling;

import org.springframework.core.task.AsyncTaskExecutor;

public interface SchedulingTaskExecutor extends AsyncTaskExecutor
{
    boolean prefersShortLivedTasks();
}
