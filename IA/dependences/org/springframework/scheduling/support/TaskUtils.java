// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.scheduling.support;

import org.springframework.util.ReflectionUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.springframework.util.ErrorHandler;

public abstract class TaskUtils
{
    public static final ErrorHandler LOG_AND_SUPPRESS_ERROR_HANDLER;
    public static final ErrorHandler LOG_AND_PROPAGATE_ERROR_HANDLER;
    
    public static DelegatingErrorHandlingRunnable decorateTaskWithErrorHandler(final Runnable task, final ErrorHandler errorHandler, final boolean isRepeatingTask) {
        if (task instanceof DelegatingErrorHandlingRunnable) {
            return (DelegatingErrorHandlingRunnable)task;
        }
        final ErrorHandler eh = (errorHandler != null) ? errorHandler : getDefaultErrorHandler(isRepeatingTask);
        return new DelegatingErrorHandlingRunnable(task, eh);
    }
    
    public static ErrorHandler getDefaultErrorHandler(final boolean isRepeatingTask) {
        return isRepeatingTask ? TaskUtils.LOG_AND_SUPPRESS_ERROR_HANDLER : TaskUtils.LOG_AND_PROPAGATE_ERROR_HANDLER;
    }
    
    static {
        LOG_AND_SUPPRESS_ERROR_HANDLER = new LoggingErrorHandler();
        LOG_AND_PROPAGATE_ERROR_HANDLER = new PropagatingErrorHandler();
    }
    
    private static class LoggingErrorHandler implements ErrorHandler
    {
        private final Log logger;
        
        private LoggingErrorHandler() {
            this.logger = LogFactory.getLog(LoggingErrorHandler.class);
        }
        
        @Override
        public void handleError(final Throwable t) {
            if (this.logger.isErrorEnabled()) {
                this.logger.error("Unexpected error occurred in scheduled task.", t);
            }
        }
    }
    
    private static class PropagatingErrorHandler extends LoggingErrorHandler
    {
        @Override
        public void handleError(final Throwable t) {
            super.handleError(t);
            ReflectionUtils.rethrowRuntimeException(t);
        }
    }
}
