// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http;

import org.springframework.util.InvalidMimeTypeException;

public class InvalidMediaTypeException extends IllegalArgumentException
{
    private String mediaType;
    
    public InvalidMediaTypeException(final String mediaType, final String message) {
        super("Invalid media type \"" + mediaType + "\": " + message);
        this.mediaType = mediaType;
    }
    
    InvalidMediaTypeException(final InvalidMimeTypeException ex) {
        super(ex.getMessage(), ex);
        this.mediaType = ex.getMimeType();
    }
    
    public String getMediaType() {
        return this.mediaType;
    }
}
