// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory;

import org.springframework.beans.FatalBeanException;

public class BeanExpressionException extends FatalBeanException
{
    public BeanExpressionException(final String msg) {
        super(msg);
    }
    
    public BeanExpressionException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
