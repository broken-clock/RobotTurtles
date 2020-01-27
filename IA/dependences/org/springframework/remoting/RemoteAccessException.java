// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting;

import org.springframework.core.NestedRuntimeException;

public class RemoteAccessException extends NestedRuntimeException
{
    private static final long serialVersionUID = -4906825139312227864L;
    
    public RemoteAccessException(final String msg) {
        super(msg);
    }
    
    public RemoteAccessException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
