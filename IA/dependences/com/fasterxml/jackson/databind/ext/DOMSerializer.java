// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ext;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Type;
import com.fasterxml.jackson.core.JsonGenerationException;
import java.io.IOException;
import org.w3c.dom.ls.LSSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.Node;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class DOMSerializer extends StdSerializer<Node>
{
    protected final DOMImplementationLS _domImpl;
    
    public DOMSerializer() {
        super(Node.class);
        DOMImplementationRegistry registry;
        try {
            registry = DOMImplementationRegistry.newInstance();
        }
        catch (Exception e) {
            throw new IllegalStateException("Could not instantiate DOMImplementationRegistry: " + e.getMessage(), e);
        }
        this._domImpl = (DOMImplementationLS)registry.getDOMImplementation("LS");
    }
    
    @Override
    public void serialize(final Node value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        if (this._domImpl == null) {
            throw new IllegalStateException("Could not find DOM LS");
        }
        final LSSerializer writer = this._domImpl.createLSSerializer();
        jgen.writeString(writer.writeToString(value));
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
        return this.createSchemaNode("string", true);
    }
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        if (visitor != null) {
            visitor.expectAnyFormat(typeHint);
        }
    }
}
