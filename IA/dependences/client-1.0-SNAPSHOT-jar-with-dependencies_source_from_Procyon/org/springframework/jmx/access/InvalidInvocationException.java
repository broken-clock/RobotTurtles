// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jmx.access;

import javax.management.JMRuntimeException;

public class InvalidInvocationException extends JMRuntimeException
{
    public InvalidInvocationException(final String msg) {
        super(msg);
    }
}
