// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core.task;

import java.util.concurrent.RejectedExecutionException;

public class TaskRejectedException extends RejectedExecutionException
{
    public TaskRejectedException(final String msg) {
        super(msg);
    }
    
    public TaskRejectedException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
