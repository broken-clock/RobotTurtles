// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.JsonSerializable;

@JacksonStdImpl
public class SerializableSerializer extends StdSerializer<JsonSerializable>
{
    public static final SerializableSerializer instance;
    
    protected SerializableSerializer() {
        super(JsonSerializable.class);
    }
    
    @Override
    public boolean isEmpty(final SerializerProvider serializers, final JsonSerializable value) {
        return value instanceof JsonSerializable.Base && ((JsonSerializable.Base)value).isEmpty(serializers);
    }
    
    @Override
    public void serialize(final JsonSerializable value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        value.serialize(gen, serializers);
    }
    
    @Override
    public final void serializeWithType(final JsonSerializable value, final JsonGenerator gen, final SerializerProvider serializers, final TypeSerializer typeSer) throws IOException {
        value.serializeWithType(gen, serializers, typeSer);
    }
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        visitor.expectAnyFormat(typeHint);
    }
    
    static {
        instance = new SerializableSerializer();
    }
}
