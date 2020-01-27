// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

public class BeanInstantiationException extends FatalBeanException
{
    private Class<?> beanClass;
    
    public BeanInstantiationException(final Class<?> beanClass, final String msg) {
        this(beanClass, msg, null);
    }
    
    public BeanInstantiationException(final Class<?> beanClass, final String msg, final Throwable cause) {
        super("Could not instantiate bean class [" + beanClass.getName() + "]: " + msg, cause);
        this.beanClass = beanClass;
    }
    
    public Class<?> getBeanClass() {
        return this.beanClass;
    }
}
