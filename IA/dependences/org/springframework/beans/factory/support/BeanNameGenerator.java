// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import org.springframework.beans.factory.config.BeanDefinition;

public interface BeanNameGenerator
{
    String generateBeanName(final BeanDefinition p0, final BeanDefinitionRegistry p1);
}
