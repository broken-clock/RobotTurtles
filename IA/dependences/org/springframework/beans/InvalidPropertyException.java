// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

public class InvalidPropertyException extends FatalBeanException
{
    private Class<?> beanClass;
    private String propertyName;
    
    public InvalidPropertyException(final Class<?> beanClass, final String propertyName, final String msg) {
        this(beanClass, propertyName, msg, null);
    }
    
    public InvalidPropertyException(final Class<?> beanClass, final String propertyName, final String msg, final Throwable cause) {
        super("Invalid property '" + propertyName + "' of bean class [" + beanClass.getName() + "]: " + msg, cause);
        this.beanClass = beanClass;
        this.propertyName = propertyName;
    }
    
    public Class<?> getBeanClass() {
        return this.beanClass;
    }
    
    public String getPropertyName() {
        return this.propertyName;
    }
}
