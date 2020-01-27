// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class ListenableFutureTask<T> extends FutureTask<T> implements ListenableFuture<T>
{
    private final ListenableFutureCallbackRegistry<T> callbacks;
    
    public ListenableFutureTask(final Callable<T> callable) {
        super(callable);
        this.callbacks = new ListenableFutureCallbackRegistry<T>();
    }
    
    public ListenableFutureTask(final Runnable runnable, final T result) {
        super(runnable, result);
        this.callbacks = new ListenableFutureCallbackRegistry<T>();
    }
    
    @Override
    public void addCallback(final ListenableFutureCallback<? super T> callback) {
        this.callbacks.addCallback(callback);
    }
    
    @Override
    protected final void done() {
        Throwable cause;
        try {
            final T result = this.get();
            this.callbacks.success(result);
            return;
        }
        catch (InterruptedException ex3) {
            Thread.currentThread().interrupt();
            return;
        }
        catch (ExecutionException ex) {
            cause = ex.getCause();
            if (cause == null) {
                cause = ex;
            }
        }
        catch (Throwable ex2) {
            cause = ex2;
        }
        this.callbacks.failure(cause);
    }
}
