// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

import org.springframework.util.Assert;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class ParameterizedTypeReference<T>
{
    private final Type type;
    
    protected ParameterizedTypeReference() {
        final Class<?> parameterizedTypeReferenceSubclass = findParameterizedTypeReferenceSubclass(this.getClass());
        final Type type = parameterizedTypeReferenceSubclass.getGenericSuperclass();
        Assert.isInstanceOf(ParameterizedType.class, type);
        final ParameterizedType parameterizedType = (ParameterizedType)type;
        Assert.isTrue(parameterizedType.getActualTypeArguments().length == 1);
        this.type = parameterizedType.getActualTypeArguments()[0];
    }
    
    public Type getType() {
        return this.type;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || (obj instanceof ParameterizedTypeReference && this.type.equals(((ParameterizedTypeReference)obj).type));
    }
    
    @Override
    public int hashCode() {
        return this.type.hashCode();
    }
    
    @Override
    public String toString() {
        return "ParameterizedTypeReference<" + this.type + ">";
    }
    
    private static Class<?> findParameterizedTypeReferenceSubclass(final Class<?> child) {
        final Class<?> parent = child.getSuperclass();
        if (Object.class.equals(parent)) {
            throw new IllegalStateException("Expected ParameterizedTypeReference superclass");
        }
        if (ParameterizedTypeReference.class.equals(parent)) {
            return child;
        }
        return findParameterizedTypeReferenceSubclass(parent);
    }
}
