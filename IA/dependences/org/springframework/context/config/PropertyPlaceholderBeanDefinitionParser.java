// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.config;

import org.springframework.util.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.w3c.dom.Element;

class PropertyPlaceholderBeanDefinitionParser extends AbstractPropertyLoadingBeanDefinitionParser
{
    private static final String SYSTEM_PROPERTIES_MODE_ATTRIB = "system-properties-mode";
    private static final String SYSTEM_PROPERTIES_MODE_DEFAULT = "ENVIRONMENT";
    
    @Override
    protected Class<?> getBeanClass(final Element element) {
        if (element.getAttribute("system-properties-mode").equals("ENVIRONMENT")) {
            return PropertySourcesPlaceholderConfigurer.class;
        }
        return PropertyPlaceholderConfigurer.class;
    }
    
    @Override
    protected void doParse(final Element element, final BeanDefinitionBuilder builder) {
        super.doParse(element, builder);
        builder.addPropertyValue("ignoreUnresolvablePlaceholders", Boolean.valueOf(element.getAttribute("ignore-unresolvable")));
        final String systemPropertiesModeName = element.getAttribute("system-properties-mode");
        if (StringUtils.hasLength(systemPropertiesModeName) && !systemPropertiesModeName.equals("ENVIRONMENT")) {
            builder.addPropertyValue("systemPropertiesModeName", "SYSTEM_PROPERTIES_MODE_" + systemPropertiesModeName);
        }
    }
}
