// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.serializer.support;

import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.springframework.util.Assert;
import org.springframework.core.serializer.DefaultSerializer;
import org.springframework.core.serializer.Serializer;
import org.springframework.core.convert.converter.Converter;

public class SerializingConverter implements Converter<Object, byte[]>
{
    private final Serializer<Object> serializer;
    
    public SerializingConverter() {
        this.serializer = new DefaultSerializer();
    }
    
    public SerializingConverter(final Serializer<Object> serializer) {
        Assert.notNull(serializer, "Serializer must not be null");
        this.serializer = serializer;
    }
    
    @Override
    public byte[] convert(final Object source) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream(128);
        try {
            this.serializer.serialize(source, byteStream);
            return byteStream.toByteArray();
        }
        catch (Throwable ex) {
            throw new SerializationFailedException("Failed to serialize object using " + this.serializer.getClass().getSimpleName(), ex);
        }
    }
}
