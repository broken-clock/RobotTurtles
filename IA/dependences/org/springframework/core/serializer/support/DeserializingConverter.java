// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.serializer.support;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import org.springframework.util.Assert;
import org.springframework.core.serializer.DefaultDeserializer;
import org.springframework.core.serializer.Deserializer;
import org.springframework.core.convert.converter.Converter;

public class DeserializingConverter implements Converter<byte[], Object>
{
    private final Deserializer<Object> deserializer;
    
    public DeserializingConverter() {
        this.deserializer = new DefaultDeserializer();
    }
    
    public DeserializingConverter(final Deserializer<Object> deserializer) {
        Assert.notNull(deserializer, "Deserializer must not be null");
        this.deserializer = deserializer;
    }
    
    @Override
    public Object convert(final byte[] source) {
        final ByteArrayInputStream byteStream = new ByteArrayInputStream(source);
        try {
            return this.deserializer.deserialize(byteStream);
        }
        catch (Throwable ex) {
            throw new SerializationFailedException("Failed to deserialize payload. Is the byte array a result of corresponding serialization for " + this.deserializer.getClass().getSimpleName() + "?", ex);
        }
    }
}
