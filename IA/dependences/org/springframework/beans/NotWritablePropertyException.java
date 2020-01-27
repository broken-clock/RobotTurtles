// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans;

public class NotWritablePropertyException extends InvalidPropertyException
{
    private String[] possibleMatches;
    
    public NotWritablePropertyException(final Class<?> beanClass, final String propertyName) {
        super(beanClass, propertyName, "Bean property '" + propertyName + "' is not writable or has an invalid setter method: " + "Does the return type of the getter match the parameter type of the setter?");
        this.possibleMatches = null;
    }
    
    public NotWritablePropertyException(final Class<?> beanClass, final String propertyName, final String msg) {
        super(beanClass, propertyName, msg);
        this.possibleMatches = null;
    }
    
    public NotWritablePropertyException(final Class<?> beanClass, final String propertyName, final String msg, final Throwable cause) {
        super(beanClass, propertyName, msg, cause);
        this.possibleMatches = null;
    }
    
    public NotWritablePropertyException(final Class<?> beanClass, final String propertyName, final String msg, final String[] possibleMatches) {
        super(beanClass, propertyName, msg);
        this.possibleMatches = null;
        this.possibleMatches = possibleMatches;
    }
    
    public String[] getPossibleMatches() {
        return this.possibleMatches;
    }
}
