// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.concurrent;

import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.SimpleTriggerContext;
import javax.enterprise.concurrent.LastExecution;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import org.springframework.util.ClassUtils;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.util.concurrent.RejectedExecutionException;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.support.TaskUtils;
import java.util.concurrent.ScheduledFuture;
import org.springframework.scheduling.Trigger;
import org.springframework.util.Assert;
import java.util.concurrent.Executors;
import java.util.concurrent.Executor;
import org.springframework.util.ErrorHandler;
import java.util.concurrent.ScheduledExecutorService;
import org.springframework.scheduling.TaskScheduler;

public class ConcurrentTaskScheduler extends ConcurrentTaskExecutor implements TaskScheduler
{
    private static Class<?> managedScheduledExecutorServiceClass;
    private ScheduledExecutorService scheduledExecutor;
    private boolean enterpriseConcurrentScheduler;
    private ErrorHandler errorHandler;
    
    public ConcurrentTaskScheduler() {
        this.enterpriseConcurrentScheduler = false;
        this.setScheduledExecutor(null);
    }
    
    public ConcurrentTaskScheduler(final ScheduledExecutorService scheduledExecutor) {
        super(scheduledExecutor);
        this.enterpriseConcurrentScheduler = false;
        this.setScheduledExecutor(scheduledExecutor);
    }
    
    public ConcurrentTaskScheduler(final Executor concurrentExecutor, final ScheduledExecutorService scheduledExecutor) {
        super(concurrentExecutor);
        this.enterpriseConcurrentScheduler = false;
        this.setScheduledExecutor(scheduledExecutor);
    }
    
    public final void setScheduledExecutor(final ScheduledExecutorService scheduledExecutor) {
        if (scheduledExecutor != null) {
            this.scheduledExecutor = scheduledExecutor;
            this.enterpriseConcurrentScheduler = (ConcurrentTaskScheduler.managedScheduledExecutorServiceClass != null && ConcurrentTaskScheduler.managedScheduledExecutorServiceClass.isInstance(scheduledExecutor));
        }
        else {
            this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
            this.enterpriseConcurrentScheduler = false;
        }
    }
    
    public void setErrorHandler(final ErrorHandler errorHandler) {
        Assert.notNull(errorHandler, "'errorHandler' must not be null");
        this.errorHandler = errorHandler;
    }
    
    @Override
    public ScheduledFuture<?> schedule(final Runnable task, final Trigger trigger) {
        try {
            if (this.enterpriseConcurrentScheduler) {
                return new EnterpriseConcurrentTriggerScheduler().schedule(this.decorateTask(task, true), trigger);
            }
            final ErrorHandler errorHandler = (this.errorHandler != null) ? this.errorHandler : TaskUtils.getDefaultErrorHandler(true);
            return new ReschedulingRunnable(task, trigger, this.scheduledExecutor, errorHandler).schedule();
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + this.scheduledExecutor + "] did not accept task: " + task, ex);
        }
    }
    
    @Override
    public ScheduledFuture<?> schedule(final Runnable task, final Date startTime) {
        final long initialDelay = startTime.getTime() - System.currentTimeMillis();
        try {
            return this.scheduledExecutor.schedule(this.decorateTask(task, false), initialDelay, TimeUnit.MILLISECONDS);
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + this.scheduledExecutor + "] did not accept task: " + task, ex);
        }
    }
    
    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(final Runnable task, final Date startTime, final long period) {
        final long initialDelay = startTime.getTime() - System.currentTimeMillis();
        try {
            return this.scheduledExecutor.scheduleAtFixedRate(this.decorateTask(task, true), initialDelay, period, TimeUnit.MILLISECONDS);
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + this.scheduledExecutor + "] did not accept task: " + task, ex);
        }
    }
    
    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(final Runnable task, final long period) {
        try {
            return this.scheduledExecutor.scheduleAtFixedRate(this.decorateTask(task, true), 0L, period, TimeUnit.MILLISECONDS);
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + this.scheduledExecutor + "] did not accept task: " + task, ex);
        }
    }
    
    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(final Runnable task, final Date startTime, final long delay) {
        final long initialDelay = startTime.getTime() - System.currentTimeMillis();
        try {
            return this.scheduledExecutor.scheduleWithFixedDelay(this.decorateTask(task, true), initialDelay, delay, TimeUnit.MILLISECONDS);
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + this.scheduledExecutor + "] did not accept task: " + task, ex);
        }
    }
    
    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(final Runnable task, final long delay) {
        try {
            return this.scheduledExecutor.scheduleWithFixedDelay(this.decorateTask(task, true), 0L, delay, TimeUnit.MILLISECONDS);
        }
        catch (RejectedExecutionException ex) {
            throw new TaskRejectedException("Executor [" + this.scheduledExecutor + "] did not accept task: " + task, ex);
        }
    }
    
    private Runnable decorateTask(final Runnable task, final boolean isRepeatingTask) {
        Runnable result = TaskUtils.decorateTaskWithErrorHandler(task, this.errorHandler, isRepeatingTask);
        if (this.enterpriseConcurrentScheduler) {
            result = ManagedTaskBuilder.buildManagedTask(result, task.toString());
        }
        return result;
    }
    
    static {
        try {
            ConcurrentTaskScheduler.managedScheduledExecutorServiceClass = ClassUtils.forName("javax.enterprise.concurrent.ManagedScheduledExecutorService", ConcurrentTaskScheduler.class.getClassLoader());
        }
        catch (ClassNotFoundException ex) {
            ConcurrentTaskScheduler.managedScheduledExecutorServiceClass = null;
        }
    }
    
    private class EnterpriseConcurrentTriggerScheduler
    {
        public ScheduledFuture<?> schedule(final Runnable task, final Trigger trigger) {
            final ManagedScheduledExecutorService executor = (ManagedScheduledExecutorService)ConcurrentTaskScheduler.this.scheduledExecutor;
            return (ScheduledFuture<?>)executor.schedule(task, (javax.enterprise.concurrent.Trigger)new javax.enterprise.concurrent.Trigger() {
                public Date getNextRunTime(final LastExecution le, final Date taskScheduledTime) {
                    return trigger.nextExecutionTime((le != null) ? new SimpleTriggerContext(le.getScheduledStart(), le.getRunStart(), le.getRunEnd()) : new SimpleTriggerContext());
                }
                
                public boolean skipRun(final LastExecution lastExecution, final Date scheduledRunTime) {
                    return false;
                }
            });
        }
    }
}
