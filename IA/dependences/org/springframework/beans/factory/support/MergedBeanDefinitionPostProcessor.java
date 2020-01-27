// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import org.springframework.beans.factory.config.BeanPostProcessor;

public interface MergedBeanDefinitionPostProcessor extends BeanPostProcessor
{
    void postProcessMergedBeanDefinition(final RootBeanDefinition p0, final Class<?> p1, final String p2);
}
