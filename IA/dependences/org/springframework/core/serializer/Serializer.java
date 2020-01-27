// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.serializer;

import java.io.IOException;
import java.io.OutputStream;

public interface Serializer<T>
{
    void serialize(final T p0, final OutputStream p1) throws IOException;
}
