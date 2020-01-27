// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.remoting.support;

import java.lang.reflect.InvocationTargetException;
import java.io.Serializable;

public class RemoteInvocationResult implements Serializable
{
    private static final long serialVersionUID = 2138555143707773549L;
    private Object value;
    private Throwable exception;
    
    public RemoteInvocationResult(final Object value) {
        this.value = value;
    }
    
    public RemoteInvocationResult(final Throwable exception) {
        this.exception = exception;
    }
    
    public RemoteInvocationResult() {
    }
    
    public void setValue(final Object value) {
        this.value = value;
    }
    
    public Object getValue() {
        return this.value;
    }
    
    public void setException(final Throwable exception) {
        this.exception = exception;
    }
    
    public Throwable getException() {
        return this.exception;
    }
    
    public boolean hasException() {
        return this.exception != null;
    }
    
    public boolean hasInvocationTargetException() {
        return this.exception instanceof InvocationTargetException;
    }
    
    public Object recreate() throws Throwable {
        if (this.exception != null) {
            Throwable exToThrow = this.exception;
            if (this.exception instanceof InvocationTargetException) {
                exToThrow = ((InvocationTargetException)this.exception).getTargetException();
            }
            RemoteInvocationUtils.fillInClientStackTraceIfPossible(exToThrow);
            throw exToThrow;
        }
        return this.value;
    }
}
