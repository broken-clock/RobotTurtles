// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.multipart;

import org.springframework.core.NestedRuntimeException;

public class MultipartException extends NestedRuntimeException
{
    public MultipartException(final String msg) {
        super(msg);
    }
    
    public MultipartException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
