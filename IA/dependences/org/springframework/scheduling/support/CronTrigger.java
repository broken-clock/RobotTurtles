// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.support;

import java.util.Date;
import org.springframework.scheduling.TriggerContext;
import java.util.TimeZone;
import org.springframework.scheduling.Trigger;

public class CronTrigger implements Trigger
{
    private final CronSequenceGenerator sequenceGenerator;
    
    public CronTrigger(final String cronExpression) {
        this.sequenceGenerator = new CronSequenceGenerator(cronExpression);
    }
    
    public CronTrigger(final String cronExpression, final TimeZone timeZone) {
        this.sequenceGenerator = new CronSequenceGenerator(cronExpression, timeZone);
    }
    
    @Override
    public Date nextExecutionTime(final TriggerContext triggerContext) {
        Date date = triggerContext.lastCompletionTime();
        if (date != null) {
            final Date scheduled = triggerContext.lastScheduledExecutionTime();
            if (scheduled != null && date.before(scheduled)) {
                date = scheduled;
            }
        }
        else {
            date = new Date();
        }
        return this.sequenceGenerator.next(date);
    }
    
    public String getExpression() {
        return this.sequenceGenerator.getExpression();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || (obj instanceof CronTrigger && this.sequenceGenerator.equals(((CronTrigger)obj).sequenceGenerator));
    }
    
    @Override
    public int hashCode() {
        return this.sequenceGenerator.hashCode();
    }
    
    @Override
    public String toString() {
        return this.sequenceGenerator.toString();
    }
}
