// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.validation.beanvalidation;

import javax.validation.ConstraintValidator;
import org.springframework.util.Assert;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import javax.validation.ConstraintValidatorFactory;

public class SpringConstraintValidatorFactory implements ConstraintValidatorFactory
{
    private final AutowireCapableBeanFactory beanFactory;
    
    public SpringConstraintValidatorFactory(final AutowireCapableBeanFactory beanFactory) {
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        this.beanFactory = beanFactory;
    }
    
    public <T extends ConstraintValidator<?, ?>> T getInstance(final Class<T> key) {
        return this.beanFactory.createBean(key);
    }
    
    public void releaseInstance(final ConstraintValidator<?, ?> instance) {
        this.beanFactory.destroyBean(instance);
    }
}
