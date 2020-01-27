// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.annotation;

import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.config.BeanDefinitionHolder;

class ScopedProxyCreator
{
    public static BeanDefinitionHolder createScopedProxy(final BeanDefinitionHolder definitionHolder, final BeanDefinitionRegistry registry, final boolean proxyTargetClass) {
        return ScopedProxyUtils.createScopedProxy(definitionHolder, registry, proxyTargetClass);
    }
    
    public static String getTargetBeanName(final String originalBeanName) {
        return ScopedProxyUtils.getTargetBeanName(originalBeanName);
    }
}
