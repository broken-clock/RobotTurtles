// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.config;

import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.w3c.dom.Element;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class CacheNamespaceHandler extends NamespaceHandlerSupport
{
    static final String CACHE_MANAGER_ATTRIBUTE = "cache-manager";
    static final String DEFAULT_CACHE_MANAGER_BEAN_NAME = "cacheManager";
    
    static String extractCacheManager(final Element element) {
        return element.hasAttribute("cache-manager") ? element.getAttribute("cache-manager") : "cacheManager";
    }
    
    static BeanDefinition parseKeyGenerator(final Element element, final BeanDefinition def) {
        final String name = element.getAttribute("key-generator");
        if (StringUtils.hasText(name)) {
            def.getPropertyValues().add("keyGenerator", new RuntimeBeanReference(name.trim()));
        }
        return def;
    }
    
    @Override
    public void init() {
        this.registerBeanDefinitionParser("annotation-driven", new AnnotationDrivenCacheBeanDefinitionParser());
        this.registerBeanDefinitionParser("advice", new CacheAdviceParser());
    }
}
