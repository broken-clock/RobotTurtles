// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;

public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor
{
    void postProcessBeanDefinitionRegistry(final BeanDefinitionRegistry p0) throws BeansException;
}
