// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jmx.access;

import org.springframework.jmx.JmxException;

public class MBeanInfoRetrievalException extends JmxException
{
    public MBeanInfoRetrievalException(final String msg) {
        super(msg);
    }
    
    public MBeanInfoRetrievalException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
