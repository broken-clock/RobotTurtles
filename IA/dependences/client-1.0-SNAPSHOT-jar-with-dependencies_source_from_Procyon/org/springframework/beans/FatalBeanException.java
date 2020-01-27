// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

public class FatalBeanException extends BeansException
{
    public FatalBeanException(final String msg) {
        super(msg);
    }
    
    public FatalBeanException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
