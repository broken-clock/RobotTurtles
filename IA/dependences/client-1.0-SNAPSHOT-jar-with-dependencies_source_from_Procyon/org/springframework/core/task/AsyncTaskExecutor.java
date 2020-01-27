// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.task;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface AsyncTaskExecutor extends TaskExecutor
{
    public static final long TIMEOUT_IMMEDIATE = 0L;
    public static final long TIMEOUT_INDEFINITE = Long.MAX_VALUE;
    
    void execute(final Runnable p0, final long p1);
    
    Future<?> submit(final Runnable p0);
    
     <T> Future<T> submit(final Callable<T> p0);
}
