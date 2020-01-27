// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.concurrent;

import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import java.util.concurrent.ForkJoinPool;
import org.springframework.beans.factory.FactoryBean;

public class ForkJoinPoolFactoryBean implements FactoryBean<ForkJoinPool>, InitializingBean, DisposableBean
{
    private boolean commonPool;
    private int parallelism;
    private ForkJoinPool.ForkJoinWorkerThreadFactory threadFactory;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
    private boolean asyncMode;
    private int awaitTerminationSeconds;
    private ForkJoinPool forkJoinPool;
    
    public ForkJoinPoolFactoryBean() {
        this.commonPool = false;
        this.parallelism = Runtime.getRuntime().availableProcessors();
        this.threadFactory = ForkJoinPool.defaultForkJoinWorkerThreadFactory;
        this.asyncMode = false;
        this.awaitTerminationSeconds = 0;
    }
    
    public void setCommonPool(final boolean commonPool) {
        this.commonPool = commonPool;
    }
    
    public void setParallelism(final int parallelism) {
        this.parallelism = parallelism;
    }
    
    public void setThreadFactory(final ForkJoinPool.ForkJoinWorkerThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }
    
    public void setUncaughtExceptionHandler(final Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }
    
    public void setAsyncMode(final boolean asyncMode) {
        this.asyncMode = asyncMode;
    }
    
    public void setAwaitTerminationSeconds(final int awaitTerminationSeconds) {
        this.awaitTerminationSeconds = awaitTerminationSeconds;
    }
    
    @Override
    public void afterPropertiesSet() {
        this.forkJoinPool = (this.commonPool ? ForkJoinPool.commonPool() : new ForkJoinPool(this.parallelism, this.threadFactory, this.uncaughtExceptionHandler, this.asyncMode));
    }
    
    @Override
    public ForkJoinPool getObject() {
        return this.forkJoinPool;
    }
    
    @Override
    public Class<?> getObjectType() {
        return ForkJoinPool.class;
    }
    
    @Override
    public boolean isSingleton() {
        return true;
    }
    
    @Override
    public void destroy() {
        this.forkJoinPool.shutdown();
        if (this.awaitTerminationSeconds > 0) {
            try {
                this.forkJoinPool.awaitTermination(this.awaitTerminationSeconds, TimeUnit.SECONDS);
            }
            catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
