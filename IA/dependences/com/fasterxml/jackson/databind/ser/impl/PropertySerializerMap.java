// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.impl;

import java.util.Arrays;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.JsonSerializer;

public abstract class PropertySerializerMap
{
    protected final boolean _resetWhenFull;
    
    protected PropertySerializerMap(final boolean resetWhenFull) {
        this._resetWhenFull = resetWhenFull;
    }
    
    protected PropertySerializerMap(final PropertySerializerMap base) {
        this._resetWhenFull = base._resetWhenFull;
    }
    
    public abstract JsonSerializer<Object> serializerFor(final Class<?> p0);
    
    public final SerializerAndMapResult findAndAddPrimarySerializer(final Class<?> type, final SerializerProvider provider, final BeanProperty property) throws JsonMappingException {
        final JsonSerializer<Object> serializer = provider.findPrimaryPropertySerializer(type, property);
        return new SerializerAndMapResult(serializer, this.newWith(type, serializer));
    }
    
    public final SerializerAndMapResult findAndAddPrimarySerializer(final JavaType type, final SerializerProvider provider, final BeanProperty property) throws JsonMappingException {
        final JsonSerializer<Object> serializer = provider.findPrimaryPropertySerializer(type, property);
        return new SerializerAndMapResult(serializer, this.newWith(type.getRawClass(), serializer));
    }
    
    public final SerializerAndMapResult findAndAddSecondarySerializer(final Class<?> type, final SerializerProvider provider, final BeanProperty property) throws JsonMappingException {
        final JsonSerializer<Object> serializer = provider.findValueSerializer(type, property);
        return new SerializerAndMapResult(serializer, this.newWith(type, serializer));
    }
    
    public final SerializerAndMapResult findAndAddSecondarySerializer(final JavaType type, final SerializerProvider provider, final BeanProperty property) throws JsonMappingException {
        final JsonSerializer<Object> serializer = provider.findValueSerializer(type, property);
        return new SerializerAndMapResult(serializer, this.newWith(type.getRawClass(), serializer));
    }
    
    public final SerializerAndMapResult findAndAddRootValueSerializer(final Class<?> type, final SerializerProvider provider) throws JsonMappingException {
        final JsonSerializer<Object> serializer = provider.findTypedValueSerializer(type, false, null);
        return new SerializerAndMapResult(serializer, this.newWith(type, serializer));
    }
    
    public final SerializerAndMapResult findAndAddRootValueSerializer(final JavaType type, final SerializerProvider provider) throws JsonMappingException {
        final JsonSerializer<Object> serializer = provider.findTypedValueSerializer(type, false, null);
        return new SerializerAndMapResult(serializer, this.newWith(type.getRawClass(), serializer));
    }
    
    public final SerializerAndMapResult findAndAddKeySerializer(final Class<?> type, final SerializerProvider provider, final BeanProperty property) throws JsonMappingException {
        final JsonSerializer<Object> serializer = provider.findKeySerializer(type, property);
        return new SerializerAndMapResult(serializer, this.newWith(type, serializer));
    }
    
    public final SerializerAndMapResult addSerializer(final Class<?> type, final JsonSerializer<Object> serializer) {
        return new SerializerAndMapResult(serializer, this.newWith(type, serializer));
    }
    
    public final SerializerAndMapResult addSerializer(final JavaType type, final JsonSerializer<Object> serializer) {
        return new SerializerAndMapResult(serializer, this.newWith(type.getRawClass(), serializer));
    }
    
    public abstract PropertySerializerMap newWith(final Class<?> p0, final JsonSerializer<Object> p1);
    
    @Deprecated
    public static PropertySerializerMap emptyMap() {
        return emptyForProperties();
    }
    
    public static PropertySerializerMap emptyForProperties() {
        return Empty.FOR_PROPERTIES;
    }
    
    public static PropertySerializerMap emptyForRootValues() {
        return Empty.FOR_ROOT_VALUES;
    }
    
    public static final class SerializerAndMapResult
    {
        public final JsonSerializer<Object> serializer;
        public final PropertySerializerMap map;
        
