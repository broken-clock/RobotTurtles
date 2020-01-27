// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.config;

import org.springframework.scheduling.Trigger;

public class TriggerTask extends Task
{
    private final Trigger trigger;
    
    public TriggerTask(final Runnable runnable, final Trigger trigger) {
        super(runnable);
        this.trigger = trigger;
    }
    
    public Trigger getTrigger() {
        return this.trigger;
    }
}
