// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.validation.beanvalidation;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import javax.validation.ValidatorFactory;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import javax.validation.Validator;
import java.lang.annotation.Annotation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.aop.framework.AbstractAdvisingBeanPostProcessor;

public class MethodValidationPostProcessor extends AbstractAdvisingBeanPostProcessor implements InitializingBean
{
    private Class<? extends Annotation> validatedAnnotationType;
    private Validator validator;
    
    public MethodValidationPostProcessor() {
        this.validatedAnnotationType = Validated.class;
    }
    
    public void setValidatedAnnotationType(final Class<? extends Annotation> validatedAnnotationType) {
        Assert.notNull(validatedAnnotationType, "'validatedAnnotationType' must not be null");
        this.validatedAnnotationType = validatedAnnotationType;
    }
    
    public void setValidator(final Validator validator) {
        if (validator instanceof LocalValidatorFactoryBean) {
            this.validator = ((LocalValidatorFactoryBean)validator).getValidator();
        }
        else {
            this.validator = validator;
        }
    }
    
    public void setValidatorFactory(final ValidatorFactory validatorFactory) {
        this.validator = validatorFactory.getValidator();
    }
    
    @Override
    public void afterPropertiesSet() {
        final Pointcut pointcut = new AnnotationMatchingPointcut(this.validatedAnnotationType, true);
        final Advice advice = (this.validator != null) ? new MethodValidationInterceptor(this.validator) : new MethodValidationInterceptor();
        this.advisor = new DefaultPointcutAdvisor(pointcut, advice);
    }
}
