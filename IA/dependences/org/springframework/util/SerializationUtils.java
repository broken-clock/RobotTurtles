// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;

public abstract class SerializationUtils
{
    public static byte[] serialize(final Object object) {
        if (object == null) {
            return null;
        }
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            final ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            oos.flush();
        }
        catch (IOException ex) {
            throw new IllegalArgumentException("Failed to serialize object of type: " + object.getClass(), ex);
        }
        return baos.toByteArray();
    }
    
    public static Object deserialize(final byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try {
            final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            return ois.readObject();
        }
        catch (IOException ex) {
            throw new IllegalArgumentException("Failed to deserialize object", ex);
        }
        catch (ClassNotFoundException ex2) {
            throw new IllegalStateException("Failed to deserialize object type", ex2);
        }
    }
}
