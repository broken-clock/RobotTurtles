// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory;

import org.springframework.beans.FatalBeanException;

public class BeanInitializationException extends FatalBeanException
{
    public BeanInitializationException(final String msg) {
        super(msg);
    }
    
    public BeanInitializationException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
