// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ser.impl.ReadOnlyClassToSerializerMap;
import java.util.concurrent.atomic.AtomicReference;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.util.TypeKey;
import java.util.HashMap;

public final class SerializerCache
{
    private final HashMap<TypeKey, JsonSerializer<Object>> _sharedMap;
    private final AtomicReference<ReadOnlyClassToSerializerMap> _readOnlyMap;
    
    public SerializerCache() {
        this._sharedMap = new HashMap<TypeKey, JsonSerializer<Object>>(64);
        this._readOnlyMap = new AtomicReference<ReadOnlyClassToSerializerMap>();
    }
    
    public ReadOnlyClassToSerializerMap getReadOnlyLookupMap() {
        final ReadOnlyClassToSerializerMap m = this._readOnlyMap.get();
        if (m != null) {
            return m;
        }
        return this._makeReadOnlyLookupMap();
    }
    
    private final synchronized ReadOnlyClassToSerializerMap _makeReadOnlyLookupMap() {
        ReadOnlyClassToSerializerMap m = this._readOnlyMap.get();
        if (m == null) {
            m = ReadOnlyClassToSerializerMap.from(this._sharedMap);
            this._readOnlyMap.set(m);
        }
        return m;
    }
    
    public synchronized int size() {
        return this._sharedMap.size();
    }
    
    public JsonSerializer<Object> untypedValueSerializer(final Class<?> type) {
        synchronized (this) {
            return this._sharedMap.get(new TypeKey(type, false));
        }
    }
    
    public JsonSerializer<Object> untypedValueSerializer(final JavaType type) {
        synchronized (this) {
            return this._sharedMap.get(new TypeKey(type, false));
        }
    }
    
    public JsonSerializer<Object> typedValueSerializer(final JavaType type) {
        synchronized (this) {
            return this._sharedMap.get(new TypeKey(type, true));
        }
    }
    
    public JsonSerializer<Object> typedValueSerializer(final Class<?> cls) {
        synchronized (this) {
            return this._sharedMap.get(new TypeKey(cls, true));
        }
    }
    
    public void addTypedSerializer(final JavaType type, final JsonSerializer<Object> ser) {
        synchronized (this) {
            if (this._sharedMap.put(new TypeKey(type, true), ser) == null) {
                this._readOnlyMap.set(null);
            }
        }
    }
    
    public void addTypedSerializer(final Class<?> cls, final JsonSerializer<Object> ser) {
        synchronized (this) {
            if (this._sharedMap.put(new TypeKey(cls, true), ser) == null) {
                this._readOnlyMap.set(null);
            }
        }
    }
    
    public void addAndResolveNonTypedSerializer(final Class<?> type, final JsonSerializer<Object> ser, final SerializerProvider provider) throws JsonMappingException {
        synchronized (this) {
            if (this._sharedMap.put(new TypeKey(type, false), ser) == null) {
                this._readOnlyMap.set(null);
            }
            if (ser instanceof ResolvableSerializer) {
                ((ResolvableSerializer)ser).resolve(provider);
            }
        }
    }
    
    public void addAndResolveNonTypedSerializer(final JavaType type, final JsonSerializer<Object> ser, final SerializerProvider provider) throws JsonMappingException {
        synchronized (this) {
            if (this._sharedMap.put(new TypeKey(type, false), ser) == null) {
                this._readOnlyMap.set(null);
            }
            if (ser instanceof ResolvableSerializer) {
                ((ResolvableSerializer)ser).resolve(provider);
            }
        }
    }
    
    public void addAndResolveNonTypedSerializer(final Class<?> rawType, final JavaType fullType, final JsonSerializer<Object> ser, final SerializerProvider provider) throws JsonMappingException {
        synchronized (this) {
            final Object ob1 = this._sharedMap.put(new TypeKey(rawType, false), ser);
            final Object ob2 = this._sharedMap.put(new TypeKey(fullType, false), ser);
            if (ob1 == null || ob2 == null) {
                this._readOnlyMap.set(null);
            }
            if (ser instanceof ResolvableSerializer) {
                ((ResolvableSerializer)ser).resolve(provider);
            }
        }
    }
    
    public synchronized void flush() {
        this._sharedMap.clear();
    }
}
