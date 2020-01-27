// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
public class ByteArraySerializer extends StdSerializer<byte[]>
{
    private static final long serialVersionUID = 1L;
    
    public ByteArraySerializer() {
        super(byte[].class);
    }
    
    @Override
    public boolean isEmpty(final SerializerProvider prov, final byte[] value) {
        return value.length == 0;
    }
    
    @Override
    public void serialize(final byte[] value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
        g.writeBinary(provider.getConfig().getBase64Variant(), value, 0, value.length);
    }
    
    @Override
    public void serializeWithType(final byte[] value, final JsonGenerator g, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
        final WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(value, JsonToken.VALUE_EMBEDDED_OBJECT));
        g.writeBinary(provider.getConfig().getBase64Variant(), value, 0, value.length);
        typeSer.writeTypeSuffix(g, typeIdDef);
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
        final ObjectNode o = this.createSchemaNode("array", true);
        final ObjectNode itemSchema = this.createSchemaNode("byte");
        return o.set("items", itemSchema);
    }
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        final JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
        if (v2 != null) {
            v2.itemsFormat(JsonFormatTypes.INTEGER);
        }
    }
}
