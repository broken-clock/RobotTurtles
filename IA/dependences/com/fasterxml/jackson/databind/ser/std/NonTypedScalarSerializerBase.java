// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ser.std;

import java.io.IOException;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;

@Deprecated
public abstract class NonTypedScalarSerializerBase<T> extends StdScalarSerializer<T>
{
    protected NonTypedScalarSerializerBase(final Class<T> t) {
        super(t);
    }
    
    protected NonTypedScalarSerializerBase(final Class<?> t, final boolean bogus) {
        super(t, bogus);
    }
    
    @Override
    public final void serializeWithType(final T value, final JsonGenerator gen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
        this.serialize(value, gen, provider);
    }
}
