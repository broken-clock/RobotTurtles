// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.env;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import org.springframework.util.Assert;
import java.util.Map;

abstract class ReadOnlySystemAttributesMap implements Map<String, String>
{
    @Override
    public boolean containsKey(final Object key) {
        return this.get(key) != null;
    }
    
    @Override
    public String get(final Object key) {
        Assert.isInstanceOf(String.class, key, String.format("Expected key [%s] to be of type String, got %s", key, key.getClass().getName()));
        return this.getSystemAttribute((String)key);
    }
    
    @Override
    public boolean isEmpty() {
        return false;
    }
    
    protected abstract String getSystemAttribute(final String p0);
    
    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String put(final String key, final String value) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean containsValue(final Object value) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String remove(final Object key) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Set<String> keySet() {
        return Collections.emptySet();
    }
    
    @Override
    public void putAll(final Map<? extends String, ? extends String> map) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Collection<String> values() {
        return (Collection<String>)Collections.emptySet();
    }
    
    @Override
    public Set<Entry<String, String>> entrySet() {
        return Collections.emptySet();
    }
}
