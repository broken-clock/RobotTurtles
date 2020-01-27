// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

import java.util.List;
import java.util.Map;

public interface MultiValueMap<K, V> extends Map<K, List<V>>
{
    V getFirst(final K p0);
    
    void add(final K p0, final V p1);
    
    void set(final K p0, final V p1);
    
    void setAll(final Map<K, V> p0);
    
    Map<K, V> toSingleValueMap();
}
