// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.validation.beanvalidation;

import java.util.Iterator;
import java.util.Set;
import org.springframework.beans.factory.BeanInitializationException;
import javax.validation.ConstraintViolation;
import org.springframework.beans.BeansException;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.Validator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class BeanValidationPostProcessor implements BeanPostProcessor, InitializingBean
{
    private Validator validator;
    private boolean afterInitialization;
    
    public BeanValidationPostProcessor() {
        this.afterInitialization = false;
    }
    
    public void setValidator(final Validator validator) {
        this.validator = validator;
    }
    
    public void setValidatorFactory(final ValidatorFactory validatorFactory) {
        this.validator = validatorFactory.getValidator();
    }
    
    public void setAfterInitialization(final boolean afterInitialization) {
        this.afterInitialization = afterInitialization;
    }
    
    @Override
    public void afterPropertiesSet() {
        if (this.validator == null) {
            this.validator = Validation.buildDefaultValidatorFactory().getValidator();
        }
    }
    
    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        if (!this.afterInitialization) {
            this.doValidate(bean);
        }
        return bean;
    }
    
    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        if (this.afterInitialization) {
            this.doValidate(bean);
        }
        return bean;
    }
    
    protected void doValidate(final Object bean) {
        final Set<ConstraintViolation<Object>> result = (Set<ConstraintViolation<Object>>)this.validator.validate(bean, new Class[0]);
        if (!result.isEmpty()) {
            final StringBuilder sb = new StringBuilder("Bean state is invalid: ");
            final Iterator<ConstraintViolation<Object>> it = result.iterator();
            while (it.hasNext()) {
                final ConstraintViolation<Object> violation = it.next();
                sb.append(violation.getPropertyPath()).append(" - ").append(violation.getMessage());
                if (it.hasNext()) {
                    sb.append("; ");
                }
            }
            throw new BeanInitializationException(sb.toString());
        }
    }
}
