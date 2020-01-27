// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web;

import java.util.List;
import org.springframework.http.MediaType;

public class HttpMediaTypeNotSupportedException extends HttpMediaTypeException
{
    private final MediaType contentType;
    
    public HttpMediaTypeNotSupportedException(final String message) {
        super(message);
        this.contentType = null;
    }
    
    public HttpMediaTypeNotSupportedException(final MediaType contentType, final List<MediaType> supportedMediaTypes) {
        this(contentType, supportedMediaTypes, "Content type '" + contentType + "' not supported");
    }
    
    public HttpMediaTypeNotSupportedException(final MediaType contentType, final List<MediaType> supportedMediaTypes, final String msg) {
        super(msg, supportedMediaTypes);
        this.contentType = contentType;
    }
    
    public MediaType getContentType() {
        return this.contentType;
    }
}
