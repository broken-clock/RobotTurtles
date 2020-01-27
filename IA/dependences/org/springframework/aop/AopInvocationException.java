// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.aop;

import org.springframework.core.NestedRuntimeException;

public class AopInvocationException extends NestedRuntimeException
{
    public AopInvocationException(final String msg) {
        super(msg);
    }
    
    public AopInvocationException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
