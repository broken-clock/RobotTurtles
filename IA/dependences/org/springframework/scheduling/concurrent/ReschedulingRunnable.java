// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.concurrent;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.springframework.scheduling.TriggerContext;
import org.springframework.util.ErrorHandler;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.scheduling.Trigger;
import java.util.concurrent.ScheduledFuture;
import org.springframework.scheduling.support.DelegatingErrorHandlingRunnable;

class ReschedulingRunnable extends DelegatingErrorHandlingRunnable implements ScheduledFuture<Object>
{
    private final Trigger trigger;
    private final SimpleTriggerContext triggerContext;
    private final ScheduledExecutorService executor;
    private ScheduledFuture<?> currentFuture;
    private Date scheduledExecutionTime;
    private final Object triggerContextMonitor;
    
    public ReschedulingRunnable(final Runnable delegate, final Trigger trigger, final ScheduledExecutorService executor, final ErrorHandler errorHandler) {
        super(delegate, errorHandler);
        this.triggerContext = new SimpleTriggerContext();
        this.triggerContextMonitor = new Object();
        this.trigger = trigger;
        this.executor = executor;
    }
    
    public ScheduledFuture<?> schedule() {
        synchronized (this.triggerContextMonitor) {
            this.scheduledExecutionTime = this.trigger.nextExecutionTime(this.triggerContext);
            if (this.scheduledExecutionTime == null) {
                return null;
            }
            final long initialDelay = this.scheduledExecutionTime.getTime() - System.currentTimeMillis();
            this.currentFuture = this.executor.schedule(this, initialDelay, TimeUnit.MILLISECONDS);
            return this;
        }
    }
    
    @Override
    public void run() {
        final Date actualExecutionTime = new Date();
        super.run();
        final Date completionTime = new Date();
        synchronized (this.triggerContextMonitor) {
            this.triggerContext.update(this.scheduledExecutionTime, actualExecutionTime, completionTime);
            if (!this.currentFuture.isCancelled()) {
                this.schedule();
            }
        }
    }
    
    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        synchronized (this.triggerContextMonitor) {
            return this.currentFuture.cancel(mayInterruptIfRunning);
        }
    }
    
    @Override
    public boolean isCancelled() {
        synchronized (this.triggerContextMonitor) {
            return this.currentFuture.isCancelled();
        }
    }
    
    @Override
    public boolean isDone() {
        synchronized (this.triggerContextMonitor) {
            return this.currentFuture.isDone();
        }
    }
    
    @Override
    public Object get() throws InterruptedException, ExecutionException {
        final ScheduledFuture<?> curr;
        synchronized (this.triggerContextMonitor) {
            curr = this.currentFuture;
        }
        return curr.get();
    }
    
    @Override
    public Object get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        final ScheduledFuture<?> curr;
        synchronized (this.triggerContextMonitor) {
            curr = this.currentFuture;
        }
        return curr.get(timeout, unit);
    }
    
    @Override
    public long getDelay(final TimeUnit unit) {
        final ScheduledFuture<?> curr;
        synchronized (this.triggerContextMonitor) {
            curr = this.currentFuture;
        }
        return curr.getDelay(unit);
    }
    
    @Override
    public int compareTo(final Delayed other) {
        if (this == other) {
            return 0;
        }
        final long diff = this.getDelay(TimeUnit.MILLISECONDS) - other.getDelay(TimeUnit.MILLISECONDS);
        return (diff == 0L) ? 0 : ((diff < 0L) ? -1 : 1);
    }
}
