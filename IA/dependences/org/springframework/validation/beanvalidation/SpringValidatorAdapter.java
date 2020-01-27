// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.validation.beanvalidation;

import java.util.HashSet;
import javax.validation.metadata.BeanDescriptor;
import java.util.List;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import java.util.LinkedList;
import javax.validation.metadata.ConstraintDescriptor;
import java.util.Iterator;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.BindingResult;
import java.util.LinkedHashSet;
import javax.validation.ConstraintViolation;
import org.springframework.validation.Errors;
import org.springframework.util.Assert;
import java.util.Set;
import javax.validation.Validator;
import org.springframework.validation.SmartValidator;

public class SpringValidatorAdapter implements SmartValidator, javax.validation.Validator
{
    private static final Set<String> internalAnnotationAttributes;
    private javax.validation.Validator targetValidator;
    
    public SpringValidatorAdapter(final javax.validation.Validator targetValidator) {
        Assert.notNull(targetValidator, "Target Validator must not be null");
        this.targetValidator = targetValidator;
    }
    
    SpringValidatorAdapter() {
    }
    
    void setTargetValidator(final javax.validation.Validator targetValidator) {
        this.targetValidator = targetValidator;
    }
    
    public boolean supports(final Class<?> clazz) {
        return this.targetValidator != null;
    }
    
    public void validate(final Object target, final Errors errors) {
        if (this.targetValidator != null) {
            this.processConstraintViolations(this.targetValidator.validate(target, new Class[0]), errors);
        }
    }
    
    @Override
    public void validate(final Object target, final Errors errors, final Object... validationHints) {
        if (this.targetValidator != null) {
            final Set<Class> groups = new LinkedHashSet<Class>();
            if (validationHints != null) {
                for (final Object hint : validationHints) {
                    if (hint instanceof Class) {
                        groups.add((Class)hint);
                    }
                }
            }
            this.processConstraintViolations(this.targetValidator.validate(target, (Class[])groups.toArray(new Class[groups.size()])), errors);
        }
    }
    
    protected void processConstraintViolations(final Set<ConstraintViolation<Object>> violations, final Errors errors) {
        for (final ConstraintViolation<Object> violation : violations) {
            final String field = violation.getPropertyPath().toString();
            final FieldError fieldError = errors.getFieldError(field);
            if (fieldError != null) {
                if (fieldError.isBindingFailure()) {
                    continue;
                }
            }
            try {
                final ConstraintDescriptor<?> cd = (ConstraintDescriptor<?>)violation.getConstraintDescriptor();
                final String errorCode = cd.getAnnotation().annotationType().getSimpleName();
                final Object[] errorArgs = this.getArgumentsForConstraint(errors.getObjectName(), field, cd);
                if (errors instanceof BindingResult) {
                    final BindingResult bindingResult = (BindingResult)errors;
                    final String nestedField = bindingResult.getNestedPath() + field;
                    if ("".equals(nestedField)) {
                        final String[] errorCodes = bindingResult.resolveMessageCodes(errorCode);
                        bindingResult.addError(new ObjectError(errors.getObjectName(), errorCodes, errorArgs, violation.getMessage()));
                    }
                    else {
                        Object invalidValue = violation.getInvalidValue();
                        if (!"".equals(field) && (invalidValue == violation.getLeafBean() || (field.contains(".") && !field.contains("[]")))) {
                            invalidValue = bindingResult.getRawFieldValue(field);
                        }
                        final String[] errorCodes2 = bindingResult.resolveMessageCodes(errorCode, field);
                        bindingResult.addError(new FieldError(errors.getObjectName(), nestedField, invalidValue, false, errorCodes2, errorArgs, violation.getMessage()));
                    }
                }
                else {
                    errors.rejectValue(field, errorCode, errorArgs, violation.getMessage());
                }
            }
            catch (NotReadablePropertyException ex) {
                throw new IllegalStateException("JSR-303 validated property '" + field + "' does not have a corresponding accessor for Spring data binding - " + "check your DataBinder's configuration (bean property versus direct field access)", ex);
            }
        }
    }
    
    protected Object[] getArgumentsForConstraint(final String objectName, final String field, final ConstraintDescriptor<?> descriptor) {
        final List<Object> arguments = new LinkedList<Object>();
        final String[] codes = { objectName + "." + field, field };
        arguments.add(new DefaultMessageSourceResolvable(codes, field));
        final Map<String, Object> attributesToExpose = new TreeMap<String, Object>();
        for (final Map.Entry<String, Object> entry : descriptor.getAttributes().entrySet()) {
            final String attributeName = entry.getKey();
            final Object attributeValue = entry.getValue();
            if (!SpringValidatorAdapter.internalAnnotationAttributes.contains(attributeName)) {
                attributesToExpose.put(attributeName, attributeValue);
            }
        }
        arguments.addAll(attributesToExpose.values());
        return arguments.toArray(new Object[arguments.size()]);
    }
    
    public <T> Set<ConstraintViolation<T>> validate(final T object, final Class<?>... groups) {
        Assert.notNull(this.targetValidator, "No target Validator set");
        return (Set<ConstraintViolation<T>>)this.targetValidator.validate((Object)object, (Class[])groups);
    }
    
    public <T> Set<ConstraintViolation<T>> validateProperty(final T object, final String propertyName, final Class<?>... groups) {
        Assert.notNull(this.targetValidator, "No target Validator set");
        return (Set<ConstraintViolation<T>>)this.targetValidator.validateProperty((Object)object, propertyName, (Class[])groups);
    }
    
    public <T> Set<ConstraintViolation<T>> validateValue(final Class<T> beanType, final String propertyName, final Object value, final Class<?>... groups) {
        Assert.notNull(this.targetValidator, "No target Validator set");
        return (Set<ConstraintViolation<T>>)this.targetValidator.validateValue((Class)beanType, propertyName, value, (Class[])groups);
    }
    
    public BeanDescriptor getConstraintsForClass(final Class<?> clazz) {
        Assert.notNull(this.targetValidator, "No target Validator set");
        return this.targetValidator.getConstraintsForClass((Class)clazz);
    }
    
    public <T> T unwrap(final Class<T> type) {
        Assert.notNull(this.targetValidator, "No target Validator set");
        return (T)this.targetValidator.unwrap((Class)type);
    }
    
    static {
        (internalAnnotationAttributes = new HashSet<String>(3)).add("message");
        SpringValidatorAdapter.internalAnnotationAttributes.add("groups");
        SpringValidatorAdapter.internalAnnotationAttributes.add("payload");
    }
}
