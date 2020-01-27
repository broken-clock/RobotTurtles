// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Type;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import java.util.HashMap;

public class StdArraySerializers
{
    protected static final HashMap<String, JsonSerializer<?>> _arraySerializers;
    
    protected StdArraySerializers() {
    }
    
    public static JsonSerializer<?> findStandardImpl(final Class<?> cls) {
        return StdArraySerializers._arraySerializers.get(cls.getName());
    }
    
    static {
        (_arraySerializers = new HashMap<String, JsonSerializer<?>>()).put(boolean[].class.getName(), new BooleanArraySerializer());
        StdArraySerializers._arraySerializers.put(byte[].class.getName(), new ByteArraySerializer());
        StdArraySerializers._arraySerializers.put(char[].class.getName(), new CharArraySerializer());
        StdArraySerializers._arraySerializers.put(short[].class.getName(), new ShortArraySerializer());
        StdArraySerializers._arraySerializers.put(int[].class.getName(), new IntArraySerializer());
        StdArraySerializers._arraySerializers.put(long[].class.getName(), new LongArraySerializer());
        StdArraySerializers._arraySerializers.put(float[].class.getName(), new FloatArraySerializer());
        StdArraySerializers._arraySerializers.put(double[].class.getName(), new DoubleArraySerializer());
    }
    
    protected abstract static class TypedPrimitiveArraySerializer<T> extends ArraySerializerBase<T>
    {
        protected TypedPrimitiveArraySerializer(final Class<T> cls) {
            super(cls);
        }
        
        protected TypedPrimitiveArraySerializer(final TypedPrimitiveArraySerializer<T> src, final BeanProperty prop, final Boolean unwrapSingle) {
            super(src, prop, unwrapSingle);
        }
        
        public final ContainerSerializer<?> _withValueTypeSerializer(final TypeSerializer vts) {
            return this;
        }
    }
    
    @JacksonStdImpl
    public static class BooleanArraySerializer extends ArraySerializerBase<boolean[]>
    {
        private static final JavaType VALUE_TYPE;
        
        public BooleanArraySerializer() {
            super(boolean[].class);
        }
        
        protected BooleanArraySerializer(final BooleanArraySerializer src, final BeanProperty prop, final Boolean unwrapSingle) {
            super(src, prop, unwrapSingle);
        }
        
        @Override
        public JsonSerializer<?> _withResolved(final BeanProperty prop, final Boolean unwrapSingle) {
            return new BooleanArraySerializer(this, prop, unwrapSingle);
        }
        
        public ContainerSerializer<?> _withValueTypeSerializer(final TypeSerializer vts) {
            return this;
        }
        
        @Override
        public JavaType getContentType() {
            return BooleanArraySerializer.VALUE_TYPE;
        }
        
        @Override
        public JsonSerializer<?> getContentSerializer() {
            return null;
        }
        
        @Override
        public boolean isEmpty(final SerializerProvider prov, final boolean[] value) {
            return value.length == 0;
        }
        
        @Override
        public boolean hasSingleElement(final boolean[] value) {
            return value.length == 1;
        }
        
        @Override
        public final void serialize(final boolean[] value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
            final int len = value.length;
            if (len == 1 && this._shouldUnwrapSingle(provider)) {
                this.serializeContents(value, g, provider);
                return;
            }
            g.writeStartArray(len);
            g.setCurrentValue(value);
            this.serializeContents(value, g, provider);
            g.writeEndArray();
        }
        
        public void serializeContents(final boolean[] value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
            for (int i = 0, len = value.length; i < len; ++i) {
                g.writeBoolean(value[i]);
            }
        }
        
        @Override
        public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
            final ObjectNode o = this.createSchemaNode("array", true);
            o.set("items", this.createSchemaNode("boolean"));
            return o;
        }
        
