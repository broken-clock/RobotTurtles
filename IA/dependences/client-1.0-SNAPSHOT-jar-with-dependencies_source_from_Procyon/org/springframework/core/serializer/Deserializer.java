// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.serializer;

import java.io.IOException;
import java.io.InputStream;

public interface Deserializer<T>
{
    T deserialize(final InputStream p0) throws IOException;
}
