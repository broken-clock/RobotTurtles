// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.expression;

public class AccessException extends Exception
{
    public AccessException(final String message, final Exception cause) {
        super(message, cause);
    }
    
    public AccessException(final String message) {
        super(message);
    }
}
