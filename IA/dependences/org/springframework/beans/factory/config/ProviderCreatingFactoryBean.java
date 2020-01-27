// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import java.io.Serializable;
import org.springframework.util.Assert;
import javax.inject.Provider;

public class ProviderCreatingFactoryBean extends AbstractFactoryBean<Provider<Object>>
{
    private String targetBeanName;
    
    public void setTargetBeanName(final String targetBeanName) {
        this.targetBeanName = targetBeanName;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(this.targetBeanName, "Property 'targetBeanName' is required");
        super.afterPropertiesSet();
    }
    
    @Override
    public Class<?> getObjectType() {
        return Provider.class;
    }
    
    @Override
    protected Provider<Object> createInstance() {
        return (Provider<Object>)new TargetBeanProvider(this.getBeanFactory(), this.targetBeanName);
    }
    
    private static class TargetBeanProvider implements Provider<Object>, Serializable
    {
        private final BeanFactory beanFactory;
        private final String targetBeanName;
        
        public TargetBeanProvider(final BeanFactory beanFactory, final String targetBeanName) {
            this.beanFactory = beanFactory;
            this.targetBeanName = targetBeanName;
        }
        
        public Object get() throws BeansException {
            return this.beanFactory.getBean(this.targetBeanName);
        }
    }
}
