// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.serializer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.OutputStream;

public class DefaultSerializer implements Serializer<Object>
{
    @Override
    public void serialize(final Object object, final OutputStream outputStream) throws IOException {
        if (!(object instanceof Serializable)) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + " requires a Serializable payload " + "but received an object of type [" + object.getClass().getName() + "]");
        }
        final ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.flush();
    }
}
