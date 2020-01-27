// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.jsonFormatVisitors;

import java.util.HashMap;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Map;

public enum JsonFormatTypes
{
    STRING, 
    NUMBER, 
    INTEGER, 
    BOOLEAN, 
    OBJECT, 
    ARRAY, 
    NULL, 
    ANY;
    
    private static final Map<String, JsonFormatTypes> _byLCName;
    
    @JsonValue
    public String value() {
        return this.name().toLowerCase();
    }
    
    @JsonCreator
    public static JsonFormatTypes forValue(final String s) {
        return JsonFormatTypes._byLCName.get(s);
    }
    
    static {
        _byLCName = new HashMap<String, JsonFormatTypes>();
        for (final JsonFormatTypes t : values()) {
            JsonFormatTypes._byLCName.put(t.name().toLowerCase(), t);
        }
    }
}
