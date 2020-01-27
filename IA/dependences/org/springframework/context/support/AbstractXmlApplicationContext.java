// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.support;

import org.springframework.core.io.Resource;
import java.io.IOException;
import org.springframework.beans.BeansException;
import org.xml.sax.EntityResolver;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

public abstract class AbstractXmlApplicationContext extends AbstractRefreshableConfigApplicationContext
{
    private boolean validating;
    
    public AbstractXmlApplicationContext() {
        this.validating = true;
    }
    
    public AbstractXmlApplicationContext(final ApplicationContext parent) {
        super(parent);
        this.validating = true;
    }
    
    public void setValidating(final boolean validating) {
        this.validating = validating;
    }
    
    @Override
    protected void loadBeanDefinitions(final DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
        final XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        beanDefinitionReader.setEnvironment(this.getEnvironment());
        beanDefinitionReader.setResourceLoader(this);
        beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));
        this.initBeanDefinitionReader(beanDefinitionReader);
        this.loadBeanDefinitions(beanDefinitionReader);
    }
    
    protected void initBeanDefinitionReader(final XmlBeanDefinitionReader reader) {
        reader.setValidating(this.validating);
    }
    
    protected void loadBeanDefinitions(final XmlBeanDefinitionReader reader) throws BeansException, IOException {
        final Resource[] configResources = this.getConfigResources();
        if (configResources != null) {
            reader.loadBeanDefinitions(configResources);
        }
        final String[] configLocations = this.getConfigLocations();
        if (configLocations != null) {
            reader.loadBeanDefinitions(configLocations);
        }
    }
    
    protected Resource[] getConfigResources() {
        return null;
    }
}
