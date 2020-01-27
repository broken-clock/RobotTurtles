// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.config;

import org.springframework.util.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Element;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;

abstract class AbstractPropertyLoadingBeanDefinitionParser extends AbstractSingleBeanDefinitionParser
{
    @Override
    protected boolean shouldGenerateId() {
        return true;
    }
    
    @Override
    protected void doParse(final Element element, final BeanDefinitionBuilder builder) {
        final String location = element.getAttribute("location");
        if (StringUtils.hasLength(location)) {
            final String[] locations = StringUtils.commaDelimitedListToStringArray(location);
            builder.addPropertyValue("locations", locations);
        }
        final String propertiesRef = element.getAttribute("properties-ref");
        if (StringUtils.hasLength(propertiesRef)) {
            builder.addPropertyReference("properties", propertiesRef);
        }
        final String fileEncoding = element.getAttribute("file-encoding");
        if (StringUtils.hasLength(fileEncoding)) {
            builder.addPropertyValue("fileEncoding", fileEncoding);
        }
        final String order = element.getAttribute("order");
        if (StringUtils.hasLength(order)) {
            builder.addPropertyValue("order", Integer.valueOf(order));
        }
        builder.addPropertyValue("ignoreResourceNotFound", Boolean.valueOf(element.getAttribute("ignore-resource-not-found")));
        builder.addPropertyValue("localOverride", Boolean.valueOf(element.getAttribute("local-override")));
        builder.setRole(2);
    }
}
