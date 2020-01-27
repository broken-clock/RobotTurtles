// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context;

import org.springframework.beans.factory.Aware;

public interface ApplicationEventPublisherAware extends Aware
{
    void setApplicationEventPublisher(final ApplicationEventPublisher p0);
}
