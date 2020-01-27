// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.scope;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.aop.framework.autoproxy.AutoProxyUtils;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.config.BeanDefinitionHolder;

public abstract class ScopedProxyUtils
{
    private static final String TARGET_NAME_PREFIX = "scopedTarget.";
    
    public static BeanDefinitionHolder createScopedProxy(final BeanDefinitionHolder definition, final BeanDefinitionRegistry registry, final boolean proxyTargetClass) {
        final String originalBeanName = definition.getBeanName();
        final BeanDefinition targetDefinition = definition.getBeanDefinition();
        final String targetBeanName = getTargetBeanName(originalBeanName);
        final RootBeanDefinition proxyDefinition = new RootBeanDefinition(ScopedProxyFactoryBean.class);
        proxyDefinition.setDecoratedDefinition(new BeanDefinitionHolder(targetDefinition, targetBeanName));
        proxyDefinition.setOriginatingBeanDefinition(targetDefinition);
        proxyDefinition.setSource(definition.getSource());
        proxyDefinition.setRole(targetDefinition.getRole());
        proxyDefinition.getPropertyValues().add("targetBeanName", targetBeanName);
        if (proxyTargetClass) {
            targetDefinition.setAttribute(AutoProxyUtils.PRESERVE_TARGET_CLASS_ATTRIBUTE, Boolean.TRUE);
        }
        else {
            proxyDefinition.getPropertyValues().add("proxyTargetClass", Boolean.FALSE);
        }
        proxyDefinition.setAutowireCandidate(targetDefinition.isAutowireCandidate());
        proxyDefinition.setPrimary(targetDefinition.isPrimary());
        if (targetDefinition instanceof AbstractBeanDefinition) {
            proxyDefinition.copyQualifiersFrom((AbstractBeanDefinition)targetDefinition);
        }
        targetDefinition.setAutowireCandidate(false);
        targetDefinition.setPrimary(false);
        registry.registerBeanDefinition(targetBeanName, targetDefinition);
        return new BeanDefinitionHolder(proxyDefinition, originalBeanName, definition.getAliases());
    }
    
    public static String getTargetBeanName(final String originalBeanName) {
        return "scopedTarget." + originalBeanName;
    }
}
