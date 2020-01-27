// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.serializer.support;

import org.springframework.core.NestedRuntimeException;

public class SerializationFailedException extends NestedRuntimeException
{
    public SerializationFailedException(final String message) {
        super(message);
    }
    
    public SerializationFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
