// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.client;

import org.springframework.core.NestedRuntimeException;

public class RestClientException extends NestedRuntimeException
{
    private static final long serialVersionUID = -4084444984163796577L;
    
    public RestClientException(final String msg) {
        super(msg);
    }
    
    public RestClientException(final String msg, final Throwable ex) {
        super(msg, ex);
    }
}
