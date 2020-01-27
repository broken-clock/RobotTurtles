// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.config;

public class Task
{
    private final Runnable runnable;
    
    public Task(final Runnable runnable) {
        this.runnable = runnable;
    }
    
    public Runnable getRunnable() {
        return this.runnable;
    }
}
