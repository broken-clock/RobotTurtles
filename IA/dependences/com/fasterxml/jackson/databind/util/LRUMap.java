// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.util;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.io.Serializable;

public class LRUMap<K, V> implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final transient int _maxEntries;
    protected final transient ConcurrentHashMap<K, V> _map;
    protected transient int _jdkSerializeMaxEntries;
    
    public LRUMap(final int initialEntries, final int maxEntries) {
        this._map = new ConcurrentHashMap<K, V>(initialEntries, 0.8f, 4);
        this._maxEntries = maxEntries;
    }
    
    public V put(final K key, final V value) {
        if (this._map.size() >= this._maxEntries) {
            synchronized (this) {
                if (this._map.size() >= this._maxEntries) {
                    this.clear();
                }
            }
        }
        return this._map.put(key, value);
    }
    
    public V putIfAbsent(final K key, final V value) {
        if (this._map.size() >= this._maxEntries) {
            synchronized (this) {
                if (this._map.size() >= this._maxEntries) {
                    this.clear();
                }
            }
        }
        return this._map.putIfAbsent(key, value);
    }
    
    public V get(final Object key) {
        return this._map.get(key);
    }
    
    public void clear() {
        this._map.clear();
    }
    
    public int size() {
        return this._map.size();
    }
    
    private void readObject(final ObjectInputStream in) throws IOException {
        this._jdkSerializeMaxEntries = in.readInt();
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.writeInt(this._jdkSerializeMaxEntries);
    }
    
    protected Object readResolve() {
        return new LRUMap(this._jdkSerializeMaxEntries, this._jdkSerializeMaxEntries);
    }
}
