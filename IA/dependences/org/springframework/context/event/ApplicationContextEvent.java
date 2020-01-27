// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.event;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

public abstract class ApplicationContextEvent extends ApplicationEvent
{
    public ApplicationContextEvent(final ApplicationContext source) {
        super(source);
    }
    
    public final ApplicationContext getApplicationContext() {
        return (ApplicationContext)this.getSource();
    }
}
