// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Type;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import java.util.Map;

public class NumberSerializers
{
    protected NumberSerializers() {
    }
    
    public static void addAll(final Map<String, JsonSerializer<?>> allDeserializers) {
        allDeserializers.put(Integer.class.getName(), new IntegerSerializer(Integer.class));
        allDeserializers.put(Integer.TYPE.getName(), new IntegerSerializer(Integer.TYPE));
        allDeserializers.put(Long.class.getName(), new LongSerializer(Long.class));
        allDeserializers.put(Long.TYPE.getName(), new LongSerializer(Long.TYPE));
        allDeserializers.put(Byte.class.getName(), IntLikeSerializer.instance);
        allDeserializers.put(Byte.TYPE.getName(), IntLikeSerializer.instance);
        allDeserializers.put(Short.class.getName(), ShortSerializer.instance);
        allDeserializers.put(Short.TYPE.getName(), ShortSerializer.instance);
        allDeserializers.put(Double.class.getName(), new DoubleSerializer(Double.class));
        allDeserializers.put(Double.TYPE.getName(), new DoubleSerializer(Double.TYPE));
        allDeserializers.put(Float.class.getName(), FloatSerializer.instance);
        allDeserializers.put(Float.TYPE.getName(), FloatSerializer.instance);
    }
    
    protected abstract static class Base<T> extends StdScalarSerializer<T> implements ContextualSerializer
    {
        protected final JsonParser.NumberType _numberType;
        protected final String _schemaType;
        protected final boolean _isInt;
        
        protected Base(final Class<?> cls, final JsonParser.NumberType numberType, final String schemaType) {
            super(cls, false);
            this._numberType = numberType;
            this._schemaType = schemaType;
            this._isInt = (numberType == JsonParser.NumberType.INT || numberType == JsonParser.NumberType.LONG || numberType == JsonParser.NumberType.BIG_INTEGER);
        }
        
        @Override
        public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
            return this.createSchemaNode(this._schemaType, true);
        }
        
        @Override
        public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
            if (this._isInt) {
                this.visitIntFormat(visitor, typeHint, this._numberType);
            }
            else {
                this.visitFloatFormat(visitor, typeHint, this._numberType);
            }
        }
        
        @Override
        public JsonSerializer<?> createContextual(final SerializerProvider prov, final BeanProperty property) throws JsonMappingException {
            final JsonFormat.Value format = this.findFormatOverrides(prov, property, this.handledType());
            if (format != null) {
                switch (format.getShape()) {
                    case STRING: {
                        return ToStringSerializer.instance;
                    }
                }
            }
            return this;
        }
    }
    
    @JacksonStdImpl
    public static final class ShortSerializer extends Base<Object>
    {
        static final ShortSerializer instance;
        
        public ShortSerializer() {
            super(Short.class, JsonParser.NumberType.INT, "number");
        }
        
        @Override
        public void serialize(final Object value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
            gen.writeNumber((short)value);
        }
        
        static {
            instance = new ShortSerializer();
        }
    }
    
    @JacksonStdImpl
    public static final class IntegerSerializer extends Base<Object>
    {
        public IntegerSerializer(final Class<?> type) {
            super(type, JsonParser.NumberType.INT, "integer");
        }
        
        @Override
        public void serialize(final Object value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
            gen.writeNumber((int)value);
        }
        
        @Override
        public void serializeWithType(final Object value, final JsonGenerator gen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
            this.serialize(value, gen, provider);
        }
    }
    
    @JacksonStdImpl
    public static final class IntLikeSerializer extends Base<Object>
    {
        static final IntLikeSerializer instance;
        
        public IntLikeSerializer() {
            super(Number.class, JsonParser.NumberType.INT, "integer");
        }
        
        @Override
        public void serialize(final Object value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
            gen.writeNumber(((Number)value).intValue());
        }
        
        static {
            instance = new IntLikeSerializer();
        }
    }
    
    @JacksonStdImpl
    public static final class LongSerializer extends Base<Object>
    {
        public LongSerializer(final Class<?> cls) {
            super(cls, JsonParser.NumberType.LONG, "number");
        }
        
        @Override
        public void serialize(final Object value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
            gen.writeNumber((long)value);
        }
    }
    
    @JacksonStdImpl
    public static final class FloatSerializer extends Base<Object>
    {
        static final FloatSerializer instance;
        
        public FloatSerializer() {
            super(Float.class, JsonParser.NumberType.FLOAT, "number");
        }
        
        @Override
        public void serialize(final Object value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
            gen.writeNumber((float)value);
        }
        
        static {
            instance = new FloatSerializer();
        }
    }
    
    @JacksonStdImpl
    public static final class DoubleSerializer extends Base<Object>
    {
        public DoubleSerializer(final Class<?> cls) {
            super(cls, JsonParser.NumberType.DOUBLE, "number");
        }
        
        @Override
        public void serialize(final Object value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
            gen.writeNumber((double)value);
        }
        
        @Override
        public void serializeWithType(final Object value, final JsonGenerator gen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
            this.serialize(value, gen, provider);
        }
    }
}
