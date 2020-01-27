// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Type;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class UnknownSerializer extends StdSerializer<Object>
{
    public UnknownSerializer() {
        super(Object.class);
    }
    
    public UnknownSerializer(final Class<?> cls) {
        super(cls, false);
    }
    
    @Override
    public void serialize(final Object value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        if (provider.isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS)) {
            this.failForEmpty(provider, value);
        }
        gen.writeStartObject();
        gen.writeEndObject();
    }
    
    @Override
    public final void serializeWithType(final Object value, final JsonGenerator gen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
        if (provider.isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS)) {
            this.failForEmpty(provider, value);
        }
        final WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen, typeSer.typeId(value, JsonToken.START_OBJECT));
        typeSer.writeTypeSuffix(gen, typeIdDef);
    }
    
    @Override
    public boolean isEmpty(final SerializerProvider provider, final Object value) {
        return true;
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) throws JsonMappingException {
        return null;
    }
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        visitor.expectAnyFormat(typeHint);
    }
    
    protected void failForEmpty(final SerializerProvider prov, final Object value) throws JsonMappingException {
        prov.reportBadDefinition(this.handledType(), String.format("No serializer found for class %s and no properties discovered to create BeanSerializer (to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS)", value.getClass().getName()));
    }
}
