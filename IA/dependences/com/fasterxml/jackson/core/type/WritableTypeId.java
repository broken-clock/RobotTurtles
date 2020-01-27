// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.core.type;

import com.fasterxml.jackson.core.JsonToken;

public class WritableTypeId
{
    public Object forValue;
    public Class<?> forValueType;
    public Object id;
    public String asProperty;
    public Inclusion include;
    public JsonToken valueShape;
    public boolean wrapperWritten;
    public Object extra;
    
    public WritableTypeId() {
    }
    
    public WritableTypeId(final Object value, final JsonToken valueShape0) {
        this(value, valueShape0, null);
    }
    
    public WritableTypeId(final Object value, final Class<?> valueType0, final JsonToken valueShape0) {
        this(value, valueShape0, null);
        this.forValueType = valueType0;
    }
    
    public WritableTypeId(final Object value, final JsonToken valueShape0, final Object id0) {
        this.forValue = value;
        this.id = id0;
        this.valueShape = valueShape0;
    }
    
    public enum Inclusion
    {
        WRAPPER_ARRAY, 
        WRAPPER_OBJECT, 
        METADATA_PROPERTY, 
        PAYLOAD_PROPERTY, 
        PARENT_PROPERTY;
        
        public boolean requiresObjectContext() {
            return this == Inclusion.METADATA_PROPERTY || this == Inclusion.PAYLOAD_PROPERTY;
        }
    }
}
