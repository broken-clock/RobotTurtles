// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

import java.util.HashMap;
import org.apache.commons.logging.LogFactory;
import java.lang.ref.WeakReference;
import java.lang.ref.Reference;
import java.util.Map;
import java.lang.ref.ReferenceQueue;
import org.apache.commons.logging.Log;

public class WeakReferenceMonitor
{
    private static final Log logger;
    private static final ReferenceQueue<Object> handleQueue;
    private static final Map<Reference<?>, ReleaseListener> trackedEntries;
    private static Thread monitoringThread;
    
    public static void monitor(final Object handle, final ReleaseListener listener) {
        if (WeakReferenceMonitor.logger.isDebugEnabled()) {
            WeakReferenceMonitor.logger.debug("Monitoring handle [" + handle + "] with release listener [" + listener + "]");
        }
        final WeakReference<Object> weakRef = new WeakReference<Object>(handle, WeakReferenceMonitor.handleQueue);
        addEntry(weakRef, listener);
    }
    
    private static void addEntry(final Reference<?> ref, final ReleaseListener entry) {
        synchronized (WeakReferenceMonitor.class) {
            WeakReferenceMonitor.trackedEntries.put(ref, entry);
            if (WeakReferenceMonitor.monitoringThread == null) {
                (WeakReferenceMonitor.monitoringThread = new Thread(new MonitoringProcess(), WeakReferenceMonitor.class.getName())).setDaemon(true);
                WeakReferenceMonitor.monitoringThread.start();
            }
        }
    }
    
    private static ReleaseListener removeEntry(final Reference<?> reference) {
        synchronized (WeakReferenceMonitor.class) {
            return WeakReferenceMonitor.trackedEntries.remove(reference);
        }
    }
    
    private static boolean keepMonitoringThreadAlive() {
        synchronized (WeakReferenceMonitor.class) {
            if (!WeakReferenceMonitor.trackedEntries.isEmpty()) {
                return true;
            }
            WeakReferenceMonitor.logger.debug("No entries left to track - stopping reference monitor thread");
            WeakReferenceMonitor.monitoringThread = null;
            return false;
        }
    }
    
    static {
        logger = LogFactory.getLog(WeakReferenceMonitor.class);
        handleQueue = new ReferenceQueue<Object>();
        trackedEntries = new HashMap<Reference<?>, ReleaseListener>();
        WeakReferenceMonitor.monitoringThread = null;
    }
    
    private static class MonitoringProcess implements Runnable
    {
        @Override
        public void run() {
            WeakReferenceMonitor.logger.debug("Starting reference monitor thread");
            while (keepMonitoringThreadAlive()) {
                try {
                    final Reference<?> reference = (Reference<?>)WeakReferenceMonitor.handleQueue.remove();
                    final ReleaseListener entry = removeEntry(reference);
                    if (entry == null) {
                        continue;
                    }
                    try {
                        entry.released();
                    }
                    catch (Throwable ex) {
                        WeakReferenceMonitor.logger.warn("Reference release listener threw exception", ex);
                    }
                    continue;
                }
                catch (InterruptedException ex2) {
                    synchronized (WeakReferenceMonitor.class) {
                        WeakReferenceMonitor.monitoringThread = null;
                    }
                    WeakReferenceMonitor.logger.debug("Reference monitor thread interrupted", ex2);
                }
                break;
            }
        }
    }
    
    public interface ReleaseListener
    {
        void released();
    }
}
