// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cglib.proxy;

import org.springframework.cglib.core.CodeGenerationException;

public class UndeclaredThrowableException extends CodeGenerationException
{
    public UndeclaredThrowableException(final Throwable t) {
        super(t);
    }
    
    public Throwable getUndeclaredThrowable() {
        return this.getCause();
    }
}
