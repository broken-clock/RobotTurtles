// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jmx;

import org.springframework.core.NestedRuntimeException;

public class JmxException extends NestedRuntimeException
{
    public JmxException(final String msg) {
        super(msg);
    }
    
    public JmxException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
