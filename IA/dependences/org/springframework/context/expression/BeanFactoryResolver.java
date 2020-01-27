// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context.expression;

import org.springframework.beans.BeansException;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.util.Assert;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.expression.BeanResolver;

public class BeanFactoryResolver implements BeanResolver
{
    private final BeanFactory beanFactory;
    
    public BeanFactoryResolver(final BeanFactory beanFactory) {
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        this.beanFactory = beanFactory;
    }
    
    @Override
    public Object resolve(final EvaluationContext context, final String beanName) throws AccessException {
        try {
            return this.beanFactory.getBean(beanName);
        }
        catch (BeansException ex) {
            throw new AccessException("Could not resolve bean reference against BeanFactory", ex);
        }
    }
}
