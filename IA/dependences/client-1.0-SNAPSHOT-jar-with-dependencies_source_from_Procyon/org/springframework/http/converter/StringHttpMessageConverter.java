// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.converter;

import org.springframework.http.HttpOutputMessage;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import org.springframework.util.StreamUtils;
import org.springframework.http.HttpInputMessage;
import java.util.Collection;
import java.util.ArrayList;
import org.springframework.http.MediaType;
import java.util.List;
import java.nio.charset.Charset;

public class StringHttpMessageConverter extends AbstractHttpMessageConverter<String>
{
    public static final Charset DEFAULT_CHARSET;
    private final Charset defaultCharset;
    private final List<Charset> availableCharsets;
    private boolean writeAcceptCharset;
    
    public StringHttpMessageConverter() {
        this(StringHttpMessageConverter.DEFAULT_CHARSET);
    }
    
    public StringHttpMessageConverter(final Charset defaultCharset) {
        super(new MediaType[] { new MediaType("text", "plain", defaultCharset), MediaType.ALL });
        this.writeAcceptCharset = true;
        this.defaultCharset = defaultCharset;
        this.availableCharsets = new ArrayList<Charset>(Charset.availableCharsets().values());
    }
    
    public void setWriteAcceptCharset(final boolean writeAcceptCharset) {
        this.writeAcceptCharset = writeAcceptCharset;
    }
    
    public boolean supports(final Class<?> clazz) {
        return String.class.equals(clazz);
    }
    
    @Override
    protected String readInternal(final Class<? extends String> clazz, final HttpInputMessage inputMessage) throws IOException {
        final Charset charset = this.getContentTypeCharset(inputMessage.getHeaders().getContentType());
        return StreamUtils.copyToString(inputMessage.getBody(), charset);
    }
    
    @Override
    protected Long getContentLength(final String s, final MediaType contentType) {
        final Charset charset = this.getContentTypeCharset(contentType);
        try {
            return (long)s.getBytes(charset.name()).length;
        }
        catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }
    
    @Override
    protected void writeInternal(final String s, final HttpOutputMessage outputMessage) throws IOException {
        if (this.writeAcceptCharset) {
            outputMessage.getHeaders().setAcceptCharset(this.getAcceptedCharsets());
        }
        final Charset charset = this.getContentTypeCharset(outputMessage.getHeaders().getContentType());
        StreamUtils.copy(s, charset, outputMessage.getBody());
    }
    
    protected List<Charset> getAcceptedCharsets() {
        return this.availableCharsets;
    }
    
    private Charset getContentTypeCharset(final MediaType contentType) {
        if (contentType != null && contentType.getCharSet() != null) {
            return contentType.getCharSet();
        }
        return this.defaultCharset;
    }
    
    static {
        DEFAULT_CHARSET = Charset.forName("ISO-8859-1");
    }
}
