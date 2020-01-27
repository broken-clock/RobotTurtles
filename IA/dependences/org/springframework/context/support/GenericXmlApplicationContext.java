// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.support;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

public class GenericXmlApplicationContext extends GenericApplicationContext
{
    private final XmlBeanDefinitionReader reader;
    
    public GenericXmlApplicationContext() {
        this.reader = new XmlBeanDefinitionReader(this);
    }
    
    public GenericXmlApplicationContext(final Resource... resources) {
        this.reader = new XmlBeanDefinitionReader(this);
        this.load(resources);
        this.refresh();
    }
    
    public GenericXmlApplicationContext(final String... resourceLocations) {
        this.reader = new XmlBeanDefinitionReader(this);
        this.load(resourceLocations);
        this.refresh();
    }
    
    public GenericXmlApplicationContext(final Class<?> relativeClass, final String... resourceNames) {
        this.reader = new XmlBeanDefinitionReader(this);
        this.load(relativeClass, resourceNames);
        this.refresh();
    }
    
    public final XmlBeanDefinitionReader getReader() {
        return this.reader;
    }
    
    public void setValidating(final boolean validating) {
        this.reader.setValidating(validating);
    }
    
    @Override
    public void setEnvironment(final ConfigurableEnvironment environment) {
        super.setEnvironment(environment);
        this.reader.setEnvironment(this.getEnvironment());
    }
    
    public void load(final Resource... resources) {
        this.reader.loadBeanDefinitions(resources);
    }
    
    public void load(final String... resourceLocations) {
        this.reader.loadBeanDefinitions(resourceLocations);
    }
    
    public void load(final Class<?> relativeClass, final String... resourceNames) {
        final Resource[] resources = new Resource[resourceNames.length];
        for (int i = 0; i < resourceNames.length; ++i) {
            resources[i] = new ClassPathResource(resourceNames[i], relativeClass);
        }
        this.load(resources);
    }
}
