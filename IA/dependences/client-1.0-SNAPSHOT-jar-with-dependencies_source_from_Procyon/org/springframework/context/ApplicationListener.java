// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context;

import java.util.EventListener;

public interface ApplicationListener<E extends ApplicationEvent> extends EventListener
{
    void onApplicationEvent(final E p0);
}
