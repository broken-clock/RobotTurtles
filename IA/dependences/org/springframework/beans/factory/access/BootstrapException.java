// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.access;

import org.springframework.beans.FatalBeanException;

public class BootstrapException extends FatalBeanException
{
    public BootstrapException(final String msg) {
        super(msg);
    }
    
    public BootstrapException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
