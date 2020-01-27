// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util.concurrent;

import org.springframework.util.Assert;
import java.util.LinkedList;
import java.util.Queue;

public class ListenableFutureCallbackRegistry<T>
{
    private final Queue<ListenableFutureCallback<? super T>> callbacks;
    private State state;
    private Object result;
    private final Object mutex;
    
    public ListenableFutureCallbackRegistry() {
        this.callbacks = new LinkedList<ListenableFutureCallback<? super T>>();
        this.state = State.NEW;
        this.result = null;
        this.mutex = new Object();
    }
    
    public void addCallback(final ListenableFutureCallback<? super T> callback) {
        Assert.notNull(callback, "'callback' must not be null");
        synchronized (this.mutex) {
            switch (this.state) {
                case NEW: {
                    this.callbacks.add(callback);
                    break;
                }
                case SUCCESS: {
                    callback.onSuccess((Object)this.result);
                    break;
                }
                case FAILURE: {
                    callback.onFailure((Throwable)this.result);
                    break;
                }
            }
        }
    }
    
    public void success(final T result) {
        synchronized (this.mutex) {
            this.state = State.SUCCESS;
            this.result = result;
            while (!this.callbacks.isEmpty()) {
                this.callbacks.poll().onSuccess((Object)result);
            }
        }
    }
    
    public void failure(final Throwable t) {
        synchronized (this.mutex) {
            this.state = State.FAILURE;
            this.result = t;
            while (!this.callbacks.isEmpty()) {
                this.callbacks.poll().onFailure(t);
            }
        }
    }
    
    private enum State
    {
        NEW, 
        SUCCESS, 
        FAILURE;
    }
}
