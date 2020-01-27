// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class ListenableFutureAdapter<T, S> extends FutureAdapter<T, S> implements ListenableFuture<T>
{
    protected ListenableFutureAdapter(final ListenableFuture<S> adaptee) {
        super(adaptee);
    }
    
    @Override
    public void addCallback(final ListenableFutureCallback<? super T> callback) {
        final ListenableFuture<S> listenableAdaptee = (ListenableFuture<S>)(ListenableFuture)this.getAdaptee();
        listenableAdaptee.addCallback(new ListenableFutureCallback<S>() {
            @Override
            public void onSuccess(final S result) {
                try {
                    callback.onSuccess(ListenableFutureAdapter.this.adaptInternal(result));
                }
                catch (ExecutionException ex) {
                    final Throwable cause = ex.getCause();
                    this.onFailure((cause != null) ? cause : ex);
                }
                catch (Throwable t) {
                    this.onFailure(t);
                }
            }
            
            @Override
            public void onFailure(final Throwable t) {
                callback.onFailure(t);
            }
        });
    }
}
