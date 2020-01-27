// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.framework.autoproxy.target;

import org.springframework.beans.factory.config.BeanPostProcessor;
import java.util.Iterator;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.target.AbstractBeanFactoryBasedTargetSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.aop.TargetSource;
import org.springframework.beans.factory.BeanFactory;
import java.util.HashMap;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import java.util.Map;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.aop.framework.autoproxy.TargetSourceCreator;

public abstract class AbstractBeanFactoryBasedTargetSourceCreator implements TargetSourceCreator, BeanFactoryAware, DisposableBean
{
    protected final Log logger;
    private ConfigurableBeanFactory beanFactory;
    private final Map<String, DefaultListableBeanFactory> internalBeanFactories;
    
    public AbstractBeanFactoryBasedTargetSourceCreator() {
        this.logger = LogFactory.getLog(this.getClass());
        this.internalBeanFactories = new HashMap<String, DefaultListableBeanFactory>();
    }
    
    @Override
    public final void setBeanFactory(final BeanFactory beanFactory) {
        if (!(beanFactory instanceof ConfigurableBeanFactory)) {
            throw new IllegalStateException("Cannot do auto-TargetSource creation with a BeanFactory that doesn't implement ConfigurableBeanFactory: " + beanFactory.getClass());
        }
        this.beanFactory = (ConfigurableBeanFactory)beanFactory;
    }
    
    protected final BeanFactory getBeanFactory() {
        return this.beanFactory;
    }
    
    @Override
    public final TargetSource getTargetSource(final Class<?> beanClass, final String beanName) {
        final AbstractBeanFactoryBasedTargetSource targetSource = this.createBeanFactoryBasedTargetSource(beanClass, beanName);
        if (targetSource == null) {
            return null;
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Configuring AbstractBeanFactoryBasedTargetSource: " + targetSource);
        }
        final DefaultListableBeanFactory internalBeanFactory = this.getInternalBeanFactoryForBean(beanName);
        final BeanDefinition bd = this.beanFactory.getMergedBeanDefinition(beanName);
        final GenericBeanDefinition bdCopy = new GenericBeanDefinition(bd);
        if (this.isPrototypeBased()) {
            bdCopy.setScope("prototype");
        }
        internalBeanFactory.registerBeanDefinition(beanName, bdCopy);
        targetSource.setTargetBeanName(beanName);
        targetSource.setBeanFactory(internalBeanFactory);
        return targetSource;
    }
    
    protected DefaultListableBeanFactory getInternalBeanFactoryForBean(final String beanName) {
        synchronized (this.internalBeanFactories) {
            DefaultListableBeanFactory internalBeanFactory = this.internalBeanFactories.get(beanName);
            if (internalBeanFactory == null) {
                internalBeanFactory = this.buildInternalBeanFactory(this.beanFactory);
                this.internalBeanFactories.put(beanName, internalBeanFactory);
            }
            return internalBeanFactory;
        }
    }
    
    protected DefaultListableBeanFactory buildInternalBeanFactory(final ConfigurableBeanFactory containingFactory) {
        final DefaultListableBeanFactory internalBeanFactory = new DefaultListableBeanFactory(containingFactory);
        internalBeanFactory.copyConfigurationFrom(containingFactory);
        final Iterator<BeanPostProcessor> it = internalBeanFactory.getBeanPostProcessors().iterator();
        while (it.hasNext()) {
            if (it.next() instanceof AopInfrastructureBean) {
                it.remove();
            }
        }
        return internalBeanFactory;
    }
    
    @Override
    public void destroy() {
        synchronized (this.internalBeanFactories) {
            for (final DefaultListableBeanFactory bf : this.internalBeanFactories.values()) {
                bf.destroySingletons();
            }
        }
    }
    
    protected boolean isPrototypeBased() {
        return true;
    }
    
    protected abstract AbstractBeanFactoryBasedTargetSource createBeanFactoryBasedTargetSource(final Class<?> p0, final String p1);
}
