// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.core;

public class CodeGenerationException extends RuntimeException
{
    private Throwable cause;
    
    public CodeGenerationException(final Throwable cause) {
        super(cause.getClass().getName() + "-->" + cause.getMessage());
        this.cause = cause;
    }
    
    public Throwable getCause() {
        return this.cause;
    }
}
