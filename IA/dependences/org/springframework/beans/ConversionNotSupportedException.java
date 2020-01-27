// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

import java.beans.PropertyChangeEvent;

public class ConversionNotSupportedException extends TypeMismatchException
{
    public ConversionNotSupportedException(final PropertyChangeEvent propertyChangeEvent, final Class<?> requiredType, final Throwable cause) {
        super(propertyChangeEvent, requiredType, cause);
    }
    
    public ConversionNotSupportedException(final Object value, final Class<?> requiredType, final Throwable cause) {
        super(value, requiredType, cause);
    }
}
