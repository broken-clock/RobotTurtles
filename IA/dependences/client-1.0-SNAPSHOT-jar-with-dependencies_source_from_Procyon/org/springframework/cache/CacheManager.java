// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache;

import java.util.Collection;

public interface CacheManager
{
    Cache getCache(final String p0);
    
    Collection<String> getCacheNames();
}
