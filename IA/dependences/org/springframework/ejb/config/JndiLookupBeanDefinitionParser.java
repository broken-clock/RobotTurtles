// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.ejb.config;

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.w3c.dom.Element;

class JndiLookupBeanDefinitionParser extends AbstractJndiLocatingBeanDefinitionParser
{
    public static final String DEFAULT_VALUE = "default-value";
    public static final String DEFAULT_REF = "default-ref";
    public static final String DEFAULT_OBJECT = "defaultObject";
    
    @Override
    protected Class<?> getBeanClass(final Element element) {
        return JndiObjectFactoryBean.class;
    }
    
    @Override
    protected boolean isEligibleAttribute(final String attributeName) {
        return super.isEligibleAttribute(attributeName) && !"default-value".equals(attributeName) && !"default-ref".equals(attributeName);
    }
    
    @Override
    protected void doParse(final Element element, final ParserContext parserContext, final BeanDefinitionBuilder builder) {
        super.doParse(element, parserContext, builder);
        final String defaultValue = element.getAttribute("default-value");
        final String defaultRef = element.getAttribute("default-ref");
        if (StringUtils.hasLength(defaultValue)) {
            if (StringUtils.hasLength(defaultRef)) {
                parserContext.getReaderContext().error("<jndi-lookup> element is only allowed to contain either 'default-value' attribute OR 'default-ref' attribute, not both", element);
            }
            builder.addPropertyValue("defaultObject", defaultValue);
        }
        else if (StringUtils.hasLength(defaultRef)) {
            builder.addPropertyValue("defaultObject", new RuntimeBeanReference(defaultRef));
        }
    }
}
