// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jmx.export;

public class UnableToRegisterMBeanException extends MBeanExportException
{
    public UnableToRegisterMBeanException(final String msg) {
        super(msg);
    }
    
    public UnableToRegisterMBeanException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
