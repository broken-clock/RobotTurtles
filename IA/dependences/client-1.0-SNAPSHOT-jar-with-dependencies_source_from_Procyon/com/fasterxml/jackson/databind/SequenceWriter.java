// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.databind.ser.impl.TypeWrappedSerializer;
import java.util.Iterator;
import java.util.Collection;
import com.fasterxml.jackson.databind.cfg.PackageVersion;
import com.fasterxml.jackson.core.Version;
import java.io.IOException;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import java.io.Flushable;
import java.io.Closeable;
import com.fasterxml.jackson.core.Versioned;

public class SequenceWriter implements Versioned, Closeable, Flushable
{
    protected final DefaultSerializerProvider _provider;
    protected final SerializationConfig _config;
    protected final JsonGenerator _generator;
    protected final JsonSerializer<Object> _rootSerializer;
    protected final TypeSerializer _typeSerializer;
    protected final boolean _closeGenerator;
    protected final boolean _cfgFlush;
    protected final boolean _cfgCloseCloseable;
    protected PropertySerializerMap _dynamicSerializers;
    protected boolean _openArray;
    protected boolean _closed;
    
    public SequenceWriter(final DefaultSerializerProvider prov, final JsonGenerator gen, final boolean closeGenerator, final ObjectWriter.Prefetch prefetch) throws IOException {
        this._provider = prov;
        this._generator = gen;
        this._closeGenerator = closeGenerator;
        this._rootSerializer = prefetch.getValueSerializer();
        this._typeSerializer = prefetch.getTypeSerializer();
        this._config = prov.getConfig();
        this._cfgFlush = this._config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE);
        this._cfgCloseCloseable = this._config.isEnabled(SerializationFeature.CLOSE_CLOSEABLE);
        this._dynamicSerializers = PropertySerializerMap.emptyForRootValues();
    }
    
    public SequenceWriter init(final boolean wrapInArray) throws IOException {
        if (wrapInArray) {
            this._generator.writeStartArray();
            this._openArray = true;
        }
        return this;
    }
    
    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }
    
    public SequenceWriter write(final Object value) throws IOException {
        if (value == null) {
            this._provider.serializeValue(this._generator, null);
            return this;
        }
        if (this._cfgCloseCloseable && value instanceof Closeable) {
            return this._writeCloseableValue(value);
        }
        JsonSerializer<Object> ser = this._rootSerializer;
        if (ser == null) {
            final Class<?> type = value.getClass();
            ser = this._dynamicSerializers.serializerFor(type);
            if (ser == null) {
                ser = this._findAndAddDynamic(type);
            }
        }
        this._provider.serializeValue(this._generator, value, null, ser);
        if (this._cfgFlush) {
            this._generator.flush();
        }
        return this;
    }
    
    public SequenceWriter write(final Object value, final JavaType type) throws IOException {
        if (value == null) {
            this._provider.serializeValue(this._generator, null);
            return this;
        }
        if (this._cfgCloseCloseable && value instanceof Closeable) {
            return this._writeCloseableValue(value, type);
        }
        JsonSerializer<Object> ser = this._dynamicSerializers.serializerFor(type.getRawClass());
        if (ser == null) {
            ser = this._findAndAddDynamic(type);
        }
        this._provider.serializeValue(this._generator, value, type, ser);
        if (this._cfgFlush) {
            this._generator.flush();
        }
        return this;
    }
    
    public SequenceWriter writeAll(final Object[] value) throws IOException {
        for (int i = 0, len = value.length; i < len; ++i) {
            this.write(value[i]);
        }
        return this;
    }
    
    public <C extends Collection<?>> SequenceWriter writeAll(final C container) throws IOException {
        for (final Object value : container) {
            this.write(value);
        }
        return this;
    }
    
    public SequenceWriter writeAll(final Iterable<?> iterable) throws IOException {
        for (final Object value : iterable) {
            this.write(value);
        }
        return this;
    }
    
    @Override
    public void flush() throws IOException {
        if (!this._closed) {
            this._generator.flush();
        }
    }
    
    @Override
    public void close() throws IOException {
        if (!this._closed) {
            this._closed = true;
            if (this._openArray) {
                this._openArray = false;
                this._generator.writeEndArray();
            }
            if (this._closeGenerator) {
                this._generator.close();
            }
        }
    }
    
    protected SequenceWriter _writeCloseableValue(final Object value) throws IOException {
        Closeable toClose = (Closeable)value;
        try {
            JsonSerializer<Object> ser = this._rootSerializer;
            if (ser == null) {
                final Class<?> type = value.getClass();
                ser = this._dynamicSerializers.serializerFor(type);
                if (ser == null) {
                    ser = this._findAndAddDynamic(type);
                }
            }
            this._provider.serializeValue(this._generator, value, null, ser);
            if (this._cfgFlush) {
                this._generator.flush();
            }
            final Closeable tmpToClose = toClose;
            toClose = null;
            tmpToClose.close();
        }
        finally {
            if (toClose != null) {
                try {
                    toClose.close();
                }
                catch (IOException ex) {}
            }
        }
        return this;
    }
    
    protected SequenceWriter _writeCloseableValue(final Object value, final JavaType type) throws IOException {
        Closeable toClose = (Closeable)value;
        try {
            JsonSerializer<Object> ser = this._dynamicSerializers.serializerFor(type.getRawClass());
            if (ser == null) {
                ser = this._findAndAddDynamic(type);
            }
            this._provider.serializeValue(this._generator, value, type, ser);
            if (this._cfgFlush) {
                this._generator.flush();
            }
            final Closeable tmpToClose = toClose;
            toClose = null;
            tmpToClose.close();
        }
        finally {
            if (toClose != null) {
                try {
                    toClose.close();
                }
                catch (IOException ex) {}
            }
        }
        return this;
    }
    
    private final JsonSerializer<Object> _findAndAddDynamic(final Class<?> type) throws JsonMappingException {
        PropertySerializerMap.SerializerAndMapResult result;
        if (this._typeSerializer == null) {
            result = this._dynamicSerializers.findAndAddRootValueSerializer(type, this._provider);
        }
        else {
            result = this._dynamicSerializers.addSerializer(type, new TypeWrappedSerializer(this._typeSerializer, this._provider.findValueSerializer(type, null)));
        }
        this._dynamicSerializers = result.map;
        return result.serializer;
    }
    
    private final JsonSerializer<Object> _findAndAddDynamic(final JavaType type) throws JsonMappingException {
        PropertySerializerMap.SerializerAndMapResult result;
        if (this._typeSerializer == null) {
            result = this._dynamicSerializers.findAndAddRootValueSerializer(type, this._provider);
        }
        else {
            result = this._dynamicSerializers.addSerializer(type, new TypeWrappedSerializer(this._typeSerializer, this._provider.findValueSerializer(type, null)));
        }
        this._dynamicSerializers = result.map;
        return result.serializer;
    }
}
