// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import org.springframework.beans.FatalBeanException;

public class BeanDefinitionValidationException extends FatalBeanException
{
    public BeanDefinitionValidationException(final String msg) {
        super(msg);
    }
    
    public BeanDefinitionValidationException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
