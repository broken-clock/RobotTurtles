// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.converter;

import org.springframework.http.HttpOutputMessage;
import java.io.IOException;
import org.springframework.http.HttpInputMessage;
import java.util.List;
import org.springframework.http.MediaType;

public interface HttpMessageConverter<T>
{
    boolean canRead(final Class<?> p0, final MediaType p1);
    
    boolean canWrite(final Class<?> p0, final MediaType p1);
    
    List<MediaType> getSupportedMediaTypes();
    
    T read(final Class<? extends T> p0, final HttpInputMessage p1) throws IOException, HttpMessageNotReadableException;
    
    void write(final T p0, final MediaType p1, final HttpOutputMessage p2) throws IOException, HttpMessageNotWritableException;
}
