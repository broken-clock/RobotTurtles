// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import java.io.IOException;
import org.springframework.core.io.support.ResourcePatternResolver;
import java.util.Set;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.core.io.Resource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.Assert;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.apache.commons.logging.Log;
import org.springframework.core.env.EnvironmentCapable;

public abstract class AbstractBeanDefinitionReader implements EnvironmentCapable, BeanDefinitionReader
{
    protected final Log logger;
    private final BeanDefinitionRegistry registry;
    private ResourceLoader resourceLoader;
    private ClassLoader beanClassLoader;
    private Environment environment;
    private BeanNameGenerator beanNameGenerator;
    
    protected AbstractBeanDefinitionReader(final BeanDefinitionRegistry registry) {
        this.logger = LogFactory.getLog(this.getClass());
        this.beanNameGenerator = new DefaultBeanNameGenerator();
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
        this.registry = registry;
        if (this.registry instanceof ResourceLoader) {
            this.resourceLoader = (ResourceLoader)this.registry;
        }
        else {
            this.resourceLoader = new PathMatchingResourcePatternResolver();
        }
        if (this.registry instanceof EnvironmentCapable) {
            this.environment = ((EnvironmentCapable)this.registry).getEnvironment();
        }
        else {
            this.environment = new StandardEnvironment();
        }
    }
    
    public final BeanDefinitionRegistry getBeanFactory() {
        return this.registry;
    }
    
    @Override
    public final BeanDefinitionRegistry getRegistry() {
        return this.registry;
    }
    
    public void setResourceLoader(final ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    
    @Override
    public ResourceLoader getResourceLoader() {
        return this.resourceLoader;
    }
    
    public void setBeanClassLoader(final ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }
    
    @Override
    public ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }
    
    public void setEnvironment(final Environment environment) {
        this.environment = environment;
    }
    
    @Override
    public Environment getEnvironment() {
        return this.environment;
    }
    
    public void setBeanNameGenerator(final BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = ((beanNameGenerator != null) ? beanNameGenerator : new DefaultBeanNameGenerator());
    }
    
    @Override
    public BeanNameGenerator getBeanNameGenerator() {
        return this.beanNameGenerator;
    }
    
    @Override
    public int loadBeanDefinitions(final Resource... resources) throws BeanDefinitionStoreException {
        Assert.notNull(resources, "Resource array must not be null");
        int counter = 0;
        for (final Resource resource : resources) {
            counter += this.loadBeanDefinitions(resource);
        }
        return counter;
    }
    
    @Override
    public int loadBeanDefinitions(final String location) throws BeanDefinitionStoreException {
        return this.loadBeanDefinitions(location, null);
    }
    
    public int loadBeanDefinitions(final String location, final Set<Resource> actualResources) throws BeanDefinitionStoreException {
        final ResourceLoader resourceLoader = this.getResourceLoader();
        if (resourceLoader == null) {
            throw new BeanDefinitionStoreException("Cannot import bean definitions from location [" + location + "]: no ResourceLoader available");
        }
        if (resourceLoader instanceof ResourcePatternResolver) {
            try {
                final Resource[] resources = ((ResourcePatternResolver)resourceLoader).getResources(location);
                final int loadCount = this.loadBeanDefinitions(resources);
                if (actualResources != null) {
                    for (final Resource resource : resources) {
                        actualResources.add(resource);
                    }
                }
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Loaded " + loadCount + " bean definitions from location pattern [" + location + "]");
                }
                return loadCount;
            }
            catch (IOException ex) {
                throw new BeanDefinitionStoreException("Could not resolve bean definition resource pattern [" + location + "]", ex);
            }
        }
        final Resource resource2 = resourceLoader.getResource(location);
        final int loadCount = this.loadBeanDefinitions(resource2);
        if (actualResources != null) {
            actualResources.add(resource2);
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Loaded " + loadCount + " bean definitions from location [" + location + "]");
        }
        return loadCount;
    }
    
    @Override
    public int loadBeanDefinitions(final String... locations) throws BeanDefinitionStoreException {
        Assert.notNull(locations, "Location array must not be null");
        int counter = 0;
        for (final String location : locations) {
            counter += this.loadBeanDefinitions(location);
        }
        return counter;
    }
}
