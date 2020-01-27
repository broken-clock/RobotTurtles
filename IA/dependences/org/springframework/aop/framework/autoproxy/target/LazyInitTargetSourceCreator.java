// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop.framework.autoproxy.target;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.aop.target.AbstractBeanFactoryBasedTargetSource;

public class LazyInitTargetSourceCreator extends AbstractBeanFactoryBasedTargetSourceCreator
{
    @Override
    protected boolean isPrototypeBased() {
        return false;
    }
    
    @Override
    protected AbstractBeanFactoryBasedTargetSource createBeanFactoryBasedTargetSource(final Class<?> beanClass, final String beanName) {
        if (this.getBeanFactory() instanceof ConfigurableListableBeanFactory) {
            final BeanDefinition definition = ((ConfigurableListableBeanFactory)this.getBeanFactory()).getBeanDefinition(beanName);
            if (definition.isLazyInit()) {
                return new LazyInitTargetSource();
            }
        }
        return null;
    }
}
