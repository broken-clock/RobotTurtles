// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import java.io.Serializable;
import org.springframework.util.Assert;
import org.springframework.beans.factory.ObjectFactory;

public class ObjectFactoryCreatingFactoryBean extends AbstractFactoryBean<ObjectFactory<Object>>
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
        return ObjectFactory.class;
    }
    
    @Override
    protected ObjectFactory<Object> createInstance() {
        return new TargetBeanObjectFactory(this.getBeanFactory(), this.targetBeanName);
    }
    
    private static class TargetBeanObjectFactory implements ObjectFactory<Object>, Serializable
    {
        private final BeanFactory beanFactory;
        private final String targetBeanName;
        
        public TargetBeanObjectFactory(final BeanFactory beanFactory, final String targetBeanName) {
            this.beanFactory = beanFactory;
            this.targetBeanName = targetBeanName;
        }
        
        @Override
        public Object getObject() throws BeansException {
            return this.beanFactory.getBean(this.targetBeanName);
        }
    }
}
