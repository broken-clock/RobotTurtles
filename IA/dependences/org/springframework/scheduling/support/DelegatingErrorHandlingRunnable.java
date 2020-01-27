// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.support;

import java.lang.reflect.UndeclaredThrowableException;
import org.springframework.util.Assert;
import org.springframework.util.ErrorHandler;

public class DelegatingErrorHandlingRunnable implements Runnable
{
    private final Runnable delegate;
    private final ErrorHandler errorHandler;
    
    public DelegatingErrorHandlingRunnable(final Runnable delegate, final ErrorHandler errorHandler) {
        Assert.notNull(delegate, "Delegate must not be null");
        Assert.notNull(errorHandler, "ErrorHandler must not be null");
        this.delegate = delegate;
        this.errorHandler = errorHandler;
    }
    
    @Override
    public void run() {
        try {
            this.delegate.run();
        }
        catch (UndeclaredThrowableException ex) {
            this.errorHandler.handleError(ex.getUndeclaredThrowable());
        }
        catch (Throwable ex2) {
            this.errorHandler.handleError(ex2);
        }
    }
    
    @Override
    public String toString() {
        return "DelegatingErrorHandlingRunnable for " + this.delegate;
    }
}
