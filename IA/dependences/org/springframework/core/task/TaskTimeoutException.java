// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.task;

public class TaskTimeoutException extends TaskRejectedException
{
    public TaskTimeoutException(final String msg) {
        super(msg);
    }
    
    public TaskTimeoutException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
