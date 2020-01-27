// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.serializer;

import java.io.IOException;
import org.springframework.core.NestedIOException;
import java.io.ObjectInputStream;
import java.io.InputStream;

public class DefaultDeserializer implements Deserializer<Object>
{
    @Override
    public Object deserialize(final InputStream inputStream) throws IOException {
        final ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        try {
            return objectInputStream.readObject();
        }
        catch (ClassNotFoundException ex) {
            throw new NestedIOException("Failed to deserialize object type", ex);
        }
    }
}
