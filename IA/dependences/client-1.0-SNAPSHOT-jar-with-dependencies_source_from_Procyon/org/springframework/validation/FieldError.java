// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.validation;

import org.springframework.util.ObjectUtils;
import org.springframework.util.Assert;

public class FieldError extends ObjectError
{
    private final String field;
    private final Object rejectedValue;
    private final boolean bindingFailure;
    
    public FieldError(final String objectName, final String field, final String defaultMessage) {
        this(objectName, field, null, false, null, null, defaultMessage);
    }
    
    public FieldError(final String objectName, final String field, final Object rejectedValue, final boolean bindingFailure, final String[] codes, final Object[] arguments, final String defaultMessage) {
        super(objectName, codes, arguments, defaultMessage);
        Assert.notNull(field, "Field must not be null");
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.bindingFailure = bindingFailure;
    }
    
    public String getField() {
        return this.field;
    }
    
    public Object getRejectedValue() {
        return this.rejectedValue;
    }
    
    public boolean isBindingFailure() {
        return this.bindingFailure;
    }
    
    @Override
    public String toString() {
        return "Field error in object '" + this.getObjectName() + "' on field '" + this.field + "': rejected value [" + this.rejectedValue + "]; " + this.resolvableToString();
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!super.equals(other)) {
            return false;
        }
        final FieldError otherError = (FieldError)other;
        return this.getField().equals(otherError.getField()) && ObjectUtils.nullSafeEquals(this.getRejectedValue(), otherError.getRejectedValue()) && this.isBindingFailure() == otherError.isBindingFailure();
    }
    
    @Override
    public int hashCode() {
        int hashCode = super.hashCode();
        hashCode = 29 * hashCode + this.getField().hashCode();
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.getRejectedValue());
        hashCode = 29 * hashCode + (this.isBindingFailure() ? 1 : 0);
        return hashCode;
    }
}
