// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.ejb.config;

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;

abstract class AbstractJndiLocatingBeanDefinitionParser extends AbstractSimpleBeanDefinitionParser
{
    public static final String ENVIRONMENT = "environment";
    public static final String ENVIRONMENT_REF = "environment-ref";
    public static final String JNDI_ENVIRONMENT = "jndiEnvironment";
    
    @Override
    protected boolean isEligibleAttribute(final String attributeName) {
        return super.isEligibleAttribute(attributeName) && !"environment-ref".equals(attributeName) && !"lazy-init".equals(attributeName);
    }
    
    @Override
    protected void postProcess(final BeanDefinitionBuilder definitionBuilder, final Element element) {
        final Object envValue = DomUtils.getChildElementValueByTagName(element, "environment");
        if (envValue != null) {
            definitionBuilder.addPropertyValue("jndiEnvironment", envValue);
        }
        else {
            final String envRef = element.getAttribute("environment-ref");
            if (StringUtils.hasLength(envRef)) {
                definitionBuilder.addPropertyValue("jndiEnvironment", new RuntimeBeanReference(envRef));
            }
        }
        final String lazyInit = element.getAttribute("lazy-init");
        if (StringUtils.hasText(lazyInit) && !"default".equals(lazyInit)) {
            definitionBuilder.setLazyInit("true".equals(lazyInit));
        }
    }
}
