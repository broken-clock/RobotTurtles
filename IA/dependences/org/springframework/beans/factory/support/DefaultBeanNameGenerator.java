// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import org.springframework.beans.factory.config.BeanDefinition;

public class DefaultBeanNameGenerator implements BeanNameGenerator
{
    @Override
    public String generateBeanName(final BeanDefinition definition, final BeanDefinitionRegistry registry) {
        return BeanDefinitionReaderUtils.generateBeanName(definition, registry);
    }
}
