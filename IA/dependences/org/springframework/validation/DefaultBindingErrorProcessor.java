// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.validation;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.util.StringUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.beans.PropertyAccessException;

public class DefaultBindingErrorProcessor implements BindingErrorProcessor
{
    public static final String MISSING_FIELD_ERROR_CODE = "required";
    
    @Override
    public void processMissingFieldError(final String missingField, final BindingResult bindingResult) {
        final String fixedField = bindingResult.getNestedPath() + missingField;
        final String[] codes = bindingResult.resolveMessageCodes("required", missingField);
        final Object[] arguments = this.getArgumentsForBindError(bindingResult.getObjectName(), fixedField);
        bindingResult.addError(new FieldError(bindingResult.getObjectName(), fixedField, "", true, codes, arguments, "Field '" + fixedField + "' is required"));
    }
    
    @Override
    public void processPropertyAccessException(final PropertyAccessException ex, final BindingResult bindingResult) {
        final String field = ex.getPropertyName();
        final String[] codes = bindingResult.resolveMessageCodes(ex.getErrorCode(), field);
        final Object[] arguments = this.getArgumentsForBindError(bindingResult.getObjectName(), field);
        Object rejectedValue = ex.getValue();
        if (rejectedValue != null && rejectedValue.getClass().isArray()) {
            rejectedValue = StringUtils.arrayToCommaDelimitedString(ObjectUtils.toObjectArray(rejectedValue));
        }
        bindingResult.addError(new FieldError(bindingResult.getObjectName(), field, rejectedValue, true, codes, arguments, ex.getLocalizedMessage()));
    }
    
    protected Object[] getArgumentsForBindError(final String objectName, final String field) {
        final String[] codes = { objectName + "." + field, field };
        return new Object[] { new DefaultMessageSourceResolvable(codes, field) };
    }
}
