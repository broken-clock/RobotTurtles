// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util.concurrent;

public interface ListenableFutureCallback<T>
{
    void onSuccess(final T p0);
    
    void onFailure(final Throwable p0);
}
