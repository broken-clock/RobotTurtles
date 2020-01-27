// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.annotation;

public interface ObjectIdResolver
{
    void bindItem(final ObjectIdGenerator.IdKey p0, final Object p1);
    
    Object resolveId(final ObjectIdGenerator.IdKey p0);
    
    ObjectIdResolver newForDeserialization(final Object p0);
    
    boolean canUseFor(final ObjectIdResolver p0);
}
