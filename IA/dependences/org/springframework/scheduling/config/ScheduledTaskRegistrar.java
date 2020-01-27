// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.config;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.Iterator;
import java.util.ArrayList;
import org.springframework.scheduling.Trigger;
import java.util.Map;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.util.Assert;
import java.util.LinkedHashSet;
import java.util.concurrent.ScheduledFuture;
import java.util.Set;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class ScheduledTaskRegistrar implements InitializingBean, DisposableBean
{
    private TaskScheduler taskScheduler;
    private ScheduledExecutorService localExecutor;
    private List<TriggerTask> triggerTasks;
    private List<CronTask> cronTasks;
    private List<IntervalTask> fixedRateTasks;
    private List<IntervalTask> fixedDelayTasks;
    private final Set<ScheduledFuture<?>> scheduledFutures;
    
    public ScheduledTaskRegistrar() {
        this.scheduledFutures = new LinkedHashSet<ScheduledFuture<?>>();
    }
    
    public void setTaskScheduler(final TaskScheduler taskScheduler) {
        Assert.notNull(taskScheduler, "TaskScheduler must not be null");
        this.taskScheduler = taskScheduler;
    }
    
    public void setScheduler(final Object scheduler) {
        Assert.notNull(scheduler, "Scheduler object must not be null");
        if (scheduler instanceof TaskScheduler) {
            this.taskScheduler = (TaskScheduler)scheduler;
        }
        else {
            if (!(scheduler instanceof ScheduledExecutorService)) {
                throw new IllegalArgumentException("Unsupported scheduler type: " + scheduler.getClass());
            }
            this.taskScheduler = new ConcurrentTaskScheduler((ScheduledExecutorService)scheduler);
        }
    }
    
    public TaskScheduler getScheduler() {
        return this.taskScheduler;
    }
    
    public void setTriggerTasks(final Map<Runnable, Trigger> triggerTasks) {
        this.triggerTasks = new ArrayList<TriggerTask>();
        for (final Map.Entry<Runnable, Trigger> task : triggerTasks.entrySet()) {
            this.triggerTasks.add(new TriggerTask(task.getKey(), task.getValue()));
        }
    }
    
    public void setTriggerTasksList(final List<TriggerTask> triggerTasks) {
        this.triggerTasks = triggerTasks;
    }
    
    public void setCronTasks(final Map<Runnable, String> cronTasks) {
        this.cronTasks = new ArrayList<CronTask>();
        for (final Map.Entry<Runnable, String> task : cronTasks.entrySet()) {
            this.addCronTask(task.getKey(), task.getValue());
        }
    }
    
    public void setCronTasksList(final List<CronTask> cronTasks) {
        this.cronTasks = cronTasks;
    }
    
    public void setFixedRateTasks(final Map<Runnable, Long> fixedRateTasks) {
        this.fixedRateTasks = new ArrayList<IntervalTask>();
        for (final Map.Entry<Runnable, Long> task : fixedRateTasks.entrySet()) {
            this.addFixedRateTask(task.getKey(), task.getValue());
        }
    }
    
    public void setFixedRateTasksList(final List<IntervalTask> fixedRateTasks) {
        this.fixedRateTasks = fixedRateTasks;
    }
    
    public void setFixedDelayTasks(final Map<Runnable, Long> fixedDelayTasks) {
        this.fixedDelayTasks = new ArrayList<IntervalTask>();
        for (final Map.Entry<Runnable, Long> task : fixedDelayTasks.entrySet()) {
            this.addFixedDelayTask(task.getKey(), task.getValue());
        }
    }
    
    public void setFixedDelayTasksList(final List<IntervalTask> fixedDelayTasks) {
        this.fixedDelayTasks = fixedDelayTasks;
    }
    
    public void addTriggerTask(final Runnable task, final Trigger trigger) {
        this.addTriggerTask(new TriggerTask(task, trigger));
    }
    
    public void addTriggerTask(final TriggerTask task) {
        if (this.triggerTasks == null) {
            this.triggerTasks = new ArrayList<TriggerTask>();
        }
        this.triggerTasks.add(task);
    }
    
    public void addCronTask(final Runnable task, final String expression) {
        this.addCronTask(new CronTask(task, expression));
    }
    
    public void addCronTask(final CronTask task) {
        if (this.cronTasks == null) {
            this.cronTasks = new ArrayList<CronTask>();
        }
        this.cronTasks.add(task);
    }
    
    public void addFixedRateTask(final Runnable task, final long period) {
        this.addFixedRateTask(new IntervalTask(task, period, 0L));
    }
    
    public void addFixedRateTask(final IntervalTask task) {
        if (this.fixedRateTasks == null) {
            this.fixedRateTasks = new ArrayList<IntervalTask>();
        }
        this.fixedRateTasks.add(task);
    }
    
    public void addFixedDelayTask(final Runnable task, final long delay) {
        this.addFixedDelayTask(new IntervalTask(task, delay, 0L));
    }
    
    public void addFixedDelayTask(final IntervalTask task) {
        if (this.fixedDelayTasks == null) {
            this.fixedDelayTasks = new ArrayList<IntervalTask>();
        }
        this.fixedDelayTasks.add(task);
    }
    
    public boolean hasTasks() {
        return (this.fixedRateTasks != null && !this.fixedRateTasks.isEmpty()) || (this.fixedDelayTasks != null && !this.fixedDelayTasks.isEmpty()) || (this.cronTasks != null && !this.cronTasks.isEmpty()) || (this.triggerTasks != null && !this.triggerTasks.isEmpty());
    }
    
    @Override
    public void afterPropertiesSet() {
        this.scheduleTasks();
    }
    
    protected void scheduleTasks() {
        final long now = System.currentTimeMillis();
        if (this.taskScheduler == null) {
            this.localExecutor = Executors.newSingleThreadScheduledExecutor();
            this.taskScheduler = new ConcurrentTaskScheduler(this.localExecutor);
        }
        if (this.triggerTasks != null) {
            for (final TriggerTask task : this.triggerTasks) {
                this.scheduledFutures.add(this.taskScheduler.schedule(task.getRunnable(), task.getTrigger()));
            }
        }
        if (this.cronTasks != null) {
            for (final CronTask task2 : this.cronTasks) {
                this.scheduledFutures.add(this.taskScheduler.schedule(task2.getRunnable(), task2.getTrigger()));
            }
        }
        if (this.fixedRateTasks != null) {
            for (final IntervalTask task3 : this.fixedRateTasks) {
                if (task3.getInitialDelay() > 0L) {
                    final Date startTime = new Date(now + task3.getInitialDelay());
                    this.scheduledFutures.add(this.taskScheduler.scheduleAtFixedRate(task3.getRunnable(), startTime, task3.getInterval()));
                }
                else {
                    this.scheduledFutures.add(this.taskScheduler.scheduleAtFixedRate(task3.getRunnable(), task3.getInterval()));
                }
            }
        }
        if (this.fixedDelayTasks != null) {
            for (final IntervalTask task3 : this.fixedDelayTasks) {
                if (task3.getInitialDelay() > 0L) {
                    final Date startTime = new Date(now + task3.getInitialDelay());
                    this.scheduledFutures.add(this.taskScheduler.scheduleWithFixedDelay(task3.getRunnable(), startTime, task3.getInterval()));
                }
                else {
                    this.scheduledFutures.add(this.taskScheduler.scheduleWithFixedDelay(task3.getRunnable(), task3.getInterval()));
                }
            }
        }
    }
    
    @Override
    public void destroy() {
        for (final ScheduledFuture<?> future : this.scheduledFutures) {
            future.cancel(true);
        }
        if (this.localExecutor != null) {
            this.localExecutor.shutdownNow();
        }
    }
}
