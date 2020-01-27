// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.ejb.config;

import org.w3c.dom.Element;

class LocalStatelessSessionBeanDefinitionParser extends AbstractJndiLocatingBeanDefinitionParser
{
    @Override
    protected String getBeanClassName(final Element element) {
        return "org.springframework.ejb.access.LocalStatelessSessionProxyFactoryBean";
    }
}
