// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.LinkedHashMap;

public class LinkedCaseInsensitiveMap<V> extends LinkedHashMap<String, V>
{
    private final Map<String, String> caseInsensitiveKeys;
    private final Locale locale;
    
    public LinkedCaseInsensitiveMap() {
        this((Locale)null);
    }
    
    public LinkedCaseInsensitiveMap(final Locale locale) {
        this.caseInsensitiveKeys = new HashMap<String, String>();
        this.locale = ((locale != null) ? locale : Locale.getDefault());
    }
    
    public LinkedCaseInsensitiveMap(final int initialCapacity) {
        this(initialCapacity, null);
    }
    
    public LinkedCaseInsensitiveMap(final int initialCapacity, final Locale locale) {
        super(initialCapacity);
        this.caseInsensitiveKeys = new HashMap<String, String>(initialCapacity);
        this.locale = ((locale != null) ? locale : Locale.getDefault());
    }
    
    @Override
    public V put(final String key, final V value) {
        final String oldKey = this.caseInsensitiveKeys.put(this.convertKey(key), key);
        if (oldKey != null && !oldKey.equals(key)) {
            super.remove(oldKey);
        }
        return super.put(key, value);
    }
    
    @Override
    public void putAll(final Map<? extends String, ? extends V> map) {
        if (map.isEmpty()) {
            return;
        }
        for (final Map.Entry<? extends String, ? extends V> entry : map.entrySet()) {
            this.put((String)entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public boolean containsKey(final Object key) {
        return key instanceof String && this.caseInsensitiveKeys.containsKey(this.convertKey((String)key));
    }
    
    @Override
    public V get(final Object key) {
        if (key instanceof String) {
            return super.get(this.caseInsensitiveKeys.get(this.convertKey((String)key)));
        }
        return null;
    }
    
    @Override
    public V remove(final Object key) {
        if (key instanceof String) {
            return super.remove(this.caseInsensitiveKeys.remove(this.convertKey((String)key)));
        }
        return null;
    }
    
    @Override
    public void clear() {
        this.caseInsensitiveKeys.clear();
        super.clear();
    }
    
    protected String convertKey(final String key) {
        return key.toLowerCase(this.locale);
    }
}
