// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.config;

import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.config.BeanDefinition;
import org.w3c.dom.Element;
import org.springframework.beans.factory.xml.ParserContext;

public abstract class AopNamespaceUtils
{
    public static final String PROXY_TARGET_CLASS_ATTRIBUTE = "proxy-target-class";
    private static final String EXPOSE_PROXY_ATTRIBUTE = "expose-proxy";
    
    public static void registerAutoProxyCreatorIfNecessary(final ParserContext parserContext, final Element sourceElement) {
        final BeanDefinition beanDefinition = AopConfigUtils.registerAutoProxyCreatorIfNecessary(parserContext.getRegistry(), parserContext.extractSource(sourceElement));
        useClassProxyingIfNecessary(parserContext.getRegistry(), sourceElement);
        registerComponentIfNecessary(beanDefinition, parserContext);
    }
    
    public static void registerAspectJAutoProxyCreatorIfNecessary(final ParserContext parserContext, final Element sourceElement) {
        final BeanDefinition beanDefinition = AopConfigUtils.registerAspectJAutoProxyCreatorIfNecessary(parserContext.getRegistry(), parserContext.extractSource(sourceElement));
        useClassProxyingIfNecessary(parserContext.getRegistry(), sourceElement);
        registerComponentIfNecessary(beanDefinition, parserContext);
    }
    
    public static void registerAspectJAnnotationAutoProxyCreatorIfNecessary(final ParserContext parserContext, final Element sourceElement) {
        final BeanDefinition beanDefinition = AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(parserContext.getRegistry(), parserContext.extractSource(sourceElement));
        useClassProxyingIfNecessary(parserContext.getRegistry(), sourceElement);
        registerComponentIfNecessary(beanDefinition, parserContext);
    }
    
    private static void useClassProxyingIfNecessary(final BeanDefinitionRegistry registry, final Element sourceElement) {
        if (sourceElement != null) {
            final boolean proxyTargetClass = Boolean.valueOf(sourceElement.getAttribute("proxy-target-class"));
            if (proxyTargetClass) {
                AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry);
            }
            final boolean exposeProxy = Boolean.valueOf(sourceElement.getAttribute("expose-proxy"));
            if (exposeProxy) {
                AopConfigUtils.forceAutoProxyCreatorToExposeProxy(registry);
            }
        }
    }
    
    private static void registerComponentIfNecessary(final BeanDefinition beanDefinition, final ParserContext parserContext) {
        if (beanDefinition != null) {
            final BeanComponentDefinition componentDefinition = new BeanComponentDefinition(beanDefinition, "org.springframework.aop.config.internalAutoProxyCreator");
            parserContext.registerComponent(componentDefinition);
        }
    }
}
