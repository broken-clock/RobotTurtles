// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.task;

import java.util.concurrent.Callable;
import org.springframework.util.concurrent.ListenableFuture;

public interface AsyncListenableTaskExecutor extends AsyncTaskExecutor
{
    ListenableFuture<?> submitListenable(final Runnable p0);
    
     <T> ListenableFuture<T> submitListenable(final Callable<T> p0);
}
