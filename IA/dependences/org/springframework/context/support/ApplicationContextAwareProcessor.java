// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.support;

import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringValueResolver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.BeansException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.beans.factory.config.BeanPostProcessor;

class ApplicationContextAwareProcessor implements BeanPostProcessor
{
    private final ConfigurableApplicationContext applicationContext;
    
    public ApplicationContextAwareProcessor(final ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        AccessControlContext acc = null;
        if (System.getSecurityManager() != null && (bean instanceof EnvironmentAware || bean instanceof EmbeddedValueResolverAware || bean instanceof ResourceLoaderAware || bean instanceof ApplicationEventPublisherAware || bean instanceof MessageSourceAware || bean instanceof ApplicationContextAware)) {
            acc = this.applicationContext.getBeanFactory().getAccessControlContext();
        }
        if (acc != null) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    ApplicationContextAwareProcessor.this.invokeAwareInterfaces(bean);
                    return null;
                }
            }, acc);
        }
        else {
            this.invokeAwareInterfaces(bean);
        }
        return bean;
    }
    
    private void invokeAwareInterfaces(final Object bean) {
        if (bean instanceof Aware) {
            if (bean instanceof EnvironmentAware) {
                ((EnvironmentAware)bean).setEnvironment(this.applicationContext.getEnvironment());
            }
            if (bean instanceof EmbeddedValueResolverAware) {
                ((EmbeddedValueResolverAware)bean).setEmbeddedValueResolver(new EmbeddedValueResolver(this.applicationContext.getBeanFactory()));
            }
            if (bean instanceof ResourceLoaderAware) {
                ((ResourceLoaderAware)bean).setResourceLoader(this.applicationContext);
            }
            if (bean instanceof ApplicationEventPublisherAware) {
                ((ApplicationEventPublisherAware)bean).setApplicationEventPublisher(this.applicationContext);
            }
            if (bean instanceof MessageSourceAware) {
                ((MessageSourceAware)bean).setMessageSource(this.applicationContext);
            }
            if (bean instanceof ApplicationContextAware) {
                ((ApplicationContextAware)bean).setApplicationContext(this.applicationContext);
            }
        }
    }
    
    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) {
        return bean;
    }
    
    private static class EmbeddedValueResolver implements StringValueResolver
    {
        private final ConfigurableBeanFactory beanFactory;
        
        public EmbeddedValueResolver(final ConfigurableBeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }
        
        @Override
        public String resolveStringValue(final String strVal) {
            return this.beanFactory.resolveEmbeddedValue(strVal);
        }
    }
}
