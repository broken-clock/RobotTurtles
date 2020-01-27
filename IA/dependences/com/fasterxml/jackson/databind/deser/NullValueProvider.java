// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.DeserializationContext;

public interface NullValueProvider
{
    Object getNullValue(final DeserializationContext p0) throws JsonMappingException;
    
    AccessPattern getNullAccessPattern();
}
