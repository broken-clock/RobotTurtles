// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.core.SerializableString;
import java.io.IOException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;

public final class WritableObjectId
{
    public final ObjectIdGenerator<?> generator;
    public Object id;
    protected boolean idWritten;
    
    public WritableObjectId(final ObjectIdGenerator<?> generator) {
        this.idWritten = false;
        this.generator = generator;
    }
    
    public boolean writeAsId(final JsonGenerator gen, final SerializerProvider provider, final ObjectIdWriter w) throws IOException {
        if (this.id != null && (this.idWritten || w.alwaysAsId)) {
            if (gen.canWriteObjectId()) {
                gen.writeObjectRef(String.valueOf(this.id));
            }
            else {
                w.serializer.serialize(this.id, gen, provider);
            }
            return true;
        }
        return false;
    }
    
    public Object generateId(final Object forPojo) {
        if (this.id == null) {
            this.id = this.generator.generateId(forPojo);
        }
        return this.id;
    }
    
    public void writeAsField(final JsonGenerator gen, final SerializerProvider provider, final ObjectIdWriter w) throws IOException {
        this.idWritten = true;
        if (gen.canWriteObjectId()) {
            gen.writeObjectId(String.valueOf(this.id));
            return;
        }
        final SerializableString name = w.propertyName;
        if (name != null) {
            gen.writeFieldName(name);
            w.serializer.serialize(this.id, gen, provider);
        }
    }
}
