// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.xml;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

@Deprecated
public class XmlBeanFactory extends DefaultListableBeanFactory
{
    private final XmlBeanDefinitionReader reader;
    
    public XmlBeanFactory(final Resource resource) throws BeansException {
        this(resource, null);
    }
    
    public XmlBeanFactory(final Resource resource, final BeanFactory parentBeanFactory) throws BeansException {
        super(parentBeanFactory);
        (this.reader = new XmlBeanDefinitionReader(this)).loadBeanDefinitions(resource);
    }
}
