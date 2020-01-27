// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;

@JacksonStdImpl
public class StringDeserializer extends StdScalarDeserializer<String>
{
    private static final long serialVersionUID = 1L;
    public static final StringDeserializer instance;
    
    public StringDeserializer() {
        super(String.class);
    }
    
    @Override
    public boolean isCachable() {
        return true;
    }
    
    @Override
    public Object getEmptyValue(final DeserializationContext ctxt) throws JsonMappingException {
        return "";
    }
    
    @Override
    public String deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            return p.getText();
        }
        final JsonToken t = p.getCurrentToken();
        if (t == JsonToken.START_ARRAY) {
            return this._deserializeFromArray(p, ctxt);
        }
        if (t != JsonToken.VALUE_EMBEDDED_OBJECT) {
            if (t.isScalarValue()) {
                final String text = p.getValueAsString();
                if (text != null) {
                    return text;
                }
            }
            return (String)ctxt.handleUnexpectedToken(this._valueClass, p);
        }
        final Object ob = p.getEmbeddedObject();
        if (ob == null) {
            return null;
        }
        if (ob instanceof byte[]) {
            return ctxt.getBase64Variant().encode((byte[])ob, false);
        }
        return ob.toString();
    }
    
    @Override
    public String deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        return this.deserialize(p, ctxt);
    }
    
    static {
        instance = new StringDeserializer();
    }
}
