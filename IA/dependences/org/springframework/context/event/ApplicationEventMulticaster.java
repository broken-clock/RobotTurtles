// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public interface ApplicationEventMulticaster
{
    void addApplicationListener(final ApplicationListener<?> p0);
    
    void addApplicationListenerBean(final String p0);
    
    void removeApplicationListener(final ApplicationListener<?> p0);
    
    void removeApplicationListenerBean(final String p0);
    
    void removeAllListeners();
    
    void multicastEvent(final ApplicationEvent p0);
}
