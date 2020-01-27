// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.jsonFormatVisitors;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JavaType;

public interface JsonFormatVisitable
{
    void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper p0, final JavaType p1) throws JsonMappingException;
}
