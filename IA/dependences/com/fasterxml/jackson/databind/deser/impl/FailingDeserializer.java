// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser.impl;

import java.io.IOException;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class FailingDeserializer extends StdDeserializer<Object>
{
    private static final long serialVersionUID = 1L;
    protected final String _message;
    
    public FailingDeserializer(final String m) {
        super(Object.class);
        this._message = m;
    }
    
    @Override
    public Object deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        ctxt.reportInputMismatch(this, this._message, new Object[0]);
        return null;
    }
}
