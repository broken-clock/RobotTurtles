// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.jndi;

import javax.naming.NamingException;

public class TypeMismatchNamingException extends NamingException
{
    private Class<?> requiredType;
    private Class<?> actualType;
    
    public TypeMismatchNamingException(final String jndiName, final Class<?> requiredType, final Class<?> actualType) {
        super("Object of type [" + actualType + "] available at JNDI location [" + jndiName + "] is not assignable to [" + requiredType.getName() + "]");
        this.requiredType = requiredType;
        this.actualType = actualType;
    }
    
    public TypeMismatchNamingException(final String explanation) {
        super(explanation);
    }
    
    public final Class<?> getRequiredType() {
        return this.requiredType;
    }
    
    public final Class<?> getActualType() {
        return this.actualType;
    }
}