        @Override
        public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
            this.visitArrayFormat(visitor, typeHint, JsonFormatTypes.BOOLEAN);
        }
        
        static {
            VALUE_TYPE = TypeFactory.defaultInstance().uncheckedSimpleType(Boolean.class);
        }
    }
    
    @JacksonStdImpl
    public static class ShortArraySerializer extends TypedPrimitiveArraySerializer<short[]>
    {
        private static final JavaType VALUE_TYPE;
        
        public ShortArraySerializer() {
            super(short[].class);
        }
        
        public ShortArraySerializer(final ShortArraySerializer src, final BeanProperty prop, final Boolean unwrapSingle) {
            super(src, prop, unwrapSingle);
        }
        
        @Override
        public JsonSerializer<?> _withResolved(final BeanProperty prop, final Boolean unwrapSingle) {
            return new ShortArraySerializer(this, prop, unwrapSingle);
        }
        
        @Override
        public JavaType getContentType() {
            return ShortArraySerializer.VALUE_TYPE;
        }
        
        @Override
        public JsonSerializer<?> getContentSerializer() {
            return null;
        }
        
        @Override
        public boolean isEmpty(final SerializerProvider prov, final short[] value) {
            return value.length == 0;
        }
        
        @Override
        public boolean hasSingleElement(final short[] value) {
            return value.length == 1;
        }
        
        @Override
        public final void serialize(final short[] value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
            final int len = value.length;
            if (len == 1 && this._shouldUnwrapSingle(provider)) {
                this.serializeContents(value, g, provider);
                return;
            }
            g.writeStartArray(len);
            g.setCurrentValue(value);
            this.serializeContents(value, g, provider);
            g.writeEndArray();
        }
        
        public void serializeContents(final short[] value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
            for (int i = 0, len = value.length; i < len; ++i) {
                g.writeNumber((int)value[i]);
            }
        }
        
        @Override
        public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
            final ObjectNode o = this.createSchemaNode("array", true);
            return o.set("items", this.createSchemaNode("integer"));
        }
        
        @Override
        public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
            this.visitArrayFormat(visitor, typeHint, JsonFormatTypes.INTEGER);
        }
        
        static {
            VALUE_TYPE = TypeFactory.defaultInstance().uncheckedSimpleType(Short.TYPE);
        }
    }
    
    @JacksonStdImpl
    public static class CharArraySerializer extends StdSerializer<char[]>
    {
        public CharArraySerializer() {
            super(char[].class);
        }
        
        @Override
        public boolean isEmpty(final SerializerProvider prov, final char[] value) {
            return value.length == 0;
        }
        
        @Override
        public void serialize(final char[] value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
            if (provider.isEnabled(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS)) {
                g.writeStartArray(value.length);
                g.setCurrentValue(value);
                this._writeArrayContents(g, value);
                g.writeEndArray();
            }
            else {
                g.writeString(value, 0, value.length);
            }
        }
        
        @Override
        public void serializeWithType(final char[] value, final JsonGenerator g, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
            final boolean asArray = provider.isEnabled(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS);
            WritableTypeId typeIdDef;
            if (asArray) {
                typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(value, JsonToken.START_ARRAY));
                this._writeArrayContents(g, value);
            }
            else {
                typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(value, JsonToken.VALUE_STRING));
                g.writeString(value, 0, value.length);
            }
            typeSer.writeTypeSuffix(g, typeIdDef);
        }
        
        private final void _writeArrayContents(final JsonGenerator g, final char[] value) throws IOException {
            for (int i = 0, len = value.length; i < len; ++i) {
                g.writeString(value, i, 1);
            }
        }
        
        @Override
        public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
            final ObjectNode o = this.createSchemaNode("array", true);
            final ObjectNode itemSchema = this.createSchemaNode("string");
            itemSchema.put("type", "string");
            return o.set("items", itemSchema);
        }
        
        @Override
        public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
            this.visitArrayFormat(visitor, typeHint, JsonFormatTypes.STRING);
        }
    }
    
    @JacksonStdImpl
    public static class IntArraySerializer extends ArraySerializerBase<int[]>
    {
        private static final JavaType VALUE_TYPE;
        
        public IntArraySerializer() {
            super(int[].class);
        }
        
        protected IntArraySerializer(final IntArraySerializer src, final BeanProperty prop, final Boolean unwrapSingle) {
            super(src, prop, unwrapSingle);
        }
        
        @Override
        public JsonSerializer<?> _withResolved(final BeanProperty prop, final Boolean unwrapSingle) {
            return new IntArraySerializer(this, prop, unwrapSingle);
        }
        
        public ContainerSerializer<?> _withValueTypeSerializer(final TypeSerializer vts) {
            return this;
        }
        
        @Override
        public JavaType getContentType() {
            return IntArraySerializer.VALUE_TYPE;
        }
        
        @Override
        public JsonSerializer<?> getContentSerializer() {
            return null;
        }
        
        @Override
        public boolean isEmpty(final SerializerProvider prov, final int[] value) {
            return value.length == 0;
        }
        
        @Override
        public boolean hasSingleElement(final int[] value) {
            return value.length == 1;
        }
        
        @Override
        public final void serialize(final int[] value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
            final int len = value.length;
            if (len == 1 && this._shouldUnwrapSingle(provider)) {
                this.serializeContents(value, g, provider);
                return;
            }
            g.setCurrentValue(value);
            g.writeArray(value, 0, value.length);
        }
        
        public void serializeContents(final int[] value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
            for (int i = 0, len = value.length; i < len; ++i) {
                g.writeNumber(value[i]);
            }
        }
        
        @Override
        public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
            return this.createSchemaNode("array", true).set("items", this.createSchemaNode("integer"));
        }
        
        @Override
        public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
            this.visitArrayFormat(visitor, typeHint, JsonFormatTypes.INTEGER);
        }
        
        static {
            VALUE_TYPE = TypeFactory.defaultInstance().uncheckedSimpleType(Integer.TYPE);
        }
    }
    
    @JacksonStdImpl
    public static class LongArraySerializer extends TypedPrimitiveArraySerializer<long[]>
    {
        private static final JavaType VALUE_TYPE;
        
        public LongArraySerializer() {
            super(long[].class);
        }
        
        public LongArraySerializer(final LongArraySerializer src, final BeanProperty prop, final Boolean unwrapSingle) {
            super(src, prop, unwrapSingle);
        }
        
        @Override
        public JsonSerializer<?> _withResolved(final BeanProperty prop, final Boolean unwrapSingle) {
            return new LongArraySerializer(this, prop, unwrapSingle);
        }
        
        @Override
        public JavaType getContentType() {
            return LongArraySerializer.VALUE_TYPE;
        }
        
        @Override
        public JsonSerializer<?> getContentSerializer() {
            return null;
        }
        
        @Override
        public boolean isEmpty(final SerializerProvider prov, final long[] value) {
            return value.length == 0;
        }
        
        @Override
        public boolean hasSingleElement(final long[] value) {
            return value.length == 1;
        }
        
        @Override
        public final void serialize(final long[] value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
            final int len = value.length;
            if (len == 1 && this._shouldUnwrapSingle(provider)) {
                this.serializeContents(value, g, provider);
                return;
            }
            g.setCurrentValue(value);
            g.writeArray(value, 0, value.length);
        }
        
        public void serializeContents(final long[] value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
            for (int i = 0, len = value.length; i < len; ++i) {
                g.writeNumber(value[i]);
            }
        }
        
        @Override
        public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
            return this.createSchemaNode("array", true).set("items", this.createSchemaNode("number", true));
        }
        
        @Override
        public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
            this.visitArrayFormat(visitor, typeHint, JsonFormatTypes.NUMBER);
        }
        
        static {
            VALUE_TYPE = TypeFactory.defaultInstance().uncheckedSimpleType(Long.TYPE);
        }
    }
    
    @JacksonStdImpl
    public static class FloatArraySerializer extends TypedPrimitiveArraySerializer<float[]>
    {
        private static final JavaType VALUE_TYPE;
        
        public FloatArraySerializer() {
            super(float[].class);
        }
        
        public FloatArraySerializer(final FloatArraySerializer src, final BeanProperty prop, final Boolean unwrapSingle) {
            super(src, prop, unwrapSingle);
        }
        
        @Override
        public JsonSerializer<?> _withResolved(final BeanProperty prop, final Boolean unwrapSingle) {
            return new FloatArraySerializer(this, prop, unwrapSingle);
        }
        
        @Override
        public JavaType getContentType() {
            return FloatArraySerializer.VALUE_TYPE;
        }
        
        @Override
        public JsonSerializer<?> getContentSerializer() {
            return null;
        }
        
        @Override
        public boolean isEmpty(final SerializerProvider prov, final float[] value) {
            return value.length == 0;
        }
        
        @Override
        public boolean hasSingleElement(final float[] value) {
            return value.length == 1;
        }
        
        @Override
        public final void serialize(final float[] value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
            final int len = value.length;
            if (len == 1 && this._shouldUnwrapSingle(provider)) {
                this.serializeContents(value, g, provider);
                return;
            }
            g.writeStartArray(len);
            g.setCurrentValue(value);
            this.serializeContents(value, g, provider);
            g.writeEndArray();
        }
        
        public void serializeContents(final float[] value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
            for (int i = 0, len = value.length; i < len; ++i) {
                g.writeNumber(value[i]);
            }
        }
        
        @Override
        public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
            return this.createSchemaNode("array", true).set("items", this.createSchemaNode("number"));
        }
        
        @Override
        public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
            this.visitArrayFormat(visitor, typeHint, JsonFormatTypes.NUMBER);
        }
        
        static {
            VALUE_TYPE = TypeFactory.defaultInstance().uncheckedSimpleType(Float.TYPE);
        }
    }
    
    @JacksonStdImpl
    public static class DoubleArraySerializer extends ArraySerializerBase<double[]>
    {
        private static final JavaType VALUE_TYPE;
        
        public DoubleArraySerializer() {
            super(double[].class);
        }
        
        protected DoubleArraySerializer(final DoubleArraySerializer src, final BeanProperty prop, final Boolean unwrapSingle) {
            super(src, prop, unwrapSingle);
        }
        
        @Override
        public JsonSerializer<?> _withResolved(final BeanProperty prop, final Boolean unwrapSingle) {
            return new DoubleArraySerializer(this, prop, unwrapSingle);
        }
        
        public ContainerSerializer<?> _withValueTypeSerializer(final TypeSerializer vts) {
            return this;
        }
        
        @Override
        public JavaType getContentType() {
            return DoubleArraySerializer.VALUE_TYPE;
        }
        
        @Override
        public JsonSerializer<?> getContentSerializer() {
            return null;
        }
        
        @Override
        public boolean isEmpty(final SerializerProvider prov, final double[] value) {
            return value.length == 0;
        }
        
        @Override
        public boolean hasSingleElement(final double[] value) {
            return value.length == 1;
        }
        
        @Override
        public final void serialize(final double[] value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
            final int len = value.length;
            if (len == 1 && this._shouldUnwrapSingle(provider)) {
                this.serializeContents(value, g, provider);
                return;
            }
            g.setCurrentValue(value);
            g.writeArray(value, 0, value.length);
        }
        
        public void serializeContents(final double[] value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
            for (int i = 0, len = value.length; i < len; ++i) {
                g.writeNumber(value[i]);
            }
        }
        
        @Override
        public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
            return this.createSchemaNode("array", true).set("items", this.createSchemaNode("number"));
        }
        
        @Override
        public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
            this.visitArrayFormat(visitor, typeHint, JsonFormatTypes.NUMBER);
        }
        
        static {
            VALUE_TYPE = TypeFactory.defaultInstance().uncheckedSimpleType(Double.TYPE);
        }
    }
}
