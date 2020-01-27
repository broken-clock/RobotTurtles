// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.convert;

import org.springframework.core.NestedRuntimeException;

public abstract class ConversionException extends NestedRuntimeException
{
    public ConversionException(final String message) {
        super(message);
    }
    
    public ConversionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
