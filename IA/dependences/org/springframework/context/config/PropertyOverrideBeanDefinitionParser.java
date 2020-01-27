// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.config.PropertyOverrideConfigurer;
import org.w3c.dom.Element;

class PropertyOverrideBeanDefinitionParser extends AbstractPropertyLoadingBeanDefinitionParser
{
    @Override
    protected Class<?> getBeanClass(final Element element) {
        return PropertyOverrideConfigurer.class;
    }
    
    @Override
    protected void doParse(final Element element, final BeanDefinitionBuilder builder) {
        super.doParse(element, builder);
        builder.addPropertyValue("ignoreInvalidKeys", Boolean.valueOf(element.getAttribute("ignore-unresolvable")));
    }
}
