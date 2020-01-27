// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationConfig;

public class NullifyingDeserializer extends StdDeserializer<Object>
{
    private static final long serialVersionUID = 1L;
    public static final NullifyingDeserializer instance;
    
    public NullifyingDeserializer() {
        super(Object.class);
    }
    
    @Override
    public Boolean supportsUpdate(final DeserializationConfig config) {
        return Boolean.FALSE;
    }
    
    @Override
    public Object deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.FIELD_NAME)) {
            while (true) {
                final JsonToken t = p.nextToken();
                if (t == null) {
                    break;
                }
                if (t == JsonToken.END_OBJECT) {
                    break;
                }
                p.skipChildren();
            }
        }
        else {
            p.skipChildren();
        }
        return null;
    }
    
    @Override
    public Object deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        switch (p.getCurrentTokenId()) {
            case 1:
            case 3:
            case 5: {
                return typeDeserializer.deserializeTypedFromAny(p, ctxt);
            }
            default: {
                return null;
            }
        }
    }
    
    static {
        instance = new NullifyingDeserializer();
    }
}
