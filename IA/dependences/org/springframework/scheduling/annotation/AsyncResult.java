// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.annotation;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.Future;

public class AsyncResult<V> implements Future<V>
{
    private final V value;
    
    public AsyncResult(final V value) {
        this.value = value;
    }
    
    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        return false;
    }
    
    @Override
    public boolean isCancelled() {
        return false;
    }
    
    @Override
    public boolean isDone() {
        return true;
    }
    
    @Override
    public V get() {
        return this.value;
    }
    
    @Override
    public V get(final long timeout, final TimeUnit unit) {
        return this.value;
    }
}
