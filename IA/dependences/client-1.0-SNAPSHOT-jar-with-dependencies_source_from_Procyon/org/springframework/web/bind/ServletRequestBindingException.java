// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.bind;

import org.springframework.web.util.NestedServletException;

public class ServletRequestBindingException extends NestedServletException
{
    public ServletRequestBindingException(final String msg) {
        super(msg);
    }
    
    public ServletRequestBindingException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
