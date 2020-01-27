// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Type;
import com.fasterxml.jackson.core.JsonGenerationException;
import java.io.IOException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import java.sql.Time;
import java.sql.Date;
import java.sql.Timestamp;
import java.io.File;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.UUID;
import java.util.Currency;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

public class StdJdkSerializers
{
    public static Collection<Map.Entry<Class<?>, Object>> all() {
        final HashMap<Class<?>, Object> sers = new HashMap<Class<?>, Object>();
        sers.put(URL.class, new ToStringSerializer(URL.class));
        sers.put(URI.class, new ToStringSerializer(URI.class));
        sers.put(Currency.class, new ToStringSerializer(Currency.class));
        sers.put(UUID.class, new UUIDSerializer());
        sers.put(Pattern.class, new ToStringSerializer(Pattern.class));
        sers.put(Locale.class, new ToStringSerializer(Locale.class));
        sers.put(AtomicBoolean.class, AtomicBooleanSerializer.class);
        sers.put(AtomicInteger.class, AtomicIntegerSerializer.class);
        sers.put(AtomicLong.class, AtomicLongSerializer.class);
        sers.put(File.class, FileSerializer.class);
        sers.put(Class.class, ClassSerializer.class);
        sers.put(Void.class, NullSerializer.instance);
        sers.put(Void.TYPE, NullSerializer.instance);
        try {
            sers.put(Timestamp.class, DateSerializer.instance);
            sers.put(Date.class, SqlDateSerializer.class);
            sers.put(Time.class, SqlTimeSerializer.class);
        }
        catch (NoClassDefFoundError noClassDefFoundError) {}
        return sers.entrySet();
    }
    
    public static class AtomicBooleanSerializer extends StdScalarSerializer<AtomicBoolean>
    {
        public AtomicBooleanSerializer() {
            super(AtomicBoolean.class, false);
        }
        
        @Override
        public void serialize(final AtomicBoolean value, final JsonGenerator gen, final SerializerProvider provider) throws IOException, JsonGenerationException {
            gen.writeBoolean(value.get());
        }
        
        @Override
        public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
            return this.createSchemaNode("boolean", true);
        }
        
        @Override
        public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
            visitor.expectBooleanFormat(typeHint);
        }
    }
    
    public static class AtomicIntegerSerializer extends StdScalarSerializer<AtomicInteger>
    {
        public AtomicIntegerSerializer() {
            super(AtomicInteger.class, false);
        }
        
        @Override
        public void serialize(final AtomicInteger value, final JsonGenerator gen, final SerializerProvider provider) throws IOException, JsonGenerationException {
            gen.writeNumber(value.get());
        }
        
        @Override
        public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
            return this.createSchemaNode("integer", true);
        }
        
        @Override
        public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
            this.visitIntFormat(visitor, typeHint, JsonParser.NumberType.INT);
        }
    }
    
    public static class AtomicLongSerializer extends StdScalarSerializer<AtomicLong>
    {
        public AtomicLongSerializer() {
            super(AtomicLong.class, false);
        }
        
        @Override
        public void serialize(final AtomicLong value, final JsonGenerator gen, final SerializerProvider provider) throws IOException, JsonGenerationException {
            gen.writeNumber(value.get());
        }
        
        @Override
        public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
            return this.createSchemaNode("integer", true);
        }
        
        @Override
        public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
            this.visitIntFormat(visitor, typeHint, JsonParser.NumberType.LONG);
        }
    }
}
