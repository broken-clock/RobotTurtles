// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jmx.access;

import org.springframework.jmx.JmxException;

public class InvocationFailureException extends JmxException
{
    public InvocationFailureException(final String msg) {
        super(msg);
    }
    
    public InvocationFailureException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
