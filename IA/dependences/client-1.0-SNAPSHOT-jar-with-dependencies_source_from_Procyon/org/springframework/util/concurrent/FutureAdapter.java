// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util.concurrent;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import org.springframework.util.Assert;
import java.util.concurrent.Future;

public abstract class FutureAdapter<T, S> implements Future<T>
{
    private final Future<S> adaptee;
    private Object result;
    private State state;
    private final Object mutex;
    
    protected FutureAdapter(final Future<S> adaptee) {
        this.result = null;
        this.state = State.NEW;
        this.mutex = new Object();
        Assert.notNull(adaptee, "'delegate' must not be null");
        this.adaptee = adaptee;
    }
    
    protected Future<S> getAdaptee() {
        return this.adaptee;
    }
    
    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        return this.adaptee.cancel(mayInterruptIfRunning);
    }
    
    @Override
    public boolean isCancelled() {
        return this.adaptee.isCancelled();
    }
    
    @Override
    public boolean isDone() {
        return this.adaptee.isDone();
    }
    
    @Override
    public T get() throws InterruptedException, ExecutionException {
        return this.adaptInternal(this.adaptee.get());
    }
    
    @Override
    public T get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.adaptInternal(this.adaptee.get(timeout, unit));
    }
    
    final T adaptInternal(final S adapteeResult) throws ExecutionException {
        synchronized (this.mutex) {
            switch (this.state) {
                case SUCCESS: {
                    return (T)this.result;
                }
                case FAILURE: {
                    throw (ExecutionException)this.result;
                }
                case NEW: {
                    try {
                        final T adapted = this.adapt(adapteeResult);
                        this.result = adapted;
                        this.state = State.SUCCESS;
                        return adapted;
                    }
                    catch (ExecutionException ex) {
                        this.result = ex;
                        this.state = State.FAILURE;
                        throw ex;
                    }
                    break;
                }
            }
            throw new IllegalStateException();
        }
    }
    
    protected abstract T adapt(final S p0) throws ExecutionException;
    
    private enum State
    {
        NEW, 
        SUCCESS, 
        FAILURE;
    }
}
