// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.config;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ApplicationContextAware;

public class ContextLifecycleScheduledTaskRegistrar extends ScheduledTaskRegistrar implements ApplicationContextAware, ApplicationListener<ContextRefreshedEvent>
{
    private ApplicationContext applicationContext;
    
    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
    @Override
    public void afterPropertiesSet() {
        if (this.applicationContext == null) {
            this.scheduleTasks();
        }
    }
    
    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (event.getApplicationContext() != this.applicationContext) {
            return;
        }
        this.scheduleTasks();
    }
}
