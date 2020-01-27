// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.config;

public class IntervalTask extends Task
{
    private final long interval;
    private final long initialDelay;
    
    public IntervalTask(final Runnable runnable, final long interval, final long initialDelay) {
        super(runnable);
        this.interval = interval;
        this.initialDelay = initialDelay;
    }
    
    public IntervalTask(final Runnable runnable, final long interval) {
        this(runnable, interval, 0L);
    }
    
    public long getInterval() {
        return this.interval;
    }
    
    public long getInitialDelay() {
        return this.initialDelay;
    }
}
