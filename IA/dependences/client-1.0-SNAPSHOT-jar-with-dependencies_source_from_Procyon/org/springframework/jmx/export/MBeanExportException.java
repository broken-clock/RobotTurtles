// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jmx.export;

import org.springframework.jmx.JmxException;

public class MBeanExportException extends JmxException
{
    public MBeanExportException(final String msg) {
        super(msg);
    }
    
    public MBeanExportException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
