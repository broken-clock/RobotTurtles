// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory;

public class BeanCurrentlyInCreationException extends BeanCreationException
{
    public BeanCurrentlyInCreationException(final String beanName) {
        super(beanName, "Requested bean is currently in creation: Is there an unresolvable circular reference?");
    }
    
    public BeanCurrentlyInCreationException(final String beanName, final String msg) {
        super(beanName, msg);
    }
}
