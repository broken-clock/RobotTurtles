// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.access;

import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import java.io.IOException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.FatalBeanException;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import java.util.HashMap;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import java.util.Map;
import org.apache.commons.logging.Log;

public class SingletonBeanFactoryLocator implements BeanFactoryLocator
{
    private static final String DEFAULT_RESOURCE_LOCATION = "classpath*:beanRefFactory.xml";
    protected static final Log logger;
    private static final Map<String, BeanFactoryLocator> instances;
    private final Map<String, BeanFactoryGroup> bfgInstancesByKey;
    private final Map<BeanFactory, BeanFactoryGroup> bfgInstancesByObj;
    private final String resourceLocation;
    
    public static BeanFactoryLocator getInstance() throws BeansException {
        return getInstance(null);
    }
    
    public static BeanFactoryLocator getInstance(final String selector) throws BeansException {
        String resourceLocation = selector;
        if (resourceLocation == null) {
            resourceLocation = "classpath*:beanRefFactory.xml";
        }
        if (!ResourcePatternUtils.isUrl(resourceLocation)) {
            resourceLocation = "classpath*:" + resourceLocation;
        }
        synchronized (SingletonBeanFactoryLocator.instances) {
            if (SingletonBeanFactoryLocator.logger.isTraceEnabled()) {
                SingletonBeanFactoryLocator.logger.trace("SingletonBeanFactoryLocator.getInstance(): instances.hashCode=" + SingletonBeanFactoryLocator.instances.hashCode() + ", instances=" + SingletonBeanFactoryLocator.instances);
            }
            BeanFactoryLocator bfl = SingletonBeanFactoryLocator.instances.get(resourceLocation);
            if (bfl == null) {
                bfl = new SingletonBeanFactoryLocator(resourceLocation);
                SingletonBeanFactoryLocator.instances.put(resourceLocation, bfl);
            }
            return bfl;
        }
    }
    
    protected SingletonBeanFactoryLocator(final String resourceLocation) {
        this.bfgInstancesByKey = new HashMap<String, BeanFactoryGroup>();
        this.bfgInstancesByObj = new HashMap<BeanFactory, BeanFactoryGroup>();
        this.resourceLocation = resourceLocation;
    }
    
    @Override
    public BeanFactoryReference useBeanFactory(final String factoryKey) throws BeansException {
        synchronized (this.bfgInstancesByKey) {
            BeanFactoryGroup bfg = this.bfgInstancesByKey.get(this.resourceLocation);
            if (bfg != null) {
                bfg.refCount++;
            }
            else {
                if (SingletonBeanFactoryLocator.logger.isTraceEnabled()) {
                    SingletonBeanFactoryLocator.logger.trace("Factory group with resource name [" + this.resourceLocation + "] requested. Creating new instance.");
                }
                final BeanFactory groupContext = this.createDefinition(this.resourceLocation, factoryKey);
                bfg = new BeanFactoryGroup();
                bfg.definition = groupContext;
                bfg.refCount = 1;
                this.bfgInstancesByKey.put(this.resourceLocation, bfg);
                this.bfgInstancesByObj.put(groupContext, bfg);
                try {
                    this.initializeDefinition(groupContext);
                }
                catch (BeansException ex) {
                    this.bfgInstancesByKey.remove(this.resourceLocation);
                    this.bfgInstancesByObj.remove(groupContext);
                    throw new BootstrapException("Unable to initialize group definition. Group resource name [" + this.resourceLocation + "], factory key [" + factoryKey + "]", ex);
                }
            }
            try {
                BeanFactory beanFactory;
                if (factoryKey != null) {
                    beanFactory = bfg.definition.getBean(factoryKey, BeanFactory.class);
                }
                else {
                    beanFactory = bfg.definition.getBean(BeanFactory.class);
                }
                return new CountingBeanFactoryReference(beanFactory, bfg.definition);
            }
            catch (BeansException ex2) {
                throw new BootstrapException("Unable to return specified BeanFactory instance: factory key [" + factoryKey + "], from group with resource name [" + this.resourceLocation + "]", ex2);
            }
        }
    }
    
    protected BeanFactory createDefinition(final String resourceLocation, final String factoryKey) {
        final DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        final XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
        final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        try {
            final Resource[] configResources = resourcePatternResolver.getResources(resourceLocation);
            if (configResources.length == 0) {
                throw new FatalBeanException("Unable to find resource for specified definition. Group resource name [" + this.resourceLocation + "], factory key [" + factoryKey + "]");
            }
            reader.loadBeanDefinitions(configResources);
        }
        catch (IOException ex) {
            throw new BeanDefinitionStoreException("Error accessing bean definition resource [" + this.resourceLocation + "]", ex);
        }
        catch (BeanDefinitionStoreException ex2) {
            throw new FatalBeanException("Unable to load group definition: group resource name [" + this.resourceLocation + "], factory key [" + factoryKey + "]", ex2);
        }
        return factory;
    }
    
    protected void initializeDefinition(final BeanFactory groupDef) {
        if (groupDef instanceof ConfigurableListableBeanFactory) {
            ((ConfigurableListableBeanFactory)groupDef).preInstantiateSingletons();
        }
    }
    
    protected void destroyDefinition(final BeanFactory groupDef, final String selector) {
        if (groupDef instanceof ConfigurableBeanFactory) {
            if (SingletonBeanFactoryLocator.logger.isTraceEnabled()) {
                SingletonBeanFactoryLocator.logger.trace("Factory group with selector '" + selector + "' being released, as there are no more references to it");
            }
            ((ConfigurableBeanFactory)groupDef).destroySingletons();
        }
    }
    
    static {
        logger = LogFactory.getLog(SingletonBeanFactoryLocator.class);
        instances = new HashMap<String, BeanFactoryLocator>();
    }
    
    private static class BeanFactoryGroup
    {
        private BeanFactory definition;
        private int refCount;
        
        private BeanFactoryGroup() {
            this.refCount = 0;
        }
    }
    
    private class CountingBeanFactoryReference implements BeanFactoryReference
    {
        private BeanFactory beanFactory;
        private BeanFactory groupContextRef;
        
        public CountingBeanFactoryReference(final BeanFactory beanFactory, final BeanFactory groupContext) {
            this.beanFactory = beanFactory;
            this.groupContextRef = groupContext;
        }
        
        @Override
        public BeanFactory getFactory() {
            return this.beanFactory;
        }
        
        @Override
        public void release() throws FatalBeanException {
            synchronized (SingletonBeanFactoryLocator.this.bfgInstancesByKey) {
                final BeanFactory savedRef = this.groupContextRef;
                if (savedRef != null) {
                    this.groupContextRef = null;
                    final BeanFactoryGroup bfg = SingletonBeanFactoryLocator.this.bfgInstancesByObj.get(savedRef);
                    if (bfg != null) {
                        bfg.refCount--;
                        if (bfg.refCount == 0) {
                            SingletonBeanFactoryLocator.this.destroyDefinition(savedRef, SingletonBeanFactoryLocator.this.resourceLocation);
                            SingletonBeanFactoryLocator.this.bfgInstancesByKey.remove(SingletonBeanFactoryLocator.this.resourceLocation);
                            SingletonBeanFactoryLocator.this.bfgInstancesByObj.remove(savedRef);
                        }
                    }
                    else {
                        SingletonBeanFactoryLocator.logger.warn("Tried to release a SingletonBeanFactoryLocator group definition more times than it has actually been used. Resource name [" + SingletonBeanFactoryLocator.this.resourceLocation + "]");
                    }
                }
            }
        }
    }
}
