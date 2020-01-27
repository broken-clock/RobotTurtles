// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

import java.beans.PropertyChangeEvent;
import org.springframework.core.ErrorCoded;

public abstract class PropertyAccessException extends BeansException implements ErrorCoded
{
    private transient PropertyChangeEvent propertyChangeEvent;
    
    public PropertyAccessException(final PropertyChangeEvent propertyChangeEvent, final String msg, final Throwable cause) {
        super(msg, cause);
        this.propertyChangeEvent = propertyChangeEvent;
    }
    
    public PropertyAccessException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
    
    public PropertyChangeEvent getPropertyChangeEvent() {
        return this.propertyChangeEvent;
    }
    
    public String getPropertyName() {
        return (this.propertyChangeEvent != null) ? this.propertyChangeEvent.getPropertyName() : null;
    }
    
    public Object getValue() {
        return (this.propertyChangeEvent != null) ? this.propertyChangeEvent.getNewValue() : null;
    }
}
