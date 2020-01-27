// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util.concurrent;

import java.util.concurrent.Future;

public interface ListenableFuture<T> extends Future<T>
{
    void addCallback(final ListenableFutureCallback<? super T> p0);
}
