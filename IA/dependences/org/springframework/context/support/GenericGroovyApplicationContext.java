// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.support;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;
import groovy.lang.GroovySystem;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import groovy.lang.MetaClass;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader;
import groovy.lang.GroovyObject;

public class GenericGroovyApplicationContext extends GenericApplicationContext implements GroovyObject
{
    private final GroovyBeanDefinitionReader reader;
    private final BeanWrapper contextWrapper;
    private MetaClass metaClass;
    
    public GenericGroovyApplicationContext() {
        this.reader = new GroovyBeanDefinitionReader(this);
        this.contextWrapper = new BeanWrapperImpl(this);
        this.metaClass = GroovySystem.getMetaClassRegistry().getMetaClass((Class)this.getClass());
    }
    
    public GenericGroovyApplicationContext(final Resource... resources) {
        this.reader = new GroovyBeanDefinitionReader(this);
        this.contextWrapper = new BeanWrapperImpl(this);
        this.metaClass = GroovySystem.getMetaClassRegistry().getMetaClass((Class)this.getClass());
        this.load(resources);
        this.refresh();
    }
    
    public GenericGroovyApplicationContext(final String... resourceLocations) {
        this.reader = new GroovyBeanDefinitionReader(this);
        this.contextWrapper = new BeanWrapperImpl(this);
        this.metaClass = GroovySystem.getMetaClassRegistry().getMetaClass((Class)this.getClass());
        this.load(resourceLocations);
        this.refresh();
    }
    
    public GenericGroovyApplicationContext(final Class<?> relativeClass, final String... resourceNames) {
        this.reader = new GroovyBeanDefinitionReader(this);
        this.contextWrapper = new BeanWrapperImpl(this);
        this.metaClass = GroovySystem.getMetaClassRegistry().getMetaClass((Class)this.getClass());
        this.load(relativeClass, resourceNames);
        this.refresh();
    }
    
    public final GroovyBeanDefinitionReader getReader() {
        return this.reader;
    }
    
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
    
    public void setMetaClass(final MetaClass metaClass) {
        this.metaClass = metaClass;
    }
    
    public MetaClass getMetaClass() {
        return this.metaClass;
    }
    
    public Object invokeMethod(final String name, final Object args) {
        return this.metaClass.invokeMethod((Object)this, name, args);
    }
    
    public void setProperty(final String property, final Object newValue) {
        if (newValue instanceof BeanDefinition) {
            this.registerBeanDefinition(property, (BeanDefinition)newValue);
        }
        else {
            this.metaClass.setProperty((Object)this, property, newValue);
        }
    }
    
    public Object getProperty(final String property) {
        if (this.containsBean(property)) {
            return this.getBean(property);
        }
        if (this.contextWrapper.isReadableProperty(property)) {
            return this.contextWrapper.getPropertyValue(property);
        }
        throw new NoSuchBeanDefinitionException(property);
    }
}
