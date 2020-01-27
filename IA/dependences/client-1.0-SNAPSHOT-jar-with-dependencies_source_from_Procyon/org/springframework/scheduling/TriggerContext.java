// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling;

import java.util.Date;

public interface TriggerContext
{
    Date lastScheduledExecutionTime();
    
    Date lastActualExecutionTime();
    
    Date lastCompletionTime();
}
