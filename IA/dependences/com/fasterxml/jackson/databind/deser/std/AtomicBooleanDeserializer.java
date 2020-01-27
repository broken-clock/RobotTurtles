// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonParser;
import java.util.concurrent.atomic.AtomicBoolean;

public class AtomicBooleanDeserializer extends StdScalarDeserializer<AtomicBoolean>
{
    private static final long serialVersionUID = 1L;
    
    public AtomicBooleanDeserializer() {
        super(AtomicBoolean.class);
    }
    
    @Override
    public AtomicBoolean deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        return new AtomicBoolean(this._parseBooleanPrimitive(jp, ctxt));
    }
}
