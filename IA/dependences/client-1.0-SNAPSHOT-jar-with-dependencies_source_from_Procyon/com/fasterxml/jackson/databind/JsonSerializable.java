// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;

public interface JsonSerializable
{
    void serialize(final JsonGenerator p0, final SerializerProvider p1) throws IOException;
    
    void serializeWithType(final JsonGenerator p0, final SerializerProvider p1, final TypeSerializer p2) throws IOException;
    
    public abstract static class Base implements JsonSerializable
    {
        public boolean isEmpty(final SerializerProvider serializers) {
            return false;
        }
    }
}
