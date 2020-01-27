// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

public class LinkedMultiValueMap<K, V> implements MultiValueMap<K, V>, Serializable
{
    private static final long serialVersionUID = 3801124242820219131L;
    private final Map<K, List<V>> targetMap;
    
    public LinkedMultiValueMap() {
        this.targetMap = new LinkedHashMap<K, List<V>>();
    }
    
    public LinkedMultiValueMap(final int initialCapacity) {
        this.targetMap = new LinkedHashMap<K, List<V>>(initialCapacity);
    }
    
    public LinkedMultiValueMap(final Map<K, List<V>> otherMap) {
        this.targetMap = new LinkedHashMap<K, List<V>>((Map<? extends K, ? extends List<V>>)otherMap);
    }
    
    @Override
    public void add(final K key, final V value) {
        List<V> values = this.targetMap.get(key);
        if (values == null) {
            values = new LinkedList<V>();
            this.targetMap.put(key, values);
        }
        values.add(value);
    }
    
    @Override
    public V getFirst(final K key) {
        final List<V> values = this.targetMap.get(key);
        return (values != null) ? values.get(0) : null;
    }
    
    @Override
    public void set(final K key, final V value) {
        final List<V> values = new LinkedList<V>();
        values.add(value);
        this.targetMap.put(key, values);
    }
    
    @Override
    public void setAll(final Map<K, V> values) {
        for (final Map.Entry<K, V> entry : values.entrySet()) {
            this.set(entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public Map<K, V> toSingleValueMap() {
        final LinkedHashMap<K, V> singleValueMap = new LinkedHashMap<K, V>(this.targetMap.size());
        for (final Map.Entry<K, List<V>> entry : this.targetMap.entrySet()) {
            singleValueMap.put(entry.getKey(), entry.getValue().get(0));
        }
        return singleValueMap;
    }
    
    @Override
    public int size() {
        return this.targetMap.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.targetMap.isEmpty();
    }
    
    @Override
    public boolean containsKey(final Object key) {
        return this.targetMap.containsKey(key);
    }
    
    @Override
    public boolean containsValue(final Object value) {
        return this.targetMap.containsValue(value);
    }
    
    @Override
    public List<V> get(final Object key) {
        return this.targetMap.get(key);
    }
    
    @Override
    public List<V> put(final K key, final List<V> value) {
        return this.targetMap.put(key, value);
    }
    
    @Override
    public List<V> remove(final Object key) {
        return this.targetMap.remove(key);
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends List<V>> m) {
        this.targetMap.putAll(m);
    }
    
    @Override
    public void clear() {
        this.targetMap.clear();
    }
    
    @Override
    public Set<K> keySet() {
        return this.targetMap.keySet();
    }
    
    @Override
    public Collection<List<V>> values() {
        return this.targetMap.values();
    }
    
    @Override
    public Set<Map.Entry<K, List<V>>> entrySet() {
        return this.targetMap.entrySet();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this.targetMap.equals(obj);
    }
    
    @Override
    public int hashCode() {
        return this.targetMap.hashCode();
    }
    
    @Override
    public String toString() {
        return this.targetMap.toString();
    }
}
