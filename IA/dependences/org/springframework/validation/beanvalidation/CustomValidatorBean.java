// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.validation.beanvalidation;

import javax.validation.ValidatorContext;
import javax.validation.Validation;
import javax.validation.TraversableResolver;
import javax.validation.MessageInterpolator;
import javax.validation.ValidatorFactory;
import org.springframework.beans.factory.InitializingBean;
import javax.validation.Validator;

public class CustomValidatorBean extends SpringValidatorAdapter implements javax.validation.Validator, InitializingBean
{
    private ValidatorFactory validatorFactory;
    private MessageInterpolator messageInterpolator;
    private TraversableResolver traversableResolver;
    
    public void setValidatorFactory(final ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
    }
    
    public void setMessageInterpolator(final MessageInterpolator messageInterpolator) {
        this.messageInterpolator = messageInterpolator;
    }
    
    public void setTraversableResolver(final TraversableResolver traversableResolver) {
        this.traversableResolver = traversableResolver;
    }
    
    public void afterPropertiesSet() {
        if (this.validatorFactory == null) {
            this.validatorFactory = Validation.buildDefaultValidatorFactory();
        }
        final ValidatorContext validatorContext = this.validatorFactory.usingContext();
        MessageInterpolator targetInterpolator = this.messageInterpolator;
        if (targetInterpolator == null) {
            targetInterpolator = this.validatorFactory.getMessageInterpolator();
        }
        validatorContext.messageInterpolator((MessageInterpolator)new LocaleContextMessageInterpolator(targetInterpolator));
        if (this.traversableResolver != null) {
            validatorContext.traversableResolver(this.traversableResolver);
        }
        this.setTargetValidator(validatorContext.getValidator());
    }
}
