// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Type;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;

@JacksonStdImpl
public final class StringSerializer extends StdScalarSerializer<Object>
{
    private static final long serialVersionUID = 1L;
    
    public StringSerializer() {
        super(String.class, false);
    }
    
    @Override
    public boolean isEmpty(final SerializerProvider prov, final Object value) {
        final String str = (String)value;
        return str.length() == 0;
    }
    
    @Override
    public void serialize(final Object value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        gen.writeString((String)value);
    }
    
    @Override
    public final void serializeWithType(final Object value, final JsonGenerator gen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
        gen.writeString((String)value);
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
        return this.createSchemaNode("string", true);
    }
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        this.visitStringFormat(visitor, typeHint);
    }
}
