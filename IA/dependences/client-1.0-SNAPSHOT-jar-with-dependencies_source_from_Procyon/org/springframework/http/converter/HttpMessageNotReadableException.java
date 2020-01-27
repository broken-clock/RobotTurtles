// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.converter;

public class HttpMessageNotReadableException extends HttpMessageConversionException
{
    public HttpMessageNotReadableException(final String msg) {
        super(msg);
    }
    
    public HttpMessageNotReadableException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
