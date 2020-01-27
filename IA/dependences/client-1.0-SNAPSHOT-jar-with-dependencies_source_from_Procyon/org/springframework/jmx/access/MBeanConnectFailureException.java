// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jmx.access;

import org.springframework.jmx.JmxException;

public class MBeanConnectFailureException extends JmxException
{
    public MBeanConnectFailureException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
