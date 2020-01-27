// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

public class NullValueInNestedPathException extends InvalidPropertyException
{
    public NullValueInNestedPathException(final Class<?> beanClass, final String propertyName) {
        super(beanClass, propertyName, "Value of nested property '" + propertyName + "' is null");
    }
    
    public NullValueInNestedPathException(final Class<?> beanClass, final String propertyName, final String msg) {
        super(beanClass, propertyName, msg);
    }
}
