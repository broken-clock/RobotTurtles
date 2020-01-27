// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.converter;

import java.io.IOException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import java.lang.reflect.Type;

public interface GenericHttpMessageConverter<T> extends HttpMessageConverter<T>
{
    boolean canRead(final Type p0, final Class<?> p1, final MediaType p2);
    
    T read(final Type p0, final Class<?> p1, final HttpInputMessage p2) throws IOException, HttpMessageNotReadableException;
}
