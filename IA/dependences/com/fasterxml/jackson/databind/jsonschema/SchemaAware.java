// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.jsonschema;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Type;
import com.fasterxml.jackson.databind.SerializerProvider;

public interface SchemaAware
{
    JsonNode getSchema(final SerializerProvider p0, final Type p1) throws JsonMappingException;
    
    JsonNode getSchema(final SerializerProvider p0, final Type p1, final boolean p2) throws JsonMappingException;
}
