// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.impl;

import java.util.Collection;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import java.util.List;
import com.fasterxml.jackson.databind.ser.std.StaticListSerializerBase;

@JacksonStdImpl
public final class IndexedStringListSerializer extends StaticListSerializerBase<List<String>>
{
    private static final long serialVersionUID = 1L;
    public static final IndexedStringListSerializer instance;
    
    protected IndexedStringListSerializer() {
        super(List.class);
    }
    
    public IndexedStringListSerializer(final IndexedStringListSerializer src, final Boolean unwrapSingle) {
        super(src, unwrapSingle);
    }
    
    @Override
    public JsonSerializer<?> _withResolved(final BeanProperty prop, final Boolean unwrapSingle) {
        return new IndexedStringListSerializer(this, unwrapSingle);
    }
    
    @Override
    protected JsonNode contentSchema() {
        return this.createSchemaNode("string", true);
    }
    
    @Override
    protected void acceptContentVisitor(final JsonArrayFormatVisitor visitor) throws JsonMappingException {
        visitor.itemsFormat(JsonFormatTypes.STRING);
    }
    
    @Override
    public void serialize(final List<String> value, final JsonGenerator g, final SerializerProvider provider) throws IOException {
        final int len = value.size();
        if (len == 1 && ((this._unwrapSingle == null && provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)) || this._unwrapSingle == Boolean.TRUE)) {
            this.serializeContents(value, g, provider, 1);
            return;
        }
        g.writeStartArray(len);
        this.serializeContents(value, g, provider, len);
        g.writeEndArray();
    }
    
    @Override
    public void serializeWithType(final List<String> value, final JsonGenerator g, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
        final WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(value, JsonToken.START_ARRAY));
        this.serializeContents(value, g, provider, value.size());
        typeSer.writeTypeSuffix(g, typeIdDef);
    }
    
    private final void serializeContents(final List<String> value, final JsonGenerator g, final SerializerProvider provider, final int len) throws IOException {
        g.setCurrentValue(value);
        int i = 0;
        try {
            while (i < len) {
                final String str = value.get(i);
                if (str == null) {
                    provider.defaultSerializeNull(g);
                }
                else {
                    g.writeString(str);
                }
                ++i;
            }
        }
        catch (Exception e) {
            this.wrapAndThrow(provider, e, value, i);
        }
    }
    
    static {
        instance = new IndexedStringListSerializer();
    }
}
