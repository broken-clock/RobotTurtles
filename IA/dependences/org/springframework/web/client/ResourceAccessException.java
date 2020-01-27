// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.client;

import java.io.IOException;

public class ResourceAccessException extends RestClientException
{
    private static final long serialVersionUID = -8513182514355844870L;
    
    public ResourceAccessException(final String msg) {
        super(msg);
    }
    
    public ResourceAccessException(final String msg, final IOException ex) {
        super(msg, ex);
    }
}
