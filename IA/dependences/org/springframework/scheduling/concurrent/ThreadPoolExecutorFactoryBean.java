// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.concurrent;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import java.util.concurrent.ExecutorService;
import org.springframework.beans.factory.FactoryBean;

public class ThreadPoolExecutorFactoryBean extends ExecutorConfigurationSupport implements FactoryBean<ExecutorService>, InitializingBean, DisposableBean
{
    private int corePoolSize;
    private int maxPoolSize;
    private int keepAliveSeconds;
    private boolean allowCoreThreadTimeOut;
    private int queueCapacity;
    private boolean exposeUnconfigurableExecutor;
    private ExecutorService exposedExecutor;
    
    public ThreadPoolExecutorFactoryBean() {
        this.corePoolSize = 1;
        this.maxPoolSize = Integer.MAX_VALUE;
        this.keepAliveSeconds = 60;
        this.allowCoreThreadTimeOut = false;
        this.queueCapacity = Integer.MAX_VALUE;
        this.exposeUnconfigurableExecutor = false;
    }
    
    public void setCorePoolSize(final int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }
    
    public void setMaxPoolSize(final int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }
    
    public void setKeepAliveSeconds(final int keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }
    
    public void setAllowCoreThreadTimeOut(final boolean allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
    }
    
    public void setQueueCapacity(final int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }
    
    public void setExposeUnconfigurableExecutor(final boolean exposeUnconfigurableExecutor) {
        this.exposeUnconfigurableExecutor = exposeUnconfigurableExecutor;
    }
    
    @Override
    protected ExecutorService initializeExecutor(final ThreadFactory threadFactory, final RejectedExecutionHandler rejectedExecutionHandler) {
        final BlockingQueue<Runnable> queue = this.createQueue(this.queueCapacity);
        final ThreadPoolExecutor executor = this.createExecutor(this.corePoolSize, this.maxPoolSize, this.keepAliveSeconds, queue, threadFactory, rejectedExecutionHandler);
        if (this.allowCoreThreadTimeOut) {
            executor.allowCoreThreadTimeOut(true);
        }
        this.exposedExecutor = (this.exposeUnconfigurableExecutor ? Executors.unconfigurableExecutorService(executor) : executor);
        return executor;
    }
    
    protected ThreadPoolExecutor createExecutor(final int corePoolSize, final int maxPoolSize, final int keepAliveSeconds, final BlockingQueue<Runnable> queue, final ThreadFactory threadFactory, final RejectedExecutionHandler rejectedExecutionHandler) {
        return new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveSeconds, TimeUnit.SECONDS, queue, threadFactory, rejectedExecutionHandler);
    }
    
    protected BlockingQueue<Runnable> createQueue(final int queueCapacity) {
        if (queueCapacity > 0) {
            return new LinkedBlockingQueue<Runnable>(queueCapacity);
        }
        return new SynchronousQueue<Runnable>();
    }
    
    @Override
    public ExecutorService getObject() throws Exception {
        return this.exposedExecutor;
    }
    
    @Override
    public Class<? extends ExecutorService> getObjectType() {
        return (this.exposedExecutor != null) ? this.exposedExecutor.getClass() : ExecutorService.class;
    }
    
    @Override
    public boolean isSingleton() {
        return true;
    }
}
