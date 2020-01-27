// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.config;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;

public class CronTask extends TriggerTask
{
    private final String expression;
    
    public CronTask(final Runnable runnable, final String expression) {
        this(runnable, new CronTrigger(expression));
    }
    
    public CronTask(final Runnable runnable, final CronTrigger cronTrigger) {
        super(runnable, cronTrigger);
        this.expression = cronTrigger.getExpression();
    }
    
    public String getExpression() {
        return this.expression;
    }
}
