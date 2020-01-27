// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.cache.concurrent;

import java.io.Serializable;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.util.Assert;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.cache.Cache;

public class ConcurrentMapCache implements Cache
{
    private static final Object NULL_HOLDER;
    private final String name;
    private final ConcurrentMap<Object, Object> store;
    private final boolean allowNullValues;
    
    public ConcurrentMapCache(final String name) {
        this(name, new ConcurrentHashMap<Object, Object>(256), true);
    }
    
    public ConcurrentMapCache(final String name, final boolean allowNullValues) {
        this(name, new ConcurrentHashMap<Object, Object>(256), allowNullValues);
    }
    
    public ConcurrentMapCache(final String name, final ConcurrentMap<Object, Object> store, final boolean allowNullValues) {
        Assert.notNull(name, "Name must not be null");
        Assert.notNull(store, "Store must not be null");
        this.name = name;
        this.store = store;
        this.allowNullValues = allowNullValues;
    }
    
    @Override
    public final String getName() {
        return this.name;
    }
    
    @Override
    public final ConcurrentMap<Object, Object> getNativeCache() {
        return this.store;
    }
    
    public final boolean isAllowNullValues() {
        return this.allowNullValues;
    }
    
    @Override
    public ValueWrapper get(final Object key) {
        final Object value = this.store.get(key);
        return (value != null) ? new SimpleValueWrapper(this.fromStoreValue(value)) : null;
    }
    
    @Override
    public <T> T get(final Object key, final Class<T> type) {
        final Object value = this.fromStoreValue(this.store.get(key));
        if (type != null && !type.isInstance(value)) {
            throw new IllegalStateException("Cached value is not of required type [" + type.getName() + "]: " + value);
        }
        return (T)value;
    }
    
    @Override
    public void put(final Object key, final Object value) {
        this.store.put(key, this.toStoreValue(value));
    }
    
    @Override
    public void evict(final Object key) {
        this.store.remove(key);
    }
    
    @Override
    public void clear() {
        this.store.clear();
    }
    
    protected Object fromStoreValue(final Object storeValue) {
        if (this.allowNullValues && storeValue == ConcurrentMapCache.NULL_HOLDER) {
            return null;
        }
        return storeValue;
    }
    
    protected Object toStoreValue(final Object userValue) {
        if (this.allowNullValues && userValue == null) {
            return ConcurrentMapCache.NULL_HOLDER;
        }
        return userValue;
    }
    
    static {
        NULL_HOLDER = new NullHolder();
    }
    
    private static class NullHolder implements Serializable
    {
    }
}
