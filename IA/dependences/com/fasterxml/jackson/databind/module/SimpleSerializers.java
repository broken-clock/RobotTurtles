// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.module;

import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializationConfig;
import java.util.Iterator;
import java.util.List;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.type.ClassKey;
import java.util.HashMap;
import java.io.Serializable;
import com.fasterxml.jackson.databind.ser.Serializers;

public class SimpleSerializers extends Serializers.Base implements Serializable
{
    private static final long serialVersionUID = 8531646511998456779L;
    protected HashMap<ClassKey, JsonSerializer<?>> _classMappings;
    protected HashMap<ClassKey, JsonSerializer<?>> _interfaceMappings;
    protected boolean _hasEnumSerializer;
    
    public SimpleSerializers() {
        this._classMappings = null;
        this._interfaceMappings = null;
        this._hasEnumSerializer = false;
    }
    
    public SimpleSerializers(final List<JsonSerializer<?>> sers) {
        this._classMappings = null;
        this._interfaceMappings = null;
        this._hasEnumSerializer = false;
        this.addSerializers(sers);
    }
    
    public void addSerializer(final JsonSerializer<?> ser) {
        final Class<?> cls = ser.handledType();
        if (cls == null || cls == Object.class) {
            throw new IllegalArgumentException("JsonSerializer of type " + ser.getClass().getName() + " does not define valid handledType() -- must either register with method that takes type argument  or make serializer extend 'com.fasterxml.jackson.databind.ser.std.StdSerializer'");
        }
        this._addSerializer(cls, ser);
    }
    
    public <T> void addSerializer(final Class<? extends T> type, final JsonSerializer<T> ser) {
        this._addSerializer(type, ser);
    }
    
    public void addSerializers(final List<JsonSerializer<?>> sers) {
        for (final JsonSerializer<?> ser : sers) {
            this.addSerializer(ser);
        }
    }
    
    @Override
    public JsonSerializer<?> findSerializer(final SerializationConfig config, final JavaType type, final BeanDescription beanDesc) {
        Class<?> cls = type.getRawClass();
        final ClassKey key = new ClassKey(cls);
        JsonSerializer<?> ser = null;
        if (cls.isInterface()) {
            if (this._interfaceMappings != null) {
                ser = this._interfaceMappings.get(key);
                if (ser != null) {
                    return ser;
                }
            }
        }
        else if (this._classMappings != null) {
            ser = this._classMappings.get(key);
            if (ser != null) {
                return ser;
            }
            if (this._hasEnumSerializer && type.isEnumType()) {
                key.reset(Enum.class);
                ser = this._classMappings.get(key);
                if (ser != null) {
                    return ser;
                }
            }
            for (Class<?> curr = cls; curr != null; curr = curr.getSuperclass()) {
                key.reset(curr);
                ser = this._classMappings.get(key);
                if (ser != null) {
                    return ser;
                }
            }
        }
        if (this._interfaceMappings != null) {
            ser = this._findInterfaceMapping(cls, key);
            if (ser != null) {
                return ser;
            }
            if (!cls.isInterface()) {
                while ((cls = cls.getSuperclass()) != null) {
                    ser = this._findInterfaceMapping(cls, key);
                    if (ser != null) {
                        return ser;
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public JsonSerializer<?> findArraySerializer(final SerializationConfig config, final ArrayType type, final BeanDescription beanDesc, final TypeSerializer elementTypeSerializer, final JsonSerializer<Object> elementValueSerializer) {
        return this.findSerializer(config, type, beanDesc);
    }
    
    @Override
    public JsonSerializer<?> findCollectionSerializer(final SerializationConfig config, final CollectionType type, final BeanDescription beanDesc, final TypeSerializer elementTypeSerializer, final JsonSerializer<Object> elementValueSerializer) {
        return this.findSerializer(config, type, beanDesc);
    }
    
    @Override
    public JsonSerializer<?> findCollectionLikeSerializer(final SerializationConfig config, final CollectionLikeType type, final BeanDescription beanDesc, final TypeSerializer elementTypeSerializer, final JsonSerializer<Object> elementValueSerializer) {
        return this.findSerializer(config, type, beanDesc);
    }
    
    @Override
    public JsonSerializer<?> findMapSerializer(final SerializationConfig config, final MapType type, final BeanDescription beanDesc, final JsonSerializer<Object> keySerializer, final TypeSerializer elementTypeSerializer, final JsonSerializer<Object> elementValueSerializer) {
        return this.findSerializer(config, type, beanDesc);
    }
    
    @Override
    public JsonSerializer<?> findMapLikeSerializer(final SerializationConfig config, final MapLikeType type, final BeanDescription beanDesc, final JsonSerializer<Object> keySerializer, final TypeSerializer elementTypeSerializer, final JsonSerializer<Object> elementValueSerializer) {
        return this.findSerializer(config, type, beanDesc);
    }
    
    protected JsonSerializer<?> _findInterfaceMapping(final Class<?> cls, final ClassKey key) {
        for (final Class<?> iface : cls.getInterfaces()) {
            key.reset(iface);
            JsonSerializer<?> ser = this._interfaceMappings.get(key);
            if (ser != null) {
                return ser;
            }
            ser = this._findInterfaceMapping(iface, key);
            if (ser != null) {
                return ser;
            }
        }
        return null;
    }
    
    protected void _addSerializer(final Class<?> cls, final JsonSerializer<?> ser) {
        final ClassKey key = new ClassKey(cls);
        if (cls.isInterface()) {
            if (this._interfaceMappings == null) {
                this._interfaceMappings = new HashMap<ClassKey, JsonSerializer<?>>();
            }
            this._interfaceMappings.put(key, ser);
        }
        else {
            if (this._classMappings == null) {
                this._classMappings = new HashMap<ClassKey, JsonSerializer<?>>();
            }
            this._classMappings.put(key, ser);
            if (cls == Enum.class) {
                this._hasEnumSerializer = true;
            }
        }
    }
}
