// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.io.Serializable;

public class CustomizableThreadCreator implements Serializable
{
    private String threadNamePrefix;
    private int threadPriority;
    private boolean daemon;
    private ThreadGroup threadGroup;
    private final AtomicInteger threadCount;
    
    public CustomizableThreadCreator() {
        this.threadPriority = 5;
        this.daemon = false;
        this.threadCount = new AtomicInteger(0);
        this.threadNamePrefix = this.getDefaultThreadNamePrefix();
    }
    
    public CustomizableThreadCreator(final String threadNamePrefix) {
        this.threadPriority = 5;
        this.daemon = false;
        this.threadCount = new AtomicInteger(0);
        this.threadNamePrefix = ((threadNamePrefix != null) ? threadNamePrefix : this.getDefaultThreadNamePrefix());
    }
    
    public void setThreadNamePrefix(final String threadNamePrefix) {
        this.threadNamePrefix = ((threadNamePrefix != null) ? threadNamePrefix : this.getDefaultThreadNamePrefix());
    }
    
    public String getThreadNamePrefix() {
        return this.threadNamePrefix;
    }
    
    public void setThreadPriority(final int threadPriority) {
        this.threadPriority = threadPriority;
    }
    
    public int getThreadPriority() {
        return this.threadPriority;
    }
    
    public void setDaemon(final boolean daemon) {
        this.daemon = daemon;
    }
    
    public boolean isDaemon() {
        return this.daemon;
    }
    
    public void setThreadGroupName(final String name) {
        this.threadGroup = new ThreadGroup(name);
    }
    
    public void setThreadGroup(final ThreadGroup threadGroup) {
        this.threadGroup = threadGroup;
    }
    
    public ThreadGroup getThreadGroup() {
        return this.threadGroup;
    }
    
    public Thread createThread(final Runnable runnable) {
        final Thread thread = new Thread(this.getThreadGroup(), runnable, this.nextThreadName());
        thread.setPriority(this.getThreadPriority());
        thread.setDaemon(this.isDaemon());
        return thread;
    }
    
    protected String nextThreadName() {
        return this.getThreadNamePrefix() + this.threadCount.incrementAndGet();
    }
    
    protected String getDefaultThreadNamePrefix() {
        return ClassUtils.getShortName(this.getClass()) + "-";
    }
}
