// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.concurrent;

import java.util.concurrent.TimeUnit;

public class ScheduledExecutorTask
{
    private Runnable runnable;
    private long delay;
    private long period;
    private TimeUnit timeUnit;
    private boolean fixedRate;
    
    public ScheduledExecutorTask() {
        this.delay = 0L;
        this.period = -1L;
        this.timeUnit = TimeUnit.MILLISECONDS;
        this.fixedRate = false;
    }
    
    public ScheduledExecutorTask(final Runnable executorTask) {
        this.delay = 0L;
        this.period = -1L;
        this.timeUnit = TimeUnit.MILLISECONDS;
        this.fixedRate = false;
        this.runnable = executorTask;
    }
    
    public ScheduledExecutorTask(final Runnable executorTask, final long delay) {
        this.delay = 0L;
        this.period = -1L;
        this.timeUnit = TimeUnit.MILLISECONDS;
        this.fixedRate = false;
        this.runnable = executorTask;
        this.delay = delay;
    }
    
    public ScheduledExecutorTask(final Runnable executorTask, final long delay, final long period, final boolean fixedRate) {
        this.delay = 0L;
        this.period = -1L;
        this.timeUnit = TimeUnit.MILLISECONDS;
        this.fixedRate = false;
        this.runnable = executorTask;
        this.delay = delay;
        this.period = period;
        this.fixedRate = fixedRate;
    }
    
    public void setRunnable(final Runnable executorTask) {
        this.runnable = executorTask;
    }
    
    public Runnable getRunnable() {
        return this.runnable;
    }
    
    public void setDelay(final long delay) {
        this.delay = delay;
    }
    
    public long getDelay() {
        return this.delay;
    }
    
    public void setPeriod(final long period) {
        this.period = period;
    }
    
    public long getPeriod() {
        return this.period;
    }
    
    public boolean isOneTimeTask() {
        return this.period <= 0L;
    }
    
    public void setTimeUnit(final TimeUnit timeUnit) {
        this.timeUnit = ((timeUnit != null) ? timeUnit : TimeUnit.MILLISECONDS);
    }
    
    public TimeUnit getTimeUnit() {
        return this.timeUnit;
    }
    
    public void setFixedRate(final boolean fixedRate) {
        this.fixedRate = fixedRate;
    }
    
    public boolean isFixedRate() {
        return this.fixedRate;
    }
}
