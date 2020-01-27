// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert;

import org.springframework.util.ObjectUtils;

public final class ConversionFailedException extends ConversionException
{
    private final TypeDescriptor sourceType;
    private final TypeDescriptor targetType;
    private final Object value;
    
    public ConversionFailedException(final TypeDescriptor sourceType, final TypeDescriptor targetType, final Object value, final Throwable cause) {
        super("Failed to convert from type " + sourceType + " to type " + targetType + " for value '" + ObjectUtils.nullSafeToString(value) + "'", cause);
        this.sourceType = sourceType;
        this.targetType = targetType;
        this.value = value;
    }
    
    public TypeDescriptor getSourceType() {
        return this.sourceType;
    }
    
    public TypeDescriptor getTargetType() {
        return this.targetType;
    }
    
    public Object getValue() {
        return this.value;
    }
}
