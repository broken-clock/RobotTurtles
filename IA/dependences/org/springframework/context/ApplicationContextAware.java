// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;

public interface ApplicationContextAware extends Aware
{
    void setApplicationContext(final ApplicationContext p0) throws BeansException;
}
