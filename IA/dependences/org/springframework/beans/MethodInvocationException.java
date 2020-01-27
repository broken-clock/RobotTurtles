// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

import java.beans.PropertyChangeEvent;

public class MethodInvocationException extends PropertyAccessException
{
    public static final String ERROR_CODE = "methodInvocation";
    
    public MethodInvocationException(final PropertyChangeEvent propertyChangeEvent, final Throwable cause) {
        super(propertyChangeEvent, "Property '" + propertyChangeEvent.getPropertyName() + "' threw exception", cause);
    }
    
    @Override
    public String getErrorCode() {
        return "methodInvocation";
    }
}
