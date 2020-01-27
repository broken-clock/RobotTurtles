// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web;

import org.springframework.http.MediaType;
import java.util.List;

public class HttpMediaTypeNotAcceptableException extends HttpMediaTypeException
{
    public HttpMediaTypeNotAcceptableException(final String message) {
        super(message);
    }
    
    public HttpMediaTypeNotAcceptableException(final List<MediaType> supportedMediaTypes) {
        super("Could not find acceptable representation", supportedMediaTypes);
    }
}
