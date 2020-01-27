// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory;

public class BeanCreationNotAllowedException extends BeanCreationException
{
    public BeanCreationNotAllowedException(final String beanName, final String msg) {
        super(beanName, msg);
    }
}
