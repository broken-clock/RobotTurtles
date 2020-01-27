// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.validation.beanvalidation;

import org.hibernate.validator.method.MethodConstraintViolation;
import org.hibernate.validator.method.MethodConstraintViolationException;
import org.hibernate.validator.method.MethodValidator;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.validation.annotation.Validated;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;
import org.springframework.util.ReflectionUtils;
import org.aopalliance.intercept.MethodInvocation;
import javax.validation.ValidatorFactory;
import javax.validation.Validation;
import javax.validation.Validator;
import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInterceptor;

public class MethodValidationInterceptor implements MethodInterceptor
{
    private static Method forExecutablesMethod;
    private static Method validateParametersMethod;
    private static Method validateReturnValueMethod;
    private final Validator validator;
    
    public MethodValidationInterceptor() {
        this((MethodValidationInterceptor.forExecutablesMethod != null) ? Validation.buildDefaultValidatorFactory() : HibernateValidatorDelegate.buildValidatorFactory());
    }
    
    public MethodValidationInterceptor(final ValidatorFactory validatorFactory) {
        this(validatorFactory.getValidator());
    }
    
    public MethodValidationInterceptor(final Validator validator) {
        this.validator = validator;
    }
    
    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        final Class<?>[] groups = this.determineValidationGroups(invocation);
        if (MethodValidationInterceptor.forExecutablesMethod == null) {
            return HibernateValidatorDelegate.invokeWithinValidation(invocation, this.validator, groups);
        }
        final Object executableValidator = ReflectionUtils.invokeMethod(MethodValidationInterceptor.forExecutablesMethod, this.validator);
        Set<ConstraintViolation<?>> result = (Set<ConstraintViolation<?>>)ReflectionUtils.invokeMethod(MethodValidationInterceptor.validateParametersMethod, executableValidator, invocation.getThis(), invocation.getMethod(), invocation.getArguments(), groups);
        if (!result.isEmpty()) {
            throw new ConstraintViolationException((Set)result);
        }
        final Object returnValue = invocation.proceed();
        result = (Set<ConstraintViolation<?>>)ReflectionUtils.invokeMethod(MethodValidationInterceptor.validateReturnValueMethod, executableValidator, invocation.getThis(), invocation.getMethod(), returnValue, groups);
        if (!result.isEmpty()) {
            throw new ConstraintViolationException((Set)result);
        }
        return returnValue;
    }
    
    protected Class<?>[] determineValidationGroups(final MethodInvocation invocation) {
        final Validated valid = AnnotationUtils.findAnnotation(invocation.getThis().getClass(), Validated.class);
        return (valid != null) ? valid.value() : new Class[0];
    }
    
    static {
        try {
            MethodValidationInterceptor.forExecutablesMethod = Validator.class.getMethod("forExecutables", (Class<?>[])new Class[0]);
            final Class<?> executableValidatorClass = MethodValidationInterceptor.forExecutablesMethod.getReturnType();
            MethodValidationInterceptor.validateParametersMethod = executableValidatorClass.getMethod("validateParameters", Object.class, Method.class, Object[].class, Class[].class);
            MethodValidationInterceptor.validateReturnValueMethod = executableValidatorClass.getMethod("validateReturnValue", Object.class, Method.class, Object.class, Class[].class);
        }
        catch (Exception ex) {}
    }
    
    private static class HibernateValidatorDelegate
    {
        public static ValidatorFactory buildValidatorFactory() {
            return ((HibernateValidatorConfiguration)Validation.byProvider((Class)HibernateValidator.class).configure()).buildValidatorFactory();
        }
        
        public static Object invokeWithinValidation(final MethodInvocation invocation, final Validator validator, final Class<?>[] groups) throws Throwable {
            final MethodValidator methodValidator = (MethodValidator)validator.unwrap((Class)MethodValidator.class);
            Set<MethodConstraintViolation<Object>> result = (Set<MethodConstraintViolation<Object>>)methodValidator.validateAllParameters(invocation.getThis(), invocation.getMethod(), invocation.getArguments(), (Class[])groups);
            if (!result.isEmpty()) {
                throw new MethodConstraintViolationException((Set)result);
            }
            final Object returnValue = invocation.proceed();
            result = (Set<MethodConstraintViolation<Object>>)methodValidator.validateReturnValue(invocation.getThis(), invocation.getMethod(), returnValue, (Class[])groups);
            if (!result.isEmpty()) {
                throw new MethodConstraintViolationException((Set)result);
            }
            return returnValue;
        }
    }
}
