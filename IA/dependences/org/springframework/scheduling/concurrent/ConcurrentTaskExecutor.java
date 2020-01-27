// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.concurrent;

import javax.enterprise.concurrent.ManagedTaskListener;
import java.util.Map;
import javax.enterprise.concurrent.ManagedExecutors;
import org.springframework.scheduling.SchedulingAwareRunnable;
import java.util.HashMap;
import org.springframework.util.ClassUtils;
import org.springframework.util.concurrent.ListenableFuture;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.Executors;
import org.springframework.core.task.support.TaskExecutorAdapter;
import java.util.concurrent.Executor;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.core.task.AsyncListenableTaskExecutor;

public class ConcurrentTaskExecutor implements AsyncListenableTaskExecutor, SchedulingTaskExecutor
{
    private static Class<?> managedExecutorServiceClass;
    private Executor concurrentExecutor;
    private TaskExecutorAdapter adaptedExecutor;
    
    public ConcurrentTaskExecutor() {
        this.setConcurrentExecutor(null);
    }
    
    public ConcurrentTaskExecutor(final Executor concurrentExecutor) {
        this.setConcurrentExecutor(concurrentExecutor);
    }
    
    public final void setConcurrentExecutor(final Executor concurrentExecutor) {
        if (concurrentExecutor != null) {
            this.concurrentExecutor = concurrentExecutor;
            if (ConcurrentTaskExecutor.managedExecutorServiceClass != null && ConcurrentTaskExecutor.managedExecutorServiceClass.isInstance(concurrentExecutor)) {
                this.adaptedExecutor = new ManagedTaskExecutorAdapter(concurrentExecutor);
            }
            else {
                this.adaptedExecutor = new TaskExecutorAdapter(concurrentExecutor);
            }
        }
        else {
            this.concurrentExecutor = Executors.newSingleThreadExecutor();
            this.adaptedExecutor = new TaskExecutorAdapter(this.concurrentExecutor);
        }
    }
    
    public final Executor getConcurrentExecutor() {
        return this.concurrentExecutor;
    }
    
    @Override
    public void execute(final Runnable task) {
        this.adaptedExecutor.execute(task);
    }
    
    @Override
    public void execute(final Runnable task, final long startTimeout) {
        this.adaptedExecutor.execute(task, startTimeout);
    }
    
    @Override
    public Future<?> submit(final Runnable task) {
        return this.adaptedExecutor.submit(task);
    }
    
    @Override
    public <T> Future<T> submit(final Callable<T> task) {
        return this.adaptedExecutor.submit(task);
    }
    
    @Override
    public ListenableFuture<?> submitListenable(final Runnable task) {
        return this.adaptedExecutor.submitListenable(task);
    }
    
    @Override
    public <T> ListenableFuture<T> submitListenable(final Callable<T> task) {
        return this.adaptedExecutor.submitListenable(task);
    }
    
    @Override
    public boolean prefersShortLivedTasks() {
        return true;
    }
    
    static {
        try {
            ConcurrentTaskExecutor.managedExecutorServiceClass = ClassUtils.forName("javax.enterprise.concurrent.ManagedExecutorService", ConcurrentTaskScheduler.class.getClassLoader());
        }
        catch (ClassNotFoundException ex) {
            ConcurrentTaskExecutor.managedExecutorServiceClass = null;
        }
    }
    
    private static class ManagedTaskExecutorAdapter extends TaskExecutorAdapter
    {
        public ManagedTaskExecutorAdapter(final Executor concurrentExecutor) {
            super(concurrentExecutor);
        }
        
        @Override
        public void execute(final Runnable task) {
            super.execute(ManagedTaskBuilder.buildManagedTask(task, task.toString()));
        }
        
        @Override
        public Future<?> submit(final Runnable task) {
            return super.submit(ManagedTaskBuilder.buildManagedTask(task, task.toString()));
        }
        
        @Override
        public <T> Future<T> submit(final Callable<T> task) {
            return super.submit((Callable<T>)ManagedTaskBuilder.buildManagedTask((Callable<T>)task, task.toString()));
        }
        
        @Override
        public ListenableFuture<?> submitListenable(final Runnable task) {
            return super.submitListenable(ManagedTaskBuilder.buildManagedTask(task, task.toString()));
        }
        
        @Override
        public <T> ListenableFuture<T> submitListenable(final Callable<T> task) {
            return super.submitListenable((Callable<T>)ManagedTaskBuilder.buildManagedTask((Callable<T>)task, task.toString()));
        }
    }
    
    protected static class ManagedTaskBuilder
    {
        public static Runnable buildManagedTask(final Runnable task, final String identityName) {
            final Map<String, String> properties = new HashMap<String, String>(2);
            if (task instanceof SchedulingAwareRunnable) {
                properties.put("javax.enterprise.concurrent.LONGRUNNING_HINT", Boolean.toString(((SchedulingAwareRunnable)task).isLongLived()));
            }
            properties.put("javax.enterprise.concurrent.IDENTITY_NAME", identityName);
            return ManagedExecutors.managedTask(task, (Map)properties, (ManagedTaskListener)null);
        }
        
        public static <T> Callable<T> buildManagedTask(final Callable<T> task, final String identityName) {
            final Map<String, String> properties = new HashMap<String, String>(1);
            properties.put("javax.enterprise.concurrent.IDENTITY_NAME", identityName);
            return (Callable<T>)ManagedExecutors.managedTask((Callable)task, (Map)properties, (ManagedTaskListener)null);
        }
    }
}
