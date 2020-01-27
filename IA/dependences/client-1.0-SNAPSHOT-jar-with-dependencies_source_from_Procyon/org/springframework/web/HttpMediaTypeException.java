// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web;

import java.util.Collections;
import org.springframework.http.MediaType;
import java.util.List;
import javax.servlet.ServletException;

public abstract class HttpMediaTypeException extends ServletException
{
    private final List<MediaType> supportedMediaTypes;
    
    protected HttpMediaTypeException(final String message) {
        super(message);
        this.supportedMediaTypes = Collections.emptyList();
    }
    
    protected HttpMediaTypeException(final String message, final List<MediaType> supportedMediaTypes) {
        super(message);
        this.supportedMediaTypes = Collections.unmodifiableList((List<? extends MediaType>)supportedMediaTypes);
    }
    
    public List<MediaType> getSupportedMediaTypes() {
        return this.supportedMediaTypes;
    }
}
