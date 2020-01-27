// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.config;

import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.w3c.dom.Element;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.w3c.dom.Node;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;

class ScopedProxyBeanDefinitionDecorator implements BeanDefinitionDecorator
{
    private static final String PROXY_TARGET_CLASS = "proxy-target-class";
    
    @Override
    public BeanDefinitionHolder decorate(final Node node, final BeanDefinitionHolder definition, final ParserContext parserContext) {
        boolean proxyTargetClass = true;
        if (node instanceof Element) {
            final Element ele = (Element)node;
            if (ele.hasAttribute("proxy-target-class")) {
                proxyTargetClass = Boolean.valueOf(ele.getAttribute("proxy-target-class"));
            }
        }
        final BeanDefinitionHolder holder = ScopedProxyUtils.createScopedProxy(definition, parserContext.getRegistry(), proxyTargetClass);
        final String targetBeanName = ScopedProxyUtils.getTargetBeanName(definition.getBeanName());
        parserContext.getReaderContext().fireComponentRegistered(new BeanComponentDefinition(definition.getBeanDefinition(), targetBeanName));
        return holder;
    }
}
