// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.core.util;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Map;

class ThreadLocalBufferManager
{
    private final Object RELEASE_LOCK;
    private final Map<SoftReference<BufferRecycler>, Boolean> _trackedRecyclers;
    private final ReferenceQueue<BufferRecycler> _refQueue;
    
    ThreadLocalBufferManager() {
        this.RELEASE_LOCK = new Object();
        this._trackedRecyclers = new ConcurrentHashMap<SoftReference<BufferRecycler>, Boolean>();
        this._refQueue = new ReferenceQueue<BufferRecycler>();
    }
    
    public static ThreadLocalBufferManager instance() {
        return ThreadLocalBufferManagerHolder.manager;
    }
    
    public int releaseBuffers() {
        synchronized (this.RELEASE_LOCK) {
            int count = 0;
            this.removeSoftRefsClearedByGc();
            for (final SoftReference<BufferRecycler> ref : this._trackedRecyclers.keySet()) {
                ref.clear();
                ++count;
            }
            this._trackedRecyclers.clear();
            return count;
        }
    }
    
    public SoftReference<BufferRecycler> wrapAndTrack(final BufferRecycler br) {
        final SoftReference<BufferRecycler> newRef = new SoftReference<BufferRecycler>(br, this._refQueue);
        this._trackedRecyclers.put(newRef, true);
        this.removeSoftRefsClearedByGc();
        return newRef;
    }
    
    private void removeSoftRefsClearedByGc() {
        SoftReference<?> clearedSoftRef;
        while ((clearedSoftRef = (SoftReference<?>)(SoftReference)this._refQueue.poll()) != null) {
            this._trackedRecyclers.remove(clearedSoftRef);
        }
    }
    
    private static final class ThreadLocalBufferManagerHolder
    {
        static final ThreadLocalBufferManager manager;
        
        static {
            manager = new ThreadLocalBufferManager();
        }
    }
}
