// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

public class NotReadablePropertyException extends InvalidPropertyException
{
    public NotReadablePropertyException(final Class<?> beanClass, final String propertyName) {
        super(beanClass, propertyName, "Bean property '" + propertyName + "' is not readable or has an invalid getter method: " + "Does the return type of the getter match the parameter type of the setter?");
    }
    
    public NotReadablePropertyException(final Class<?> beanClass, final String propertyName, final String msg) {
        super(beanClass, propertyName, msg);
    }
}
