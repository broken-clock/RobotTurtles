// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.config;

import java.util.ArrayList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.util.Assert;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.aop.aspectj.autoproxy.AspectJAwareAdvisorAutoProxyCreator;
import org.springframework.aop.framework.autoproxy.InfrastructureAdvisorAutoProxyCreator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import java.util.List;

public abstract class AopConfigUtils
{
    public static final String AUTO_PROXY_CREATOR_BEAN_NAME = "org.springframework.aop.config.internalAutoProxyCreator";
    private static final List<Class<?>> APC_PRIORITY_LIST;
    
    public static BeanDefinition registerAutoProxyCreatorIfNecessary(final BeanDefinitionRegistry registry) {
        return registerAutoProxyCreatorIfNecessary(registry, null);
    }
    
    public static BeanDefinition registerAutoProxyCreatorIfNecessary(final BeanDefinitionRegistry registry, final Object source) {
        return registerOrEscalateApcAsRequired(InfrastructureAdvisorAutoProxyCreator.class, registry, source);
    }
    
    public static BeanDefinition registerAspectJAutoProxyCreatorIfNecessary(final BeanDefinitionRegistry registry) {
        return registerAspectJAutoProxyCreatorIfNecessary(registry, null);
    }
    
    public static BeanDefinition registerAspectJAutoProxyCreatorIfNecessary(final BeanDefinitionRegistry registry, final Object source) {
        return registerOrEscalateApcAsRequired(AspectJAwareAdvisorAutoProxyCreator.class, registry, source);
    }
    
    public static BeanDefinition registerAspectJAnnotationAutoProxyCreatorIfNecessary(final BeanDefinitionRegistry registry) {
        return registerAspectJAnnotationAutoProxyCreatorIfNecessary(registry, null);
    }
    
    public static BeanDefinition registerAspectJAnnotationAutoProxyCreatorIfNecessary(final BeanDefinitionRegistry registry, final Object source) {
        return registerOrEscalateApcAsRequired(AnnotationAwareAspectJAutoProxyCreator.class, registry, source);
    }
    
    public static void forceAutoProxyCreatorToUseClassProxying(final BeanDefinitionRegistry registry) {
        if (registry.containsBeanDefinition("org.springframework.aop.config.internalAutoProxyCreator")) {
            final BeanDefinition definition = registry.getBeanDefinition("org.springframework.aop.config.internalAutoProxyCreator");
            definition.getPropertyValues().add("proxyTargetClass", Boolean.TRUE);
        }
    }
    
    static void forceAutoProxyCreatorToExposeProxy(final BeanDefinitionRegistry registry) {
        if (registry.containsBeanDefinition("org.springframework.aop.config.internalAutoProxyCreator")) {
            final BeanDefinition definition = registry.getBeanDefinition("org.springframework.aop.config.internalAutoProxyCreator");
            definition.getPropertyValues().add("exposeProxy", Boolean.TRUE);
        }
    }
    
    private static BeanDefinition registerOrEscalateApcAsRequired(final Class<?> cls, final BeanDefinitionRegistry registry, final Object source) {
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
        if (registry.containsBeanDefinition("org.springframework.aop.config.internalAutoProxyCreator")) {
            final BeanDefinition apcDefinition = registry.getBeanDefinition("org.springframework.aop.config.internalAutoProxyCreator");
            if (!cls.getName().equals(apcDefinition.getBeanClassName())) {
                final int currentPriority = findPriorityForClass(apcDefinition.getBeanClassName());
                final int requiredPriority = findPriorityForClass(cls);
                if (currentPriority < requiredPriority) {
                    apcDefinition.setBeanClassName(cls.getName());
                }
            }
            return null;
        }
        final RootBeanDefinition beanDefinition = new RootBeanDefinition(cls);
        beanDefinition.setSource(source);
        beanDefinition.getPropertyValues().add("order", Integer.MIN_VALUE);
        beanDefinition.setRole(2);
        registry.registerBeanDefinition("org.springframework.aop.config.internalAutoProxyCreator", beanDefinition);
        return beanDefinition;
    }
    
    private static int findPriorityForClass(final Class<?> clazz) {
        return AopConfigUtils.APC_PRIORITY_LIST.indexOf(clazz);
    }
    
    private static int findPriorityForClass(final String className) {
        for (int i = 0; i < AopConfigUtils.APC_PRIORITY_LIST.size(); ++i) {
            final Class<?> clazz = AopConfigUtils.APC_PRIORITY_LIST.get(i);
            if (clazz.getName().equals(className)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Class name [" + className + "] is not a known auto-proxy creator class");
    }
    
    static {
        (APC_PRIORITY_LIST = new ArrayList<Class<?>>()).add(InfrastructureAdvisorAutoProxyCreator.class);
        AopConfigUtils.APC_PRIORITY_LIST.add(AspectJAwareAdvisorAutoProxyCreator.class);
        AopConfigUtils.APC_PRIORITY_LIST.add(AnnotationAwareAspectJAutoProxyCreator.class);
    }
}
