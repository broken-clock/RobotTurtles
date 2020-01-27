// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

public interface TaskScheduler
{
    ScheduledFuture<?> schedule(final Runnable p0, final Trigger p1);
    
    ScheduledFuture<?> schedule(final Runnable p0, final Date p1);
    
    ScheduledFuture<?> scheduleAtFixedRate(final Runnable p0, final Date p1, final long p2);
    
    ScheduledFuture<?> scheduleAtFixedRate(final Runnable p0, final long p1);
    
    ScheduledFuture<?> scheduleWithFixedDelay(final Runnable p0, final Date p1, final long p2);
    
    ScheduledFuture<?> scheduleWithFixedDelay(final Runnable p0, final long p1);
}
