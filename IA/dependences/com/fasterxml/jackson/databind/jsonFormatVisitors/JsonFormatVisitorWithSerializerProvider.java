// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.jsonFormatVisitors;

import com.fasterxml.jackson.databind.SerializerProvider;

public interface JsonFormatVisitorWithSerializerProvider
{
    SerializerProvider getProvider();
    
    void setProvider(final SerializerProvider p0);
}
