// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.env.EnvironmentCapable;

public interface ApplicationContext extends EnvironmentCapable, ListableBeanFactory, HierarchicalBeanFactory, MessageSource, ApplicationEventPublisher, ResourcePatternResolver
{
    String getId();
    
    String getApplicationName();
    
    String getDisplayName();
    
    long getStartupDate();
    
    ApplicationContext getParent();
    
    AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException;
}
