// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.exc;

import java.util.Collection;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;

public class UnrecognizedPropertyException extends PropertyBindingException
{
    private static final long serialVersionUID = 1L;
    
    public UnrecognizedPropertyException(final JsonParser p, final String msg, final JsonLocation loc, final Class<?> referringClass, final String propName, final Collection<Object> propertyIds) {
        super(p, msg, loc, referringClass, propName, propertyIds);
    }
    
    @Deprecated
    public UnrecognizedPropertyException(final String msg, final JsonLocation loc, final Class<?> referringClass, final String propName, final Collection<Object> propertyIds) {
        super(msg, loc, referringClass, propName, propertyIds);
    }
    
    public static UnrecognizedPropertyException from(final JsonParser p, final Object fromObjectOrClass, final String propertyName, final Collection<Object> propertyIds) {
        Class<?> ref;
        if (fromObjectOrClass instanceof Class) {
            ref = (Class<?>)fromObjectOrClass;
        }
        else {
            ref = fromObjectOrClass.getClass();
        }
        final String msg = String.format("Unrecognized field \"%s\" (class %s), not marked as ignorable", propertyName, ref.getName());
        final UnrecognizedPropertyException e = new UnrecognizedPropertyException(p, msg, p.getCurrentLocation(), ref, propertyName, propertyIds);
        e.prependPath(fromObjectOrClass, propertyName);
        return e;
    }
}
