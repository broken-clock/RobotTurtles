// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.xml;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.w3c.dom.Element;

public abstract class AbstractSingleBeanDefinitionParser extends AbstractBeanDefinitionParser
{
    @Override
    protected final AbstractBeanDefinition parseInternal(final Element element, final ParserContext parserContext) {
        final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
        final String parentName = this.getParentName(element);
        if (parentName != null) {
            builder.getRawBeanDefinition().setParentName(parentName);
        }
        final Class<?> beanClass = this.getBeanClass(element);
        if (beanClass != null) {
            builder.getRawBeanDefinition().setBeanClass(beanClass);
        }
        else {
            final String beanClassName = this.getBeanClassName(element);
            if (beanClassName != null) {
                builder.getRawBeanDefinition().setBeanClassName(beanClassName);
            }
        }
        builder.getRawBeanDefinition().setSource(parserContext.extractSource(element));
        if (parserContext.isNested()) {
            builder.setScope(parserContext.getContainingBeanDefinition().getScope());
        }
        if (parserContext.isDefaultLazyInit()) {
            builder.setLazyInit(true);
        }
        this.doParse(element, parserContext, builder);
        return builder.getBeanDefinition();
    }
    
    protected String getParentName(final Element element) {
        return null;
    }
    
    protected Class<?> getBeanClass(final Element element) {
        return null;
    }
    
    protected String getBeanClassName(final Element element) {
        return null;
    }
    
    protected void doParse(final Element element, final ParserContext parserContext, final BeanDefinitionBuilder builder) {
        this.doParse(element, builder);
    }
    
    protected void doParse(final Element element, final BeanDefinitionBuilder builder) {
    }
}
