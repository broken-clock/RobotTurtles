// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling;

import org.springframework.core.NestedRuntimeException;

public class SchedulingException extends NestedRuntimeException
{
    public SchedulingException(final String msg) {
        super(msg);
    }
    
    public SchedulingException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
