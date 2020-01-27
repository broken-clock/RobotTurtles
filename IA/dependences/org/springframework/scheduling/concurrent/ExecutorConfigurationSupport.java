// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.commons.logging.LogFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.BeanNameAware;

public abstract class ExecutorConfigurationSupport extends CustomizableThreadFactory implements BeanNameAware, InitializingBean, DisposableBean
{
    protected final Log logger;
    private ThreadFactory threadFactory;
    private boolean threadNamePrefixSet;
    private RejectedExecutionHandler rejectedExecutionHandler;
    private boolean waitForTasksToCompleteOnShutdown;
    private int awaitTerminationSeconds;
    private String beanName;
    private ExecutorService executor;
    
    public ExecutorConfigurationSupport() {
        this.logger = LogFactory.getLog(this.getClass());
        this.threadFactory = this;
        this.threadNamePrefixSet = false;
        this.rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
        this.waitForTasksToCompleteOnShutdown = false;
        this.awaitTerminationSeconds = 0;
    }
    
    public void setThreadFactory(final ThreadFactory threadFactory) {
        this.threadFactory = ((threadFactory != null) ? threadFactory : this);
    }
    
    @Override
    public void setThreadNamePrefix(final String threadNamePrefix) {
        super.setThreadNamePrefix(threadNamePrefix);
        this.threadNamePrefixSet = true;
    }
    
    public void setRejectedExecutionHandler(final RejectedExecutionHandler rejectedExecutionHandler) {
        this.rejectedExecutionHandler = ((rejectedExecutionHandler != null) ? rejectedExecutionHandler : new ThreadPoolExecutor.AbortPolicy());
    }
    
    public void setWaitForTasksToCompleteOnShutdown(final boolean waitForJobsToCompleteOnShutdown) {
        this.waitForTasksToCompleteOnShutdown = waitForJobsToCompleteOnShutdown;
    }
    
    public void setAwaitTerminationSeconds(final int awaitTerminationSeconds) {
        this.awaitTerminationSeconds = awaitTerminationSeconds;
    }
    
    @Override
    public void setBeanName(final String name) {
        this.beanName = name;
    }
    
    @Override
    public void afterPropertiesSet() {
        this.initialize();
    }
    
    public void initialize() {
        if (this.logger.isInfoEnabled()) {
            this.logger.info("Initializing ExecutorService " + ((this.beanName != null) ? (" '" + this.beanName + "'") : ""));
        }
        if (!this.threadNamePrefixSet && this.beanName != null) {
            this.setThreadNamePrefix(this.beanName + "-");
        }
        this.executor = this.initializeExecutor(this.threadFactory, this.rejectedExecutionHandler);
    }
    
    protected abstract ExecutorService initializeExecutor(final ThreadFactory p0, final RejectedExecutionHandler p1);
    
    @Override
    public void destroy() {
        this.shutdown();
    }
    
    public void shutdown() {
        if (this.logger.isInfoEnabled()) {
            this.logger.info("Shutting down ExecutorService" + ((this.beanName != null) ? (" '" + this.beanName + "'") : ""));
        }
        if (this.waitForTasksToCompleteOnShutdown) {
            this.executor.shutdown();
        }
        else {
            this.executor.shutdownNow();
        }
        this.awaitTerminationIfNecessary();
    }
    
    private void awaitTerminationIfNecessary() {
        if (this.awaitTerminationSeconds > 0) {
            try {
                if (!this.executor.awaitTermination(this.awaitTerminationSeconds, TimeUnit.SECONDS) && this.logger.isWarnEnabled()) {
                    this.logger.warn("Timed out while waiting for executor" + ((this.beanName != null) ? (" '" + this.beanName + "'") : "") + " to terminate");
                }
            }
            catch (InterruptedException ex) {
                if (this.logger.isWarnEnabled()) {
                    this.logger.warn("Interrupted while waiting for executor" + ((this.beanName != null) ? (" '" + this.beanName + "'") : "") + " to terminate");
                }
                Thread.currentThread().interrupt();
            }
        }
    }
}
