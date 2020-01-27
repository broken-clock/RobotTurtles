// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory;

public class BeanIsAbstractException extends BeanCreationException
{
    public BeanIsAbstractException(final String beanName) {
        super(beanName, "Bean definition is abstract");
    }
}
