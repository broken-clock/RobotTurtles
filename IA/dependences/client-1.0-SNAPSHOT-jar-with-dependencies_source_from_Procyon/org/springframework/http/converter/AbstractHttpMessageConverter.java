// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.converter;

import java.io.OutputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.http.HttpOutputMessage;
import java.io.IOException;
import org.springframework.http.HttpInputMessage;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.util.Assert;
import java.util.Arrays;
import java.util.Collections;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import java.util.List;
import org.apache.commons.logging.Log;

public abstract class AbstractHttpMessageConverter<T> implements HttpMessageConverter<T>
{
    protected final Log logger;
    private List<MediaType> supportedMediaTypes;
    
    protected AbstractHttpMessageConverter() {
        this.logger = LogFactory.getLog(this.getClass());
        this.supportedMediaTypes = Collections.emptyList();
    }
    
    protected AbstractHttpMessageConverter(final MediaType supportedMediaType) {
        this.logger = LogFactory.getLog(this.getClass());
        this.supportedMediaTypes = Collections.emptyList();
        this.setSupportedMediaTypes(Collections.singletonList(supportedMediaType));
    }
    
    protected AbstractHttpMessageConverter(final MediaType... supportedMediaTypes) {
        this.logger = LogFactory.getLog(this.getClass());
        this.supportedMediaTypes = Collections.emptyList();
        this.setSupportedMediaTypes(Arrays.asList(supportedMediaTypes));
    }
    
    public void setSupportedMediaTypes(final List<MediaType> supportedMediaTypes) {
        Assert.notEmpty(supportedMediaTypes, "'supportedMediaTypes' must not be empty");
        this.supportedMediaTypes = new ArrayList<MediaType>(supportedMediaTypes);
    }
    
    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Collections.unmodifiableList((List<? extends MediaType>)this.supportedMediaTypes);
    }
    
    @Override
    public boolean canRead(final Class<?> clazz, final MediaType mediaType) {
        return this.supports(clazz) && this.canRead(mediaType);
    }
    
    protected boolean canRead(final MediaType mediaType) {
        if (mediaType == null) {
            return true;
        }
        for (final MediaType supportedMediaType : this.getSupportedMediaTypes()) {
            if (supportedMediaType.includes(mediaType)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean canWrite(final Class<?> clazz, final MediaType mediaType) {
        return this.supports(clazz) && this.canWrite(mediaType);
    }
    
    protected boolean canWrite(final MediaType mediaType) {
        if (mediaType == null || MediaType.ALL.equals(mediaType)) {
            return true;
        }
        for (final MediaType supportedMediaType : this.getSupportedMediaTypes()) {
            if (supportedMediaType.isCompatibleWith(mediaType)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public final T read(final Class<? extends T> clazz, final HttpInputMessage inputMessage) throws IOException {
        return this.readInternal(clazz, inputMessage);
    }
    
    @Override
    public final void write(final T t, MediaType contentType, final HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        final HttpHeaders headers = outputMessage.getHeaders();
        if (headers.getContentType() == null) {
            if (contentType == null || contentType.isWildcardType() || contentType.isWildcardSubtype()) {
                contentType = this.getDefaultContentType(t);
            }
            if (contentType != null) {
                headers.setContentType(contentType);
            }
        }
        if (headers.getContentLength() == -1L) {
            final Long contentLength = this.getContentLength(t, headers.getContentType());
            if (contentLength != null) {
                headers.setContentLength(contentLength);
            }
        }
        if (outputMessage instanceof StreamingHttpOutputMessage) {
            final StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage)outputMessage;
            streamingOutputMessage.setBody(new StreamingHttpOutputMessage.Body() {
                @Override
                public void writeTo(final OutputStream outputStream) throws IOException {
                    AbstractHttpMessageConverter.this.writeInternal(t, new HttpOutputMessage() {
                        @Override
                        public OutputStream getBody() throws IOException {
                            return outputStream;
                        }
                        
                        @Override
                        public HttpHeaders getHeaders() {
                            return headers;
                        }
                    });
                }
            });
        }
        else {
            this.writeInternal(t, outputMessage);
            outputMessage.getBody().flush();
        }
    }
    
    protected MediaType getDefaultContentType(final T t) throws IOException {
        final List<MediaType> mediaTypes = this.getSupportedMediaTypes();
        return mediaTypes.isEmpty() ? null : mediaTypes.get(0);
    }
    
    protected Long getContentLength(final T t, final MediaType contentType) throws IOException {
        return null;
    }
    
    protected abstract boolean supports(final Class<?> p0);
    
    protected abstract T readInternal(final Class<? extends T> p0, final HttpInputMessage p1) throws IOException, HttpMessageNotReadableException;
    
    protected abstract void writeInternal(final T p0, final HttpOutputMessage p1) throws IOException, HttpMessageNotWritableException;
}
