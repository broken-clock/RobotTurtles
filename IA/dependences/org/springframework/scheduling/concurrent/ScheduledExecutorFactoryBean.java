// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.concurrent;

import org.springframework.scheduling.support.DelegatingErrorHandlingRunnable;
import org.springframework.scheduling.support.TaskUtils;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.Executors;
import org.springframework.util.ObjectUtils;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import org.springframework.util.Assert;
import java.util.concurrent.ScheduledExecutorService;
import org.springframework.beans.factory.FactoryBean;

public class ScheduledExecutorFactoryBean extends ExecutorConfigurationSupport implements FactoryBean<ScheduledExecutorService>
{
    private int poolSize;
    private ScheduledExecutorTask[] scheduledExecutorTasks;
    private boolean continueScheduledExecutionAfterException;
    private boolean exposeUnconfigurableExecutor;
    private ScheduledExecutorService exposedExecutor;
    
    public ScheduledExecutorFactoryBean() {
        this.poolSize = 1;
        this.continueScheduledExecutionAfterException = false;
        this.exposeUnconfigurableExecutor = false;
    }
    
    public void setPoolSize(final int poolSize) {
        Assert.isTrue(poolSize > 0, "'poolSize' must be 1 or higher");
        this.poolSize = poolSize;
    }
    
    public void setScheduledExecutorTasks(final ScheduledExecutorTask... scheduledExecutorTasks) {
        this.scheduledExecutorTasks = scheduledExecutorTasks;
    }
    
    public void setContinueScheduledExecutionAfterException(final boolean continueScheduledExecutionAfterException) {
        this.continueScheduledExecutionAfterException = continueScheduledExecutionAfterException;
    }
    
    public void setExposeUnconfigurableExecutor(final boolean exposeUnconfigurableExecutor) {
        this.exposeUnconfigurableExecutor = exposeUnconfigurableExecutor;
    }
    
    @Override
    protected ExecutorService initializeExecutor(final ThreadFactory threadFactory, final RejectedExecutionHandler rejectedExecutionHandler) {
        final ScheduledExecutorService executor = this.createExecutor(this.poolSize, threadFactory, rejectedExecutionHandler);
        if (!ObjectUtils.isEmpty(this.scheduledExecutorTasks)) {
            this.registerTasks(this.scheduledExecutorTasks, executor);
        }
        this.exposedExecutor = (this.exposeUnconfigurableExecutor ? Executors.unconfigurableScheduledExecutorService(executor) : executor);
        return executor;
    }
    
    protected ScheduledExecutorService createExecutor(final int poolSize, final ThreadFactory threadFactory, final RejectedExecutionHandler rejectedExecutionHandler) {
        return new ScheduledThreadPoolExecutor(poolSize, threadFactory, rejectedExecutionHandler);
    }
    
    protected void registerTasks(final ScheduledExecutorTask[] tasks, final ScheduledExecutorService executor) {
        for (final ScheduledExecutorTask task : tasks) {
            final Runnable runnable = this.getRunnableToSchedule(task);
            if (task.isOneTimeTask()) {
                executor.schedule(runnable, task.getDelay(), task.getTimeUnit());
            }
            else if (task.isFixedRate()) {
                executor.scheduleAtFixedRate(runnable, task.getDelay(), task.getPeriod(), task.getTimeUnit());
            }
            else {
                executor.scheduleWithFixedDelay(runnable, task.getDelay(), task.getPeriod(), task.getTimeUnit());
            }
        }
    }
    
    protected Runnable getRunnableToSchedule(final ScheduledExecutorTask task) {
        return this.continueScheduledExecutionAfterException ? new DelegatingErrorHandlingRunnable(task.getRunnable(), TaskUtils.LOG_AND_SUPPRESS_ERROR_HANDLER) : new DelegatingErrorHandlingRunnable(task.getRunnable(), TaskUtils.LOG_AND_PROPAGATE_ERROR_HANDLER);
    }
    
    @Override
    public ScheduledExecutorService getObject() {
        return this.exposedExecutor;
    }
    
    @Override
    public Class<? extends ScheduledExecutorService> getObjectType() {
        return (this.exposedExecutor != null) ? this.exposedExecutor.getClass() : ScheduledExecutorService.class;
    }
    
    @Override
    public boolean isSingleton() {
        return true;
    }
}
