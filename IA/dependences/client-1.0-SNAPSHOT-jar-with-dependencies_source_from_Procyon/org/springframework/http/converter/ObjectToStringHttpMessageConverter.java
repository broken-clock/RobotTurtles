// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.converter;

import org.springframework.http.HttpOutputMessage;
import java.io.IOException;
import org.springframework.http.HttpInputMessage;
import org.springframework.util.Assert;
import org.springframework.http.MediaType;
import java.nio.charset.Charset;
import org.springframework.core.convert.ConversionService;

public class ObjectToStringHttpMessageConverter extends AbstractHttpMessageConverter<Object>
{
    private ConversionService conversionService;
    private StringHttpMessageConverter stringHttpMessageConverter;
    
    public ObjectToStringHttpMessageConverter(final ConversionService conversionService) {
        this(conversionService, StringHttpMessageConverter.DEFAULT_CHARSET);
    }
    
    public ObjectToStringHttpMessageConverter(final ConversionService conversionService, final Charset defaultCharset) {
        super(new MediaType("text", "plain", defaultCharset));
        Assert.notNull(conversionService, "conversionService is required");
        this.conversionService = conversionService;
        this.stringHttpMessageConverter = new StringHttpMessageConverter(defaultCharset);
    }
    
    public void setWriteAcceptCharset(final boolean writeAcceptCharset) {
        this.stringHttpMessageConverter.setWriteAcceptCharset(writeAcceptCharset);
    }
    
    @Override
    public boolean canRead(final Class<?> clazz, final MediaType mediaType) {
        return this.conversionService.canConvert(String.class, clazz) && this.canRead(mediaType);
    }
    
    @Override
    public boolean canWrite(final Class<?> clazz, final MediaType mediaType) {
        return this.conversionService.canConvert(clazz, String.class) && this.canWrite(mediaType);
    }
    
    @Override
    protected boolean supports(final Class<?> clazz) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected Object readInternal(final Class<?> clazz, final HttpInputMessage inputMessage) throws IOException {
        final String value = this.stringHttpMessageConverter.readInternal(String.class, inputMessage);
        return this.conversionService.convert(value, clazz);
    }
    
    @Override
    protected void writeInternal(final Object obj, final HttpOutputMessage outputMessage) throws IOException {
        final String s = this.conversionService.convert(obj, String.class);
        this.stringHttpMessageConverter.writeInternal(s, outputMessage);
    }
    
    @Override
    protected Long getContentLength(final Object obj, final MediaType contentType) {
        final String value = this.conversionService.convert(obj, String.class);
        return this.stringHttpMessageConverter.getContentLength(value, contentType);
    }
}
