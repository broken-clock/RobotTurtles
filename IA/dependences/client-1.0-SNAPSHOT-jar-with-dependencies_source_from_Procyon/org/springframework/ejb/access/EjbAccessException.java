// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.ejb.access;

import org.springframework.core.NestedRuntimeException;

public class EjbAccessException extends NestedRuntimeException
{
    public EjbAccessException(final String msg) {
        super(msg);
    }
    
    public EjbAccessException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
