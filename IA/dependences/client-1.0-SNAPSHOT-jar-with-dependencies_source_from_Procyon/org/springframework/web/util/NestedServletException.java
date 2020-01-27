// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.web.util;

import org.springframework.core.NestedExceptionUtils;
import javax.servlet.ServletException;

public class NestedServletException extends ServletException
{
    private static final long serialVersionUID = -5292377985529381145L;
    
    public NestedServletException(final String msg) {
        super(msg);
    }
    
    public NestedServletException(final String msg, final Throwable cause) {
        super(msg, cause);
        if (this.getCause() == null && cause != null) {
            this.initCause(cause);
        }
    }
    
    public String getMessage() {
        return NestedExceptionUtils.buildMessage(super.getMessage(), this.getCause());
    }
    
    static {
        NestedExceptionUtils.class.getName();
    }
}
