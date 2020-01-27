// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.databind.JavaType;
import java.util.HashMap;
import java.util.Iterator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.util.TypeKey;
import java.util.Map;

public final class ReadOnlyClassToSerializerMap
{
    private final Bucket[] _buckets;
    private final int _size;
    private final int _mask;
    
    public ReadOnlyClassToSerializerMap(final Map<TypeKey, JsonSerializer<Object>> serializers) {
        final int size = findSize(serializers.size());
        this._size = size;
        this._mask = size - 1;
        final Bucket[] buckets = new Bucket[size];
        for (final Map.Entry<TypeKey, JsonSerializer<Object>> entry : serializers.entrySet()) {
            final TypeKey key = entry.getKey();
            final int index = key.hashCode() & this._mask;
            buckets[index] = new Bucket(buckets[index], key, entry.getValue());
        }
        this._buckets = buckets;
    }
    
    private static final int findSize(final int size) {
        int needed;
        int result;
        for (needed = ((size <= 64) ? (size + size) : (size + (size >> 2))), result = 8; result < needed; result += result) {}
        return result;
    }
    
    public static ReadOnlyClassToSerializerMap from(final HashMap<TypeKey, JsonSerializer<Object>> src) {
        return new ReadOnlyClassToSerializerMap(src);
    }
    
    public int size() {
        return this._size;
    }
    
    public JsonSerializer<Object> typedValueSerializer(final JavaType type) {
        Bucket bucket = this._buckets[TypeKey.typedHash(type) & this._mask];
        if (bucket == null) {
            return null;
        }
        if (bucket.matchesTyped(type)) {
            return bucket.value;
        }
        while ((bucket = bucket.next) != null) {
            if (bucket.matchesTyped(type)) {
                return bucket.value;
            }
        }
        return null;
    }
    
    public JsonSerializer<Object> typedValueSerializer(final Class<?> type) {
        Bucket bucket = this._buckets[TypeKey.typedHash(type) & this._mask];
        if (bucket == null) {
            return null;
        }
        if (bucket.matchesTyped(type)) {
            return bucket.value;
        }
        while ((bucket = bucket.next) != null) {
            if (bucket.matchesTyped(type)) {
                return bucket.value;
            }
        }
        return null;
    }
    
    public JsonSerializer<Object> untypedValueSerializer(final JavaType type) {
        Bucket bucket = this._buckets[TypeKey.untypedHash(type) & this._mask];
        if (bucket == null) {
            return null;
        }
        if (bucket.matchesUntyped(type)) {
            return bucket.value;
        }
        while ((bucket = bucket.next) != null) {
            if (bucket.matchesUntyped(type)) {
                return bucket.value;
            }
        }
        return null;
    }
    
    public JsonSerializer<Object> untypedValueSerializer(final Class<?> type) {
        Bucket bucket = this._buckets[TypeKey.untypedHash(type) & this._mask];
        if (bucket == null) {
            return null;
        }
        if (bucket.matchesUntyped(type)) {
            return bucket.value;
        }
        while ((bucket = bucket.next) != null) {
            if (bucket.matchesUntyped(type)) {
                return bucket.value;
            }
        }
        return null;
    }
    
    private static final class Bucket
    {
        public final JsonSerializer<Object> value;
        public final Bucket next;
        protected final Class<?> _class;
        protected final JavaType _type;
        protected final boolean _isTyped;
        
        public Bucket(final Bucket next, final TypeKey key, final JsonSerializer<Object> value) {
            this.next = next;
            this.value = value;
            this._isTyped = key.isTyped();
            this._class = key.getRawType();
            this._type = key.getType();
        }
        
        public boolean matchesTyped(final Class<?> key) {
            return this._class == key && this._isTyped;
        }
        
        public boolean matchesUntyped(final Class<?> key) {
            return this._class == key && !this._isTyped;
        }
        
        public boolean matchesTyped(final JavaType key) {
            return this._isTyped && key.equals(this._type);
        }
        
        public boolean matchesUntyped(final JavaType key) {
            return !this._isTyped && key.equals(this._type);
        }
    }
}
