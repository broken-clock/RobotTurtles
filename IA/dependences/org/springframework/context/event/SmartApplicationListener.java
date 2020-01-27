// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.event;

import org.springframework.core.Ordered;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public interface SmartApplicationListener extends ApplicationListener<ApplicationEvent>, Ordered
{
    boolean supportsEventType(final Class<? extends ApplicationEvent> p0);
    
    boolean supportsSourceType(final Class<?> p0);
}
