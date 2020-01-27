// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Type;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;

@JacksonStdImpl
public class ToStringSerializer extends StdSerializer<Object>
{
    public static final ToStringSerializer instance;
    
    public ToStringSerializer() {
        super(Object.class);
    }
    
    public ToStringSerializer(final Class<?> handledType) {
        super(handledType, false);
    }
    
    @Override
    public boolean isEmpty(final SerializerProvider prov, final Object value) {
        return value.toString().isEmpty();
    }
    
    @Override
    public void serialize(final Object value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        gen.writeString(value.toString());
    }
    
    @Override
    public void serializeWithType(final Object value, final JsonGenerator g, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
        final WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(value, JsonToken.VALUE_STRING));
        this.serialize(value, g, provider);
        typeSer.writeTypeSuffix(g, typeIdDef);
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) throws JsonMappingException {
        return this.createSchemaNode("string", true);
    }
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        this.visitStringFormat(visitor, typeHint);
    }
    
    static {
        instance = new ToStringSerializer();
    }
}
