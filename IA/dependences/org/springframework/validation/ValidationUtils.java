// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.validation;

import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.Assert;
import org.apache.commons.logging.Log;

public abstract class ValidationUtils
{
    private static Log logger;
    
    public static void invokeValidator(final Validator validator, final Object obj, final Errors errors) {
        invokeValidator(validator, obj, errors, (Object[])null);
    }
    
    public static void invokeValidator(final Validator validator, final Object obj, final Errors errors, final Object... validationHints) {
        Assert.notNull(validator, "Validator must not be null");
        Assert.notNull(errors, "Errors object must not be null");
        if (ValidationUtils.logger.isDebugEnabled()) {
            ValidationUtils.logger.debug("Invoking validator [" + validator + "]");
        }
        if (obj != null && !validator.supports(obj.getClass())) {
            throw new IllegalArgumentException("Validator [" + validator.getClass() + "] does not support [" + obj.getClass() + "]");
        }
        if (!ObjectUtils.isEmpty(validationHints) && validator instanceof SmartValidator) {
            ((SmartValidator)validator).validate(obj, errors, validationHints);
        }
        else {
            validator.validate(obj, errors);
        }
        if (ValidationUtils.logger.isDebugEnabled()) {
            if (errors.hasErrors()) {
                ValidationUtils.logger.debug("Validator found " + errors.getErrorCount() + " errors");
            }
            else {
                ValidationUtils.logger.debug("Validator found no errors");
            }
        }
    }
    
    public static void rejectIfEmpty(final Errors errors, final String field, final String errorCode) {
        rejectIfEmpty(errors, field, errorCode, null, null);
    }
    
    public static void rejectIfEmpty(final Errors errors, final String field, final String errorCode, final String defaultMessage) {
        rejectIfEmpty(errors, field, errorCode, null, defaultMessage);
    }
    
    public static void rejectIfEmpty(final Errors errors, final String field, final String errorCode, final Object[] errorArgs) {
        rejectIfEmpty(errors, field, errorCode, errorArgs, null);
    }
    
    public static void rejectIfEmpty(final Errors errors, final String field, final String errorCode, final Object[] errorArgs, final String defaultMessage) {
        Assert.notNull(errors, "Errors object must not be null");
        final Object value = errors.getFieldValue(field);
        if (value == null || !StringUtils.hasLength(value.toString())) {
            errors.rejectValue(field, errorCode, errorArgs, defaultMessage);
        }
    }
    
    public static void rejectIfEmptyOrWhitespace(final Errors errors, final String field, final String errorCode) {
        rejectIfEmptyOrWhitespace(errors, field, errorCode, null, null);
    }
    
    public static void rejectIfEmptyOrWhitespace(final Errors errors, final String field, final String errorCode, final String defaultMessage) {
        rejectIfEmptyOrWhitespace(errors, field, errorCode, null, defaultMessage);
    }
    
    public static void rejectIfEmptyOrWhitespace(final Errors errors, final String field, final String errorCode, final Object[] errorArgs) {
        rejectIfEmptyOrWhitespace(errors, field, errorCode, errorArgs, null);
    }
    
    public static void rejectIfEmptyOrWhitespace(final Errors errors, final String field, final String errorCode, final Object[] errorArgs, final String defaultMessage) {
        Assert.notNull(errors, "Errors object must not be null");
        final Object value = errors.getFieldValue(field);
        if (value == null || !StringUtils.hasText(value.toString())) {
            errors.rejectValue(field, errorCode, errorArgs, defaultMessage);
        }
    }
    
    static {
        ValidationUtils.logger = LogFactory.getLog(ValidationUtils.class);
    }
}
