// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

import java.io.IOException;

public class NestedIOException extends IOException
{
    public NestedIOException(final String msg) {
        super(msg);
    }
    
    public NestedIOException(final String msg, final Throwable cause) {
        super(msg);
        this.initCause(cause);
    }
    
    @Override
    public String getMessage() {
        return NestedExceptionUtils.buildMessage(super.getMessage(), this.getCause());
    }
    
    static {
        NestedExceptionUtils.class.getName();
    }
}
