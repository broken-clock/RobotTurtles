// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

public class ConstantException extends IllegalArgumentException
{
    public ConstantException(final String className, final String field, final String message) {
        super("Field '" + field + "' " + message + " in class [" + className + "]");
    }
    
    public ConstantException(final String className, final String namePrefix, final Object value) {
        super("No '" + namePrefix + "' field with value '" + value + "' found in class [" + className + "]");
    }
}
