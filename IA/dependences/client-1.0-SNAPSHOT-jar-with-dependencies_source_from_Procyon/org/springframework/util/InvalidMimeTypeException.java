// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

public class InvalidMimeTypeException extends IllegalArgumentException
{
    private String mimeType;
    
    public InvalidMimeTypeException(final String mimeType, final String message) {
        super("Invalid mime type \"" + mimeType + "\": " + message);
        this.mimeType = mimeType;
    }
    
    public String getMimeType() {
        return this.mimeType;
    }
}
