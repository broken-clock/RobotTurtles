// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.xml;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.core.Conventions;
import org.w3c.dom.Attr;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.w3c.dom.Node;
import org.springframework.beans.factory.config.BeanDefinition;
import org.w3c.dom.Element;

public class SimplePropertyNamespaceHandler implements NamespaceHandler
{
    private static final String REF_SUFFIX = "-ref";
    
    @Override
    public void init() {
    }
    
    @Override
    public BeanDefinition parse(final Element element, final ParserContext parserContext) {
        parserContext.getReaderContext().error("Class [" + this.getClass().getName() + "] does not support custom elements.", element);
        return null;
    }
    
    @Override
    public BeanDefinitionHolder decorate(final Node node, final BeanDefinitionHolder definition, final ParserContext parserContext) {
        if (node instanceof Attr) {
            final Attr attr = (Attr)node;
            String propertyName = parserContext.getDelegate().getLocalName(attr);
            final String propertyValue = attr.getValue();
            final MutablePropertyValues pvs = definition.getBeanDefinition().getPropertyValues();
            if (pvs.contains(propertyName)) {
                parserContext.getReaderContext().error("Property '" + propertyName + "' is already defined using " + "both <property> and inline syntax. Only one approach may be used per property.", attr);
            }
            if (propertyName.endsWith("-ref")) {
                propertyName = propertyName.substring(0, propertyName.length() - "-ref".length());
                pvs.add(Conventions.attributeNameToPropertyName(propertyName), new RuntimeBeanReference(propertyValue));
            }
            else {
                pvs.add(Conventions.attributeNameToPropertyName(propertyName), propertyValue);
            }
        }
        return definition;
    }
}
