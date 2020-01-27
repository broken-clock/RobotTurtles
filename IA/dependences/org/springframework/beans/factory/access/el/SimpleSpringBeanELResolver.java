// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.access.el;

import javax.el.ELContext;
import org.springframework.util.Assert;
import org.springframework.beans.factory.BeanFactory;

public class SimpleSpringBeanELResolver extends SpringBeanELResolver
{
    private final BeanFactory beanFactory;
    
    public SimpleSpringBeanELResolver(final BeanFactory beanFactory) {
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        this.beanFactory = beanFactory;
    }
    
    @Override
    protected BeanFactory getBeanFactory(final ELContext elContext) {
        return this.beanFactory;
    }
}