        public SerializerAndMapResult(final JsonSerializer<Object> serializer, final PropertySerializerMap map) {
            this.serializer = serializer;
            this.map = map;
        }
    }
    
    private static final class TypeAndSerializer
    {
        public final Class<?> type;
        public final JsonSerializer<Object> serializer;
        
        public TypeAndSerializer(final Class<?> type, final JsonSerializer<Object> serializer) {
            this.type = type;
            this.serializer = serializer;
        }
    }
    
    private static final class Empty extends PropertySerializerMap
    {
        public static final Empty FOR_PROPERTIES;
        public static final Empty FOR_ROOT_VALUES;
        
        protected Empty(final boolean resetWhenFull) {
            super(resetWhenFull);
        }
        
        @Override
        public JsonSerializer<Object> serializerFor(final Class<?> type) {
            return null;
        }
        
        @Override
        public PropertySerializerMap newWith(final Class<?> type, final JsonSerializer<Object> serializer) {
            return new Single(this, type, serializer);
        }
        
        static {
            FOR_PROPERTIES = new Empty(false);
            FOR_ROOT_VALUES = new Empty(true);
        }
    }
    
    private static final class Single extends PropertySerializerMap
    {
        private final Class<?> _type;
        private final JsonSerializer<Object> _serializer;
        
        public Single(final PropertySerializerMap base, final Class<?> type, final JsonSerializer<Object> serializer) {
            super(base);
            this._type = type;
            this._serializer = serializer;
        }
        
        @Override
        public JsonSerializer<Object> serializerFor(final Class<?> type) {
            if (type == this._type) {
                return this._serializer;
            }
            return null;
        }
        
        @Override
        public PropertySerializerMap newWith(final Class<?> type, final JsonSerializer<Object> serializer) {
            return new Double(this, this._type, this._serializer, type, serializer);
        }
    }
    
    private static final class Double extends PropertySerializerMap
    {
        private final Class<?> _type1;
        private final Class<?> _type2;
        private final JsonSerializer<Object> _serializer1;
        private final JsonSerializer<Object> _serializer2;
        
        public Double(final PropertySerializerMap base, final Class<?> type1, final JsonSerializer<Object> serializer1, final Class<?> type2, final JsonSerializer<Object> serializer2) {
            super(base);
            this._type1 = type1;
            this._serializer1 = serializer1;
            this._type2 = type2;
            this._serializer2 = serializer2;
        }
        
        @Override
        public JsonSerializer<Object> serializerFor(final Class<?> type) {
            if (type == this._type1) {
                return this._serializer1;
            }
            if (type == this._type2) {
                return this._serializer2;
            }
            return null;
        }
        
        @Override
        public PropertySerializerMap newWith(final Class<?> type, final JsonSerializer<Object> serializer) {
            final TypeAndSerializer[] ts = { new TypeAndSerializer(this._type1, this._serializer1), new TypeAndSerializer(this._type2, this._serializer2), new TypeAndSerializer(type, serializer) };
            return new Multi(this, ts);
        }
    }
    
    private static final class Multi extends PropertySerializerMap
    {
        private static final int MAX_ENTRIES = 8;
        private final TypeAndSerializer[] _entries;
        
        public Multi(final PropertySerializerMap base, final TypeAndSerializer[] entries) {
            super(base);
            this._entries = entries;
        }
        
        @Override
        public JsonSerializer<Object> serializerFor(final Class<?> type) {
            for (int i = 0, len = this._entries.length; i < len; ++i) {
                final TypeAndSerializer entry = this._entries[i];
                if (entry.type == type) {
                    return entry.serializer;
                }
            }
            return null;
        }
        
        @Override
        public PropertySerializerMap newWith(final Class<?> type, final JsonSerializer<Object> serializer) {
            final int len = this._entries.length;
            if (len != 8) {
                final TypeAndSerializer[] entries = Arrays.copyOf(this._entries, len + 1);
                entries[len] = new TypeAndSerializer(type, serializer);
                return new Multi(this, entries);
            }
            if (this._resetWhenFull) {
                return new Single(this, type, serializer);
            }
            return this;
        }
    }
}
