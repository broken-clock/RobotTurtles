// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.support;

import java.util.Date;
import org.springframework.scheduling.TriggerContext;

public class SimpleTriggerContext implements TriggerContext
{
    private volatile Date lastScheduledExecutionTime;
    private volatile Date lastActualExecutionTime;
    private volatile Date lastCompletionTime;
    
    public SimpleTriggerContext() {
    }
    
    public SimpleTriggerContext(final Date lastScheduledExecutionTime, final Date lastActualExecutionTime, final Date lastCompletionTime) {
        this.lastScheduledExecutionTime = lastScheduledExecutionTime;
        this.lastActualExecutionTime = lastActualExecutionTime;
        this.lastCompletionTime = lastCompletionTime;
    }
    
    public void update(final Date lastScheduledExecutionTime, final Date lastActualExecutionTime, final Date lastCompletionTime) {
        this.lastScheduledExecutionTime = lastScheduledExecutionTime;
        this.lastActualExecutionTime = lastActualExecutionTime;
        this.lastCompletionTime = lastCompletionTime;
    }
    
    @Override
    public Date lastScheduledExecutionTime() {
        return this.lastScheduledExecutionTime;
    }
    
    @Override
    public Date lastActualExecutionTime() {
        return this.lastActualExecutionTime;
    }
    
    @Override
    public Date lastCompletionTime() {
        return this.lastCompletionTime;
    }
}
