// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

public abstract class NestedRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = 5439915454935047936L;
    
    public NestedRuntimeException(final String msg) {
        super(msg);
    }
    
    public NestedRuntimeException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
    
    @Override
    public String getMessage() {
        return NestedExceptionUtils.buildMessage(super.getMessage(), this.getCause());
    }
    
    public Throwable getRootCause() {
        Throwable rootCause = null;
        for (Throwable cause = this.getCause(); cause != null && cause != rootCause; rootCause = cause, cause = cause.getCause()) {}
        return rootCause;
    }
    
    public Throwable getMostSpecificCause() {
        final Throwable rootCause = this.getRootCause();
        return (rootCause != null) ? rootCause : this;
    }
    
    public boolean contains(final Class<?> exType) {
        if (exType == null) {
            return false;
        }
        if (exType.isInstance(this)) {
            return true;
        }
        Throwable cause = this.getCause();
        if (cause == this) {
            return false;
        }
        if (cause instanceof NestedRuntimeException) {
            return ((NestedRuntimeException)cause).contains(exType);
        }
        while (cause != null) {
            if (exType.isInstance(cause)) {
                return true;
            }
            if (cause.getCause() == cause) {
                break;
            }
            cause = cause.getCause();
        }
        return false;
    }
    
    static {
        NestedExceptionUtils.class.getName();
    }
}
