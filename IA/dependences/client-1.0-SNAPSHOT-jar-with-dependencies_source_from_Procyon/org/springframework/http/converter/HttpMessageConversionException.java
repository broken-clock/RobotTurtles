// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.http.converter;

import org.springframework.core.NestedRuntimeException;

public class HttpMessageConversionException extends NestedRuntimeException
{
    public HttpMessageConversionException(final String msg) {
        super(msg);
    }
    
    public HttpMessageConversionException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
