// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.config;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.springframework.beans.factory.xml.BeanDefinitionParser;

class AspectJAutoProxyBeanDefinitionParser implements BeanDefinitionParser
{
    @Override
    public BeanDefinition parse(final Element element, final ParserContext parserContext) {
        AopNamespaceUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(parserContext, element);
        this.extendBeanDefinition(element, parserContext);
        return null;
    }
    
    private void extendBeanDefinition(final Element element, final ParserContext parserContext) {
        final BeanDefinition beanDef = parserContext.getRegistry().getBeanDefinition("org.springframework.aop.config.internalAutoProxyCreator");
        if (element.hasChildNodes()) {
            this.addIncludePatterns(element, parserContext, beanDef);
        }
    }
    
    private void addIncludePatterns(final Element element, final ParserContext parserContext, final BeanDefinition beanDef) {
        final ManagedList<TypedStringValue> includePatterns = new ManagedList<TypedStringValue>();
        final NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            final Node node = childNodes.item(i);
            if (node instanceof Element) {
                final Element includeElement = (Element)node;
                final TypedStringValue valueHolder = new TypedStringValue(includeElement.getAttribute("name"));
                valueHolder.setSource(parserContext.extractSource(includeElement));
                includePatterns.add(valueHolder);
            }
        }
        if (!includePatterns.isEmpty()) {
            includePatterns.setSource(parserContext.extractSource(element));
            beanDef.getPropertyValues().add("includePatterns", includePatterns);
        }
    }
}
