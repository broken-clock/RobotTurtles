// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.factory.support;

import org.springframework.util.Assert;

public class ManagedArray extends ManagedList<Object>
{
    volatile Class<?> resolvedElementType;
    
    public ManagedArray(final String elementTypeName, final int size) {
        super(size);
        Assert.notNull(elementTypeName, "elementTypeName must not be null");
        this.setElementTypeName(elementTypeName);
    }
}
