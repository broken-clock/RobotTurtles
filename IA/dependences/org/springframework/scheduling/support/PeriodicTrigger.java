// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.support;

import java.util.Date;
import org.springframework.scheduling.TriggerContext;
import org.springframework.util.Assert;
import java.util.concurrent.TimeUnit;
import org.springframework.scheduling.Trigger;

public class PeriodicTrigger implements Trigger
{
    private final long period;
    private final TimeUnit timeUnit;
    private volatile long initialDelay;
    private volatile boolean fixedRate;
    
    public PeriodicTrigger(final long period) {
        this(period, null);
    }
    
    public PeriodicTrigger(final long period, final TimeUnit timeUnit) {
        this.initialDelay = 0L;
        this.fixedRate = false;
        Assert.isTrue(period >= 0L, "period must not be negative");
        this.timeUnit = ((timeUnit != null) ? timeUnit : TimeUnit.MILLISECONDS);
        this.period = this.timeUnit.toMillis(period);
    }
    
    public void setInitialDelay(final long initialDelay) {
        this.initialDelay = this.timeUnit.toMillis(initialDelay);
    }
    
    public void setFixedRate(final boolean fixedRate) {
        this.fixedRate = fixedRate;
    }
    
    @Override
    public Date nextExecutionTime(final TriggerContext triggerContext) {
        if (triggerContext.lastScheduledExecutionTime() == null) {
            return new Date(System.currentTimeMillis() + this.initialDelay);
        }
        if (this.fixedRate) {
            return new Date(triggerContext.lastScheduledExecutionTime().getTime() + this.period);
        }
        return new Date(triggerContext.lastCompletionTime().getTime() + this.period);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PeriodicTrigger)) {
            return false;
        }
        final PeriodicTrigger other = (PeriodicTrigger)obj;
        return this.fixedRate == other.fixedRate && this.initialDelay == other.initialDelay && this.period == other.period;
    }
    
    @Override
    public int hashCode() {
        return (this.fixedRate ? 17 : 29) + (int)(37L * this.period) + (int)(41L * this.initialDelay);
    }
}
