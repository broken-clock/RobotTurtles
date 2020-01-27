// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.config;

import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.annotation.AnnotationCacheOperationSource;
import org.springframework.aop.config.AopNamespaceUtils;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.springframework.beans.factory.xml.BeanDefinitionParser;

class AnnotationDrivenCacheBeanDefinitionParser implements BeanDefinitionParser
{
    @Override
    public BeanDefinition parse(final Element element, final ParserContext parserContext) {
        final String mode = element.getAttribute("mode");
        if ("aspectj".equals(mode)) {
            this.registerCacheAspect(element, parserContext);
        }
        else {
            AopAutoProxyConfigurer.configureAutoProxyCreator(element, parserContext);
        }
        return null;
    }
    
    private static void parseCacheManagerProperty(final Element element, final BeanDefinition def) {
        def.getPropertyValues().add("cacheManager", new RuntimeBeanReference(CacheNamespaceHandler.extractCacheManager(element)));
    }
    
    private void registerCacheAspect(final Element element, final ParserContext parserContext) {
        if (!parserContext.getRegistry().containsBeanDefinition("org.springframework.cache.config.internalCacheAspect")) {
            final RootBeanDefinition def = new RootBeanDefinition();
            def.setBeanClassName("org.springframework.cache.aspectj.AnnotationCacheAspect");
            def.setFactoryMethodName("aspectOf");
            parseCacheManagerProperty(element, def);
            CacheNamespaceHandler.parseKeyGenerator(element, def);
            parserContext.registerBeanComponent(new BeanComponentDefinition(def, "org.springframework.cache.config.internalCacheAspect"));
        }
    }
    
    private static class AopAutoProxyConfigurer
    {
        public static void configureAutoProxyCreator(final Element element, final ParserContext parserContext) {
            AopNamespaceUtils.registerAutoProxyCreatorIfNecessary(parserContext, element);
            if (!parserContext.getRegistry().containsBeanDefinition("org.springframework.cache.config.internalCacheAdvisor")) {
                final Object eleSource = parserContext.extractSource(element);
                final RootBeanDefinition sourceDef = new RootBeanDefinition(AnnotationCacheOperationSource.class);
                sourceDef.setSource(eleSource);
                sourceDef.setRole(2);
                final String sourceName = parserContext.getReaderContext().registerWithGeneratedName(sourceDef);
                final RootBeanDefinition interceptorDef = new RootBeanDefinition(CacheInterceptor.class);
                interceptorDef.setSource(eleSource);
                interceptorDef.setRole(2);
                parseCacheManagerProperty(element, interceptorDef);
                CacheNamespaceHandler.parseKeyGenerator(element, interceptorDef);
                interceptorDef.getPropertyValues().add("cacheOperationSources", new RuntimeBeanReference(sourceName));
                final String interceptorName = parserContext.getReaderContext().registerWithGeneratedName(interceptorDef);
                final RootBeanDefinition advisorDef = new RootBeanDefinition(BeanFactoryCacheOperationSourceAdvisor.class);
                advisorDef.setSource(eleSource);
                advisorDef.setRole(2);
                advisorDef.getPropertyValues().add("cacheOperationSource", new RuntimeBeanReference(sourceName));
                advisorDef.getPropertyValues().add("adviceBeanName", interceptorName);
                if (element.hasAttribute("order")) {
                    advisorDef.getPropertyValues().add("order", element.getAttribute("order"));
                }
                parserContext.getRegistry().registerBeanDefinition("org.springframework.cache.config.internalCacheAdvisor", advisorDef);
                final CompositeComponentDefinition compositeDef = new CompositeComponentDefinition(element.getTagName(), eleSource);
                compositeDef.addNestedComponent(new BeanComponentDefinition(sourceDef, sourceName));
                compositeDef.addNestedComponent(new BeanComponentDefinition(interceptorDef, interceptorName));
                compositeDef.addNestedComponent(new BeanComponentDefinition(advisorDef, "org.springframework.cache.config.internalCacheAdvisor"));
                parserContext.registerComponent(compositeDef);
            }
        }
    }
}
