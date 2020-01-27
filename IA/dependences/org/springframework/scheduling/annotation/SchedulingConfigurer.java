// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.annotation;

import org.springframework.scheduling.config.ScheduledTaskRegistrar;

public interface SchedulingConfigurer
{
    void configureTasks(final ScheduledTaskRegistrar p0);
}
