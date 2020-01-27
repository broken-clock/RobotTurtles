// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.converter;

import org.springframework.http.HttpOutputMessage;
import java.io.IOException;
import java.io.OutputStream;
import org.springframework.util.StreamUtils;
import java.io.ByteArrayOutputStream;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;

public class ByteArrayHttpMessageConverter extends AbstractHttpMessageConverter<byte[]>
{
    public ByteArrayHttpMessageConverter() {
        super(new MediaType[] { new MediaType("application", "octet-stream"), MediaType.ALL });
    }
    
    public boolean supports(final Class<?> clazz) {
        return byte[].class.equals(clazz);
    }
    
    public byte[] readInternal(final Class<? extends byte[]> clazz, final HttpInputMessage inputMessage) throws IOException {
        final long contentLength = inputMessage.getHeaders().getContentLength();
        final ByteArrayOutputStream bos = new ByteArrayOutputStream((contentLength >= 0L) ? ((int)contentLength) : 4096);
        StreamUtils.copy(inputMessage.getBody(), bos);
        return bos.toByteArray();
    }
    
    @Override
    protected Long getContentLength(final byte[] bytes, final MediaType contentType) {
        return (long)bytes.length;
    }
    
    @Override
    protected void writeInternal(final byte[] bytes, final HttpOutputMessage outputMessage) throws IOException {
        StreamUtils.copy(bytes, outputMessage.getBody());
    }
}
