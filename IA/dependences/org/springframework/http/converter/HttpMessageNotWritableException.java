// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.converter;

public class HttpMessageNotWritableException extends HttpMessageConversionException
{
    public HttpMessageNotWritableException(final String msg) {
        super(msg);
    }
    
    public HttpMessageNotWritableException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
