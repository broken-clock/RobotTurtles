// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.access;

import java.util.HashMap;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import java.util.Map;
import org.springframework.beans.factory.access.SingletonBeanFactoryLocator;

public class ContextSingletonBeanFactoryLocator extends SingletonBeanFactoryLocator
{
    private static final String DEFAULT_RESOURCE_LOCATION = "classpath*:beanRefContext.xml";
    private static final Map<String, BeanFactoryLocator> instances;
    
    public static BeanFactoryLocator getInstance() throws BeansException {
        return getInstance(null);
    }
    
    public static BeanFactoryLocator getInstance(final String selector) throws BeansException {
        String resourceLocation = selector;
        if (resourceLocation == null) {
            resourceLocation = "classpath*:beanRefContext.xml";
        }
        if (!ResourcePatternUtils.isUrl(resourceLocation)) {
            resourceLocation = "classpath*:" + resourceLocation;
        }
        synchronized (ContextSingletonBeanFactoryLocator.instances) {
            if (ContextSingletonBeanFactoryLocator.logger.isTraceEnabled()) {
                ContextSingletonBeanFactoryLocator.logger.trace("ContextSingletonBeanFactoryLocator.getInstance(): instances.hashCode=" + ContextSingletonBeanFactoryLocator.instances.hashCode() + ", instances=" + ContextSingletonBeanFactoryLocator.instances);
            }
            BeanFactoryLocator bfl = ContextSingletonBeanFactoryLocator.instances.get(resourceLocation);
            if (bfl == null) {
                bfl = new ContextSingletonBeanFactoryLocator(resourceLocation);
                ContextSingletonBeanFactoryLocator.instances.put(resourceLocation, bfl);
            }
            return bfl;
        }
    }
    
    protected ContextSingletonBeanFactoryLocator(final String resourceLocation) {
        super(resourceLocation);
    }
    
    @Override
    protected BeanFactory createDefinition(final String resourceLocation, final String factoryKey) {
        return new ClassPathXmlApplicationContext(new String[] { resourceLocation }, false);
    }
    
    @Override
    protected void initializeDefinition(final BeanFactory groupDef) {
        if (groupDef instanceof ConfigurableApplicationContext) {
            ((ConfigurableApplicationContext)groupDef).refresh();
        }
    }
    
    @Override
    protected void destroyDefinition(final BeanFactory groupDef, final String selector) {
        if (groupDef instanceof ConfigurableApplicationContext) {
            if (ContextSingletonBeanFactoryLocator.logger.isTraceEnabled()) {
                ContextSingletonBeanFactoryLocator.logger.trace("Context group with selector '" + selector + "' being released, as there are no more references to it");
            }
            ((ConfigurableApplicationContext)groupDef).close();
        }
    }
    
    static {
        instances = new HashMap<String, BeanFactoryLocator>();
    }
}
