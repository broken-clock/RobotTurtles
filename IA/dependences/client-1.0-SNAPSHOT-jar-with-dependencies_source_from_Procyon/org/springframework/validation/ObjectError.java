// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.validation;

import org.springframework.util.Assert;
import org.springframework.context.support.DefaultMessageSourceResolvable;

public class ObjectError extends DefaultMessageSourceResolvable
{
    private final String objectName;
    
    public ObjectError(final String objectName, final String defaultMessage) {
        this(objectName, null, null, defaultMessage);
    }
    
    public ObjectError(final String objectName, final String[] codes, final Object[] arguments, final String defaultMessage) {
        super(codes, arguments, defaultMessage);
        Assert.notNull(objectName, "Object name must not be null");
        this.objectName = objectName;
    }
    
    public String getObjectName() {
        return this.objectName;
    }
    
    @Override
    public String toString() {
        return "Error in object '" + this.objectName + "': " + this.resolvableToString();
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!this.getClass().equals(other.getClass()) || !super.equals(other)) {
            return false;
        }
        final ObjectError otherError = (ObjectError)other;
        return this.getObjectName().equals(otherError.getObjectName());
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() * 29 + this.getObjectName().hashCode();
    }
}
