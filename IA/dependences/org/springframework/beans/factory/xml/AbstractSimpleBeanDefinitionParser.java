// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.xml;

import org.springframework.core.Conventions;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.w3c.dom.Attr;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Element;

public abstract class AbstractSimpleBeanDefinitionParser extends AbstractSingleBeanDefinitionParser
{
    @Override
    protected void doParse(final Element element, final ParserContext parserContext, final BeanDefinitionBuilder builder) {
        final NamedNodeMap attributes = element.getAttributes();
        for (int x = 0; x < attributes.getLength(); ++x) {
            final Attr attribute = (Attr)attributes.item(x);
            if (this.isEligibleAttribute(attribute, parserContext)) {
                final String propertyName = this.extractPropertyName(attribute.getLocalName());
                Assert.state(StringUtils.hasText(propertyName), "Illegal property name returned from 'extractPropertyName(String)': cannot be null or empty.");
                builder.addPropertyValue(propertyName, attribute.getValue());
            }
        }
        this.postProcess(builder, element);
    }
    
    protected boolean isEligibleAttribute(final Attr attribute, final ParserContext parserContext) {
        boolean eligible = this.isEligibleAttribute(attribute);
        if (!eligible) {
            final String fullName = attribute.getName();
            eligible = (!fullName.equals("xmlns") && !fullName.startsWith("xmlns:") && this.isEligibleAttribute(parserContext.getDelegate().getLocalName(attribute)));
        }
        return eligible;
    }
    
    @Deprecated
    protected boolean isEligibleAttribute(final Attr attribute) {
        return false;
    }
    
    protected boolean isEligibleAttribute(final String attributeName) {
        return !"id".equals(attributeName);
    }
    
    protected String extractPropertyName(final String attributeName) {
        return Conventions.attributeNameToPropertyName(attributeName);
    }
    
    protected void postProcess(final BeanDefinitionBuilder beanDefinition, final Element element) {
    }
}
