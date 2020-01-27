// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;

public interface BeanFactoryPostProcessor
{
    void postProcessBeanFactory(final ConfigurableListableBeanFactory p0) throws BeansException;
}
