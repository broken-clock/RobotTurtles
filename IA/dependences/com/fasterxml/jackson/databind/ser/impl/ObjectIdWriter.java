// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.databind.JavaType;

public final class ObjectIdWriter
{
    public final JavaType idType;
    public final SerializableString propertyName;
    public final ObjectIdGenerator<?> generator;
    public final JsonSerializer<Object> serializer;
    public final boolean alwaysAsId;
    
    protected ObjectIdWriter(final JavaType t, final SerializableString propName, final ObjectIdGenerator<?> gen, final JsonSerializer<?> ser, final boolean alwaysAsId) {
        this.idType = t;
        this.propertyName = propName;
        this.generator = gen;
        this.serializer = (JsonSerializer<Object>)ser;
        this.alwaysAsId = alwaysAsId;
    }
    
    public static ObjectIdWriter construct(final JavaType idType, final PropertyName propName, final ObjectIdGenerator<?> generator, final boolean alwaysAsId) {
        final String simpleName = (propName == null) ? null : propName.getSimpleName();
        final SerializableString serName = (simpleName == null) ? null : new SerializedString(simpleName);
        return new ObjectIdWriter(idType, serName, generator, null, alwaysAsId);
    }
    
    public ObjectIdWriter withSerializer(final JsonSerializer<?> ser) {
        return new ObjectIdWriter(this.idType, this.propertyName, this.generator, ser, this.alwaysAsId);
    }
    
    public ObjectIdWriter withAlwaysAsId(final boolean newState) {
        if (newState == this.alwaysAsId) {
            return this;
        }
        return new ObjectIdWriter(this.idType, this.propertyName, this.generator, this.serializer, newState);
    }
}
