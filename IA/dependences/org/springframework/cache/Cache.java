// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache;

public interface Cache
{
    String getName();
    
    Object getNativeCache();
    
    ValueWrapper get(final Object p0);
    
     <T> T get(final Object p0, final Class<T> p1);
    
    void put(final Object p0, final Object p1);
    
    void evict(final Object p0);
    
    void clear();
    
    public interface ValueWrapper
    {
        Object get();
    }
}
