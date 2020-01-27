// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.context;

import org.springframework.beans.FatalBeanException;

public class ApplicationContextException extends FatalBeanException
{
    public ApplicationContextException(final String msg) {
        super(msg);
    }
    
    public ApplicationContextException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
